package com.example.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.util.Log
import java.io.File
import java.security.MessageDigest

/**
 * SecurityHelper provides lightweight, non-intrusive environment and integrity checks.
 *
 * Designed specifically for healthcare/elder-companion apps:
 * - Detects reverse-engineering environments (attached debuggers, rooted devices)
 * - Identifies certificate hashes and installer origins (Play Store, sideload, etc.)
 * - Flags debuggable builds
 * - Avoids aggressive false-positive lockouts to prevent disrupting elderly patients and caregivers
 * - Provides audit signals for secure handling of sensitive health data
 */
object SecurityHelper {

    private const val TAG = "SecurityHelper"

    data class SecurityStatus(
        val isRooted: Boolean,
        val isDebuggerAttached: Boolean,
        val isDebuggable: Boolean,
        val isEmulator: Boolean,
        val installerPackage: String?,
        val signatureHash: String?,
        val isSafeEnvironment: Boolean
    )

    /**
     * Performs a comprehensive but lightweight check of the execution environment.
     */
    fun evaluateEnvironment(context: Context): SecurityStatus {
        val rooted = checkRoot()
        val debugger = checkDebugger()
        val debuggable = isAppDebuggable(context)
        val emulator = checkEmulator()
        val installer = getInstallerPackage(context)
        val sigHash = getAppSignatureHash(context)
        val isSafe = !rooted && !debugger && !debuggable

        Log.d(TAG, "Security Evaluation: rooted=$rooted, debugger=$debugger, debuggable=$debuggable, emulator=$emulator, installer=$installer, sig=$sigHash, safe=$isSafe")

        return SecurityStatus(
            isRooted = rooted,
            isDebuggerAttached = debugger,
            isDebuggable = debuggable,
            isEmulator = emulator,
            installerPackage = installer,
            signatureHash = sigHash,
            isSafeEnvironment = isSafe
        )
    }

    /**
     * Checks if the app is marked as debuggable in its manifest/build flags.
     */
    fun isAppDebuggable(context: Context): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    /**
     * Retrieves the package name of the installer (e.g. "com.android.vending" for Google Play).
     */
    fun getInstallerPackage(context: Context): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstallerPackageName(context.packageName)
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Computes the SHA-256 fingerprint of the app's signing certificate.
     * Can be used to verify that the APK has not been re-signed with a malicious certificate.
     */
    fun getAppSignatureHash(context: Context): String? {
        return try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                val signingInfo = packageInfo.signingInfo
                if (signingInfo != null) {
                    if (signingInfo.hasMultipleSigners()) {
                        signingInfo.apkContentsSigners
                    } else {
                        signingInfo.signingCertificateHistory
                    }
                } else null
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            val primarySig = signatures?.firstOrNull() ?: return null
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(primarySig.toByteArray())
            digest.joinToString(":") { "%02X".format(it) }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to compute signature fingerprint", e)
            null
        }
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
