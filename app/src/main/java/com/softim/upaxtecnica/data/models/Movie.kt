package com.softim.upaxtecnica.domain.data.models

import com.google.gson.annotations.SerializedName

//Modelo de pelicula que se muestra en el RecyclerView
data class Movie(
    @SerializedName("id")
    val id : String ? = "",

    @SerializedName("title")
    val title : String? ="",

    @SerializedName("poster_path")
    val poster : String?="",

    @SerializedName("overview")
    val overview : String?="",

    @SerializedName("vote_average")
    val vote_average : String?=""

)