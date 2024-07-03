package com.bsgmakeup.repository

import com.bsgmakeup.entity.UserTokenEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface UserTokenRepository: MongoRepository<UserTokenEntity, String> {

    fun findAllByRole(role: String): List<UserTokenEntity>

    fun findByUserId(userId: String): UserTokenEntity?

    fun deleteByUserId(userId: String)
}
