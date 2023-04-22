package com.ritualkart.sevahetu.model.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse (

    @SerializedName("user_profile_details" ) var userProfileDetails : UserProfileDetails? = UserProfileDetails(),
    @SerializedName("_id"                  ) var Id                 : String?             = null,
    @SerializedName("phone_number"         ) var phoneNumber        : Int?                = null,
    @SerializedName("uuid"                 ) var uuid               : String?             = null,
    @SerializedName("user_id"              ) var userId             : String?             = null,
    @SerializedName("address"              ) var address            : ArrayList<Address>  = arrayListOf(),
    @SerializedName("__v"                  ) var _v                 : Int?                = null

)

data class UserProfileDetails (

    @SerializedName("profile_image" ) var profileImage : String? = null,
    @SerializedName("name"          ) var fullname         : String? = null,
    @SerializedName("email"         ) var email        : String? = null,
    @SerializedName("gender"        ) var gender       : String? = null,
    @SerializedName("designation"   ) var designation  : String? = null,
    @SerializedName("age"           ) var age          : String? = null

)



data class Address (

    @SerializedName("area"              ) var area             : String? = null,
    @SerializedName("city"              ) var city             : String? = null,
    @SerializedName("district"          ) var district         : String? = null,
    @SerializedName("formatted_address" ) var formattedAddress : String? = null,
    @SerializedName("houseName"         ) var houseName        : String? = null,
    @SerializedName("houseNumber"       ) var houseNumber      : String? = null,
    @SerializedName("lat"               ) var lat              : String? = null,
    @SerializedName("lng"               ) var lng              : String? = null,
    @SerializedName("locality"          ) var locality         : String? = null,
    @SerializedName("pincode"           ) var pincode          : String? = null,
    @SerializedName("poi"               ) var poi              : String? = null,
    @SerializedName("poi_dist"          ) var poiDist          : String? = null,
    @SerializedName("state"             ) var state            : String? = null,
    @SerializedName("street"            ) var street           : String? = null,
    @SerializedName("street_dist"       ) var streetDist       : String? = null,
    @SerializedName("subDistrict"       ) var subDistrict      : String? = null,
    @SerializedName("subLocality"       ) var subLocality      : String? = null,
    @SerializedName("subSubLocality"    ) var subSubLocality   : String? = null,
    @SerializedName("village"           ) var village          : String? = null,
    @SerializedName("uuid"              ) var uuid             : String? = null

)