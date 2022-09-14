package com.softim.upaxtecnica.domain.data.services

import com.softim.upaxtecnica.domain.data.models.MoviesResponse
import com.softim.upaxtecnica.domain.data.models.PersonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

//Interface de las peticiones realizadas a la API themoviedb
interface MovieAPIInterface {
    @GET
    fun getMovieList(@Url url:String): Call<MoviesResponse> //Peticion Get para obtener las peliculas segun su categoria

    //Peticion Get para obtener la informacion de una Persona
    @GET("287?api_key=e8a82c4e3f0e112131ea103fade4eb93")
    fun getPersonDetail(): Call<PersonResponse>

}