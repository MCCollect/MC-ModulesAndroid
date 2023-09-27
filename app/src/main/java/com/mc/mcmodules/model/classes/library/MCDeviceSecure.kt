package com.mc.mcmodules.model.classes.library

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MCDeviceSecure(private val context: Context) {

    var isRootedDevice: Boolean = false
    var isVirtualDevice: Boolean = false


    init {
        checkStatusDevice()
    }


    private fun isRooted(context: Context): Boolean {
        val appsRoot = ArrayList<String>()
        appsRoot.add("com.noshufou.android.su")
        appsRoot.add("eu.chainfire.supersu")
        appsRoot.add("com.thirdparty.superuser")
        appsRoot.add("com.koushikdutta.superuser")
        appsRoot.add("com.zachspong.temprootremovejb")
        appsRoot.add("com.ramdroid.appquarantine")
        appsRoot.add("com.topjohnwu.magisk")

        // get from build info
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }
        try {
            val superUserapk = File("/system/app/Superuser.apk")
            val otacertsZip = File("/etc/security/otacerts.zip")
            return if (superUserapk.exists() || otacertsZip.exists()) true else
                checkRootMethod2() ||
                        checkRootMethod3() ||
                        existAnyAppForRoot(appsRoot, context) ||
                        checkRootPath() ||
                        checkRunningProcesses(context)
        } catch (e1: Exception) {
            //System.out.println("Metodo 1 Root : Fail");
        }
        return false
    }

    private fun checkRunningProcesses(contex: Context): Boolean {
        var returnValue = false
        try {
            val am = contex.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list = am.getRunningServices(300)
            if (list != null) {
                var tempName: String
                for (i in list.indices) {
                    tempName = list[i].process
                    if (tempName.contains("supersu") || tempName.contains("superuser")) {
                        returnValue = true
                    }
                }
            }
        } catch (e: Exception) {
            return returnValue
        }
        return returnValue
    }

    // executes a command on the system
    private fun canExecuteCommand(command: String): Boolean {
        val executedSuccesfully: Boolean = try {
            Runtime.getRuntime().exec(command)
            true
        } catch (e: Exception) {

            //System.out.println("Metodo 2 Root : Fail");
            false
        }
        return executedSuccesfully
    }


    private fun checkRootMethod2(): Boolean {
        val paths = ArrayList<String>()
        try {
            paths.add("/system/bin/su")
            paths.add("/system/xbin/su")
            paths.add("/system/su")
            paths.add("/system/bin/.ext/.su")
            paths.add("/system/bin/magiskinit")
            paths.add("/system/bin/magisk")
            paths.add("/system/bin/magiskpolicy")
            paths.add("/system/bin/magiskhide")
            paths.add("/datos/local/su")
            paths.add("/datos/local/bin/su")
            paths.add("/datos/local/xbin/su")
            paths.add("/system/bin/a prueba de fallos/su")
            paths.add("/system/usr/necesitamos-root/su")
            paths.add("/caché/su")
            paths.add("/datos/su")
            paths.add("/sbin/su")
            paths.add("/dev/su")
            paths.add("/system/app/Superuser.apk")
            paths.add("/system/bin/su")
            paths.add("/system/xbin/su")
            paths.add("/data/local/xbin/su")
            paths.add("/data/local/bin/su")
            paths.add("/system/sd/xbin/su")
            paths.add("/system/bin/failsafe/su")
            paths.add("/data/local/su")
            paths.add("/su/bin/su")
            for (path in paths) {
                if (File(path).exists()) return true
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    private fun checkRootPath(): Boolean {
        try {
            for (pathDir in System.getenv("PATH").split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()) {
                if (File(pathDir, "su").exists()) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val ini = BufferedReader(InputStreamReader(process.inputStream))
            ini.readLine() != null
        } catch (t: Exception) {
            false
        } finally {
            try {
                process!!.destroy()
            } catch (ignore: Exception) {
            }
        }
    }

    private fun getInstalledApp(namePackage: String, context: Context): Boolean {
        val reply: Boolean
        val pm = context.packageManager
        reply = try {
            pm.getPackageInfo(namePackage, PackageManager.GET_ACTIVITIES)
            pm.getApplicationInfo(namePackage, PackageManager.GET_META_DATA)
            return true
        } catch (e: Exception) {
            false
        }
        return reply
    }

    private fun existAnyAppForRoot(listApps: ArrayList<String>, context: Context): Boolean {
        for (app in listApps) {
            if (getInstalledApp(app, context)) {
                return true
            }
        }
        return false
    }


    private fun isEmulatorDevice(): Boolean {
        if (Build.BOARD + "" == "unknown" || Build.BRAND + "" == "unknown" || Build.DEVICE + "" == "generic" || Build.FINGERPRINT + "" == "generic" || Build.HARDWARE + "" == "goldfish" || Build.ID + "" == "FRF91" || Build.MANUFACTURER + "" == "unknown" ||
            (Build.MODEL + "").contains("SDK") ||
            (Build.MODEL + "").contains("sdk") ||
            (Build.PRODUCT + "").contains("sdk") ||
            (Build.PRODUCT + "").contains("SDK") || Build.USER + "" == "android-build"
        ) {
            println("CPU_ABI:" + Build.CPU_ABI)
            println("CPU_ABI2:" + Build.CPU_ABI2)
            println("BOARD:" + Build.BOARD)
            println("BRAND:" + Build.BRAND)
            println("DEVICE:" + Build.DEVICE)
            println("FINGERPRINT:" + Build.FINGERPRINT)
            println("Hardware:" + Build.HARDWARE)
            println("Host:" + Build.HOST)
            println("ID:" + Build.ID)
            println("MANUFACTURER:" + Build.MANUFACTURER)
            println("MODEL:" + Build.MODEL)
            println("PRODUCT:" + Build.PRODUCT)
            println("RADIO:" + Build.RADIO)
            println("USER:" + Build.USER)
            return true
        }
        return false
    }


    private fun checkStatusDevice() {
        isRootedDevice = isRooted(context)
        isVirtualDevice = isEmulatorDevice()
    }


}