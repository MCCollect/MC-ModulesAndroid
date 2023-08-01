package com.mc.mcmodules.view.logcat.viewModels

import androidx.lifecycle.ViewModel
import com.mc.mcmodules.model.classes.data.DataLogcatInput
import com.mc.mcmodules.view.logcat.datasource.LogsDataSource

class LogcatViewModel(
    private val dataSource: LogsDataSource
): ViewModel() {
    val listLogs = dataSource.listLogs

    fun getAllLogs() = dataSource.allLogs()

    fun cleanLog() = dataSource.deleteAllLogs()

    fun getCustomFileLogs(): DataLogcatInput = dataSource.getCustomFileLogs()
}