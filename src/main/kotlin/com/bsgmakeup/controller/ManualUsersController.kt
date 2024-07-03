package com.bsgmakeup.controller

import com.bsgmakeup.dto.ResponseData
import com.bsgmakeup.service.AuthenticationService
import com.bsgmakeup.service.ManualUsersService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manual-users")
class ManualUsersController(
    private val authenticationService: AuthenticationService,
    private val manualUsersService: ManualUsersService
) {

    @GetMapping()
    fun getManualUsers(@RequestHeader("Authorization") token: String?): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val users = manualUsersService.getAllUsersRegistered()

            return ResponseEntity.ok(ResponseData(true, "SCHEDULE.ALL_USERS_SUCCESS", users))
        }

        return badRequestResponse()
    }

    private fun badRequestResponse(): ResponseEntity<ResponseData> {
        return ResponseEntity.badRequest().body(
            ResponseData(false, "Invalid token", null)
        )
    }
}
