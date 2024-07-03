package com.bsgmakeup.service

import com.bsgmakeup.dto.*
import com.bsgmakeup.entity.AppointmentsEntity
import com.bsgmakeup.entity.AppointmentsFeedbackEntity
import com.bsgmakeup.entity.ManualUserEntity
import com.bsgmakeup.repository.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class AppointmentsService(
    private val appointmentsRepository: AppointmentsRepository,
    private val appointmentsFeedbackRepository: AppointmentsFeedbackRepository,
    private val servicesCatalogRepository: ServicesCatalogRepository,
    private val microRetouchRepository: MicroRetouchRepository,
    private val manualUserRepository: ManualUserRepository,
    private val userRepository: UserRepository,
    private val sendNotificationService: SendNotificationService
) {

    fun createAppointment(appointment: CreateAppointmentDTO): AppointmentsEntity {
        val appointmentEntity = AppointmentsEntity(
            scheduledDate = appointment.scheduledDate,
            time = appointment.time,
            userId = appointment.userId,
            serviceId = appointment.serviceId
        )

        val appointmentUser = userRepository.findById(appointment.userId).get()
        val notificationContent = (appointmentUser.name + " " + appointmentUser.lastName
            + " ha agendado una cita, confirmala en la agenda o contacta al usuario al " + appointmentUser.phoneNumber)

        sendNotificationService.notifyAdmins(NotificationDTO(
            userIdFrom = appointmentUser.id,
            icon = "today",
            title = "Cita creada",
            content = notificationContent
        ))

        return appointmentsRepository.save(appointmentEntity)
    }

    fun createManualAppointment(manualAppointment: CreateManualAppointmentDTO): AppointmentsEntity {
        val manualUserEntity = ManualUserEntity(
            name = manualAppointment.name,
            lastName = manualAppointment.lastName,
            phone = manualAppointment.phone
        )

        val userCreated: ManualUserEntity = if (manualAppointment.userExists) {
            manualUserRepository.findById(manualAppointment.userId).get()
        } else {
            manualUserRepository.save(manualUserEntity)
        }

        val appointmentEntity = AppointmentsEntity(
            scheduledDate = manualAppointment.scheduledDate,
            time = manualAppointment.time,
            userId = userCreated.id,
            serviceId = manualAppointment.serviceId,
            createdByAdmin = true
        )

        return appointmentsRepository.save(appointmentEntity)
    }

    fun createFeedbackRequest(appointmentId: String, userId: String): AppointmentsFeedbackEntity {
        val appointment = appointmentsRepository.findById(appointmentId)

        val appointmentsFeedbackEntity = AppointmentsFeedbackEntity(appointmentId = appointmentId)

        if (appointment.isPresent) run {
            val appointmentUpdated: AppointmentsEntity = appointment.get()

            appointmentUpdated.status = 3
            appointmentUpdated.updatedAt = Date()

            appointmentsRepository.save(appointmentUpdated)

            sendNotificationService.notifyUser(NotificationDTO(
                userIdFrom = userId,
                userIdTo = appointmentUpdated.userId,
                title = "Retroalimentación solicitada",
                content = "Se han solicitado sus comentarios sobre su última cita",
                icon = "reviews"
            ))
        }

        return appointmentsFeedbackRepository.save(appointmentsFeedbackEntity)
    }

    fun rejectFeedback(appointmentId: String): AppointmentsFeedbackEntity? {
        val appointmentsFeedbackEntity = appointmentsFeedbackRepository.findByAppointmentId(appointmentId)

        if (appointmentsFeedbackEntity.isPresent) run {
            val feedbackUpdated: AppointmentsFeedbackEntity = appointmentsFeedbackEntity.get()
            feedbackUpdated.status = 2
            feedbackUpdated.updatedAt = Date()

            return appointmentsFeedbackRepository.save(feedbackUpdated)
        }

        return null
    }

    fun saveFeedback(feedbackDTO: SaveFeedbackDTO): AppointmentsFeedbackEntity? {
        val appointmentsFeedbackEntity =
            appointmentsFeedbackRepository.findByAppointmentId(feedbackDTO.appointmentId)

        if (appointmentsFeedbackEntity.isPresent) run {
            val feedbackUpdated: AppointmentsFeedbackEntity = appointmentsFeedbackEntity.get()
            feedbackUpdated.feedback = feedbackDTO.feedback
            feedbackUpdated.updatedAt = Date()

            return appointmentsFeedbackRepository.save(feedbackUpdated)
        }

        return null
    }

    fun completeAppointment(appointmentId: String): AppointmentsEntity? {
        val appointmentFeedback = appointmentsFeedbackRepository.findByAppointmentId(appointmentId)
        val appointment = appointmentsRepository.findById(appointmentId)

        if (appointmentFeedback.isPresent) run {
            val feedbackUpdated: AppointmentsFeedbackEntity = appointmentFeedback.get()
            feedbackUpdated.status = 1
            feedbackUpdated.updatedAt = Date()

            appointmentsFeedbackRepository.save(feedbackUpdated)
        }

        if (appointment.isPresent) run {
            val appointmentUpdated: AppointmentsEntity = appointment.get()
            appointmentUpdated.status = 4
            appointmentUpdated.updatedAt = Date()

            return appointmentsRepository.save(appointmentUpdated)
        }

        return null
    }

    fun updateStatus(updateAppointmentStatus: UpdateAppointmentStatus, userId: String): AppointmentsEntity? {
        val appointment = appointmentsRepository.findById(updateAppointmentStatus.appointmentId)

        if (appointment.isPresent) run {
            val appointmentUpdated: AppointmentsEntity = appointment.get()
            appointmentUpdated.status = updateAppointmentStatus.status
            appointmentUpdated.updatedAt = Date()

            if (updateAppointmentStatus.status == 1) {
                sendNotificationService.notifyUser(NotificationDTO(
                    userIdFrom = userId,
                    userIdTo = appointmentUpdated.userId,
                    title = "Cita confirmada",
                    content = "Tu cita ha sido recibida y confirmada.",
                    icon = "event_available"
                ))
            }

            if (updateAppointmentStatus.status == 2) {
                sendNotificationService.notifyUser(NotificationDTO(
                    userIdFrom = userId,
                    userIdTo = appointmentUpdated.userId,
                    title = "Cita rechazada",
                    content = "Tu cita ha sido cancelada, por favor selecciona otro horario.",
                    icon = "event_busy"
                ))
            }

            return appointmentsRepository.save(appointmentUpdated)
        }

        return null
    }

    fun getAllAppointments(userRole: String, scheduledDate: String): List<AppointmentListDTO> {
        val appointmentsList =
            appointmentsRepository.findAllByAvailableAndScheduledDate(true, scheduledDate)

        if (userRole == "user") {
            return cleanAppointments(appointmentsList)
        }

        return getFullAppointmentsData(appointmentsList)
    }

    fun getAllAppointmentsByUser(userId: String, userRole: String): List<AppointmentListDTO> {
        if (userRole == "user") {
            val appointmentsList = appointmentsRepository.findAllByAvailableAndUserId(true, userId)

            return getFullAppointmentsData(appointmentsList)
        }

        val appointmentsList = appointmentsRepository.findAllByAvailable(true)

        return getFullAppointmentsData(appointmentsList)
    }

    private fun cleanAppointments(appointments: List<AppointmentsEntity>): List<AppointmentListDTO> {
        val appointmentsDTO: List<AppointmentListDTO> = appointments.map {
            val service = servicesCatalogRepository.findById(it.serviceId)
            val adminService = microRetouchRepository.findById(it.serviceId)

            AppointmentListDTO(
                scheduledDate = it.scheduledDate,
                time = it.time,
                userId = "",
                serviceId = "",
                id = it.id,
                serviceName = "Busy",
                serviceTime = if (service.isPresent) service.get().time else adminService.get().time,
                userName = "",
                userPhoneNumber = "",
                status = 0,
                serviceCost = 0,
                feedback = null,
                feedbackStatus = null,
                createdByAdmin = it.createdByAdmin
            )
        }

        return appointmentsDTO
    }

    private fun getFullAppointmentsData(appointments: List<AppointmentsEntity>): List<AppointmentListDTO> {
        val appointmentsDTO: List<AppointmentListDTO> = appointments.map {
            val user = userRepository.findById(it.userId)
            val manualUser = manualUserRepository.findById(it.userId)
            val service = servicesCatalogRepository.findById(it.serviceId)
            val adminService = microRetouchRepository.findById(it.serviceId)
            val feedback = appointmentsFeedbackRepository.findByAppointmentId(it.id)

            AppointmentListDTO(
                id = it.id,
                status = it.status,
                scheduledDate = it.scheduledDate,
                time = it.time,
                userId = it.userId,
                createdByAdmin = it.createdByAdmin,
                serviceId = it.serviceId,
                userName = if (it.createdByAdmin) (manualUser.get().name + " " + manualUser.get().lastName) else (user.get().name + " " + user.get().lastName),
                userPhoneNumber = if (it.createdByAdmin) manualUser.get().phone else user.get().phoneNumber,
                serviceName = if (service.isPresent) service.get().name else adminService.get().name,
                serviceTime = if (service.isPresent) service.get().time else adminService.get().time,
                serviceCost = if (service.isPresent) service.get().cost else adminService.get().cost,
                feedback = if (feedback.isPresent) feedback.get().feedback else null,
                feedbackStatus = if (feedback.isPresent) feedback.get().status else null
            )
        }

        return appointmentsDTO
    }
}
