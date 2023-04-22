package com.ritualkart.sevahetu.ui.maplocation

import com.ritualkart.sevahetu.model.Place


interface AddressOnItemClickListener{
    fun onDeleteClick(position: Int,addressResults : Place)
    fun onEditClick(position: Int,addressResults : Place)
    fun onAddAddress(addressResults: Place)
}