package com.bsgmakeup.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("appointments")
class AppointmentsEntity (
    @Id
    val id: String = ObjectId.get().toString(),
    val scheduledDate: String,
    val time: String,
    val userId: String,
    val serviceId: String,
    var status: Int = 0,
    var createdByAdmin: Boolean = false,
    @JsonIgnore
    val available: Boolean = true,
    @JsonIgnore
    val createdAt: Date = Date(),
    @JsonIgnore
    val deletedAt: Date = Date(),
    @JsonIgnore
    var updatedAt: Date = Date()
)
