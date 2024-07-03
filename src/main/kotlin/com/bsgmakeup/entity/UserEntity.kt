package com.bsgmakeup.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("user")
data class UserEntity (
    @Id
    val id: String = ObjectId.get().toString(),
    var name: String,
    var lastName: String,
    val phoneNumber: String,
    var email: String,
    var profileUrl: String?,
    @JsonIgnore
    val password: String,
    val role: String = "user",
    val createdDate: Date = Date(),
    var modifiedDate: Date = Date()
)
