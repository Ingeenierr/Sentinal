package com.example.phantom.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.phantom.service.BehavioralMonitoringService

object MonitoringServiceHelper {
    
    fun isServiceEnabled(context: Context): Boolean {
        val serviceName = "${context.packageName}/${BehavioralMonitoringService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(serviceName) == true
    }
    
    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}