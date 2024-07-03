package com.bsgmakeup.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("users_to_contact")
class UsersToContactEntity (
    @Id
    val id: String = ObjectId.get().toString(),
    val name: String,
    val phone: String,
    val message: String,
    val createdAt: Date = Date(),
    val active: Boolean = true
)
