package com.example.phantom.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.phantom.domain.model.UserBehaviorSnapshot
import com.example.phantom.domain.model.TrustScore
import com.example.phantom.domain.model.DefensiveAction
import com.example.phantom.domain.model.DefensiveActionType
import com.example.phantom.domain.usecase.SaveBehaviorSnapshotUseCase
import com.example.phantom.domain.usecase.CalculateTrustScoreUseCase
import com.example.phantom.domain.usecase.LogDefensiveActionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.sqrt

@AndroidEntryPoint
class BehavioralMonitoringService : AccessibilityService() {

    @Inject
    lateinit var saveBehaviorSnapshotUseCase: SaveBehaviorSnapshotUseCase

    @Inject
    lateinit var calculateTrustScoreUseCase: CalculateTrustScoreUseCase

    @Inject
    lateinit var logDefensiveActionUseCase: LogDefensiveActionUseCase

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Variables to track behavior metrics
    private val lastTouchTime = AtomicLong(0L)
    private val touchCadenceSum = AtomicLong(0L)
    private val touchCount = AtomicInteger(0)
    private val scrollStartY = AtomicLong(-1L) // -1 means no ongoing scroll
    private val scrollStartTime = AtomicLong(0L)
    private val gestureCount = AtomicInteger(0)
    private val appSwitchCount = AtomicInteger(0)
    private val lastPackageName = AtomicLong(0L) // Hash of last package name
    private val screenOnStartTime = AtomicLong(0L)
    private val totalScreenOnTime = AtomicLong(0L)
    private val touchPressureSum = AtomicFloat(0f)
    private val swipeDistance = AtomicFloat(0f)
    private val swipeTime = AtomicLong(0L)
    private val swipeCount = AtomicInteger(0)

    companion object {
        const val TAG = "PhantomMonitoringService"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Phantom Monitoring Service Connected")
        // Initialize tracking
        screenOnStartTime.set(System.currentTimeMillis())
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let { accEvent ->
            when (accEvent.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    recordTouchBehavior(accEvent)
                }
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    recordScrollBehavior(accEvent)
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    recordAppSwitchBehavior(accEvent)
                }
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                    recordFocusBehavior(accEvent)
                }
                AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> {
                    recordGestureStart(accEvent)
                }
                AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> {
                    recordGestureEnd(accEvent)
                }
            }
        }
    }

    private fun recordTouchBehavior(event: AccessibilityEvent) {
        val currentTime = System.currentTimeMillis()
        val lastTime = lastTouchTime.getAndSet(currentTime)

        if (lastTime != 0L) {
            val cadence = currentTime - lastTime
            touchCadenceSum.addAndGet(cadence)
            touchCount.incrementAndGet()
        }

        // Record touch pressure if available
        if (event.parcelableData != null) {
            // Note: Pressure data is limited through accessibility service
            // Additional sensors would be needed for accurate pressure
        }
    }

    private fun recordScrollBehavior(event: AccessibilityEvent) {
        val currentTime = System.currentTimeMillis()

        if (scrollStartY.get() == -1L) {
            // Starting a new scroll
            scrollStartY.set(event.beforeScrollY.toLong())
            scrollStartTime.set(currentTime)
        } else {
            // Finishing the scroll
            val scrollDistance = abs(event.beforeScrollY - scrollStartY.get()).toFloat()
            val scrollDuration = currentTime - scrollStartTime.get()
            
            if (scrollDuration > 0) {
                val velocity = scrollDistance / (scrollDuration / 1000f) // pixels per second
                // Process scroll velocity
            }
            
            scrollStartY.set(-1L) // Reset for next scroll
        }
    }

    private fun recordAppSwitchBehavior(event: AccessibilityEvent) {
        val currentPackageName = event.packageName?.hashCode()?.toLong() ?: 0L
        
        if (currentPackageName != lastPackageName.get()) {
            appSwitchCount.incrementAndGet()
            lastPackageName.set(currentPackageName)
        }
    }

    private fun recordFocusBehavior(event: AccessibilityEvent) {
        // Track focus changes to understand user attention patterns
    }

    private fun recordGestureStart(event: AccessibilityEvent) {
        gestureCount.incrementAndGet()
    }

    private fun recordGestureEnd(event: AccessibilityEvent) {
        // Complete gesture recording
    }

    override fun onInterrupt() {
        Log.d(TAG, "Monitoring service interrupted")
    }

    fun captureBehaviorSnapshot(): UserBehaviorSnapshot {
        val currentTime = System.currentTimeMillis()

        // Calculate averages and metrics
        val avgTouchCadence = if (touchCount.get() > 0) {
            (touchCadenceSum.get() / touchCount.get()).toFloat()
        } else 0f

        val scrollVelocity = 0f // Placeholder - would be calculated from scroll events
        val gestureSmoothness = calculateGestureSmoothness() // Placeholder calculation
        val appSwitchFrequency = calculateAppSwitchFrequency(currentTime)
        val deviceOrientation = getDeviceOrientation()
        val usageTimingPattern = getCurrentUsagePattern(currentTime)
        val screenOnDuration = totalScreenOnTime.get() + (currentTime - screenOnStartTime.get())
        val avgTouchPressure = if (touchCount.get() > 0) {
            touchPressureSum.get() / touchCount.get()
        } else 0f
        val swipeAccuracy = calculateSwipeAccuracy() // Placeholder
        val typingRhythm = calculateTypingRhythm() // Placeholder

        return UserBehaviorSnapshot(
            timestamp = currentTime,
            touchCadence = avgTouchCadence,
            scrollVelocity = scrollVelocity,
            gestureSmoothness = gestureSmoothness,
            appSwitchFrequency = appSwitchFrequency,
            deviceOrientation = deviceOrientation,
            usageTimingPattern = usageTimingPattern,
            screenOnDuration = screenOnDuration,
            avgTouchPressure = avgTouchPressure,
            swipeAccuracy = swipeAccuracy,
            typingRhythm = typingRhythm,
            trustScore = 0 // Will be calculated after saving
        )
    }

    private fun calculateGestureSmoothness(): Float {
        // Simplified calculation - in reality this would analyze movement patterns
        return (gestureCount.get() % 100).toFloat() / 100f
    }

    private fun calculateAppSwitchFrequency(currentTime: Long): Float {
        val sessionDuration = currentTime - screenOnStartTime.get()
        if (sessionDuration > 0) {
            return (appSwitchCount.get() * 60000f) / sessionDuration // Switches per minute
        }
        return 0f
    }

    private fun getDeviceOrientation(): Int {
        return resources.configuration.orientation
    }

    private fun getCurrentUsagePattern(currentTime: Long): String {
        // Return a representation of usage timing patterns
        val hour = (currentTime % 86400000) / 3600000 // Milliseconds since start of day divided by hour
        return "{\"hourly_pattern\": [$hour]}"
    }

    private fun calculateSwipeAccuracy(): Float {
        // Placeholder for swipe accuracy calculation
        return 0.8f
    }

    private fun calculateTypingRhythm(): Float {
        // Placeholder for typing rhythm calculation
        return 0.7f
    }

    fun evaluateTrustAndApplyDefenses(thresholdLow: Int = 60, thresholdMedium: Int = 40, thresholdHigh: Int = 20) {
        scope.launch {
            try {
                // Capture current behavior
                val snapshot = captureBehaviorSnapshot()
                
                // Save the snapshot
                saveBehaviorSnapshotUseCase(snapshot)
                
                // Calculate trust score based on the snapshot
                val trustScore = calculateTrustScoreUseCase()
                
                // Update the snapshot with the calculated trust score
                val updatedSnapshot = snapshot.copy(trustScore = trustScore.score)
                saveBehaviorSnapshotUseCase(updatedSnapshot)

                // Apply defensive actions based on trust score
                when {
                    trustScore.score < thresholdHigh -> {
                        // High risk - apply strong defenses
                        applyDefensiveAction(DefensiveActionType.CONTENT_BLUR)
                        applyDefensiveAction(DefensiveActionType.CLIPBOARD_DISABLE)
                        applyDefensiveAction(DefensiveActionType.NOTIFICATION_MASK)
                    }
                    trustScore.score < thresholdMedium -> {
                        // Medium risk - apply moderate defenses
                        applyDefensiveAction(DefensiveActionType.LAUNCH_DELAY)
                        applyDefensiveAction(DefensiveActionType.APP_HIDE)
                    }
                    trustScore.score < thresholdLow -> {
                        // Low risk - apply minimal defenses
                        applyDefensiveAction(DefensiveActionType.LAUNCH_DELAY)
                    }
                    else -> {
                        // Normal trust level - no defensive actions needed
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error evaluating trust: ${e.message}", e)
            }
        }
    }

    private suspend fun applyDefensiveAction(actionType: DefensiveActionType) {
        val action = DefensiveAction(
            timestamp = System.currentTimeMillis(),
            actionType = actionType,
            triggeredByTrustScore = trustScore.value?.score ?: 0,
            durationMs = 0L, // Will be updated when action ends
            description = getActionDescription(actionType)
        )

        logDefensiveActionUseCase(action)
        
        // Actually apply the defensive measure
        when (actionType) {
            DefensiveActionType.LAUNCH_DELAY -> delayAppLaunches()
            DefensiveActionType.APP_HIDE -> hideSensitiveApps()
            DefensiveActionType.CONTENT_BLUR -> blurSensitiveContent()
            DefensiveActionType.CLIPBOARD_DISABLE -> disableClipboardAccess()
            DefensiveActionType.NOTIFICATION_MASK -> maskNotifications()
            DefensiveActionType.SCREEN_LOCK_DELAY -> delayScreenLock()
        }
    }

    private fun getActionDescription(actionType: DefensiveActionType): String {
        return when (actionType) {
            DefensiveActionType.LAUNCH_DELAY -> "Delayed app launch due to low trust score"
            DefensiveActionType.APP_HIDE -> "Hidden sensitive apps due to low trust score"
            DefensiveActionType.CONTENT_BLUR -> "Blurred sensitive content due to low trust score"
            DefensiveActionType.CLIPBOARD_DISABLE -> "Disabled clipboard access due to low trust score"
            DefensiveActionType.NOTIFICATION_MASK -> "Masked notifications due to low trust score"
            DefensiveActionType.SCREEN_LOCK_DELAY -> "Delayed screen lock due to low trust score"
        }
    }

    private fun delayAppLaunches() {
        // Implementation would delay app launches using a custom launcher overlay
        Log.d(TAG, "Delaying app launches due to security concerns")
    }

    private fun hideSensitiveApps() {
        // Implementation would hide certain apps from the launcher
        Log.d(TAG, "Hiding sensitive apps due to security concerns")
    }

    private fun blurSensitiveContent() {
        // Implementation would blur content using overlays
        Log.d(TAG, "Blurring sensitive content due to security concerns")
    }

    private fun disableClipboardAccess() {
        // Implementation would intercept clipboard operations
        Log.d(TAG, "Disabling clipboard access due to security concerns")
    }

    private fun maskNotifications() {
        // Implementation would mask notification content
        Log.d(TAG, "Masking notifications due to security concerns")
    }

    private fun delayScreenLock() {
        // Implementation would prevent immediate screen lock
        Log.d(TAG, "Delaying screen lock due to security concerns")
    }

    fun startMonitoring() {
        // Start monitoring behaviors
        Log.d(TAG, "Starting behavioral monitoring")
    }

    fun stopMonitoring() {
        // Stop monitoring behaviors
        Log.d(TAG, "Stopping behavioral monitoring")
    }
}