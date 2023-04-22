package com.ritualkart.sevahetu.ui.maplocation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ritualkart.sevahetu.databinding.ItemAddressRowBinding
import com.ritualkart.sevahetu.model.Place


class AddressAdapter(private val context: Context, private val addressDataList : ArrayList<Place>
,private val addressOnItemClick : AddressOnItemClickListener) :RecyclerView.Adapter< AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: ItemAddressRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressAdapter.AddressViewHolder {
        val binding = ItemAddressRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressAdapter.AddressViewHolder, position: Int) {
        with(holder){
            var houseName = ""
            var cityPinCode = ""
            var cityName = ""
            var streetName = ""
            if(!addressDataList[position].houseNumber.isNullOrEmpty()) {
               houseName = addressDataList[position].houseNumber.toString()
            }
            if(!addressDataList[position].street.isNullOrEmpty()) {
                streetName = ", "+ addressDataList[position].street
            }
            binding.tvHouseName.text = houseName + streetName
            if(!addressDataList[position].formattedAddress.isNullOrEmpty()) {
                binding.tvAddressAdd.text = addressDataList[position].formattedAddress
            }
            addressDataList[position].city?.let {
                cityName = addressDataList[position].city?.toString().toString()
            }
            if(!addressDataList[position].pincode.isNullOrEmpty()) {
                cityPinCode =  ", "+addressDataList[position].pincode
            }

            binding.tvAddressCity.text = cityName + cityPinCode

            binding.ivAddressDelete.setOnClickListener {
                addressOnItemClick.onDeleteClick(position,addressDataList[position])
            }

            binding.ivAddressEdit.setOnClickListener {
                addressOnItemClick.onEditClick(position,addressDataList[position])
            }
            binding.rlAddressBox.setOnClickListener {
                addressOnItemClick.onAddAddress(addressDataList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return addressDataList.size
    }
}