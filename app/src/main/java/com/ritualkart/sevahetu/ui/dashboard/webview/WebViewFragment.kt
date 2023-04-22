package com.ritualkart.sevahetu.ui.dashboard.webview

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ritualkart.sevahetu.customview.RedCliffeWebView
import com.ritualkart.sevahetu.databinding.FragmentWebViewBinding
import com.ritualkart.sevahetu.utiliy.Preference
import com.ritualkart.sevahetu.utiliy.Utility
import kotlinx.coroutines.launch
import org.json.JSONObject


class WebViewFragment : Fragment(), IWebViewListener {

    private var binding: FragmentWebViewBinding? = null
    private var url = ""
    private var pageName = ""
    private var preUrl = ""
    private lateinit var sharedPreference: Preference
    private lateinit var javaScriptReceiver: JavaScriptReceiver
    private lateinit var redCliffeChromeClient: RedCliffeChromeClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        sharedPreference = Preference(requireContext())
        if (bundle != null) {
            url = bundle.getString("url")!!
            pageName = bundle.getString("pageName")!!
        }

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (false) {

                } else {
                    if (binding!!.webview.canGoBack()) {
                        binding!!.webview.goBack()
                    } else {
                        isEnabled = false
                        activity?.onBackPressed()
                    }

                }
            }
        })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeader()
        setUpWebView(binding!!.webview)
        binding!!.includeHeader.ivBackFragment.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setHeader() {
        binding!!.includeHeader.tvTitleFragment.text = pageName ?: ""
    }

  /*  override fun onBackPressed() {
        if (binding!!.webview.canGoBack()) {
            binding!!.webview.goBack()
        } else {
            activity?.onBackPressed()
        }

    }
*/
    private fun setUpWebView(mainUIWebView: RedCliffeWebView) {

        // Enable webview debugging in debug mode
        if (0 != context?.getApplicationInfo()!!.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mainUIWebView.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
        }
        javaScriptReceiver = JavaScriptReceiver(context, this)
        val settings = mainUIWebView.settings
        settings.javaScriptEnabled = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        settings.saveFormData = true
        settings.builtInZoomControls = false
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.setSupportMultipleWindows(true)
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.useWideViewPort = false
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(true)
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        val ua = settings.userAgentString
        settings.setUserAgentString("RedcliffeLabsAndroidApp")
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mainUIWebView, true)
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        mainUIWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        mainUIWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mainUIWebView.addJavascriptInterface(javaScriptReceiver, "MyJSClient")
        mainUIWebView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (isAdded && context != null && binding?.progressBar != null) {
                    binding!!.progressBar.visibility = View.VISIBLE
                }

                if (isAdded && context != null && binding?.progressBar != null) {
                    view!!.visibility = View.GONE
                    super.onPageStarted(view, url, favicon)
                   /* val data = sharedPreference?.getUserModel()
                    var json = JSONObject()
                    json.put("username", data?.username)
                    json.put("password_change", data?.passwordChange)
                    json.put("token", data?.token)
                    json.put("refresh", data?.refresh)
                    json.put("access", data?.access)*/

                }

                super.onPageStarted(view, url, favicon)
            }

         override fun onPageFinished(view: WebView?, url: String?) {
                if (isAdded && context != null && binding?.progressBar != null) {
                    binding?.progressBar!!.visibility = View.GONE
                }
                view!!.visibility = View.VISIBLE
                super.onPageFinished(view, url)
            }


            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
            }


        }
        mainUIWebView.webChromeClient = (object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                dialog: Boolean,
                userGesture: Boolean,
                resultMsg: Message?
            ): Boolean {

                val result = view.hitTestResult
                val data = result.extra
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                startActivity(browserIntent)
                return false
            }
        })
        mainUIWebView.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
                if (keyEvent.getAction() === KeyEvent.ACTION_DOWN) {
                    val webView = view as WebView
                    if (i == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {
                            webView.goBack()

                            return true
                        }
                    }
                }
                return false
            }
        })

        lifecycleScope.launchWhenCreated {
            try {
                mainUIWebView.loadUrl(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCrashHandled(url: String) {


    }

    override fun onProgressChanged(newProgress: Int) {
    }

    override fun onWebViewLoadUrl(url: String?) {

    }
}