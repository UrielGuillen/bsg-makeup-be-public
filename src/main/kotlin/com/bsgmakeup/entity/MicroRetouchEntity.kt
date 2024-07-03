package com.bsgmakeup.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("micro_retouch")
class MicroRetouchEntity (
    @Id
    val id: String = ObjectId.get().toString(),
    val name: String,
    val time: Int,
    val cost: Int,
    val description: String,
    val group: String,
    @JsonIgnore
    val userType: String
)
