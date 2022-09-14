package com.softim.upaxtecnica.domain.data.models

import com.google.gson.annotations.SerializedName

//Modelo respuesta de la peticion con Retrofit a las peliculas
data class MoviesResponse(
    @SerializedName("results")
    val movies : List<Movie> ?= emptyList()

)
