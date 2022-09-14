package com.softim.upaxtecnica.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softim.upaxtecnica.domain.data.models.PersonResponse
import com.softim.upaxtecnica.domain.data.services.MovieAPIInterface
import com.softim.upaxtecnica.domain.data.services.MovieApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel(){
    val profile: MutableLiveData<PersonResponse> = MutableLiveData()

    init {
        this.getPersonData {
            profile.value = it
        }
    }

    fun getPersonData(callback: (PersonResponse) -> Unit){
        val apiService = MovieApiService.getInstance().create(MovieAPIInterface::class.java)

        viewModelScope.launch {
            apiService.getPersonDetail().enqueue(object : Callback<PersonResponse> {
                override fun onFailure(call: Call<PersonResponse>, t: Throwable) {
                }
                override fun onResponse(call: Call<PersonResponse>, response: Response<PersonResponse>) {
                    if (response.body()!= null)
                        return callback(response.body()!!)
                }
            })
        }

    }
}