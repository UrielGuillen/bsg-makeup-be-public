package com.bsgmakeup.repository

import com.bsgmakeup.entity.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<UserEntity, String> {
    fun findByPhoneNumber(phoneNumber: String): UserEntity?
}