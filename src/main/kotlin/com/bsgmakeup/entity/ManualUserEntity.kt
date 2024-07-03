package com.bsgmakeup.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("manual_user")
class ManualUserEntity (
    @Id
    val id: String = ObjectId.get().toString(),
    val name: String,
    val lastName: String,
    val phone: String,
    @JsonIgnore
    val createdAt: Date = Date(),
    @JsonIgnore
    val deletedAt: Date = Date(),
    @JsonIgnore
    var updatedAt: Date = Date()
)
