package com.mc.mcmodules.view.logcat.datasource

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.mc.mcmodules.R
import com.mc.mcmodules.model.classes.data.DataLogcatInput
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.view.ActTest
import com.mc.mcmodules.view.logcat.models.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * This class is responsible for managing modifications to the log file, such as adding a new log,
 * getting all the logs registered in the file, and clearing all current logs from the file.
 * It uses MutableLiveData for the list of logs, allowing it to be linked to a ViewModel and
 * subsequently adding an observer in the UI or accessing it through its value.
 * The context provided in the constructor is weakly referenced, and it is validated if it is still
 * in memory every time it is used.
 *
 * The class offers the following functionalities:
 * 1. Get and set customizable elements of the file, such as the number of logs and the file name.
 * 2. Get all the logs from the file.
 * 3. Add a log of type REQUEST to the file.
 * 4. Add a log of type RESPONSE to the file.
 * 5. Add a log of type ERROR to the file.
 * 6. Clear all the logs from the file.
 *
 * When adding a new log to the file, the corresponding thread should be handled outside this class,
 * to ensure that multiple writes to the file do not cause damage or overwrite each other.
 *
 * @param weakContext Encapsulates the weak reference to the activity's context.
 * @param listLogs Current list of logs in the log file.
 * @param logFile Name of the file that will be created in the application's file directory.
 * @param maxLogs Maximum number of logs that can be written to the file.
 *
 * @sample ActTest.testLogcat
 * */
class LogsDataSource(
    context: Context,
    private val featuresFile: DataLogcatInput
) {
    private val scope by lazy { CoroutineScope(SupervisorJob()) }
    private val weakContext = WeakReference(context)
    val listLogs: MutableLiveData<MutableList<Log>> = MutableLiveData(mutableListOf())

    /**
     * Get custom features from the Log file
     * @return DataLogcatInput(name of file, number logs)
     * */
    fun getCustomFileLogs(): DataLogcatInput {
        return DataLogcatInput(
            featuresFile.fileLogsName, featuresFile.maxNumberLogs, featuresFile.isVisibleSend
        )
    }

    /**
     * Get all the logs from the file. Update value listLogs in the observer activity.
     * */
    fun allLogs() {
        scope.launch(Dispatchers.IO) {
            val list = getAllLogs().mapNotNull { item ->
                try {
                    val partsLog = item.split(",")
                    Log(
                        image = partsLog[0].toInt(),
                        date = partsLog[1],
                        description = partsLog[2],
                        type = partsLog[3]
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    //addNewLogError("Ocurrio un error al cargar logs a la interfaz de usuario")
                    null
                }
            }.toMutableList()

            if (list.isNotEmpty()) {
                listLogs.value?.clear()
                listLogs.postValue(list)
            }
        }
    }

    /**
     * Add a log of type REQUEST to the file.
     * @param description Add an error description that is user-friendly or specific.
     * */
    fun addNewLogRequest(description: String) {
        addLog(
            "${R.drawable.ic_request},${Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss")}," +
                "$description,request"
        )
    }

    /**
     * Add a log of type RESPONSE to the file.
     * @param description Add an error description that is user-friendly or specific.
     * */
    fun addNewLogResponse(description: String) {
        addLog(
            "${R.drawable.ic_warning},${Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss")}," +
                    "$description,response"
        )
    }

    /**
     * Add a log of type ERROR to the file.
     * @param description Add an error description that is user-friendly or specific.
     * */
    fun addNewLogError(description: String) {
        addLog(
            "${R.drawable.ic_response},${Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss")}," +
                    "$description,error"
        )
    }

    private fun addLog(log: String) {
        val logs = getLogsFromFile()
        if (logs.size >= featuresFile.maxNumberLogs) logs.removeFirst()
        logs.add(log)
        writeLogsToFile(logs)
    }

    /**
     * Clear all the logs from the file.
     * */
    fun deleteAllLogs() {
        scope.launch(Dispatchers.IO) {
            val logs = mutableListOf<String>()
            writeLogsToFile(logs)
            listLogs.value?.clear()
            listLogs.postValue(mutableListOf())
        }
    }

    private fun getAllLogs(): List<String> {
        return getLogsFromFile()
    }

    private fun getLogsFromFile(): MutableList<String> {
        val logs = mutableListOf<String>()
        try {
            println(":::LOGCAT::: file = ${featuresFile.fileLogsName}")

            weakContext.get()?.openFileInput(featuresFile.fileLogsName).use { stream ->
                BufferedReader(InputStreamReader(stream)).use { reader ->
                    var line = reader.readLine()
                    while (line != null) {
                        logs.add(line)
                        line = reader.readLine()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            //addNewLogError("Ocurrio un error al obtener logs del archivo")
        }
        return logs
    }

    private fun writeLogsToFile(logs: List<String>) {
        try {
            println(":::LOGCAT::: file = ${featuresFile.fileLogsName}")

            weakContext.get()?.openFileOutput(featuresFile.fileLogsName, Context.MODE_PRIVATE).use { stream ->
                BufferedWriter(OutputStreamWriter(stream)).use { writer ->
                    for (log in logs) {
                        writer.write(log)
                        writer.newLine()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            //addNewLogError("Ocurrio un error al escribir logs del archivo")
        }
    }
}