package com.ritualkart.sevahetu.model.response

import com.google.gson.annotations.SerializedName

data class ProductItemResponse(
    var item_id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var price: Int? = null,
    var quantity: String? = null,
    var image_url: String? = null
)
