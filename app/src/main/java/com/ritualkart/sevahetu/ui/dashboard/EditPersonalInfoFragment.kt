package com.ritualkart.sevahetu.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SpinnerAdapter
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentEditPersonalInfofragmentBinding
import com.ritualkart.sevahetu.model.request.EditProfileRequest
import com.ritualkart.sevahetu.network.ApiHelperImpl
import com.ritualkart.sevahetu.network.RetrofitBuilder
import com.ritualkart.sevahetu.network.Status
import com.ritualkart.sevahetu.network.ViewModelFactory
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.Utility

class EditPersonalInfoFragment : Fragment() {

    private var binding: FragmentEditPersonalInfofragmentBinding? = null
    private lateinit var editProfileViewModel: EditProfileViewModel
    private lateinit var sharedPreference : Preference
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var salutationArr = listOf<String>(
        "Mr", "Ms (Mrs/Miss)" , "Other"
    )
    private var designation = "Mr"
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(isAdded && context != null)
        sharedPreference = Preference(requireContext())
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                activity?.onBackPressed()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPersonalInfofragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setHeader()
        setUpEditButton()
        setupViewModel()
        setSalutationAdapter()
        setListener()
        setData()
        setupEditProfileObserver()

        binding!!.includeHeader.ivBackFragment.setOnClickListener {
            if(requireActivity()!= null && isAdded  && context != null && Utility.isKeyboardOpen(requireActivity())){
                Utility.hideKeyboard(binding!!.editProfileParentView,requireContext())
            }
            activity?.onBackPressed()
        }

        binding!!.btnSave.rlBtnSubmit.setOnClickListener {
            if(isAdded && context != null) {
                Utility.hideKeyboard(binding!!.editProfileParentView,requireContext())
                if (validation(binding!!.etEditName.text?.trim().toString(),binding!!.etEditGender.text?.trim().toString(),binding!!.etEditAge.text?.trim().toString(),binding!!.etEditEmail.text?.trim().toString())) {
                    if (Utility.isNetworkAvailable(requireContext())==true) {
                        val token = "Token " + sharedPreference.getMobileNumber()
                        val editProfileRequest = EditProfileRequest(
                            "",
                            binding!!.etEditAge.text?.trim().toString().toInt(),
                            binding!!.etEditEmail.text?.trim().toString(),
                            binding!!.etEditName.text?.trim().toString(),
                            designation,
                            binding!!.etEditGender.text?.trim().toString()
                        )
                        editProfileViewModel.getEditProfile(
                            token,
                            editProfileRequest,
                            sharedPreference.getMobileNumber().toString()
                        )
                    }else {
                        Utility.showSnackBar(binding!!.editProfileParentView,getString(R.string.no_internet))
                    }
                }
            }
        }

        binding!!.btnCancel.rlBtnSubmit.setOnClickListener {
            if(requireActivity()!= null && isAdded  && context != null && Utility.isKeyboardOpen(requireActivity())){
                Utility.hideKeyboard(binding!!.editProfileParentView,requireContext())
            }
            activity?.onBackPressed()
        }
    }

    private fun setHeader(){
        binding!!.includeHeader.tvTitleFragment.text = getString(R.string.edit_personal)
    }

    private fun setUpEditButton(){
        binding!!.btnSave.tvButtonName.text = getString(R.string.save)
        binding!!.btnSave.tvButtonName.setTextColor(resources.getColor(R.color.white,null))
        binding!!.btnSave.rlBtnSubmit.background = resources.getDrawable(R.drawable.bg_button_rounded_sky_blue,null)

        binding!!.btnCancel.tvButtonName.text = getString(R.string.cancel)
        binding!!.btnCancel.tvButtonName.setTextColor(resources.getColor(R.color.sky_blue,null))
        binding!!.btnCancel.rlBtnSubmit.background = resources.getDrawable(R.drawable.bg_button_rounded_white,null)
    }

    private fun setupViewModel(){
        editProfileViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService)
            )
        ).get(EditProfileViewModel::class.java)
    }

    private fun setSalutationAdapter(){
        val salutationAdapter = SpinnerAdapter(requireContext(), R.layout.item_auto_complete, salutationArr)
        binding!!.tvMr.setAdapter( salutationAdapter)
    }

    private fun setListener(){
        binding!!.tvMr.setOnTouchListener(View.OnTouchListener { paramView, paramMotionEvent -> // TODO Auto-generated method stub
            if(isAdded && context != null)
             Utility.hideKeyboard(binding!!.editProfileParentView,requireContext())
            binding!!.tvMr.showDropDown()
            binding!!.tvMr.requestFocus()
            false
        })

        binding!!.tvMr.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if(position == 0 || position == 2) {
                    binding!!.etEditGender.setText("Male")
                    if(position == 0){
                        designation = "Mr"
                    }
                }else if(position == 4){
                    binding!!.etEditGender.setText("Other")
                    designation = "Other"
                } else {
                    binding!!.etEditGender.setText("Female")
                    if(position == 1){
                        designation = "Ms"
                    }else{
                        designation = "Baby Girl"
                    }
                }
            }
    }

    private fun setupEditProfileObserver(){
        editProfileViewModel.getEditProfileResult().observe(requireActivity(), Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let {
                        if(isAdded && context != null) {
                            binding!!.progressBar.visibility = View.GONE
                            navController!!.popBackStack()
                            sharedPreference.saveProfileModel(it)
                           // navController!!.navigate(R.id.action_editPersonalInfoFragment_to_navigation_home)
                            /*activity?.supportFragmentManager?.popBackStack()
                                                     profileClickListener?.onUpdate(it)*/
                        }
                    }
                }
                Status.LOADING -> {
                    if(isAdded && context != null)
                     binding!!.progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    if(isAdded && context != null) {
                        binding!!.progressBar.visibility = View.GONE
                        it.message?.let { it1 ->
                            Utility.showSnackBar(
                                binding!!.editProfileParentView,
                                it1
                            )
                        }
                        it.message?.let { it1 ->
                            Utility.logAnalyticsEvent(
                                requireContext(),
                                "rcl_api_error",
                                "error_type",
                                it1
                            )
                        }
                    }
                }
            }
        })
    }

    private fun setData(){
        if(sharedPreference.getProfileModel()?.userProfileDetails?.designation.isNullOrEmpty()){
            designation = "Mr"
            binding!!.tvMr.setText("Mr")
            binding!!.etEditName.setText(sharedPreference.getProfileModel()?.userProfileDetails?.fullname)
            binding!!.etEditGender.setText("Male")
            binding!!.etEditAge.setText(sharedPreference.getProfileModel()?.userProfileDetails?.age?.toString())
            binding!!.etEditEmail.setText(sharedPreference.getProfileModel()?.userProfileDetails?.email)
        }else{
            designation = sharedPreference.getProfileModel()?.userProfileDetails?.designation?:"Mr"
            if(designation.lowercase().contains("miss") || designation.lowercase().contains("mrs")){
                designation = "Ms"
            }
            binding!!.tvMr.setText(designation)
            binding!!.etEditName.setText(sharedPreference.getProfileModel()?.userProfileDetails?.fullname)
            binding!!.etEditGender.setText(sharedPreference.getProfileModel()?.userProfileDetails?.gender)
            binding!!.etEditAge.setText(sharedPreference.getProfileModel()?.userProfileDetails?.age?.toString())
            binding!!.etEditEmail.setText(sharedPreference.getProfileModel()?.userProfileDetails?.email)
        }

    }

    private fun validation(name : String,gender:String,age : String,email : String) : Boolean{
        if(name.isNullOrEmpty()){
            if(isAdded && context != null)
                Utility.showSnackBar(binding!!.editProfileParentView,"Name can not be empty")
            return false
        }else if(gender.isNullOrEmpty()){
            if(isAdded && context != null)
                Utility.showSnackBar(binding!!.editProfileParentView,"Gender can not be empty")
            return false
        }else if(age.isNullOrEmpty()){
            if(isAdded && context != null)
                Utility.showSnackBar(binding!!.editProfileParentView,"Age can not be empty")
            return false
        }else if(age.toInt() !in 5..105){
            if(isAdded && context != null) {
                Utility.showSnackBar(binding!!.editProfileParentView, "Age should be between 5 years to 105 years")
            }
            return false
        }else if(email.isNullOrEmpty()){
            if(isAdded && context != null) {
                Utility.showSnackBar(binding!!.editProfileParentView, "Email can not be empty")
            }
            return false
        }else if(!email.matches(emailPattern.toRegex())){
            if(isAdded && context != null) {
                Utility.showSnackBar(binding!!.editProfileParentView, "Invalid email address")
            }
            return false
        }else{
            return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}