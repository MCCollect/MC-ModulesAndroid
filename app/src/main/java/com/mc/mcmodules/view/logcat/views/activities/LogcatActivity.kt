package com.mc.mcmodules.view.logcat.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.mc.mcmodules.databinding.ActivityLogcatBinding
import com.mc.mcmodules.model.classes.data.DataLogcatInput
import com.mc.mcmodules.model.classes.data.DataLogcatOutput
import com.mc.mcmodules.model.classes.library.CustomAlert
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.view.ActTest
import com.mc.mcmodules.view.logcat.datasource.LogsDataSource
import com.mc.mcmodules.view.logcat.viewModels.LogcatViewModel
import com.mc.mcmodules.view.logcat.views.adapters.LogsAdapter

/**
 * User interface where the user can view all the logs previously registered in their application.
 * Using MVVM as the design pattern and architecture.
 *
 * The logcat module has the following functionalities:
 * 1. Display the current logs limited to the number defined by the user using the property
 *    DataLogcatInput.maxNumberLogs or the default maximum number of logs, which is 80.
 *    The log file name is also defined using the property DataLogcatInput.logFileName.
 * 2. Refresh the list of logs.
 * 3. Clear the entire list of logs.
 * 4. Optionally, send the logs for support purposes. The visibility of this functionality can be
 *    customized using DataLogcatInput.isVisibleSend in case it is not necessary. This functionality is
 *    triggered by an event generated from the activity in which the logcat is launched.
 *
 * @property DATA_INTENT Key for receiving the Logcat customization data.
 * @property RESULT_DATA_INTENT Key for sending the Logcat data (log file name, list of logs).
 * @property viewModel Intermediary for DataSource modifications to the Log file.
 * @property binding Use of ViewBinding for quick reference to the views.
 *
 * @sample ActTest.logcatLauncher
 * @sample ActTest.testLogcat
 * */
class LogcatActivity : AppCompatActivity() {
    companion object {
        const val DATA_INTENT = "data_logcat"
        const val RESULT_DATA_INTENT = "result_data_logcat"
    }
    private lateinit var binding: ActivityLogcatBinding
    private lateinit var viewModel: LogcatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        recoverDataIntent()
        initObjects()
        initObservers()
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.freeMemory()
    }

    private fun recoverDataIntent() {
        val intent = intent
        if (intent == null) {
            throw java.lang.Exception(
                "Los datos de archivo log son obligatorios DataLogcatInput(numberMaxLogs,logFileName)"
            )
        } else checkData(intent)
    }

    private fun checkData(intent: Intent) {
        val data = intent.getParcelableExtra<DataLogcatInput>(DATA_INTENT)
        if (data == null) {
            throw java.lang.Exception("Los datos de la firma son obligatorios (path,usuario)")
        } else {
            val regex = "^[^.]+\\.txt$".toRegex()
            if (data.fileLogsName.matches(regex)) {
                if (data.maxNumberLogs >= 10) {
                    if (data.isVisibleSend) binding.buttonSend.visibility = View.VISIBLE
                    else binding.buttonSend.visibility = View.GONE
                    viewModel = getViewModel {
                        LogcatViewModel( LogsDataSource(
                            this, DataLogcatInput("log.txt", 10, true)
                        ))
                    }
                } else throw java.lang.Exception(
                    "Parametros fileLogsName:${data.fileLogsName} user:${data.maxNumberLogs}. " +
                            "El numero minimo de logs por almacenar es 10"
                )
            } else throw java.lang.Exception(
                "Parametros fileLogsName:${data.fileLogsName} user:${data.maxNumberLogs}. " +
                        "El nombre del archivo no cumnple cumple con el formato requerido nombre.txt"
            )
        }
    }

    private fun initBinding() {
        binding = ActivityLogcatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initObjects() {
        initRecyclers()
        viewModel.getAllLogs()
    }

    private fun initRecyclers() {
        val adapter = LogsAdapter(mutableListOf())
        binding.recyclerLogs.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        viewModel.listLogs.observe(this) { data ->
            (binding.recyclerLogs.adapter as LogsAdapter).populateData(data)
        }
    }

    private fun initListeners() {
        with(binding) {
            mcLogo.setOnClickListener { showLogcatConfig() }
            buttonClean.setOnClickListener { deleteAllLogs() }
            buttonRefresh.setOnClickListener { refreshLogs() }
            buttonSend.setOnClickListener {sendLogsServer()}
            buttonExit.setOnClickListener { finish() }
        }
    }

    private fun showLogcatConfig() {
        val data = viewModel.getCustomFileLogs()
        Toast.makeText(
            this, "Nombre del archivo: ${data.fileLogsName}, " +
                    "Numero maximo de logs a almacenar ${data.maxNumberLogs}", Toast.LENGTH_LONG
        ).show()
    }

    private fun deleteAllLogs() {
        CustomAlert(this).apply {
            setTypeWarning(
            "Atención", "¿Estás seguro de que quieres eliminar todos los registros?",
            "ACEPTAR", "CANCELAR"
            )
            btnLeft.setOnClickListener {
                viewModel.cleanLog()
                Toast.makeText(
                    this@LogcatActivity,
                    "Se eliminaron todos los logs registrados",
                    Toast.LENGTH_SHORT
                ).show()
                close()
            }
            btnRight.setOnClickListener { close() }
            show()
        }
    }

    private fun refreshLogs() {
        viewModel.getAllLogs()
        Toast.makeText(this, "Se actualizarón los logs registrados", Toast.LENGTH_SHORT).show()
    }

    private fun sendLogsServer() {
        CustomAlert(this).apply {
            if (!viewModel.listLogs.value.isNullOrEmpty()) {
                setTypeWarning(
                    "Atención", "¿Quiere enviar los datos registrados al servidor?",
                    "ACEPTAR", "CANCELAR"
                )
                btnLeft.setOnClickListener {
                    val resultIntent = Intent()
                    resultIntent.putExtra(
                        RESULT_DATA_INTENT,
                        DataLogcatOutput(
                            viewModel.getCustomFileLogs().fileLogsName,
                            viewModel.listLogs.value ?: emptyList()
                        )
                    )
                    setResult(RESULT_OK, resultIntent)
                    viewModel.cleanLog()
                    finish()
                    close()
                }
                btnRight.setOnClickListener { close() }
                show()
            } else {
                setTypeWarning(
                    "Atención", "No hay datos por enviar",
                    "ACEPTAR"
                )
                btnLeft.setOnClickListener { close() }
                show()
            }
        }
    }

    inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(crossinline factory: () -> T): T {
        val vmFactory = object : ViewModelProvider.Factory {
            override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
        }
        return ViewModelProvider(this, vmFactory)[T::class.java]
    }
}