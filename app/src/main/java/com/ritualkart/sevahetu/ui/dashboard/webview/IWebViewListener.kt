package com.ritualkart.sevahetu.ui.dashboard.webview

interface IWebViewListener {
    fun onCrashHandled(url: String)
    fun onProgressChanged(newProgress: Int)
    fun onWebViewLoadUrl(url: String?)
}