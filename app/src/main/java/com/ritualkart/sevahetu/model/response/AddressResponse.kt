package com.ritualkart.sevahetu.model.response

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName("id"               ) var id              : String?         = null,
    @SerializedName("houseNumber"      ) var houseNumber     : String? = null,
    @SerializedName("houseName"             ) var houseName            : String? = null,
    @SerializedName("poi"       ) var poi       : String?      = null,
    @SerializedName("poiDist"       ) var poiDist       : String?      = null,
    @SerializedName("street"             ) var street            : String?      = null,
    @SerializedName("streetDist"     ) var streetDist     : String?      = null,
    @SerializedName("subSubLocality"      ) var subSubLocality      : String?      = null,
    @SerializedName("subLocality"    ) var subLocality    : String?      = null,
    @SerializedName("locality"    ) var locality    : String?      = null,
    @SerializedName("village"         ) var village        : String?      = null,
    @SerializedName("district"        ) var district       : String?      = null,
    @SerializedName("subDistrict"           ) var subDistrict          : String?      = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("state"            ) var state           : String?      = null,
    @SerializedName("pincode"        ) var pincode        : String?     = null,
    @SerializedName("lat"      ) var lat      : String?      = null,
    @SerializedName("lng"      ) var lng     : String?      = null,
    @SerializedName("area" ) var area : String?      = null,
    @SerializedName("formattedAddress"             ) var formattedAddress            : String?      = null,
    @SerializedName("placeId"             ) var placeId            : String?      = null

) {
}
