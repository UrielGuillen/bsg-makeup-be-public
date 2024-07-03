package com.bsgmakeup.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("user_token")
class UserTokenEntity (
    @Id
    val id: String = ObjectId.get().toString(),
    val userId: String,
    var token: String,
    val role: String,
    @JsonIgnore
    val active: Boolean = true,
    @JsonIgnore
    val createdAt: Date = Date(),
    @JsonIgnore
    val deletedAt: Date = Date(),
    @JsonIgnore
    var updatedAt: Date = Date()
)
