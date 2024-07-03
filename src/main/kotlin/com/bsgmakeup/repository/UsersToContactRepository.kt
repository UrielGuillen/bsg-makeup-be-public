package com.bsgmakeup.repository

import com.bsgmakeup.entity.UsersToContactEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface UsersToContactRepository: MongoRepository<UsersToContactEntity, String> {

    fun findByPhone(phone: String): UsersToContactEntity?
}
