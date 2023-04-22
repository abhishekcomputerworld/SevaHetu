package com.ritualkart.sevahetu.ui.maplocation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentSeletLocationBottomSheetBinding
import com.ritualkart.sevahetu.model.Place
import com.ritualkart.sevahetu.utiliy.AlertFragment
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.SuccessFragment
import org.json.simple.JSONValue


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SelectLocationBottomSheetFragment : SuperBottomSheetFragment(), AddressOnItemClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPreference: Preference
    private var binding: FragmentSeletLocationBottomSheetBinding? = null
    private var navController: NavController? = null
    private lateinit var addressAdapter: AddressAdapter
    private var addressDataList: ArrayList<Place> = ArrayList()

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
        binding = FragmentSeletLocationBottomSheetBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun getCornerRadius() =
        requireContext().resources.getDimension(R.dimen.demo_sheet_rounded_corner)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeader()
        setUpListener()
        if(!sharedPreference.getMobileNumber().isNullOrEmpty()){
            getAddressData()
        }
    }

    private fun getAddressData() {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(sharedPreference.getMobileNumber().toString())
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                for ((key, value) in document?.data?.entries!!) {
                    println("$key = ${value}")
                    if (key == "address") {
                        val jsonStr: String = JSONValue.toJSONString(value)
                        val gson = Gson()
                        val organisation: Place = gson.fromJson(
                            jsonStr.toString(),
                            Place::class.java
                        )
                        addressDataList.add(organisation)

                    }
                    /*  productItemMap = value as HashMap<String, String>
                      Log.d("TAGLOG", "Cached document data:\"$key = $value\"")
                      Log.d("TAGLOG", "Cached document data:\"$productItemMap = $productItemMap\"")
                      productItem.add(
                          ProductItemResponse(
                              productItemMap["item_id"],
                              productItemMap["name"],
                              productItemMap["description"],
                              productItemMap["price"]?.toInt(),
                              productItemMap["quantity"],
                              productItemMap["image_url"]
                          )
                      )*/
                }
                addressAdapter = AddressAdapter(requireContext(), addressDataList, this)
                binding!!.rlAddress.adapter = addressAdapter
            } else {
                Log.d("TAGLOG", "Cached get failed: ", task.exception)
            }
        }
    }

    private fun setUpListener() {
        binding!!.backButton.setOnClickListener {
            if (dialog != null && dialog!!.isShowing)
                dismiss()
        }

        binding!!.tvCurrentLocation.setOnClickListener {
            findNavController()!!.navigate(R.id.action_selectLocationBottomSheetFragment_to_selectLocationFromMapFragment)

        }

    }

    private fun setHeader() {
        binding!!.tvLabelCity.text = "Select address"
    }

    override fun getStatusBarColor() = Color.RED

    override fun getPeekHeight() = 1200

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectLocationBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDeleteClick(position: Int, addressResults: Place) {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(sharedPreference.getMobileNumber().toString())
        val updates = hashMapOf<String, Any>(
            "address" to FieldValue.delete()
        )
        docRef.update(updates).addOnCompleteListener { task ->
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

    override fun onEditClick(position: Int, addressResults: Place) {
        findNavController()!!.navigate(R.id.action_selectLocationBottomSheetFragment_to_selectLocationFromMapFragment)

    }

    override fun onAddAddress(addressResults: Place) {
        sharedPreference.saveAddress(addressResults)
        if (dialog != null && dialog!!.isShowing)
            dismiss()
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
            }
        }
    }

}