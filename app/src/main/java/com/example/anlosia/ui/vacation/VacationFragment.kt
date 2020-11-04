package com.example.anlosia.ui.vacation

import VacationViewModel
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.anlosia.R
import com.example.anlosia.model.VacationResponse
import com.example.anlosia.ui.list.vacation.ListVacationActivity
import com.example.anlosia.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_vacation.*
import okhttp3.internal.wait
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class VacationFragment : Fragment() {
    private lateinit var vacationViewModel: VacationViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dialog: BottomSheetDialog
    private lateinit var fileAttachment: File
    private lateinit var vacationDialog: BottomSheetDialog
    private lateinit var calendar: Calendar

    override fun onAttach(context: Context) {
        super.onAttach(context)

        vacationViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[VacationViewModel::class.java]

        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        dialog = BottomSheetDialog(requireContext())
        vacationDialog = BottomSheetDialog(requireContext())
        calendar = Calendar.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vacationViewModel.getVacationResponse().observe(requireActivity(), Observer<VacationResponse> {
            it?.let {
                dialog.dismiss()
                val finishedDialog = inflater.inflate(R.layout.dialog_finished_sending_vacation_success, null)
                dialog.setContentView(finishedDialog)
                dialog.show()
            }
        })

        return inflater.inflate(R.layout.fragment_vacation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachment_preview.setImageURI(null)

        tx_start.setOnClickListener {
            tx_start.requestFocus()
        }
        tx_end.setOnClickListener {
            tx_end.requestFocus()
        }

        tx_start.setOnFocusChangeListener(object: View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(hasFocus) {
                    val dialogInflater = layoutInflater.inflate(R.layout.dialog_date_picker, null)
                    dialog.setContentView(dialogInflater)
                    val datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)!!
                    datePicker.minDate = Date().time
                    datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), object: DatePicker.OnDateChangedListener {
                        override fun onDateChanged(
                            view: DatePicker?,
                            year: Int,
                            monthOfYear: Int,
                            dayOfMonth: Int
                        ) {
                            var _month = monthOfYear + 1
                            var month : String
                            if(_month.toString().length == 1) {
                                month = "0${_month}"
                            }
                            else {
                                month = _month.toString()
                            }
                            var _day = dayOfMonth
                            var day: String
                            if(_day.toString().length == 1) {
                                day = "0${_day}"
                            }
                            else {
                                day = _day.toString()
                            }
                            tx_start.setText("$year-$month-$day")
                        }
                    })
                    dialog.show()
                }
            }
        })

        tx_end.setOnFocusChangeListener(object: View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(hasFocus) {
                    val dialogInflater = layoutInflater.inflate(R.layout.dialog_date_picker, null)
                    dialog.setContentView(dialogInflater)
                    val datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)!!
                    datePicker.minDate = Date().time
                    datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), object: DatePicker.OnDateChangedListener {
                        override fun onDateChanged(
                            view: DatePicker?,
                            year: Int,
                            monthOfYear: Int,
                            dayOfMonth: Int
                        ) {
                            var _month = monthOfYear + 1
                            var month : String
                            if(_month.toString().length == 1) {
                                month = "0${_month}"
                            }
                            else {
                                month = _month.toString()
                            }
                            var _day = dayOfMonth
                            var day: String
                            if(_day.toString().length == 1) {
                                day = "0${_day}"
                            }
                            else {
                                day = _day.toString()
                            }
                            tx_end.setText("$year-$month-$day")
                        }
                    })
                    dialog.show()
                }
            }
        })

        btn_attachment.setOnClickListener {
            startActivityForResult(Intent(requireActivity(), VacationCamera::class.java), 200)
        }

        btn_vacation.setOnClickListener {
            val sendingVacationDialog = layoutInflater.inflate(R.layout.dialog_sending_vacation, null)
            dialog.setContentView(sendingVacationDialog)
            dialog.show()

            val id_user = sharedPreferences.getInt("id", 0)
            val id_company = sharedPreferences.getInt("id_company", 0)
            val start_day = tx_start.text.toString()
            val end_day = tx_end.text.toString()
            val selected = vacation_type.checkedRadioButtonId
            val radio: RadioButton? = vacation_type.findViewById(selected)
            val vacation_type = radio?.text.toString().toUpperCase(Locale.ROOT)
            val message = tx_message.text.toString()
            try {
                val attachment = fileAttachment
                vacationViewModel.postVacation(id_user, id_company, start_day, end_day, vacation_type, message, attachment)
            }
            catch(e: UninitializedPropertyAccessException) {
                Toast.makeText(requireContext(), "Anda belum memasang bukti", Toast.LENGTH_SHORT)
            }

        }

        tx_list_vacation.setOnClickListener {
            startActivity(Intent(requireActivity(), ListVacationActivity::class.java))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(dialog.isShowing)
            dialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 200) {
            data?.let {
                attachment_preview.setImageURI(Uri.parse(data.getStringExtra("attachment")))
                fileAttachment = Uri.parse(data.getStringExtra("attachment")).toFile()
            }
        }
    }
}