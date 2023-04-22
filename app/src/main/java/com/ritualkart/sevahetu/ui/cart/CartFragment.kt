package com.ritualkart.sevahetu.ui.cart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ritualkart.sevahetu.PaymentActivity
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentCartBinding
import com.ritualkart.sevahetu.model.response.ProductItemResponse
import com.ritualkart.sevahetu.ui.home.HomeFragmentProductAdapter
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.Utility

class CartFragment : Fragment(), CartFragmentProductInterface {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var navController: NavController? = null
    private lateinit var sharedPreference: Preference
    private lateinit var productItemMap: HashMap<String, String>
    private lateinit var productAdapter: CartFragmentProductAdapter
    private var productItem: ArrayList<ProductItemResponse> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvCartEmpty
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = Preference(requireContext())
        navController = Navigation.findNavController(view)
        setUpHeader()
        setUpListener()
        getProductItemData()
        paymentSetUp()
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (false) {

                    } else {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            })
    }

    override fun onStart() {
        super.onStart()
        paymentSetUp()
    }

    private fun getProductItemData() {
        val db = Firebase.firestore
        val docRef = db.collection("product_items").document("item_page_1")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.rlNoCartData.visibility = View.GONE
                val document = task.result
                for ((key, value) in document?.data?.entries!!) {
                    println("$key = ${value}")
                    productItemMap = value as HashMap<String, String>
                    if (sharedPreference.packageAvailable(productItemMap["item_id"])) {
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
                }
                productAdapter = CartFragmentProductAdapter(
                    requireContext(),
                    productItem,
                    sharedPreference,
                    this
                )
                binding!!.rlProductItem.adapter = productAdapter
            } else {
                binding.rlNoCartData.visibility = View.VISIBLE
                Log.d("TAGLOG", "Cached get failed: ", task.exception)
            }
        }
    }

    private fun setUpListener() {
        binding.includeHeader.ivBackFragment.setOnClickListener {
            navController?.popBackStack()
        }
        binding.cvCartStrip.setOnClickListener {
            if (sharedPreference.getMobileNumber().isNullOrEmpty()) {
                navController!!.navigate(R.id.action_navigation_cartFragment_to_sendOtpFragment)
            } else if (sharedPreference.getAddress() == null) {
                navController!!.navigate(R.id.action_navigation_cartFragment_to_selectLocationBottomSheetFragment)
            } else {
                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra("orderId", "orderId")
                intent.putExtra("pkId", 78)
                intent.putExtra("amount", 77.0)
                startActivity(intent)
            }
        }
        binding.tvChangeAddress.setOnClickListener {
            navController!!.navigate(R.id.action_navigation_cartFragment_to_selectLocationBottomSheetFragment)
        }

    }

    private fun setUpHeader() {
        binding.includeHeader.tvTitleFragment.text = "Cart Activity"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun paymentSetUp() {
        binding.tvItemTotalAmount.text =
            Utility.cartDataUpdate(sharedPreference.getCartData()).cartPrice.toString()
        binding.tvDeliveryCharge.text = " Free"
        binding.tvPayableAmount.text =
            Utility.cartDataUpdate(sharedPreference.getCartData()).cartPrice.toString()
        if (sharedPreference.getMobileNumber().isNullOrEmpty()) {
            binding.tvViewCart.text = "Please Login"
        } else if (sharedPreference.getAddress() == null) {
            binding.tvViewCart.text = "Choose Address Please"
        } else {
            binding.rlSelectedLocation.visibility=View.VISIBLE
            binding.tvSelectedLocationName.text = sharedPreference.getAddress()?.formattedAddress
            binding.tvViewCart.text = "Proceed to Payment"
        }
    }

    override fun onProductItemDelete(productItemResponse: ProductItemResponse, position: Int) {
        productItem.remove(productItemResponse)
        //productAdapter.notifyItemChanged(position)
        productAdapter.notifyDataSetChanged()
        paymentSetUp()
    }

    override fun onProductItemAdded(productItemResponse: ProductItemResponse, position: Int) {
        paymentSetUp()
    }

    override fun onProductItemRemove(productItemResponse: ProductItemResponse, position: Int) {
        paymentSetUp()
    }

}