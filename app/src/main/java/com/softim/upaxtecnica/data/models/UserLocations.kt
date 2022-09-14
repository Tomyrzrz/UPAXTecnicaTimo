package com.softim.upaxtecnica.data.models

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class UserLocations(
    @SerializedName("user") var user: String ?= "",
    @SerializedName("latitude") var latitude: Double ?= 0.0,
    @SerializedName("longitude") var longitude: Double ?= 0.0,
    @SerializedName("id") var time: Timestamp ?= Timestamp.now()
)
