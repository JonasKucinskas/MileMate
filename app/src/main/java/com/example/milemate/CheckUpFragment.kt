package com.example.milemate

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numPickerMonth = view.findViewById<NumberPicker>(R.id.numberPickerMonth)
        val numPickerDay = view.findViewById<NumberPicker>(R.id.numberPickerDay)

        val checkUpSaveBtn = view.findViewById<Button>(R.id.CheckUpSaveBtn)
        val datePicker = view.findViewById<DatePicker>(R.id.CheckUpDatePicker)

        numberPickersInit(datePicker, numPickerMonth, numPickerDay)
        updateNumPickerMonthRange(numPickerMonth, numPickerDay)
        updateNumPickerDayRange(numPickerMonth, numPickerDay)

        checkUpSaveBtn.setOnClickListener{

            val reminderDate = Calendar.getInstance()
            val checkUpDate = Calendar.getInstance()

            //birką dėjau ant datų
            //subtract time gap in years from current year
            var years = datePicker.year - floor((numPickerMonth.value * 31 + numPickerDay.value) / 365.0)

            reminderDate.set(years.toInt(), datePicker.month - numPickerMonth.value, datePicker.dayOfMonth - numPickerDay.value)
            checkUpDate.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)

            val reminder = Reminder(reminderDate, checkUpDate)
            val reminderJson = Gson().toJson(reminder)

            writeToJson(reminderJson, "reminder.json")

            Toast.makeText(activity, "Reminder set for ${reminderDate.time}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun numberPickersInit(datePicker: DatePicker, numPickerMonth: NumberPicker, numPickerDay: NumberPicker){

        val calendar = Calendar.getInstance()
        // set calendar time to midnight otherwise days don't round properly
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)

        datePicker.minDate = calendar.timeInMillis+24*60*60*1000//set min available date to tomorrow in datePicker
        //dynamic adjustment of numPickers. This ensures that negative date can't be set for reminder.
        datePicker.setOnDateChangedListener { datePicker, year, month, day ->

            val calendar2 = Calendar.getInstance()
            calendar2.set(year, month, day)

            //calculate day difference
            val msDiff = calendar2.timeInMillis - calendar.timeInMillis
            val dayDiff = TimeUnit.MILLISECONDS.toDays(msDiff)

            //not very accurate, but it will work for now.
            val monthDiff = dayDiff / 31

            //set numPickers to maximum date for reminder

            numPickerMonth.minValue = 0
            numPickerMonth.maxValue = monthDiff.toInt()

            numPickerDay.minValue = 0
            numPickerDay.maxValue = dayDiff.toInt()
        }
    }

    private fun updateNumPickerMonthRange(numPickerMonth: NumberPicker, numPickerDay: NumberPicker){
        numPickerDay.setOnValueChangedListener { numberPicker, changedFromNum, changedToNum ->

            if (changedToNum / 31.0 > 0 && numPickerMonth.maxValue > 0){
                numPickerMonth.maxValue -= ceil(changedToNum / 31.0).toInt()
            }
            else if (changedToNum == 0 && numPickerMonth.maxValue * 31 <= numPickerDay.maxValue){
                numPickerMonth.maxValue++
            }
        }
    }

    private fun updateNumPickerDayRange(numPickerMonth: NumberPicker, numPickerDay: NumberPicker){
        numPickerMonth.setOnValueChangedListener { numberPicker, changedFromNum, changedToNum ->

            if (changedToNum > 0){
                numPickerDay.maxValue -= changedToNum * 31
            }

            if (changedFromNum > 0){
                numPickerDay.maxValue += changedFromNum * 31
            }

        }
    }

    private fun writeToJson(json: String, filename: String){

        val file = File(context?.filesDir.toString() + "/$filename")

        if (file.exists()){
            file.delete()
        }

        val writer = BufferedWriter(FileWriter(context?.filesDir.toString() + "/$filename"))
        writer.write(json)
        writer.close()
    }

}