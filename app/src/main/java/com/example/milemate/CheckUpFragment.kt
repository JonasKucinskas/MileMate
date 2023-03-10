package com.example.milemate

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CheckUpPage_DatePicker_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment




        return inflater.inflate(R.layout.fragment_check_up, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CheckUpPage_DatePicker_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CheckUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numPickerYear = view.findViewById<NumberPicker>(R.id.numberPickerYear)
        val numPickerMonth = view.findViewById<NumberPicker>(R.id.numberPickerMonth)
        val numPickerDay = view.findViewById<NumberPicker>(R.id.numberPickerDay)

        numPickerYear.minValue = 0
        numPickerYear.maxValue = 4

        numPickerMonth.minValue = 0
        numPickerMonth.maxValue = 12

        numPickerDay.minValue = 1
        numPickerDay.maxValue = 31

        val checkUpSaveBtn = view.findViewById<Button>(R.id.CheckUpSaveBtn)
        val datePicker = view.findViewById<DatePicker>(R.id.CheckUpDatePicker)

        val calendar = Calendar.getInstance()
        datePicker.minDate = calendar.timeInMillis//set min available date as today in datePicker

        checkUpSaveBtn.setOnClickListener(){

            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)

            //need to convert to negative values for subtraction.
            val years =  -numPickerYear.value
            val months = -numPickerMonth.value
            val days = -numPickerDay.value

            //subtraction from selected date in date picker
            calendar.add(Calendar.YEAR, years)
            calendar.add(Calendar.MONTH, months)
            calendar.add(Calendar.DAY_OF_MONTH, days)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val reminderDate = dateFormat.format(calendar.time)

            Toast.makeText(activity, "Reminder set for $reminderDate", Toast.LENGTH_SHORT).show()
        }
    }
}