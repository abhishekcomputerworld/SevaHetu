package com.ritualkart.sevahetu.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapmyindia.sdk.plugins.places.placepicker.PlacePicker
import com.mapmyindia.sdk.plugins.places.placepicker.model.PlacePickerOptions
import com.mmi.services.api.Place
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.ShareCartDataListener
import com.ritualkart.sevahetu.databinding.FragmentHomeBinding
import com.ritualkart.sevahetu.model.CartDataItem
import com.ritualkart.sevahetu.model.response.HomeFragmentViewPagerResponse
import com.ritualkart.sevahetu.model.response.ProductItemResponse
import com.ritualkart.sevahetu.network.ApiHelperImpl
import com.ritualkart.sevahetu.network.RetrofitBuilder
import com.ritualkart.sevahetu.network.Status
import com.ritualkart.sevahetu.network.ViewModelFactory
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.Utility

class HomeFragment : Fragment(), HomeFragmentProductInterface {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var navController: NavController? = null
    private lateinit var sharedPreference: Preference
    private lateinit var homeViewModel: HomeViewModel
    private var productItem: ArrayList<ProductItemResponse> = ArrayList()
    private lateinit var productItemMap: HashMap<String, String>
    private lateinit var productAdapter: HomeFragmentProductAdapter
    private var bannerDataList: ArrayList<HomeFragmentViewPagerResponse> = arrayListOf()
    private lateinit var bannerDataAdapter: BannerDataAdapter
    private lateinit var listener: ShareCartDataListener
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* val textView: TextView = binding.tvSearch
         homeViewModel.text.observe(viewLifecycleOwner) {
             //textView.text = it
         }*/
        return root
    }

    private fun setUpViewModel() {
        // homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel = ViewModelProvider(
            this, ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService)
            )
        ).get(HomeViewModel::class.java)

        profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService)
            )
        ).get(ProfileViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = Preference(requireContext())
        navController = Navigation.findNavController(view)
        if (sharedPreference.getAddress() != null) {
            var place = sharedPreference.getAddress()
            binding.includedToolbar.tvLocationName.text = place?.city
            binding.viewpager.startAutoScroll()
        }
        getBannerData()
        getProfileData()
        setOnClickListener()
        getProductItemData()
        observeViewPagerData()
        setupProfileObserver()
    }



    private fun setupProfileObserver() {
        profileViewModel.getProfileResult().observe(viewLifecycleOwner, Observer {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { it ->
                            if (isAdded && context != null) {
                                sharedPreference.saveProfileModel(it)
                            }

                        }
                    }
                    Status.LOADING -> {
                        // binding!!.progressBar.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        if (isAdded && context != null) {
                            Utility.logAnalyticsEvent(
                                requireContext(),
                                "rcl_api_error",
                                "error_type",
                                it.message
                            )
                            it.message?.let {
                                if (it == "logout") {
                                    Utility.loginUser(requireContext())
                                } else {
                                    Utility.logAnalyticsEvent(
                                        requireContext(),
                                        "rcl_api_error",
                                        "error_type",
                                        it
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })
    }


    private fun getProfileData() {
        if (Utility.isNetworkAvailable(requireContext()) == true) {
            val token = "Token " + sharedPreference.getMobileNumber()
            if (sharedPreference.getMobileNumber() != "") {
                profileViewModel.getProfile(token)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        shareData(Utility.cartDataUpdate(sharedPreference.getCartData()))
        listener.shareData(Utility.cartDataUpdate(sharedPreference.getCartData()))
    }


    private fun getProductItemData() {
        val db = Firebase.firestore
        val docRef = db.collection("product_items").document("item_page_1")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                for ((key, value) in document?.data?.entries!!) {
                    println("$key = ${value}")
                    productItemMap = value as HashMap<String, String>
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
                    )
                }
                productAdapter = HomeFragmentProductAdapter(
                    requireContext(),
                    productItem,
                    sharedPreference,
                    this
                )
                binding.rlProductItem.adapter = productAdapter
            } else {
                Log.d("TAGLOG", "Cached get failed: ", task.exception)
            }
        }
    }

    private fun observeViewPagerData() {
        homeViewModel.getViewPagerResult().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    if (isAdded && context != null) {
                        binding!!.progressBar.visibility = View.GONE
                        if (it.data?.isNullOrEmpty() == false) {
                            bannerDataList = it.data as ArrayList<HomeFragmentViewPagerResponse>
                            bannerDataAdapter = BannerDataAdapter(requireContext(), bannerDataList)
                            binding!!.viewpager.adapter = bannerDataAdapter
                            binding!!.indicator.setViewPager(binding!!.viewpager)
                        }
                    }
                }
                Status.LOADING -> {
                    if (isAdded && context != null) binding!!.progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    if (isAdded && context != null) {
                        binding!!.progressBar.visibility = View.GONE
                        it?.message?.let {
                            Utility.logAnalyticsEvent(
                                requireContext(), "rcl_api_error", "error_type", it
                            )
                        }
                    }
                }
            }
        })
    }

    private fun getBannerData() {
        homeViewModel.viewPagerData()
    }

    private fun setOnClickListener() {
        binding.includedToolbar.rlCity.setOnClickListener {
            /* val intent = PlacePicker.IntentBuilder().placeOptions(PlacePickerOptions.builder().build())
                  .build(requireActivity())
              startActivityForResult(intent, 101)*/
            navController!!.navigate(R.id.action_navigation_home_to_selectLocationBottomSheetFragment)

        }
        binding.includedToolbar.rlCart.setOnClickListener {
            //  navController!!.navigate(R.id.action_navigation_home_to_navigation_dashboard)
            navController!!.navigate(R.id.action_navigation_home_to_navigation_cartFragment)

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProductItemClick(productItemResponse: ProductItemResponse, position: Int) {
        listener.shareData(Utility.cartDataUpdate(sharedPreference.getCartData()))
        shareData(Utility.cartDataUpdate(sharedPreference.getCartData()))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ShareCartDataListener) {
            listener = context
        }
    }

    fun shareData(cartData: CartDataItem) {
        if (cartData.cartItem > 0) {
            if (cartData.cartItem > 9) {
                binding.includedToolbar.tvCartCount.text = cartData.cartItem.toString()
            } else {
                binding.includedToolbar.tvCartCount.text = cartData.cartItem.toString() + " "
            }
            binding.includedToolbar.tvCartCount.visibility = View.VISIBLE
            binding.includedToolbar.ivCartNotification.visibility = View.VISIBLE
        } else {
            binding.includedToolbar.tvCartCount.visibility = View.GONE
            binding.includedToolbar.ivCartNotification.visibility = View.GONE
        }
    }
}