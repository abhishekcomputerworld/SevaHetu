package com.ritualkart.sevahetu.utiliy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.ritualkart.sevahetu.model.CartDataItem
import com.ritualkart.sevahetu.model.SelectedProductItems
import com.ritualkart.sevahetu.model.response.ProductItemResponse
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utility {
    fun hideKeyboard(view: View, context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View, context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    fun showSnackBar(view: View, message: String) {
        try {
            val snackBar = Snackbar.make(
                view,
                message,
                Snackbar.LENGTH_LONG
            )
            snackBar.view.setBackgroundColor(Color.parseColor("#0E3F6C"))
            snackBar.setTextColor(Color.parseColor("#FFFFFF"))
            snackBar.setTextMaxLines(3)
            snackBar.view.minimumHeight = 100 // here
            snackBar.show()
        }catch (e : IllegalArgumentException){
            e.printStackTrace()
        }

    }

    fun isValidMobile(mobile: String): Boolean {
        val p: Pattern = Pattern.compile("(0|91)?[5-9][0-9]{9}")
        val m: Matcher = p.matcher(mobile)
        return m.find() && m.group().equals(mobile)
    }

    fun isKeyboardOpen(activity: Activity): Boolean {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.isAcceptingText
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun logAnalyticsEvent(context: Context,eventType: String?, key: String?, value: String?) {
        val eventBundle = Bundle()
        eventBundle.putString(key, value)
        FirebaseAnalytics.getInstance(context).logEvent(eventType!!, eventBundle)
    }

    fun cartDataUpdate(cartResponse: List<SelectedProductItems>): CartDataItem {
        var cartPrice = 0.0
        var cartItem = 0
        for (item in cartResponse) {
            cartPrice += (item.productItemPrice *  item.productItemQuantity).toDouble()
            cartItem +=item.productItemQuantity
        }
        return CartDataItem(cartItem, cartPrice.toInt())
    }
    fun isNetworkAvailable(context: Context?): Boolean? {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            return if(connectivityManager?.activeNetworkInfo != null) {
                connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
            }else{
                   connectivityManager?.isDefaultNetworkActive
            }
        }
        return false
    }

    fun loginUser(context: Context) {
       /* val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)*/
    }

}