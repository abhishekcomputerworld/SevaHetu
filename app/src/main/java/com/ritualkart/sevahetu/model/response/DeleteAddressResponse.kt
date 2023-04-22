package com.ritualkart.sevahetu.model.response

import com.google.gson.annotations.SerializedName

data class DeleteAddressResponse(
    @SerializedName("deleted"             ) var user            : Boolean?      = null
)
