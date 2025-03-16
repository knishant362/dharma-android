package com.aurora.app.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import timber.log.Timber

class AppNavigationHelper(private val context: Context) {

    fun openPlayStore() {
        val appPackageName = context.packageName // Your app's package name
        val playStoreUri = Uri.parse("market://details?id=$appPackageName")
        val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")

        val rateIntent = Intent(Intent.ACTION_VIEW, playStoreUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(rateIntent) // Try to open in Play Store app
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri)) // Open in browser as fallback
        }
    }

    fun shareApp() {
        val appPackageName = context.packageName
        val shareText =
            "Check out this app: https://play.google.com/store/apps/details?id=$appPackageName"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    fun openPrivacyPolicy() {
        val url = "https://www.freeprivacypolicy.com"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    }

    fun openSupportEmail() {
        val email = arrayOf("sample@gmail.com") // Email should be in array format
        val subject = "Support Request"

        val androidVersion = "Android Version: ${Build.VERSION.RELEASE}"
        val appVersion = "App Version: ${getAppVersion()}"
        val manufacturer = "Manufacturer: ${Build.MANUFACTURER}"
        val deviceModel = "Device Model: ${Build.MODEL}"

        val body = """
        Dear Support Team,






        App Details:
        $androidVersion
        $appVersion
        $manufacturer
        $deviceModel

        Thank you.
    """.trimIndent()

        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only "mailto:" scheme, no need to include the email here
            putExtra(Intent.EXTRA_EMAIL, email) // Email in array
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            context.startActivity(Intent.createChooser(supportIntent, "Contact Support"))
        } catch (e: Exception) {
            Timber.tag("SupportEmail").e("Error sending email: ${e.message}")
        }
    }


    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "Version: ${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "Version: Unknown"
        }
    }

}
