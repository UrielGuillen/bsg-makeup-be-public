package com.bsgmakeup.repository

import com.bsgmakeup.entity.AppointmentsFeedbackEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface AppointmentsFeedbackRepository: MongoRepository<AppointmentsFeedbackEntity, String> {

    fun findByAppointmentId(appointmentId: String): Optional<AppointmentsFeedbackEntity>
}
