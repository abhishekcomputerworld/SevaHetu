package com.ritualkart.sevahetu.ui.cart

import com.ritualkart.sevahetu.model.response.ProductItemResponse

interface CartFragmentProductInterface {
    fun onProductItemDelete(productItemResponse: ProductItemResponse, position: Int)
    fun onProductItemAdded(productItemResponse: ProductItemResponse, position: Int)
    fun onProductItemRemove(productItemResponse: ProductItemResponse, position: Int)

}