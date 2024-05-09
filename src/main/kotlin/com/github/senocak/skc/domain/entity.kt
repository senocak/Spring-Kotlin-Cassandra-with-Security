package com.github.senocak.skc.domain

import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.cql.Statement
import java.io.Serializable
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import org.springframework.core.Ordered
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.SASI
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.cassandra.core.mapping.event.AbstractCassandraEventListener
import org.springframework.data.cassandra.core.mapping.event.BeforeSaveCallback
import org.springframework.data.cassandra.core.mapping.event.BeforeSaveEvent
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed


open class BaseDomain(
    @PrimaryKey var id: UUID? = null,
    @CreatedDate var createdAt: Date = Date(),
    @LastModifiedDate var updatedAt: Date = Date()
): Serializable

@Table("users")
data class User(
    @org.springframework.data.cassandra.core.mapping.Indexed
    @SASI(indexMode = SASI.IndexMode.CONTAINS)
    @SASI.StandardAnalyzed
    var name: String? = null,
    var email: String? = null,
    var password: String? = null
): BaseDomain() {
    //@CassandraType(type = CassandraType.Name.SET, typeArguments = [CassandraType.Name.VARCHAR])
    var roles: List<String> = arrayListOf()
    var emailActivationToken: String? = null
    var emailActivatedAt: Date? = null

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "second")
    var secondaryCaptain: Captain? = null
}

internal class BeforeSaveListener : AbstractCassandraEventListener<User>() {
    override fun onBeforeSave(event: BeforeSaveEvent<User>) {
        println("------onBeforeSave------$event")
    }
}

class DefaultingEntityCallback: BeforeSaveCallback<User>, Ordered {
    override fun getOrder() = 100
    override fun onBeforeSave(entity: User, tableName: CqlIdentifier, statement: Statement<*>): User {
        println("------onBeforeSave------$entity, $tableName, $statement")
        return entity
    }
}

data class Captain (
    val name: String,
    val rank: String
)

@RedisHash(value = "jwtTokens")
data class JwtToken (
    @Id
    @Indexed
    val token: String,

    val tokenType: String,

    @Indexed
    val email: String,

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    val timeToLive: Long = TimeUnit.MINUTES.toMillis(30)
)

@RedisHash("password_reset_tokens")
data class PasswordResetToken(
    @Id
    @Indexed
    val token: String,

    @Indexed
    val userId: UUID,

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    val timeToLive: Long = TimeUnit.MINUTES.toMillis(30)
)