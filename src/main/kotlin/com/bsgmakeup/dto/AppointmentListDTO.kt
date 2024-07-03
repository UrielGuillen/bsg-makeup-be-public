package com.bsgmakeup.dto

class AppointmentListDTO(
    val id: String,
    val scheduledDate: String,
    val time: String,
    var userId: String,
    val userName: String,
    val serviceId: String,
    val serviceName: String,
    val serviceTime: Int,
    val userPhoneNumber: String,
    val createdByAdmin: Boolean,
    val status: Int,
    val serviceCost: Int,
    val feedback: String?,
    val feedbackStatus: Int?
)
