package com.ritualkart.sevahetu.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ritualkart.sevahetu.model.response.ProfileResponse
import com.ritualkart.sevahetu.network.ApiHelper
import com.ritualkart.sevahetu.network.Resource
import com.ritualkart.sevahetu.utiliy.Constants
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException

class ProfileViewModel( private val apiHelper: ApiHelper): ViewModel() {

    private val getProfileResult = MutableLiveData<Resource<ProfileResponse>>()

    fun getProfile(token : String) {
        viewModelScope.launch {
            getProfileResult.postValue(Resource.loading(null))
            try {
                val addressData = apiHelper.getProfile(token)
                getProfileResult.postValue(Resource.success(addressData))
            }catch (t: Throwable) {
                val errorMessage = when (t) {
                    is IOException -> "profile_api_io_exception"
                    is HttpException -> if(t.code() == 400 || t.code() == 404) "Data not found"
                    else if(t.code() == 403 || t.code() == 401) "logout" else Constants.HttpExceptionMsg
                    is JSONException -> "profile_api_json_exception"
                    else -> "profile_api_"+ Constants.ErrorMsg
                }
                getProfileResult.postValue(Resource.error(errorMessage, null))
            }
        }
    }

    fun getProfileResult(): LiveData<Resource<ProfileResponse>> {
        return getProfileResult
    }
}