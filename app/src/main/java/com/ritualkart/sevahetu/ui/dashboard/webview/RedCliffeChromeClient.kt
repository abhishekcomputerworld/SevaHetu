package com.ritualkart.sevahetu.ui.dashboard.webview

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.Nullable
import com.ritualkart.sevahetu.customview.RedCliffeWebView


class RedCliffeChromeClient(
    context: Context,
    webView: RedCliffeWebView,
) :
    WebChromeClient() {
    private val webView: RedCliffeWebView
    private var childView: RedCliffeWebView? = null
    private val context: Context
    private val webViewListener: IWebViewListener

    init {
        this.webView = webView
        this.context = context
        webViewListener = context as IWebViewListener
    }

    override fun onCreateWindow(
        view: WebView?, dialog: Boolean,
        userGesture: Boolean, resultMsg: Message
    ): Boolean {

        return true
    }



    override fun onProgressChanged(view: WebView, newProgress: Int) {
        val url = view.url
        if (url != null && !url.contains("pageId")) {
            webViewListener.onProgressChanged(newProgress)
        }
    }

    override fun onCloseWindow(window: WebView) {
        super.onCloseWindow(window)
        //   frameLayout.removeViewAt(frameLayout.getChildCount()-1);
    }

    override fun onShowCustomView(
        view: View?,
        requestedOrientation: Int,
        callback: CustomViewCallback?
    ) {
        onShowCustomView(
            view,
            callback
        ) //To change body of overridden methods use File | Settings | File Templates.
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri?>?>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        // make sure there is no existing message

        return true
    }

    @Nullable
    override fun getDefaultVideoPoster(): Bitmap? {
        return  super.getDefaultVideoPoster()

    }

    private inner class UriWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            //  String host = Uri.parse(url).getHost();
            var url: String? = url
            return true
        }

        override fun onReceivedSslError(
            view: WebView, handler: SslErrorHandler,
            error: SslError
        ) {
            //super.onReceivedSslError(view, handler, error);
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {}
        override fun onPageFinished(view: WebView, url: String) {
            //    String host = Uri.parse(url).getHost();

        }
    }

    companion object {
        const val REQUEST_SELECT_FILE = 100
    }
}