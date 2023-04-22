package com.ritualkart.sevahetu.ui.dashboard.webview

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.webkit.JavascriptInterface


class JavaScriptReceiver(var mContext: Context?,var webViewListener :IWebViewListener) {

    @JavascriptInterface
    fun getAndroidAppVersion(): String? {
        try {
            val pInfo: PackageInfo =
                mContext?.packageManager!!?.getPackageInfo(mContext!!.packageName, 0)!!
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}