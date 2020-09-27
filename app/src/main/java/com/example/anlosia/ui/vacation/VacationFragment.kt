package com.example.anlosia.ui.vacation

import VacationViewModel
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anlosia.R
import com.example.anlosia.model.VacationResponse
import com.example.anlosia.ui.list.vacation.ListVacationActivity
import com.example.anlosia.util.Util
import kotlinx.android.synthetic.main.fragment_vacation.*
import java.util.*

class VacationFragment : Fragment() {
    private lateinit var vacationViewModel: VacationViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vacationViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[VacationViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        vacationViewModel.getVacationResponse().observe(requireActivity(), Observer<VacationResponse> {
            it?.let {
                Util.logD(it.toString())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vacation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_vacation.setOnClickListener {
            val id_user = sharedPreferences.getInt("id", 0)
            val id_company = sharedPreferences.getInt("id_company", 0)
            val start_day = tx_start.text.toString()
            val end_day = tx_end.text.toString()
            val selected = vacation_type.checkedRadioButtonId
            val radio: RadioButton = vacation_type.findViewById(selected)
            val vacation_type = radio.text.toString().toUpperCase(Locale.ROOT)
            val message = tx_message.text.toString()

            vacationViewModel.postVacation(id_user, id_company, start_day, end_day, vacation_type, message)
        }

        tx_list_vacation.setOnClickListener {
            startActivity(Intent(requireActivity(), ListVacationActivity::class.java))
        }
    }
}