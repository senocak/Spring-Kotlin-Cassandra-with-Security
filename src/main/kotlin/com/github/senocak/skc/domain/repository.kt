package com.github.senocak.skc.domain

import java.util.UUID
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CassandraRepository<User, UUID> {
    @AllowFiltering
    fun findByEmail(email: String): User?
    fun findByEmailActivationToken(token: String): User?
    fun existsByEmail(email: String): Boolean
}

interface JwtTokenRepository : CrudRepository<JwtToken, UUID> {
    fun findByEmail(email: String): JwtToken?
    fun findAllByEmail(email: String): List<JwtToken?>
    fun findByToken(token: String): JwtToken?
}

interface PasswordResetTokenRepository : CrudRepository<PasswordResetToken, UUID> {
    fun findByToken(token: String): PasswordResetToken?
    fun findByUserId(userId: UUID): PasswordResetToken?
}