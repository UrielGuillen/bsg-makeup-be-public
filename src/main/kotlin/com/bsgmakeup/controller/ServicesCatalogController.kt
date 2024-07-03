package com.bsgmakeup.controller

import com.bsgmakeup.dto.ResponseData
import com.bsgmakeup.service.AuthenticationService
import com.bsgmakeup.service.ServicesCatalogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("catalog/services")
class ServicesCatalogController(
    private val authenticationService: AuthenticationService,
    private val servicesCatalogService: ServicesCatalogService
) {

    @GetMapping
    fun getServicesCatalog(@RequestHeader("Authorization") token: String?): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userRole = authenticationService.getDataFromToken(token, "userRole")

            val servicesCatalogList = servicesCatalogService.getServicesByUserType(userRole)

            return ResponseEntity.ok(
                ResponseData(true, "Catalog retrieved successfully", servicesCatalogList)
            )
        }

        return ResponseEntity.badRequest().body(
            ResponseData(false, "Invalid token", null)
        )
    }
}
