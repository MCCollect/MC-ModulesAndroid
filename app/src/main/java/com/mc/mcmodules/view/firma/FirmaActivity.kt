package com.mc.mcmodules.view.firma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.ActivityFirmaBinding
import com.mc.mcmodules.model.classes.data.DataFirmaRequest
import com.mc.mcmodules.model.classes.library.SharePreference
import com.mc.mcmodules.view.firmae.FrgAutorizacionE

class FirmaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirmaBinding
    private val preference by lazy { SharePreference.getInstance(this) }
    private var path: String? = null
    private var user: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recoverDataIntent()
        initSetContentView()
        initView()
    }

    private fun initView() {
        val framentTransaction = supportFragmentManager.beginTransaction()
        framentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        framentTransaction.commitAllowingStateLoss()
        framentTransaction.replace(
            binding.contentFragment.id,
            FirmaFragment()
        )
        binding.user.text = user
    }

    private fun initSetContentView() {
        binding = ActivityFirmaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun recoverDataIntent() {
        val intent = intent
        if (intent == null) {
            throw java.lang.Exception("Los datos de la firma son obligatorios (path,usuario)")
        } else checkData(intent)
    }

    private fun checkData(intent: Intent) {
        val data = intent.getParcelableExtra<DataFirmaRequest>("data_firma")
        if (data == null) {
            throw java.lang.Exception("Los datos de la firma son obligatorios (path,usuario)")
        } else {
            if (data.path.isNotEmpty() && data.user.isNotEmpty()) initData(data)
            else java.lang.Exception("Parametros path:${data.path} user:${data.user}")
        }
    }

    private fun initData(data: DataFirmaRequest) {
        preference.saveData("path",data.path)
        path = data.path
        user = data.user
    }

    companion object {
        const val CODIGO_OK_FIRMA_DATA: Int = 303
    }
}