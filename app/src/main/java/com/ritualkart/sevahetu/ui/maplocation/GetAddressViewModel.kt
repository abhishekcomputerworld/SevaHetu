package com.ritualkart.sevahetu.ui.maplocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ritualkart.sevahetu.model.response.AddressResponse
import com.ritualkart.sevahetu.network.ApiHelper
import com.ritualkart.sevahetu.network.Resource
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException

class GetAddressViewModel( private val apiHelper: ApiHelper): ViewModel() {

    private val getAddressResult = MutableLiveData<Resource<AddressResponse>>()

    fun getAddress(token : String) {
        viewModelScope.launch {
            getAddressResult.postValue(Resource.loading(null))
            try {
                val addressData = apiHelper.getAddress(token)
                getAddressResult.postValue(Resource.success(addressData))
            }catch (t: Throwable) {
                val errorMessage = when (t) {
                    is IOException -> "get_address_api_io_exception"
                    is HttpException ->  "get_address_api"
                    is JSONException -> "get_address_api_json_exception"
                    else -> "get_address_api_exception"
                }
                getAddressResult.postValue(Resource.error(errorMessage, null))
            }
        }
    }

    fun getAddressResult(): LiveData<Resource<AddressResponse>> {
        return getAddressResult
    }
}