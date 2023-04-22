package com.ritualkart.sevahetu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class
import com.ritualkart.sevahetu.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private  var binding: ActivitySplashBinding? = null

    companion object {
        internal const val splashTimeOut = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
    }

    private fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)
        Handler().postDelayed({
            val intentMain = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intentMain)
            finish() }, splashTimeOut.toLong())
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}