package com.ritualkart.sevahetu

import com.ritualkart.sevahetu.model.CartDataItem


interface ShareCartDataListener {
    fun shareData(cartDataItem: CartDataItem)
}