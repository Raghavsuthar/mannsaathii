package com.example.util

import android.content.Context
import android.os.Build
import android.os.Debug
import android.util.Log
import java.io.File

/**
 * SecurityHelper provides lightweight, non-intrusive environment and integrity checks.
 *
 * Designed specifically for healthcare/elder-companion apps:
 * - Detects reverse-engineering environments (attached debuggers, rooted devices)
 * - Avoids aggressive false-positive lockouts to prevent disrupting elderly patients and caregivers
 * - Provides audit signals for secure handling of sensitive health data
 */
object SecurityHelper {

    private const val TAG = "SecurityHelper"

    data class SecurityStatus(
        val isRooted: Boolean,
        val isDebuggerAttached: Boolean,
        val isEmulator: Boolean,
        val isSafeEnvironment: Boolean
    )

    /**
     * Performs a comprehensive but lightweight check of the execution environment.
     */
    fun evaluateEnvironment(context: Context): SecurityStatus {
        val rooted = checkRoot()
        val debugger = checkDebugger()
        val emulator = checkEmulator()
        val isSafe = !rooted && !debugger

        Log.d(TAG, "Security Evaluation: rooted=$rooted, debugger=$debugger, emulator=$emulator, safe=$isSafe")

        return SecurityStatus(
            isRooted = rooted,
            isDebuggerAttached = debugger,
            isEmulator = emulator,
            isSafeEnvironment = isSafe
        )
    }

    /**
     * Checks for evidence of a rooted device without crashing.
     * Looks for test-keys build tags and common su/Superuser binaries.
     */
    fun checkRoot(): Boolean {
        return checkBuildTags() || checkSuBinaryPaths() || checkSuExecution()
    }

    private fun checkBuildTags(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkSuBinaryPaths(): Boolean {
        val knownPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        for (path in knownPaths) {
            try {
                if (File(path).exists()) return true
            } catch (_: Exception) {
                // Ignore security manager access exceptions
            }
        }
        return false
    }

    private fun checkSuExecution(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (_: Exception) {
            false
        } finally {
            process?.destroy()
        }
    }

    /**
     * Checks if an active debugger is attached to the running process.
     */
    fun checkDebugger(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    /**
     * Detects if the app is executing inside a generic virtual Android emulator.
     * Useful for diagnostics; does not block AI Studio's streaming preview.
     */
    fun checkEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val hardware = Build.HARDWARE.lowercase()
        val product = Build.PRODUCT.lowercase()

        return fingerprint.startsWith("generic") ||
                fingerprint.startsWith("unknown") ||
                model.contains("google_sdk") ||
                model.contains("emulator") ||
                model.contains("android sdk built for x86") ||
                manufacturer.contains("genymotion") ||
                hardware.contains("goldfish") ||
                hardware.contains("ranchu") ||
                product.contains("sdk_gphone") ||
                product.contains("vbox86p")
    }
}
