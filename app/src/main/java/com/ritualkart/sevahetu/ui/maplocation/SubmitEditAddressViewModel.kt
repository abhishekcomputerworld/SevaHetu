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

class SubmitEditAddressViewModel(private val apiHelper: ApiHelper): ViewModel() {

    private val submitEditAddressResult = MutableLiveData<Resource<AddressResponse>>()

    fun submitEditAddress(token: String, submitAddressRequest: SubmitAddressRequest, id:String) {
        viewModelScope.launch {
            submitEditAddressResult.postValue(Resource.loading(null))
            try {
                val submitEditAddressFromApi = apiHelper.submitEditAddress(token,submitAddressRequest,id)
                submitEditAddressResult.postValue(Resource.success(submitEditAddressFromApi))
            } catch (t: Throwable) {
                val errorMessage = when (t) {
                    is IOException -> "submit_edit_address_api_io_exception"
                    is HttpException -> if (t.code() == 400 || t.code() == 404) "Data not found"
                    else if (t.code() == 403 || t.code() == 401) "logout" else "submit_edit_address_api_http_exception"
                    is JSONException -> "submit_edit_address_api_json_exception"
                    else -> "submit_edit_address_api_exception"
                }
                submitEditAddressResult.postValue(Resource.error(errorMessage, null))
            }
        }
    }

    fun getSubmitEditAddressResult(): LiveData<Resource<AddressResponse>> {
        return submitEditAddressResult
    }
}