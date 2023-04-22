package com.ritualkart.sevahetu.network

import com.ritualkart.sevahetu.model.request.EditProfileRequest
import com.ritualkart.sevahetu.model.request.SubmitAddressRequest
import com.ritualkart.sevahetu.model.response.HomeFragmentViewPagerResponse
import com.ritualkart.sevahetu.model.response.ProfileResponse

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {

    override suspend fun getMainFragmentViewPagerData() = apiService.getMainFragmentViewPagerData()

    override suspend fun getProfile(token: String): ProfileResponse = apiService.getProfile(token)

    override suspend fun editProfile(
        token: String,
        editProfileRequest: EditProfileRequest,
        id: String
    ) = apiService.editProfile(token,editProfileRequest,id)

    override suspend fun getAddress(token: String) = apiService.getAddress(token)

    override suspend fun deleteAddress(token : String,id : String) = apiService.deleteAddress(token,id)

    override suspend fun submitAddress(
        token: String,
        submitAddressRequest: SubmitAddressRequest
    ) = apiService.submitAddress(token,submitAddressRequest)

    override suspend fun submitEditAddress(
        token: String,
        submitAddressRequest: SubmitAddressRequest,
        id: String
    ) = apiService.submitEditAddress(token,submitAddressRequest,id)

}