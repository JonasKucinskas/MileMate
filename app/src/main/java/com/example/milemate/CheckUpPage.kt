package com.example.milemate
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity


class CheckUpPage : AppCompatActivity() {

    private lateinit var datePicker : DatePicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_up_page)

        datePicker = findViewById(R.id.setCheckUpDatePicker)//get datePicker object


    }

    fun openDatePicker(view: View) {


        val calendar = Calendar.getInstance()


        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }



        DatePickerDialog(this@CheckUpPage, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()

        datePicker.minDate = calendar.timeInMillis
    }
}