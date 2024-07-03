package com.bsgmakeup.dto

open class CreateAppointmentDTO (
    val scheduledDate: String,
    val time: String,
    var userId: String,
    val serviceId: String
)

class CreateManualAppointmentDTO(
    val name: String,
    val lastName: String,
    val phone: String,
    val scheduledDate: String,
    val time: String,
    var userId: String,
    val serviceId: String,
    val userExists: Boolean
)
