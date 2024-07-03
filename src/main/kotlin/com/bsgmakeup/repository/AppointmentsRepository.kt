package com.bsgmakeup.repository

import com.bsgmakeup.entity.AppointmentsEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface AppointmentsRepository: MongoRepository<AppointmentsEntity, String> {

    fun findAllByAvailableAndScheduledDate(available: Boolean = true, scheduledDate: String): List<AppointmentsEntity>

    fun findAllByAvailableAndUserId(available: Boolean = true, userId: String): List<AppointmentsEntity>

    fun findAllByAvailable(available: Boolean): List<AppointmentsEntity>

}
