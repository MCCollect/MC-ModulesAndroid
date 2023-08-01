package com.mc.mcmodules.view.logcat.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mc.mcmodules.databinding.ActivityLogcatBinding
import com.mc.mcmodules.model.classes.data.DataLogcatInput
import com.mc.mcmodules.model.classes.library.CustomAlert
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.view.logcat.datasource.LogsDataSource
import com.mc.mcmodules.view.logcat.viewModels.LogcatViewModel
import com.mc.mcmodules.view.logcat.views.adapters.LogsAdapter

class LogcatActivity : AppCompatActivity() {
    companion object {
        const val DATA_INTENT = "data_logcat"
    }
    private lateinit var binding: ActivityLogcatBinding
    private val viewModel by lazy { LogcatViewModel(LogsDataSource(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recoverDataIntent()
        initBinding()
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
                if (data.maxNumberLogs >= 10) viewModel.setCustomFileLogs(data)
                else throw java.lang.Exception(
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
            setTypeWarning(
                "Atención", "¿Quiere enviar los datos registrados al servidor?",
                "ACEPTAR", "CANCELAR"
            )
            btnLeft.setOnClickListener {
                Toast.makeText(
                    this@LogcatActivity, "Enviando logcat...", Toast.LENGTH_SHORT
                ).show()
                close()
            }
            btnRight.setOnClickListener { close() }
            show()
        }
    }
}