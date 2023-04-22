package com.ritualkart.sevahetu.network


import com.ritualkart.sevahetu.model.request.EditProfileRequest
import com.ritualkart.sevahetu.model.request.SubmitAddressRequest
import com.ritualkart.sevahetu.model.response.AddressResponse
import com.ritualkart.sevahetu.model.response.DeleteAddressResponse
import com.ritualkart.sevahetu.model.response.HomeFragmentViewPagerResponse
import com.ritualkart.sevahetu.model.response.ProfileResponse
import retrofit2.http.*

interface ApiService {

    @GET("view_pager_jason")
    suspend fun getMainFragmentViewPagerData(): List<HomeFragmentViewPagerResponse>

    @GET("v1/auth/profile/")
    suspend fun getProfile(@Header ("Authorization") token : String) : ProfileResponse

    // @PUT("v1/auth/users/{id}/")
    @PUT("profile/get/{id}/")
    suspend fun editProfile(@Header ("Authorization") token : String, @Body editProfileRequest: EditProfileRequest, @Path ("id") id : String ): ProfileResponse

    @POST("v1/auth/address/")
    suspend fun submitAddress(@Header ("Authorization") token : String,@Body submitAddressRequest: SubmitAddressRequest): AddressResponse

    @PUT("v1/auth/address/{id}/")
    suspend fun submitEditAddress(@Header ("Authorization") token : String,@Body submitAddressRequest: SubmitAddressRequest,@Path ("id") id : String ): AddressResponse

    @GET("v1/auth/address/")
    suspend fun getAddress(@Header ("Authorization") token : String) : AddressResponse

    @PUT("v1/auth/address/{id}/")
    suspend fun deleteAddress(@Header ("Authorization") token : String,@Path ("id") id : String): DeleteAddressResponse

}
