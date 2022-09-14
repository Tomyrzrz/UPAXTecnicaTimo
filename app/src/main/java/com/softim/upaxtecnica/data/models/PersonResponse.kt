package com.softim.upaxtecnica.domain.data.models

import com.google.gson.annotations.SerializedName

data class PersonResponse(
    @SerializedName("id")
    val id : String ? = "",

    @SerializedName("name")
    val name : String? ="",

    @SerializedName("profile_path")
    val profile_path : String?="",

    @SerializedName("biography")
    val biography : String?="",

    @SerializedName("popularity")
    val popularity : Double?= 0.0
)
