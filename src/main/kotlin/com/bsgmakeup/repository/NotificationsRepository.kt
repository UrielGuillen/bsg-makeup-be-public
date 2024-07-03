package com.bsgmakeup.repository

import com.bsgmakeup.entity.NotificationsEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationsRepository: MongoRepository<NotificationsEntity, String> {

    fun findByUserIdToAndActiveOrderByCreatedAtDesc(userIdTo: String, active: Boolean): List<NotificationsEntity>
}
