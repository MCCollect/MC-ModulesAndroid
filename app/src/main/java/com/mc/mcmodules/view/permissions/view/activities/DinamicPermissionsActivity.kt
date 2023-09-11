package com.mc.mcmodules.view.permissions.view.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.mc.mcmodules.databinding.ActivityDinamicPermissionsBinding
import com.mc.mcmodules.model.classes.library.CustomAlert
import com.mc.mcmodules.view.permissions.model.Permission
import com.mc.mcmodules.view.permissions.model.PermissionsResult
import com.mc.mcmodules.view.permissions.model.PermissionsView
import com.mc.mcmodules.view.permissions.view.adapters.AdapterViewPagerDinamicPermissions
import com.mc.mcmodules.view.permissions.view.fragments.DinamicPermissionFragment
import com.mc.mcmodules.view.permissions.view.fragments.PresentationPermissionsFragment
import com.mc.mcmodules.view.permissions.view.fragments.ResumePermissionsFragment
import com.mc.mcmodules.view.permissions.viewmodel.ViewModelDinamicPermissions

/**
 *
 * */
class DinamicPermissionsActivity : AppCompatActivity() {
    private val viewModel by lazy { getViewModel { ViewModelDinamicPermissions(this) } }
    private lateinit var binding: ActivityDinamicPermissionsBinding
    private lateinit var permissionsView: PermissionsView
    private lateinit var adapter: AdapterViewPagerDinamicPermissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permiso aceptado", Toast.LENGTH_SHORT).show()
            viewModel.changeStatusPermission(true)
        } else {
            viewModel.requestPermission.value?.let { permission ->
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.permission)) {
                    Toast.makeText(
                        this,
                        "El permiso es necesario para el correcto funcionamiento de la aplicación.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    CustomAlert(this).apply {
                        val description = "Para el correcto funcionamiento del sistema, nuestra aplicación necesita acceder a tu ${permission.title}. A continuación, te guiaremos paso a paso sobre cómo conceder este permiso en tu dispositivo Android:\n1) Serás redirigido a la información de la aplicación.\n2) Verás una pantalla con información sobre ella. Busca y selecciona la opción 'Permisos'.\n3) Dentro de la sección de permisos, busca '${permission.title}'. Allí, encontrarás la opción para otorgar el permiso. Asegúrate de activar esta opción para permitir que nuestra aplicación acceda a tu ${permission.title}.\n4) Es posible que veas una ventana emergente solicitando confirmación para conceder el permiso. Confirma tu elección tocando 'Permitir' o 'Aceptar'.\n" +
                                "\n" +
                                "¡Listo! Ahora nuestra aplicación tiene acceso a tu ${permission.title} para continuar."
                        setTypeWarning(
                            "Otorgar permisos manualmente",
                            description,
                            "Cancelar", "Iniciar"
                        )
                        btnLeft.setOnClickListener { close() }
                        btnRight.setOnClickListener {
                            goToInfoApp()
                            close()
                        }
                        show()
                    }
                }
                viewModel.changeStatusPermission(true)
            }
        }
    }

    private fun goToInfoApp() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    companion object {
        const val RESULT_PERMISSIONS = "result_permission"
        const val DATA_INTENT = "data_intent_permission"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recoverDataIntent()
        initBinding()
        initData()
        loadDinamicPager()
        initListeners()
        initObservers()
    }

    private fun recoverDataIntent() {
        val intent = intent
        if (intent == null) {
            throw Exception(
                "Los datos de archivo log son obligatorios DataLogcatInput(numberMaxLogs,logFileName)"
            )
        } else checkData(intent)
    }

    private fun checkData(intent: Intent) {
        val data =  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra(DATA_INTENT)
        else intent.getParcelableExtra(DATA_INTENT, PermissionsView::class.java)

        if (data == null) {
            throw Exception("Ingresar permisos es obligatorio PermissionsView(appName,logo,descriptionModule,permissions)")
        } else {
            if (data.permissions.isNotEmpty()) {
                permissionsView = data
            } else throw Exception(
                "Ingresar al menos UN permiso es obligatorio"
            )
        }
    }

    private fun initBinding() {
        binding = ActivityDinamicPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initData() {
        onBackPressedDispatcher.addCallback {
            sendPermissionsStatus()
        }
    }

    /**
     *
     * */
    private fun loadDinamicPager() {
        try {
            val permission = getListDinamicPermissionsFregments()
            adapter = AdapterViewPagerDinamicPermissions(this)
            if (permission.isEmpty()) sendPermissionsStatus()
            if (permission.size > 1) loadPresentationFragment()
            permission.forEach { fragmentPermission -> adapter.addFragment(fragmentPermission) }
            if (permission.size > 1) loadResumeFragment()
            binding.pagerPermissions.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getListDinamicPermissionsFregments(): List<DinamicPermissionFragment> {
        viewModel.listPermissions.clear()
        return permissionsView.permissions.mapNotNull { permission ->
            if (!checkIsGrantedPermission(permission.permission) && checkApiLevel(permission)) {
                viewModel.listPermissions.add(permission)
                DinamicPermissionFragment.newInstanceWithArguments(permission)
            } else null
        }
    }

    private fun checkApiLevel(permission: Permission): Boolean {
        val range = permission.sdkMin .. permission.sdkMax
        return  Build.VERSION.SDK_INT in range
    }

    private fun loadPresentationFragment() {
        viewModel.titleFragment = permissionsView.title
        viewModel.descriptionFragment = permissionsView.descriptionModule
        val fragmentPresentation = PresentationPermissionsFragment()
        adapter.addFragment(fragmentPresentation)
    }

    private fun checkIsGrantedPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED

    private fun loadResumeFragment() {
        val fragmentResume = ResumePermissionsFragment()
        adapter.addFragment(fragmentResume)
    }

    private fun initListeners() {
        binding.buttonBack.setOnClickListener {
            sendPermissionsStatus()
        }
    }

    private inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(crossinline factory: () -> T): T {
        val vmFactory = object : ViewModelProvider.Factory {
            override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
        }
        return ViewModelProvider(this, vmFactory)[T::class.java]
    }

    private fun sendPermissionsStatus() {
        val resultIntent = Intent()
        resultIntent.putExtra(
            RESULT_PERMISSIONS,
            PermissionsResult(
                viewModel.checkAllPermissionsStatus()
            )
        )
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun initObservers() {
        viewModel.goToNextPage.observe(this) { status ->
            if (status) {
                if (viewModel.listPermissions.size > 1) {
                    try {
                        if (binding.pagerPermissions.currentItem < (binding.pagerPermissions.adapter?.itemCount ?: 0)) {
                            val nextPage = binding.pagerPermissions.currentItem + 1
                            binding.pagerPermissions.setCurrentItem(nextPage,true)
                            viewModel.goToNextPage(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    if (viewModel.checkAllPermissionsStatus()) {
                        sendPermissionsStatus()
                    } else Toast.makeText(
                        this,
                        "Por favor, antes de proceder, necesitamos que aceptes el permiso para continuar.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewModel.requestPermission.observe(this) { permission ->
            if (permission != null) requestPermissionLauncher.launch(permission.permission)
        }

        viewModel.goToPagePermission.observe(this) { page ->
            if (viewModel.listPermissions.size > 1) {
                try {
                    val finalPage = page ?: binding.pagerPermissions.currentItem
                    if (binding.pagerPermissions.currentItem < (binding.pagerPermissions.adapter?.itemCount ?: 0)) {
                        binding.pagerPermissions.setCurrentItem(finalPage, true)
                        viewModel.goToPagePermission(null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

fun Context.isPortraitMode(): Boolean {
    val orientation = this.resources.configuration.orientation
    return orientation == Configuration.ORIENTATION_PORTRAIT
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}