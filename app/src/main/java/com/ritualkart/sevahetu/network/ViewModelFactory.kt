package com.ritualkart.sevahetu.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ritualkart.sevahetu.ui.dashboard.EditProfileViewModel
import com.ritualkart.sevahetu.ui.home.HomeViewModel
import com.ritualkart.sevahetu.ui.home.ProfileViewModel
import com.ritualkart.sevahetu.ui.maplocation.DeleteAddressViewModel
import com.ritualkart.sevahetu.ui.maplocation.GetAddressViewModel
import com.ritualkart.sevahetu.ui.maplocation.SubmitAddressViewModel
import com.ritualkart.sevahetu.ui.maplocation.SubmitEditAddressViewModel

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(apiHelper) as T
        }
        else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(apiHelper) as T
        }else if (modelClass.isAssignableFrom(GetAddressViewModel::class.java)) {
            return GetAddressViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(DeleteAddressViewModel::class.java)) {
            return DeleteAddressViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(SubmitAddressViewModel::class.java)) {
            return SubmitAddressViewModel(apiHelper) as T
        } else if (modelClass.isAssignableFrom(SubmitEditAddressViewModel::class.java)) {
            return SubmitEditAddressViewModel(apiHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")


    }

}