package com.ritualkart.sevahetu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mapmyindia.sdk.plugins.places.placepicker.PlacePicker
import com.mapmyindia.sdk.plugins.places.placepicker.model.PlacePickerOptions
import com.mmi.services.api.Place
import com.ritualkart.sevahetu.databinding.ActivityMainBinding
import com.ritualkart.sevahetu.model.CartDataItem
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.Utility

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,ShareCartDataListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreference: Preference
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPreference = Preference(this)
        setContentView(binding.root)
        setupListener()
        val navView: BottomNavigationView = binding.navView

         navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController!!.addOnDestinationChangedListener(this)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
       /* val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_cartFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
 */       navView.setupWithNavController(navController!!)
        shareData(Utility.cartDataUpdate(sharedPreference.getCartData()))

    }


    private fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
        showCartStrip()
    }

    private fun hideBottomNav() {
        binding.navView.visibility = View.GONE

    }
    private fun showCartStrip() {
        binding.cartStrip.cvCartStrip.visibility = View.VISIBLE

    }

    private fun hideCartStrip() {
        binding.cartStrip.cvCartStrip.visibility = View.GONE

    }
    private fun hideBothCartStripAndNav() {
        binding.cartStrip.cvCartStrip.visibility = View.GONE
        binding.navView.visibility = View.GONE
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.navigation_home -> showCartStrip()
            R.id.sendOtpFragment -> hideBottomNav()
            R.id.otpVerificationFragment -> hideBottomNav()
            R.id.selectLocationFromMapFragment -> hideBottomNav()
            else -> {
                showBottomNav()
                hideCartStrip()
            }
        }
    }


    override fun shareData(cartData: CartDataItem) {
       /* if (cartData.cartItem > 0) {
            if (cartData.cartItem > 9) {
                binding.bottomNav.tvCartCount.text = cartData.cartItem.toString()
            } else {
                binding.bottomNav.tvCartCount.text = cartData.cartItem.toString() + " "
            }
            binding.bottomNav.tvCartCount.visibility = View.VISIBLE
            binding.bottomNav.ivCartNotification.visibility = View.VISIBLE
        } else {
            binding.bottomNav.tvCartCount.visibility = View.GONE
            binding.bottomNav.ivCartNotification.visibility = View.GONE
        }*/

        if (cartData.cartItem > 0) {
            if (cartData.cartItem > 1) {
                binding!!.cartStrip.tvCartCount.text = cartData.cartItem.toString() + " Items"
            } else {
                binding!!.cartStrip.tvCartCount.text = cartData.cartItem.toString() + " Item"
            }
            binding!!.cartStrip.tvCartPrice.text = " | " + "\u20B9" + cartData.cartPrice.toString()
            binding!!.cartStrip.cvCartStrip.visibility = View.VISIBLE
        } else {
            binding!!.cartStrip.cvCartStrip.visibility = View.GONE
        }
    }

    private fun setupListener() {
        binding!!.cartStrip.cvCartStrip?.setOnClickListener {
           /* val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()*/
            navController!!.navigate(R.id.action_navigation_home_to_navigation_cartFragment)
        }
    }
}