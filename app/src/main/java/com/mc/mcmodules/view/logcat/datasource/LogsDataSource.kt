package com.mc.mcmodules.view.logcat.datasource

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.mc.mcmodules.R
import com.mc.mcmodules.model.classes.data.DataLogcatInput
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.view.logcat.models.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.ref.WeakReference

class LogsDataSource(context: Context) {
    private val scope by lazy { CoroutineScope(SupervisorJob()) }
    private val weakContext = WeakReference(context)
    val listLogs: MutableLiveData<MutableList<Log>> = MutableLiveData(mutableListOf())

    private var logFile = "log.txt"
    private var maxLogs = 80

    fun setCustomFileLogs(data: DataLogcatInput) {
        logFile = data.fileLogsName
        maxLogs = data.maxNumberLogs
    }

    fun getCustomFileLogs(): DataLogcatInput {
        return DataLogcatInput(logFile, maxLogs)
    }

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

    fun addNewLogRequest(description: String) {
        addLog(
            "${R.drawable.ic_warning},${Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss")}," +
                "$description,request"
        )
    }

    fun addNewLogResponse(description: String) {
        addLog(
            "${R.drawable.ic_warning},${Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss")}," +
                    "$description,response"
        )
    }

    fun addNewLogError(description: String) {
        addLog(
            "${R.drawable.ic_warning},${Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss")}," +
                    "$description,error"
        )
    }

    private fun addLog(log: String) {
        val logs = getLogsFromFile()
        if (logs.size >= maxLogs) logs.removeFirst()
        logs.add(log)
        writeLogsToFile(logs)
    }

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
            println(":::LOGCAT::: file = $logFile")

            weakContext.get()?.openFileInput(logFile).use { stream ->
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
            println(":::LOGCAT::: file = $logFile")

            weakContext.get()?.openFileOutput(logFile, Context.MODE_PRIVATE).use { stream ->
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