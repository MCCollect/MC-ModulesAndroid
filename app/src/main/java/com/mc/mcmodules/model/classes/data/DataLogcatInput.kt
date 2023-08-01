package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import com.mc.mcmodules.view.ActTest
import com.mc.mcmodules.view.logcat.models.Log
import kotlinx.parcelize.Parcelize

/**
 * Model used to customize the Logcat module.
 * @param fileLogsName Name of the file that will be created in the application's file directory.
 * @param maxNumberLogs Maximum number of logs that can be written to the file.
 * @param isVisibleSend Status of send button visibility
 *
 * @sample ActTest.testLogcat
 * */
@Parcelize
data class DataLogcatInput(
    val fileLogsName: String = "log.txt",
    val maxNumberLogs: Int = 80,
    val isVisibleSend: Boolean = false
) : Parcelable

/**
 * Model for receiving the total logs in case sending is required.
 * @param fileLogsName Name of the file that will be created in the application's file directory.
 * @param listLogs All the logs that the user was viewing in the module are retrieved. These logs
 * are removed from the Log file and sent back to the activity that launched the Logcat.
 *
 * @sample ActTest.testLogcat
 * */
@Parcelize
data class DataLogcatOutput(
    val fileLogsName: String = "log.txt",
    val listLogs: List<Log>,
) : Parcelable
