package com.ritualkart.sevahetu.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ritualkart.sevahetu.model.request.EditProfileRequest
import com.ritualkart.sevahetu.model.response.ProfileResponse
import com.ritualkart.sevahetu.network.ApiHelper
import com.ritualkart.sevahetu.network.Resource
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException

class EditProfileViewModel ( private val apiHelper: ApiHelper): ViewModel() {

    private val getEditProfileResult = MutableLiveData<Resource<ProfileResponse>>()

    fun getEditProfile(token : String, editProfileRequest: EditProfileRequest, id:String) {
        viewModelScope.launch {
            getEditProfileResult.postValue(Resource.loading(null))
            try {
                val addressData = apiHelper.editProfile(token,editProfileRequest,id)
                getEditProfileResult.postValue(Resource.success(addressData))
            }catch (t: Throwable) {
                val errorMessage = when (t) {
                    is IOException -> "edit_profile_api_io_exception"
                    is HttpException -> "edit_profile_api_http_exception"
                    is JSONException -> "edit_profile_api_json_exception"
                    else -> "edit_profile_api_exception"
                }
                getEditProfileResult.postValue(Resource.error(errorMessage, null))
            }
        }
    }

    fun getEditProfileResult(): LiveData<Resource<ProfileResponse>> {
        return getEditProfileResult
    }
}