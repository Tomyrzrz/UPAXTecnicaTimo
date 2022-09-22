package com.softim.upaxtecnica.ui.movies

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softim.upaxtecnica.domain.data.models.Movie
import com.softim.upaxtecnica.domain.data.models.MoviesResponse
import com.softim.upaxtecnica.domain.data.services.MovieAPIInterface
import com.softim.upaxtecnica.domain.data.services.MovieApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesViewModel : ViewModel(){
    val movies: MutableLiveData<List<Movie>> = MutableLiveData()
    val category: MutableLiveData<String> = MutableLiveData<String>()
    init {
        category.value = "popular"
        this.getMovieData {
            movies.value = it
        }
    }


    fun getMovieData(callback: (List<Movie>) -> Unit){
        val apiService = MovieApiService.getInstance().create(MovieAPIInterface::class.java)

        viewModelScope.launch(Dispatchers.IO) {
            apiService.getMovieList("movie/${category.value}?api_key=e8a82c4e3f0e112131ea103fade4eb93").enqueue(object : Callback<MoviesResponse> {
                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                }
                override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                    if (response.body()?.movies != null) return callback(response.body()?.movies!!)
                }
            })
        }
    }
    fun updateMovieData(query: String, callback: (List<Movie>) -> Unit ){
        val apiService = MovieApiService.getInstance().create(MovieAPIInterface::class.java)

        viewModelScope.launch(Dispatchers.IO) {
            apiService.getMovieList("movie/${query}?api_key=e8a82c4e3f0e112131ea103fade4eb93").enqueue(object : Callback<MoviesResponse> {
                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                }
                override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                    if (response.body()?.movies != null) return callback(response.body()?.movies!!)
                }
            })
        }
    }

}