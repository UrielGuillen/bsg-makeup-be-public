package com.bsgmakeup.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("appointments_feedback")
class AppointmentsFeedbackEntity (
    @Id
    val id: String = ObjectId.get().toString(),
    val appointmentId: String,
    var feedback: String = "",
    var status: Int = 0,
    @JsonIgnore
    val available: Boolean = true,
    @JsonIgnore
    val createdAt: Date = Date(),
    @JsonIgnore
    val deletedAt: Date = Date(),
    @JsonIgnore
    var updatedAt: Date = Date()
)
