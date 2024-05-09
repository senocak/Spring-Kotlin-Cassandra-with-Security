package com.github.senocak.skc.config

import com.datastax.oss.driver.api.core.CqlSession
import java.util.Optional
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.EnableCassandraAuditing
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean
import org.springframework.data.cassandra.core.convert.CassandraConverter
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component

@Configuration
@EnableCassandraAuditing
@EnableCassandraRepositories(basePackages = ["com.github.senocak.skc.domain"])
class CassandraConfig(
    private val dataSourceConfig: DataSourceConfig
): AbstractCassandraConfiguration() {
    override fun getKeyspaceName(): String = dataSourceConfig.keyspaceName
    public override fun getContactPoints(): String = dataSourceConfig.contactPoints
    public override fun getPort(): Int = dataSourceConfig.port.toInt()
    override fun getSchemaAction(): SchemaAction = SchemaAction.CREATE_IF_NOT_EXISTS
    override fun keyspacePopulator(): KeyspacePopulator =
        ResourceKeyspacePopulator()
            .also { it.addScripts(ClassPathResource("schema.cql")) }

    /*
     * Automatically creates a Keyspace if it doesn't exist
     */
    override fun getKeyspaceCreations(): List<CreateKeyspaceSpecification> =
        CreateKeyspaceSpecification
            .createKeyspace(dataSourceConfig.keyspaceName)
            .ifNotExists()
            .with(KeyspaceOption.DURABLE_WRITES, true)
            .withSimpleReplication()
            .run { listOf(element = this) }

    @Bean
    override fun cassandraSessionFactory(cqlSession: CqlSession): SessionFactoryFactoryBean {
        val bean = SessionFactoryFactoryBean()
        bean.setSession(cqlSession)
        bean.setConverter(requireBeanOfType(CassandraConverter::class.java))
        bean.setKeyspaceCleaner(this.keyspaceCleaner())
        if (dataSourceConfig.ddl == "create")
            bean.setKeyspacePopulator(this.keyspacePopulator())
        bean.setSchemaAction(this.schemaAction)
        return bean
    }

    @Bean
    fun exceptionTranslation(): PersistenceExceptionTranslationPostProcessor = PersistenceExceptionTranslationPostProcessor()

    @Bean
    fun auditorAwareRef(): AuditorAware<String> = AuditorAware<String> { Optional.of("Mr. Senocak") }
}

@Component
@ConfigurationProperties(prefix = "spring.datasource")
class DataSourceConfig {
    lateinit var contactPoints: String
    lateinit var port: String
    lateinit var ddl: String
    lateinit var keyspaceName: String
}
