package com.bsgmakeup.controller

import com.bsgmakeup.dto.*
import com.bsgmakeup.service.AppointmentsService
import com.bsgmakeup.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/appointment")
class AppointmentsController(
    private val authenticationService: AuthenticationService,
    private val appointmentsService: AppointmentsService
) {

    @GetMapping("/get-appointments")
    fun getAppointments(@RequestHeader("Authorization") token: String?,
                        @RequestParam scheduledDate: String): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userRole = authenticationService.getDataFromToken(token, "userRole") as String

            val appointmentsList = appointmentsService.getAllAppointments(userRole, scheduledDate)

            return ResponseEntity.ok(
                ResponseData(true, "AGENDA.APPOINTMENTS_BY_DATE_SUCCESS", appointmentsList)
            )
        }

        return badRequestResponse()
    }
    
    @GetMapping("/get-user-appointments")
    fun getAppointmentsByUser(@RequestHeader("Authorization") token: String?): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId = authenticationService.getDataFromToken(token, "userId") as String
            val userRole = authenticationService.getDataFromToken(token, "userRole") as String

            val appointmentsList = appointmentsService.getAllAppointmentsByUser(userId, userRole)

            return ResponseEntity.ok(
                ResponseData(true, "AGENDA.USER_APPOINTMENTS_SUCCESS", appointmentsList)
            )
        }
        
        return badRequestResponse()
    }

    @PostMapping("/create-appointment")
    fun createAppointment(@RequestHeader("Authorization") token: String?,
                          @RequestBody createAppointment: CreateAppointmentDTO): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {

            createAppointment.userId = authenticationService.getDataFromToken(token, "userId") as String

            val appointmentCreated = appointmentsService.createAppointment(createAppointment)

            return ResponseEntity.ok(
                ResponseData(true, "SCHEDULE.APPOINTMENT_CREATED_SUCCESS", appointmentCreated)
            )
        }

        return badRequestResponse()
    }

    @PostMapping("/create-manual-appointment")
    fun createManualAppointment(@RequestHeader("Authorization") token: String?,
                                @RequestBody createManualAppointmentDTO: CreateManualAppointmentDTO): ResponseEntity<ResponseData> {
        if (authenticationService.isUserAllowed(token)) {
            val appointmentCreated = appointmentsService.createManualAppointment(createManualAppointmentDTO)

            return ResponseEntity.ok(
                ResponseData(true, "SCHEDULE.APPOINTMENT_CREATED_SUCCESS", appointmentCreated)
            )
        }

        return badRequestResponse()
    }

    @PostMapping("/request-feedback")
    fun requestFeedback(@RequestHeader("Authorization") token: String?,
                          @RequestBody appointmentId: String): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId: String = authenticationService.getDataFromToken(token, "userId") as String
            val requestFeedback = appointmentsService.createFeedbackRequest(appointmentId, userId)

            return ResponseEntity.ok(
                ResponseData(true, "HISTORY.FEEDBACK_REQUEST_SUCCESS", requestFeedback)
            )
        }

        return badRequestResponse()
    }

    @PutMapping("/reject-feedback")
    fun rejectFeedback(@RequestHeader("Authorization") token: String?,
                        @RequestBody appointmentId: String): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val rejectFeedback = appointmentsService.rejectFeedback(appointmentId)

            return ResponseEntity.ok(
                ResponseData(true, "HISTORY.FEEDBACK_REQUEST_REJECT", rejectFeedback)
            )
        }

        return badRequestResponse()
    }

    @PutMapping("/save-feedback")
    fun saveFeedback(@RequestHeader("Authorization") token: String?,
                       @RequestBody feedbackDTO: SaveFeedbackDTO): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val saveFeedback = appointmentsService.saveFeedback(feedbackDTO)

            return ResponseEntity.ok(
                ResponseData(true, "HISTORY.FEEDBACK_REQUEST_SAVED", saveFeedback)
            )
        }

        return badRequestResponse()
    }

    @PutMapping("/complete")
    fun completeAppointment(@RequestHeader("Authorization") token: String?,
                     @RequestBody appointmentId: String): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val saveFeedback = appointmentsService.completeAppointment(appointmentId)

            return ResponseEntity.ok(
                ResponseData(true, "HISTORY.FEEDBACK_REQUEST_SAVED", saveFeedback)
            )
        }

        return badRequestResponse()
    }

    @PutMapping("/update-status")
    fun updateStatus(@RequestHeader("Authorization") token: String?,
                            @RequestBody appointment: UpdateAppointmentStatus): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId: String = authenticationService.getDataFromToken(token, "userId") as String
            val appointmentUpdated = appointmentsService.updateStatus(appointment, userId)
            val message = if (appointment.status == 1) "AGENDA.APPOINTMENT_STATUS_CONFIRMED" else "AGENDA.APPOINTMENT_STATUS_REJECTED"

            return ResponseEntity.ok(
                ResponseData(appointmentUpdated != null, message, appointmentUpdated)
            )
        }

        return badRequestResponse()
    }

    private fun badRequestResponse(): ResponseEntity<ResponseData> {
        return ResponseEntity.badRequest().body(
            ResponseData(false, "Invalid token", null)
        )
    }
}
