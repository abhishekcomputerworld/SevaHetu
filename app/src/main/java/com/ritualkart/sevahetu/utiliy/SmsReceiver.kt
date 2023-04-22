package com.ritualkart.sevahetu.utiliy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver() {
    var otpListener: OTPReceiveListener? = null

    /**
     * @param otpListener
     */
    fun setOTPListener(otpListener: OTPReceiveListener) {
        this.otpListener = otpListener
    }

    /**
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status?
            // status?.statusCode
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String?
                    sms?.let {
                        // val p = Pattern.compile("[0-9]+") check a pattern with only digit
                        val p = Pattern.compile("\\d+")
                        val m = p.matcher(it)
                        if (m.find()) {
                            val otp = m.group()
                            otpListener?.onOTPReceived(otp)
                        }
                    }
                }
                CommonStatusCodes.TIMEOUT ->                     // Waiting for SMS timed out (5 minutes)
                    otpListener?.onOTPTimeOut()
                CommonStatusCodes.API_NOT_CONNECTED ->
                    otpListener?.onOTPReceivedError("API NOT CONNECTED")
                CommonStatusCodes.NETWORK_ERROR ->
                    otpListener?.onOTPReceivedError("NETWORK ERROR")
                CommonStatusCodes.ERROR ->
                    otpListener?.onOTPReceivedError("SOME THING WENT WRONG")
            }
        }
    }

    /**
     *
     */
    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
        fun onOTPReceivedError(error: String?)
    }
}