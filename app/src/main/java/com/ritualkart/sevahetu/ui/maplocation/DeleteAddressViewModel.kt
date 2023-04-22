package com.ritualkart.sevahetu.ui.maplocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ritualkart.sevahetu.model.response.DeleteAddressResponse
import com.ritualkart.sevahetu.network.ApiHelper
import com.ritualkart.sevahetu.network.Resource
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException

class DeleteAddressViewModel( private val apiHelper: ApiHelper): ViewModel()  {

    private val deleteAddressResult = MutableLiveData<Resource<DeleteAddressResponse>>()

    fun deleteAddress(token: String,id : String) {
        viewModelScope.launch {
            deleteAddressResult.postValue(Resource.loading(null))
            try {
                val deleteAddressFromApi = apiHelper.deleteAddress(token,id)
                deleteAddressResult.postValue(Resource.success(deleteAddressFromApi))
            } catch (t: Throwable) {
                val errorMessage = when (t) {
                    is IOException -> "delete_address_api_io_exception"
                    is HttpException -> if (t.code() == 400 || t.code() == 404) "Data not found"
                    else if (t.code() == 403 || t.code() == 401) "logout" else "delete_address_api_http_exception"
                    is JSONException -> "delete_address_api_json_exception"
                    else -> "delete_address_api_exception"
                }
                deleteAddressResult.postValue(Resource.error(errorMessage, null))
            }
        }
    }

    fun getDeleteAddress(): LiveData<Resource<DeleteAddressResponse>> {
        return deleteAddressResult
    }
}