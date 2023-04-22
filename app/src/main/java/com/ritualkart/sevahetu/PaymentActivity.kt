package com.ritualkart.sevahetu

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.ritualkart.sevahetu.databinding.ActivityPaymentBinding
import com.ritualkart.sevahetu.utiliy.Constants
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.Utility
import org.json.JSONObject


class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener,
    ExternalWalletListener {

    private var binding: ActivityPaymentBinding? = null
    private lateinit var sharedPreference :Preference
    private var orderId = ""
    private var pkId = 0
    private var amount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        sharedPreference = Preference(this)
        val view = binding!!.root
        setContentView(view)
        orderId = intent.getStringExtra("orderId").toString()
        pkId = intent.getIntExtra("pkId",0)
        amount =intent.getDoubleExtra("amount",0.0)
        startPayment(pkId,orderId,amount.toInt())

    }



    private fun startPayment(pkId : Int,orderId : String,amount : Int) {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val uuid = pkId
        val activity: Activity = this
        val co = Checkout()
        if(BuildConfig.DEBUG){
            co.setKeyID(Constants.RezorpayDebugKey)
        }else {
            co.setKeyID(Constants.RezorpayReleaseKey)
        }

        try {
            var options = JSONObject()
            options.put("name","Sheva Hetu")
            options.put("description","Credit Towards " + sharedPreference.getMobileNumber()+" for pay after booking ")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://staticcdn.redcliffelabs.com/media/gallary-file/None/a0ac09fa-d4e4-485a-9f41-9453cbeb7d90.webp")
            options.put("currency","INR")
            options.put("amount",amount*100)
            options.put("order_id", orderId)

            val notes = JSONObject()
            notes.put("bookingid",pkId)
            notes.put("link_type","android")
            options.put("notes",notes)

            val prefill = JSONObject()
            prefill.put("email",/*sharedPreference.getSelectedAddress()?.email*/"a@gmail.com")
            prefill.put("contact",sharedPreference.getMobileNumber())
            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Utility.showSnackBar(binding!!.rlUpcomingPayNow,"Error in payment")
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        if (Utility.isNetworkAvailable(this) == true) {
           /* val token = "Token " + sharedPreference.getUserModel()?.token
            val bookingPaymentRequest = CreateBookingPaymentRequest(p1?.paymentId,pkId)
            createBookingPaymentViewModel.createPayment(token,bookingPaymentRequest)*/
        }else{
            Utility.showSnackBar(binding!!.rlUpcomingPayNow,getString(R.string.no_internet))
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            Utility.showSnackBar(binding!!.rlUpcomingPayNow,"Payment Failed")
            onBackPressed()
            finish()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try{
            Utility.showSnackBar(binding!!.rlUpcomingPayNow,"External")
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}