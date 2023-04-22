package com.ritualkart.sevahetu.ui.maplocation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmi.services.api.Place
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentEditSavedAddressBottomSheetBinding
import com.ritualkart.sevahetu.model.request.SubmitAddressRequest
import com.ritualkart.sevahetu.model.response.ProductItemResponse
import com.ritualkart.sevahetu.network.ApiHelperImpl
import com.ritualkart.sevahetu.network.RetrofitBuilder
import com.ritualkart.sevahetu.network.ViewModelFactory
import com.ritualkart.sevahetu.ui.home.HomeFragmentProductAdapter
import com.ritualkart.sevahetu.utiliy.AlertFragment
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.SuccessFragment
import com.ritualkart.sevahetu.utiliy.Utility

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddressBottomSheetFragment : SuperBottomSheetFragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPreference: Preference
    private var binding: FragmentEditSavedAddressBottomSheetBinding? = null
    private var isEditClick = false
    private var addAddressListener: AddressOnItemClickListener? = null
    private var addressResults: Place? = null

    private lateinit var submitAddressViewModel: SubmitAddressViewModel
    private lateinit var submitEditAddressViewModel: SubmitEditAddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        if (isAdded && context != null)
            sharedPreference = Preference(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (isAdded && context != null && dialog != null && dialog?.window != null) {
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        binding = FragmentEditSavedAddressBottomSheetBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeader()
        setUpEditButton()
        setData()
        setUpListener()
    }

    private fun setupViewModel() {

        submitAddressViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService)
            )
        ).get(SubmitAddressViewModel::class.java)

        submitEditAddressViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService)
            )
        ).get(SubmitEditAddressViewModel::class.java)
    }

    private fun setUpListener() {
        binding!!.backButton.setOnClickListener {
            if (requireActivity() != null && isAdded && context != null && Utility.isKeyboardOpen(
                    requireActivity()
                )
            ) {
                Utility.hideKeyboard(binding!!.rlAddAddressParentView, requireContext())
            }
            if (dialog != null && dialog!!.isShowing)
                dismiss()
        }

        binding!!.btnEdit.rlBtnSubmit.setOnClickListener {
            if (!sharedPreference.getMobileNumber().isNullOrEmpty()) {
                if (isAdded && context != null) {
                    if (!binding!!.etHouseFlat.text?.trim().toString()
                            .isNullOrEmpty()
                        && !binding!!.etApartment.text?.trim().toString()
                            .isNullOrEmpty() && !binding!!.etLandmark.text?.trim().toString()
                            .isNullOrEmpty()
                        && !binding!!.etLandmark.text?.trim().toString().isNullOrEmpty()
                    ) {

                        if (Utility.isNetworkAvailable(requireContext()) == true) {
                            val token = "Token " + sharedPreference.getMobileNumber()
                            val submitAddressRequest = SubmitAddressRequest(
                                binding!!.etHouseFlat.text?.trim().toString(),
                                binding!!.etApartment.text?.trim().toString(),
                                addressResults?.poi,
                                addressResults?.poiDist,
                                addressResults?.street,
                                addressResults?.streetDist,
                                binding!!.etLandmark.text?.trim().toString(),
                                addressResults?.subLocality,
                                addressResults?.locality,
                                addressResults?.village,
                                addressResults?.district,
                                addressResults?.subDistrict,
                                addressResults?.city,
                                addressResults?.state,
                                addressResults?.pincode,
                                addressResults?.lat,
                                addressResults?.lng,
                                addressResults?.area,
                                addressResults?.formattedAddress,
                                addressResults?.placeId
                            )

                            if (isEditClick) {
                                submitEditAddressViewModel.submitEditAddress(
                                    token,
                                    submitAddressRequest,
                                  "22"//  addressResults?.id.toString()
                                )
                                saveDataAtServer()
                            } else {
                                submitAddressViewModel.submitAddress(token, submitAddressRequest)
                                saveDataAtServer()
                            }
                        }
                        else {
                            showDialogError(getString(R.string.no_internet))
                        }
                    } else {
                        showDialogError("Mandatory fields should be filled")
                    }
                }
            } else {
                showDialogError("Please Login to Save Address")

            }
        }

    }

    private fun saveDataAtServer() {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(sharedPreference.getMobileNumber().toString())
        docRef.update("address", addressResults!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showDialogSuccess("Address Saved")
                if (dialog != null && dialog!!.isShowing)
                    dismiss()
                Log.d("TAGLOG", "Added ", task.exception)

            } else {
                showDialogError("Address Not Saved")
                Log.d("TAGLOG", "Cached get failed: ", task.exception)
            }
        }
    }

    fun setFragmentData(
        addressOnItemClickListener: AddressOnItemClickListener?,
        addressResults: Place?,
        isEdit: Boolean
    ) {
        this.addAddressListener = addressOnItemClickListener
        this.addressResults = addressResults
        this.isEditClick = isEdit
    }

    private fun setData() {
        binding!!.tvLocality.setText(addressResults?.formattedAddress)
        binding!!.tvLandmark.setText(addressResults?.subLocality)

        binding!!.etHouseFlat.setText(addressResults?.houseNumber)
        binding!!.etApartment.setText(addressResults?.houseName)
        binding!!.etLandmark.setText(addressResults?.subSubLocality)

    }


    private fun setHeader() {
        if (isEditClick) {
            binding!!.tvLabelCity.text = getString(R.string.edit_address_details)
        } else {
            binding!!.tvLabelCity.text = getString(R.string.add_address_details)
        }
    }

    private fun setUpEditButton() {
        binding!!.btnEdit.tvButtonName.text = getString(R.string.save_address)
        binding!!.btnEdit.tvButtonName.setTextColor(resources.getColor(R.color.white, null))
        binding!!.btnEdit.rlBtnSubmit.background =
            resources.getDrawable(R.drawable.bg_button_rounded_sky_blue, null)
    }

    override fun getCornerRadius() =
        requireContext().resources.getDimension(R.dimen.demo_sheet_rounded_corner)

    override fun getStatusBarColor() = Color.RED

    override fun getPeekHeight() = 1200

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showDialogError(msg: String) {
        if (isAdded && context != null) {
            val sheet = AlertFragment()
            sheet.setFragmentData(
                R.drawable.ic_alert,
                msg
            )
            activity?.supportFragmentManager?.let { it1 ->
                sheet.show(
                    it1,
                    "AlertFragment"
                )
            }
        }
    }

    private fun showDialogSuccess(msg: String) {
        if (isAdded && context != null) {
            val sheet = SuccessFragment()
            sheet.setFragmentData(
                R.drawable.ic_upload_success,
                msg
            )
            activity?.supportFragmentManager?.let { it1 ->
                sheet.show(
                    it1,
                    "SuccessFragment"
                )
                dismiss()
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddressBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}