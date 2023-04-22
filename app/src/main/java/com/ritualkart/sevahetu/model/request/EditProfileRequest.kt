package com.ritualkart.sevahetu.model.request

import com.google.gson.annotations.SerializedName

data class EditProfileRequest(
    @SerializedName("address") var address    : String? = null,
    @SerializedName("age") var age    : Int? = null,
    @SerializedName("email") var email    : String? = null,
    @SerializedName("fullname") var fullName    : String? = null,
    @SerializedName("designation") var designation    : String? = null,
    @SerializedName("gender") var gender    : String? = null,
) {
}