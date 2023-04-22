package com.ritualkart.sevahetu.network

import com.ritualkart.sevahetu.model.request.EditProfileRequest
import com.ritualkart.sevahetu.model.request.SubmitAddressRequest
import com.ritualkart.sevahetu.model.response.AddressResponse
import com.ritualkart.sevahetu.model.response.DeleteAddressResponse
import com.ritualkart.sevahetu.model.response.HomeFragmentViewPagerResponse
import com.ritualkart.sevahetu.model.response.ProfileResponse


interface ApiHelper {

    suspend fun getMainFragmentViewPagerData(): List<HomeFragmentViewPagerResponse>

    suspend fun getProfile(token : String) : ProfileResponse

    suspend fun editProfile(token : String,editProfileRequest: EditProfileRequest,id: String) : ProfileResponse

    suspend fun submitAddress(token: String,submitAddressRequest: SubmitAddressRequest) :AddressResponse

    suspend fun submitEditAddress(token: String,submitAddressRequest: SubmitAddressRequest,id : String) : AddressResponse

    suspend fun getAddress(token : String) : AddressResponse

    suspend fun deleteAddress(token : String,id : String) : DeleteAddressResponse

}
