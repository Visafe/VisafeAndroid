package vn.ncsc.visafe.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import vn.ncsc.visafe.GlobalConst
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityWebviewBinding

class WebViewActivity : BaseActivity() {
    lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWebViewUtilities()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebViewUtilities() {
        val setting = binding.wvHomeUtilities.settings
        setting.useWideViewPort = true
        setting.loadWithOverviewMode = true
        setting.javaScriptEnabled = true
        setting.allowContentAccess = true
        setting.setSupportZoom(false)
        setting.builtInZoomControls = true
        setting.displayZoomControls = false
        binding.wvHomeUtilities.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

        }
        binding.wvHomeUtilities.loadUrl(GlobalConst.URL_WEB_UTILITIES)

    }
}