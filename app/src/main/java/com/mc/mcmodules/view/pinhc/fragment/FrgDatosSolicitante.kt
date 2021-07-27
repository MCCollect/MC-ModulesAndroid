package com.mc.mcmodules.view.pinhc.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.badoualy.stepperindicator.StepperIndicator
import com.google.android.material.textfield.TextInputLayout
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.FrgDatosSolicitanteBinding
import com.mc.mcmodules.model.classes.DataInfoSolicitante
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.viewmodel.pinhc.DatosSolicitanteViewmodel
import com.tapadoo.alerter.Alerter
import java.util.*
import kotlin.collections.ArrayList

class FrgDatosSolicitante(
    private val viewPager: ViewPager2,
    private val stepsView: StepperIndicator,
    private val dataInfoSolicitante: DataInfoSolicitante
) : Fragment() {

    private lateinit var selfViewModel: DatosSolicitanteViewmodel
    private lateinit var binding: FrgDatosSolicitanteBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return initContentView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
        setDataSolicitante()
    }


    private fun initContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        selfViewModel = ViewModelProvider(this).get(DatosSolicitanteViewmodel::class.java)
        selfViewModel.setInstanceCompanionViewModel(selfViewModel)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.frg_datos_solicitante, container, false)
        binding.lifecycleOwner = this
        binding.mainView = this
        binding.viewModel = selfViewModel

        return binding.root
    }

    private fun initObservers() {

        selfViewModel.liveDatosSolicitante.observe(requireActivity(), { datosSolicitante ->

            datosSolicitante.LOCK_FIELDS.forEachIndexed { index, lock ->


                with(binding) {
                    when (index) {
                        0 -> inNombre.isEnabled = lock
                        1 -> inPaterno.isEnabled = lock
                        2 -> inMaterno.isEnabled = lock
                        3 -> inSexo.isEnabled = lock
                        4 -> inEstadoCivil.isEnabled = lock
                        5 -> inCURP.isEnabled = lock
                        6 -> btnCaptureFecha.isEnabled = lock

                    }
                }


            }

        })
    }


    private fun initListeners() {
        with(binding) {
            next.setOnClickListener {

                if (validateFields()) {
                    viewPager.setCurrentItem(1, true)
                    it.postDelayed({
                        stepsView.currentStep = 1
                    }, 300)


                } else {
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)?.let { it1 ->
                        Alerter.create(requireActivity())
                            .setTitle("Campos sin llenar")
                            .setText("Rellena todos los campos por favor")
                            .setIcon(it1)
                            .setBackgroundColorRes(R.color.error)
                            .setDuration(2500)
                            .show()
                    }
                }
            }

            btnCaptureFecha.setOnClickListener {
                DatePickerFecha().setField(inFechaNac)

                val picker = DatePickerFecha()
                picker.setField(inFechaNac)
                picker.show(requireActivity().supportFragmentManager, "datePicker")


            }


        }

    }

    private fun setDataSolicitante() {
        selfViewModel.setDataSolicitante(dataInfoSolicitante)
    }

    private fun validateFields(): Boolean {

        with(binding) {


            return Utils.checkField(inNombre) &&
                    Utils.checkField(inPaterno) &&
                    Utils.checkField(inMaterno) &&
                    Utils.checkField(inSexo) &&
                    Utils.checkField(inEstadoCivil) && Utils.checkField(inCURP) &&
                    Utils.checkField(inFechaNac)

        }

    }

    class DatePickerFecha : DialogFragment(),
        OnDateSetListener {

        private var year = 0
        private var month = 0
        private var day = 0
        private lateinit var input: TextInputLayout

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            year = c[Calendar.YEAR] - 18
            month = c[Calendar.MONTH]
            day = c[Calendar.DAY_OF_MONTH]
            c[year, month] = day

            val dialog = DatePickerDialog(requireContext(), this, year, month, day)
            dialog.datePicker.maxDate = c.timeInMillis
            return dialog
        }

        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {


            input.editText?.setText(
                String.format(
                    "%02d",
                    day
                ) + "/" + String.format("%02d", month + 1) + "/" + year
            )
            input.error = null
        }

        fun setField(inputLayout: TextInputLayout) {
            this.input = inputLayout
        }
    }

    fun lockFields(fields: ArrayList<Boolean>) {

    }


}