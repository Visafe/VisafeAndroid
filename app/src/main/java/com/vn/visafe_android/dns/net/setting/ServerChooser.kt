package com.vn.visafe_android.dns.net.setting

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.preference.DialogPreference
import android.util.AttributeSet
import com.vn.visafe_android.R
import com.vn.visafe_android.dns.sys.PersistentState.Companion.instance
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * A preference that opens a dialog, allowing the user to choose their preferred server from a list
 * or by entering a URL.
 */
class ServerChooser : DialogPreference {
    private var url: String? = null
    private var summaryTemplate: String? = null

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initialize(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyle,
        defStyleRes
    ) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        // Setting the key in code here, with the same value as in preferences.xml, allows this
        // preference to be initialized both declaratively (from XML) and imperatively (from Java).
        key = context.resources.getString(R.string.server_choice_key)
        isPersistent = true
        dialogLayoutResource = R.layout.servers
        summaryTemplate = context.resources.getString(R.string.server_choice_summary)
        setPositiveButtonText(R.string.intro_accept)
    }

    protected fun onSetInitialValue(defaultValue: Any?) {
        val storedUrl = getPersistedString(url)
        setUrl(storedUrl ?: defaultValue as String?)
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getString(index)!!
    }

    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String?) {
        this.url = url
        persistString(url)
        updateSummary(url)
    }

    // Updates the "Currently <servername>" summary under the title.
    private fun updateSummary(url: String?) {
        var url = url
        url = instance.expandUrl(context, url)
        var domain: String? = null
        try {
            val parsed = URL(url)
            domain = parsed.host
        } catch (e: MalformedURLException) {
            // Leave domain null.
        }
        summary = if (domain != null) {
            String.format(Locale.ROOT, summaryTemplate!!, domain)
        } else {
            null
        }
    }
}