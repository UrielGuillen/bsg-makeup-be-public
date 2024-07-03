package com.bsgmakeup.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("notifications")
class NotificationsEntity (
    @Id
    val id: String = ObjectId.get().toString(),
    val userIdFrom: String,
    val userIdTo: String,
    val title: String,
    val content: String,
    val icon: String,
    var hasBeenRead: Boolean = false,
    @JsonIgnore
    var active: Boolean = true,
    var createdAt: Date = Date(),
    @JsonIgnore
    var deletedAt: Date = Date(),
    @JsonIgnore
    var updatedAt: Date = Date()
)
