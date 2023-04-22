package com.ritualkart.sevahetu.ui.dashboard.login

import android.R.attr.phoneNumber
import android.app.Activity
import android.app.PendingIntent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentSendOtpBinding
import com.ritualkart.sevahetu.utiliy.Constants
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.Utility
import java.util.concurrent.TimeUnit


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SendOtpFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSendOtpBinding? = null
    private val binding get() = _binding!!
    private var navController: NavController? = null
    private var showPhoneOptions = true
    private val googlePlayServices = 1972
    private var continueLastClickTime: Long = 0
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var sharedPreference: Preference


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val credential: Credential? = it.data?.getParcelableExtra(Credential.EXTRA_KEY)
                credential?.id?.let {
                    var pNo = it.trim()
                    binding!!.etPhoneNumber.etPhoneNumber.setText(pNo.takeLast(10))
                    binding!!.etPhoneNumber.etPhoneNumber.setSelection(binding!!.etPhoneNumber.etPhoneNumber.text.trim().length)
                    binding!!.etPhoneNumber.ivRightMark.visibility = View.VISIBLE
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = Preference(requireContext())
        navController = Navigation.findNavController(view)
        setHeader()
        setUpUi()
        setClickListener()
    }

    private fun setHeader() {
        binding.includeHeader.tvTitleFragment.text = "Login"
    }

    private fun setClickListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding!!.etPhoneNumber.etPhoneNumber.setAutofillHints(View.AUTOFILL_HINT_PHONE)
        }
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.e("PhoneAuthCredential", "onVerificationCompleted")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("PhoneAuthCredential", "onVerificationFailed" + e.toString())
                binding!!.progressBar.visibility = View.GONE
                view?.let { Utility.showSnackBar(it, "Something Went Wrong Try Again") }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding!!.progressBar.visibility = View.GONE
                val bundle = Bundle()
                bundle.putString("verificationId", verificationId)
                bundle.putString("mobileNo", binding!!.etPhoneNumber.etPhoneNumber.text.trim().toString())
                navController!!.navigate(
                    R.id.action_sendOtpFragment_to_otpVerificationFragment,
                    bundle
                )
            }
        }


        binding!!.etPhoneNumber.etPhoneNumber.setOnTouchListener { _, _ ->
            if (showPhoneOptions && isAdded && context != null) {
                showPhoneOptions = false
                requestHint()
                Utility.hideKeyboard(binding!!.loginParentView, requireContext())
            }
            false
        }

        binding!!.btnContinue.rlBtnSubmit.setOnClickListener {
            if (isAdded && context != null) {
                if (SystemClock.elapsedRealtime() - continueLastClickTime < 1000) {
                    return@setOnClickListener
                }
                continueLastClickTime = SystemClock.elapsedRealtime()
                Utility.hideKeyboard(binding!!.loginParentView, requireContext())
                if (!validation(binding!!.etPhoneNumber.etPhoneNumber.text.trim().toString())) {
                    return@setOnClickListener
                }
                binding!!.progressBar.visibility = View.VISIBLE
                var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(
                        "+91" + binding!!.etPhoneNumber.etPhoneNumber.text.trim().toString()
                    ) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity()) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            }
        }

        binding!!.includeHeader.ivBackFragment.setOnClickListener {
            activity?.onBackPressed()
        }

        binding!!.etPhoneNumber.etPhoneNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if ((start - before) <= 8) {
                    binding!!.etPhoneNumber.ivRightMark.visibility = View.GONE
                } else {
                    binding!!.etPhoneNumber.ivRightMark.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun requestHint() {
        if (checkPlayServices()) {
            val intent = getPhoneHintIntent(requireActivity())
            launcher.launch(IntentSenderRequest.Builder(intent).build())
        }
    }

    private fun checkPlayServices(): Boolean {
        if (isAdded && context != null) {
            val googleAPI = GoogleApiAvailability.getInstance()
            val result = googleAPI.isGooglePlayServicesAvailable(requireContext())
            if (result != ConnectionResult.SUCCESS) {
                if (googleAPI.isUserResolvableError(result)) {
                    googleAPI.getErrorDialog(this, result, googlePlayServices)?.show()
                }
                return false
            }
        }
        return true
    }

    // Construct a request for phone numbers and show the picker
    private fun getPhoneHintIntent(activity: FragmentActivity): PendingIntent {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()
        return Credentials.getClient(activity, options).getHintPickerIntent(hintRequest)
    }


    private fun setUpUi() {
        if (isAdded && context != null) {
            val termText = buildTermsTextView(binding!!.tvTerm)
            binding!!.tvTerm.movementMethod = LinkMovementMethod.getInstance()
            binding!!.tvTerm.setText(termText, TextView.BufferType.SPANNABLE)
            binding!!.tvTerm.highlightColor = Color.TRANSPARENT
        }
    }

    private fun buildTermsTextView(mTextViewTerms: AppCompatTextView): SpannableStringBuilder? {
        val termText = SpannableStringBuilder()
        val privacy = getString(R.string.privacy_policy)
        val tnc = getString(R.string.term_and_conditions)
        termText.append(getString(R.string.by_continuing))
        termText.append(" $tnc")
        termText.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                val bundle = Bundle()
                bundle.putString("url", Constants.TERM_OF_USE)
                bundle.putString(
                    "pageName",
                    getString(R.string.term_and_conditions)
                )
                navController!!.navigate(R.id.action_sendOtpFragment_to_webViewFragment, bundle)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.black)
                ds.isUnderlineText = true
                val typeface: Typeface? = if (Build.VERSION.SDK_INT >= 28) {
                    // This does only works from SDK 28 and higher
                    val typefaceA = ResourcesCompat.getFont(requireContext(), R.font.jost_medium)
                    Typeface.create(typefaceA, 500, false)
                } else {
                    // This always works (Whole name without .ttf)
                    ResourcesCompat.getFont(requireContext(), R.font.jost_medium)
                }
                ds.typeface = typeface
            }
        }, termText.length - tnc.length, termText.length, 0)
        termText.append(" and ")
        termText.append(privacy)
        termText.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                val bundle = Bundle()
                bundle.putString("url", Constants.PRIVACY_POLICY)
                bundle.putString(
                    "pageName",
                    getString(R.string.privacy_policy)
                )
                navController!!.navigate(R.id.action_sendOtpFragment_to_webViewFragment, bundle)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                val typeface: Typeface?
                ds.color = resources.getColor(R.color.black)
                ds.isUnderlineText = true
                typeface = if (Build.VERSION.SDK_INT >= 28) {
                    // This does only works from SDK 28 and higher
                    val typefaceA = ResourcesCompat.getFont(requireContext(), R.font.jost_medium)
                    Typeface.create(typefaceA, 500, false)
                } else {
                    // This always works (Whole name without .ttf)
                    ResourcesCompat.getFont(requireContext(), R.font.jost_medium)
                }
                ds.typeface = typeface
            }
        }, termText.length - privacy.length, termText.length, 0)
        return termText
    }

    private fun validation(text: String): Boolean {
        if (text.isNullOrEmpty()) {
            if (isAdded && context != null)
                Utility.showSnackBar(binding!!.loginParentView, getString(R.string.empty_or_wrong))
            return false
        } else if (text.length < 10) {
            if (isAdded && context != null)
                Utility.showSnackBar(
                    binding!!.loginParentView,
                    getString(R.string.mobile_number_size)
                )
            return false
        } else if (!Utility.isValidMobile(text)) {
            if (isAdded && context != null)
                Utility.showSnackBar(
                    binding!!.loginParentView,
                    getString(R.string.mobile_number_validation)
                )
            return false
        } else if (!binding!!.cbTerm.isChecked) {
            if (isAdded && context != null)
                wobbleAnimation()
            return false
        } else {
            return true
        }
    }

    private fun wobbleAnimation() {
        val shake: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.shake_animation)
        binding!!.rlTerm.startAnimation(shake)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SendOtpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}