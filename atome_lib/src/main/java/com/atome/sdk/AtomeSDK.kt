package com.atome.sdk

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log

object AtomeSDK {

    private var application: Application? = null
    private val pkgNameList = listOf(
        "com.apaylater.android",
        "hk.atome.paylater",
        "my.atome.paylater",
        "com.apaylater.android.staging",
        "hk.atome.paylater.staging",
        "my.atome.paylater.staging"
    )
    private const val TAG = "ATOME_SDK"

    fun init(app: Application) {
        application = app
    }

    private fun checkApplication() {
        if (application == null) {
            throw RuntimeException("Please call the init method before calling setPaymentUrl method ！")
        }
    }

    /**
     * Please call the init method before calling this method.
     */
    fun setPaymentUrl(paymentUrl: String) {
        checkApplication()
        try {
            val packageManager = application?.packageManager
            val appInstalledPackageName = getAppInstalledPackageName()
            if (appInstalledPackageName != null) {
                val intent =
                    packageManager?.getLaunchIntentForPackage(appInstalledPackageName)
                intent?.data = Uri.parse(paymentUrl)
                application?.startActivity(intent)
            } else {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (intent.resolveActivity(application!!.packageManager) != null) {
                    application?.applicationContext?.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "setPaymentUrl method Exception: $e")
        }
    }

    private fun getAppInstalledPackageName(): String? {
        pkgNameList.forEach {
            if (isAppInstalled(it))
                return it
        }
        Log.d(TAG, "You don't have the Atome app installed!")
        return null
    }

    /**
     * Return whether the app is installed.
     *
     * @param pkgName The name of the package.
     * @return `true`: yes<br></br>`false`: no
     */
    private fun isAppInstalled(pkgName: String?): Boolean {
        if (isSpace(pkgName)) return false
        return try {
            val pm: PackageManager = application!!.packageManager
            pm.getApplicationInfo(pkgName!!, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun isSpace(s: String?): Boolean {
        if (s.isNullOrBlank()) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }
}