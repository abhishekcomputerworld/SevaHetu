package com.ritualkart.sevahetu.model.response

import com.google.gson.annotations.SerializedName

data class EditProfileResponse(
    @SerializedName("uuid"             ) var uuid            : String?           = null,
    @SerializedName("email"            ) var email           : String?           = null,
    @SerializedName("phonenumber"      ) var phonenumber     : String?           = null,
    @SerializedName("username"         ) var username        : String?           = null,
    @SerializedName("usergroup"        ) var usergroup       : String?           = null,
    @SerializedName("address"          ) var address         : String?           = null,
    @SerializedName("age"              ) var age             : String?           = null,
    @SerializedName("fullname"         ) var fullname        : String?           = null,
    @SerializedName("is_active"        ) var isActive        : Boolean?          = null,
    @SerializedName("image"            ) var image           : String?           = null,
    @SerializedName("gender"           ) var gender          : String?           = null,
    @SerializedName("designation"      ) var designation     : String?           = null,
    @SerializedName("dob"              ) var dob             : String?           = null,
    @SerializedName("last_login"       ) var lastLogin       : String?           = null,
    @SerializedName("is_superuser"     ) var isSuperuser     : Boolean?          = null,
    @SerializedName("created_at"       ) var createdAt       : String?           = null,
    @SerializedName("updated_at"       ) var updatedAt       : String?           = null,
    @SerializedName("is_staff"         ) var isStaff         : Boolean?          = null,
    @SerializedName("password_change"  ) var passwordChange  : Boolean?          = null,
    @SerializedName("login_disabled"   ) var loginDisabled   : Boolean?          = null,
)
