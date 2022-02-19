package com.yoosin.openingwifiusingaccessability

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!checkAccessibilityPermission()){
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onCreate: no permition")
            startActivity( Intent("android.settings.ACCESSIBILITY_SETTINGS"));
        }else{
            Log.e(TAG, "onCreate: has permision" )

        }
    }


    // method to check is the user has permitted the accessibility permission
    // if not then prompt user to the system's Settings activity
    fun checkAccessibilityPermission(): Boolean {

        var accessibilityEnabled = 0
        val service = "com.yoosin.openingwifiusingaccessability" + "/" + GlobalActionBarService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                getApplicationContext().getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val mStringColonSplitter = SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}