package com.ritualkart.sevahetu.ui.home

import com.ritualkart.sevahetu.model.response.ProductItemResponse

interface HomeFragmentProductInterface {
    fun onProductItemClick(productItemResponse: ProductItemResponse, position: Int)
}