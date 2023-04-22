package com.ritualkart.sevahetu.ui.maplocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ritualkart.sevahetu.model.request.SubmitAddressRequest
import com.ritualkart.sevahetu.model.response.AddressResponse
import com.ritualkart.sevahetu.network.ApiHelper
import com.ritualkart.sevahetu.network.Resource
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException

class SubmitAddressViewModel(private val apiHelper: ApiHelper): ViewModel() {

    private val submitAddressResult = MutableLiveData<Resource<AddressResponse>>()

    fun submitAddress(token: String,submitAddressRequest: SubmitAddressRequest) {
        viewModelScope.launch {
            submitAddressResult.postValue(Resource.loading(null))
            try {
                val submitAddressFromApi = apiHelper.submitAddress(token,submitAddressRequest)
                submitAddressResult.postValue(Resource.success(submitAddressFromApi))
            } catch (t: Throwable) {
                val errorMessage = when (t) {
                    is IOException -> "submit_address_api_io_exception"
                    is HttpException -> if (t.code() == 400 || t.code() == 404) "Data not found"
                    else if (t.code() == 403 || t.code() == 401) "logout" else "submit_address_api_http_exception"
                    is JSONException -> "submit_address_api_json_exception"
                    else -> "submit_address_api_exception"
                }
                submitAddressResult.postValue(Resource.error(errorMessage, null))
            }
        }
    }

    fun getSubmitAddressResult(): LiveData<Resource<AddressResponse>> {
        return submitAddressResult
    }
}