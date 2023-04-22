package com.ritualkart.sevahetu.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapmyindia.sdk.plugins.places.placepicker.PlacePicker
import com.mapmyindia.sdk.plugins.places.placepicker.model.PlacePickerOptions
import com.mmi.services.api.Place
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentDashboardBinding
import com.ritualkart.sevahetu.ui.dashboard.login.OtpVerificationFragment
import com.ritualkart.sevahetu.ui.dashboard.login.SendOtpFragment
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.Utility
import java.util.concurrent.TimeUnit


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var navController: NavController? = null
    private lateinit var sharedPreference: Preference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*    val textView: TextView = binding.textDashboard
            dashboardViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it
            }*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        sharedPreference = Preference(requireContext())

        setHeader()
        setClickListener()
        userLogin()
    }

    private fun setHeader() {
        binding.includeHeader.tvTitleFragment.text = "DashBoard"
    }

    private fun userLogin() {
         if(!sharedPreference.getMobileNumber().isNullOrEmpty()){
            binding.llLogOut.visibility=View.VISIBLE
            binding.llSendOtp.visibility=View.GONE
            binding.llPhoneNo.visibility=View.VISIBLE
            binding.tvPhoneNo.text= sharedPreference.getMobileNumber() as CharSequence?
        }else{
            binding.llLogOut.visibility=View.GONE
            binding.llSendOtp.visibility=View.VISIBLE
        }

    }



    private fun setClickListener() {
        binding.includeHeader.ivBackFragment.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.llYourOrder.setOnClickListener {

        }
        binding.llYourAddress.setOnClickListener {
            navController!!.navigate(R.id.action_navigation_dashboard_to_selectLocationBottomSheetFragment)
        }
        binding.llContactUs.setOnClickListener {
            try {
                val mobile = "9711918658"
                val msg = "I need Help"
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://api.whatsapp.com/send?phone=$mobile&text=$msg")
                    )
                )
            } catch (e: Exception) {
                //whatsapp app not install
            }
        }
        binding.llShareApp.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Download SevaHetu App at: https://play.google.com/store/apps/details?id=" + "com.rockstargames.gtasa"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        binding.llRateUs.setOnClickListener {
            startActivity(Intent(android.content.Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.rockstargames.gtasa")))
        }
        binding.llCheckUpdate.setOnClickListener {
            startActivity(Intent(android.content.Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.rockstargames.gtasa")))
        }
        binding.llAboutUs.setOnClickListener {

        }
        binding.llLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            sharedPreference.saveMobileNumber("")
            navController!!.navigate(R.id.action_navigation_dashboard_self)
        }
        binding.llSendOtp.setOnClickListener {
            navController!!.navigate(R.id.action_navigation_dashboard_to_sendOtpFragment)
            /* val fragmentTransaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
              fragmentTransaction.add(R.id.flFragment, SendOtpFragment(), "")
             fragmentTransaction.commit()*/
        }
        binding.ivEditProfile.setOnClickListener {
            navController!!.navigate(R.id.action_navigation_dashboard_to_editPersonalInfoFragment)
            /*val fragment = EditPersonalInfoFragment()
            val fragmentTransaction: FragmentTransaction =
                activity?.supportFragmentManager!!?.beginTransaction()
            fragmentTransaction.add(R.id.flFragment, fragment, "");
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()*/
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}