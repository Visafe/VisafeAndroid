package vn.ncsc.visafe.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityWebviewBinding
import vn.ncsc.visafe.utils.OnSingleClickListener

class WebViewActivity : BaseActivity() {
    lateinit var binding: ActivityWebviewBinding
    private var url = ""

    companion object {
        const val URL_KEY = "URL_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            url = it.getStringExtra(URL_KEY).toString()
        }
        setupWebViewUtilities()
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })
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
                view?.loadUrl(url)
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                url?.let { Log.e("onPageFinished: ", it) }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                url?.let { Log.e("onPageFinished: ", it) }
            }

        }
        binding.wvHomeUtilities.loadUrl(url)

    }
}