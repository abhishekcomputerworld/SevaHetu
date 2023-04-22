package com.ritualkart.sevahetu.ui.dashboard.login

import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentOtpVerificationBinding
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.SmsReceiver
import com.ritualkart.sevahetu.utiliy.Utility
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class OtpVerificationFragment : Fragment(), SmsReceiver.OTPReceiveListener {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentOtpVerificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreference: Preference
    private val start = 30000L
    var timer = start
    lateinit var countDownTimer: CountDownTimer
    private var intentFilter: IntentFilter? = null
    private lateinit var smsReceiver: SmsReceiver
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var navController: NavController? = null
    private var verificationId: String? = null
    private var mobileNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            verificationId = it.getString("verificationId")
            mobileNo = it.getString("mobileNo")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = Preference(requireContext())
        navController = Navigation.findNavController(view)
        setHeader()
        setupUi()
        setClickListener()
        setTextTimer()
        startTimer()
        initBroadCast()
        initSmsListener()
    }

    private fun setClickListener() {
        binding!!.includeHeader.ivBackFragment.setOnClickListener {
            navController!!.popBackStack()
        }
        binding!!.changeMobileNo.setOnClickListener {
            navController!!.popBackStack()

        }


        binding!!.tvClearOtp.setOnClickListener {
            binding!!.customOtpView.setText("")
            binding!!.customOtpView.requestFocus()
            binding!!.rlOtpError.visibility = View.GONE
        }

        binding!!.tvResend.setOnClickListener {
            if (isAdded && context != null) {
                binding!!.tvResendValue.visibility = View.VISIBLE
                binding!!.rlOtpError.visibility = View.GONE
                binding!!.tvResend.isEnabled = false
                if (countDownTimer != null) {
                    countDownTimer.start()
                } else {
                    startTimer()
                }
                binding!!.progressBar.visibility = View.VISIBLE
                val loginRequest = sharedPreference.getMobileNumber()
                var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Log.e("PhoneAuthCredential", "onVerificationCompleted")
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        binding!!.progressBar.visibility = View.GONE
                        Log.e("PhoneAuthCredential", "onVerificationFailed" + e.toString())
                        binding!!.progressBar.visibility = View.GONE
                        view?.let { Utility.showSnackBar(it, "Something Went Wrong Try Again") }   }

                    override fun onCodeSent(
                        verification_Id: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        binding!!.progressBar.visibility = View.GONE
                        verificationId = verification_Id

                        Utility.showSnackBar(binding!!.otpParentView, "OTP Send Again")
                        Log.e("PhoneAuthCredential", "onVerificationFailed" + verificationId)
                        Log.e("PhoneAuthCredential", "onVerificationFailed" + token)
                    }
                }
                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(loginRequest.toString()) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity()) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }

        }

        binding!!.btnContinue.rlBtnSubmit.setOnClickListener {
            if (isAdded && context != null) {
                binding!!.rlOtpError.visibility = View.GONE
                Utility.hideKeyboard(binding!!.otpParentView, requireContext())
                val otp = binding!!.customOtpView.text.toString()
                if (!validation(otp)) {
                    return@setOnClickListener
                }
                binding!!.progressBar.visibility = View.VISIBLE
                val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
                signInWithPhoneAuthCredential(credential)
            }
        }


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    // Log.d(TAG, "signInWithCredential:success")

                    sharedPreference.saveMobileNumber(/*"+91" +*/ mobileNo.toString())

                    val user = task.result?.user
                    binding!!.progressBar.visibility = View.GONE

                    val db = Firebase.firestore
                    val docRef = db.collection("users").document(mobileNo!!)
                    docRef.get().addOnSuccessListener { document ->
                            if (document != null) {
                                val profile: MutableMap<String, Any> = HashMap()
                                profile["name"] = ""
                                profile["user_name"] = mobileNo!!
                                db.collection("users").document(mobileNo!!)
                                    .set(profile)
                                    .addOnSuccessListener(OnSuccessListener<Void?> {
                                        navController!!.navigate(R.id.action_otpVerificationFragment_to_navigation_home)
                                        Utility.showSnackBar(requireView().rootView, "Login Success Full")

                                    })
                                    .addOnFailureListener(OnFailureListener { e ->
                                        Utility.showSnackBar(requireView().rootView, "Plaese Try Again SomeThing Went Wrong")
                                    })
                            } else {
                                Log.d("TAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                        }


                }
                else {
                    // Sign in failed, display a message and update the UI
                    //  Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Utility.showSnackBar(binding!!.otpParentView, "OTP Incorrect")
                        binding!!.progressBar.visibility = View.GONE

                    }
                    // Update UI
                }
            }
    }

    private fun setHeader() {
        binding.includeHeader.tvTitleFragment.text = "Login"
    }

    private fun setupUi() {
        binding!!.tvLabelVerificationPhone.text =
            getString(R.string.enter_the_digit) + "\n+91 " + sharedPreference.getMobileNumber()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timer, 1000) {
            //            end of timer
            override fun onFinish() {
                restTimer()
            }

            override fun onTick(millisUntilFinished: Long) {
                timer = millisUntilFinished
                setTextTimer()
            }
        }.start()
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
    }

    private fun restTimer() {
        if (isAdded && context != null) {
            countDownTimer.cancel()
            timer = start
            binding!!.tvResend.isEnabled = true
            binding!!.tvResendValue.visibility = View.INVISIBLE
            setTextTimer()
        }
    }

    fun setTextTimer() {
        if (isAdded && context != null) {
            var m = (timer / 1000) / 60
            var s = (timer / 1000) % 60
            var format = String.format("%02d:%02d", m, s)
            binding!!.tvResendValue.setText(": " + format)
        }
    }

    private fun validation(text: String): Boolean {
        if (text.isNullOrEmpty()) {
            if (isAdded && context != null)
                Utility.showSnackBar(binding!!.otpParentView, getString(R.string.otp_empty))
            return false
        } else if (text.length != 6) {
            if (isAdded && context != null)
                Utility.showSnackBar(binding!!.otpParentView, getString(R.string.otp_size))
            return false
        } else {
            return true
        }
    }

    override fun onOTPTimeOut() {
        if (isAdded && context != null) {
            Utility.showSnackBar(binding!!.otpParentView, "OTP Timeout")
        }
    }

    override fun onOTPReceivedError(error: String?) {
        if (isAdded && context != null)
            Utility.showSnackBar(binding!!.otpParentView, error.toString())
    }

    override fun onOTPReceived(otp: String?) {
        if (isAdded && context != null) {
            binding!!.rlOtpError.visibility = View.GONE
            Utility.hideKeyboard(binding!!.otpParentView, requireContext())
            binding!!.customOtpView.setText(otp?.toString())
            val otp = binding!!.customOtpView.text.toString()
            if (!validation(otp)) {
                return
            }
            binding!!.progressBar.visibility = View.VISIBLE
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            signInWithPhoneAuthCredential(credential)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun initBroadCast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        try {
            smsReceiver = SmsReceiver()
            smsReceiver.setOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            requireActivity().registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(requireActivity())
            val task: Task<Void> = client.startSmsRetriever()
            task.addOnSuccessListener {
                Log.v("ValidateOTPActivity", "SMS receiver started")
            }
            task.addOnFailureListener {
                Log.v("ValidateOTPActivity", " SMS receiver failed")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initSmsListener() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsRetriever()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OtpVerificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}