package com.bsgmakeup.controller

import com.bsgmakeup.dto.ResponseData
import com.bsgmakeup.dto.UserToContactDTO
import com.bsgmakeup.service.UsersToContactService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contact")
class UsersToContactController(
    private val usersToContactService: UsersToContactService
) {

    @PostMapping("/user")
    fun createContactUser(@RequestBody userToContactDTO: UserToContactDTO): ResponseEntity<ResponseData> {
        val prefixMessage = "HOME.SECTION_FOUR.CONTACT_REQUEST_"
        val errorList: Array<String> = arrayOf("ERROR", "DUPLICATED")

        val createContactMessage: String = usersToContactService.createUserToContact(userToContactDTO)

        val createContactSuccess: Boolean = !errorList.contains(createContactMessage)

        return ResponseEntity.ok(
            ResponseData(createContactSuccess, prefixMessage + createContactMessage, null)
        )
    }
}
