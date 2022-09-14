package com.softim.upaxtecnica.domain.data.services

import com.softim.upaxtecnica.domain.data.models.MoviesResponse
import com.softim.upaxtecnica.domain.data.models.PersonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface MovieAPIInterface {
    @GET
    fun getMovieList(@Url url:String): Call<MoviesResponse>

    @GET("287?api_key=e8a82c4e3f0e112131ea103fade4eb93")
    fun getPersonDetail(): Call<PersonResponse>

}