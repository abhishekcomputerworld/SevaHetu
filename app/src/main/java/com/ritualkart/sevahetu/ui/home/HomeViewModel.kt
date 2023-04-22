package com.ritualkart.sevahetu.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ritualkart.sevahetu.model.response.HomeFragmentViewPagerResponse
import com.ritualkart.sevahetu.network.ApiHelper
import com.ritualkart.sevahetu.network.Resource
import com.ritualkart.sevahetu.utiliy.Constants
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel(var apiHelper: ApiHelper) : ViewModel() {

/*    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text*/

    private val bannerDataResult = MutableLiveData<Resource<List<HomeFragmentViewPagerResponse>>>()

    fun viewPagerData() {
        viewModelScope.launch {
            bannerDataResult.postValue(Resource.loading(null))
            try {
                val bannersFromApi = apiHelper.getMainFragmentViewPagerData()
                bannerDataResult.postValue(Resource.success(bannersFromApi))
            } catch (t: Throwable) {
                val errorMessage = when (t) {
                    is IOException -> "banner_data_api_io_exception"
                    is HttpException ->  "banner_data_api"
                    is JSONException -> "banner_data_api_json_exception"
                    else -> "banner_data_"+ Constants.ErrorMsg
                }
                bannerDataResult.postValue(Resource.error(errorMessage, null))
            }
        }
    }

    fun getViewPagerResult(): LiveData<Resource<List<HomeFragmentViewPagerResponse>>> {
        return bannerDataResult
    }
}