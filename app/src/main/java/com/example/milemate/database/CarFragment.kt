package com.example.milemate.database

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.milemate.R
import com.example.milemate.databinding.FragmentFirstBinding
import java.io.File
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding : FragmentFirstBinding

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
        view?.findViewById<TextView>(R.id.textViewDaysCar)?.text = ARG_PARAM2

        return inflater.inflate(R.layout.fragment_car, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkupSetTextView = view.findViewById<TextView>(R.id.textViewExpiryCar)


        setFragmentResultListener("CarData") { _, bundle ->

            // Getting a selected car id from previous fragment
            val result = bundle.getString("carID")
            val carID = result?.toInt()

            // Database init
            val database = ViewModelProvider(this)[DBManager::class.java]

            if (carID != null) {
                // Getting car from database by its ID
                database.getCar(carID).observe(viewLifecycleOwner)
                { car ->
                    view.findViewById<TextView>(R.id.editTextCarName)?.text = car.name
                    view.findViewById<TextView>(R.id.editTextCarModel)?.text = car.brand
                    view.findViewById<TextView>(R.id.editTextNumberDecimal)?.text = car.mileage.toString()


                    if (car.checkupReminder != null){
                        checkupSetTextView.text = car.checkupReminder.toString()
                    }


                    val imageFolder = getString(R.string.saved_images)
                    val currentCarImagePath = imageFolder + "/" + car.name + ".jpg"

                    if(File(currentCarImagePath).exists()) {
                        val bitmap = BitmapFactory.decodeFile(currentCarImagePath)
                        view.findViewById<ImageButton>(R.id.imageButtonCar).setImageBitmap(bitmap)
                    }
                }
            }
        }

        val numPickerMonth = view.findViewById<NumberPicker>(R.id.numberPickerMonth)
        val numPickerDay = view.findViewById<NumberPicker>(R.id.numberPickerDay)

        var dayDiff = 0
        var monthDiff = 0

        //NOTE: I always assume that all months have 31 days, so sometimes this is inaccurate:
        checkupSetTextView.setOnClickListener{

            val calendar = Calendar.getInstance()
            //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)

            val datePickerDialog = DatePickerDialog(requireContext())
            datePickerDialog.datePicker.minDate = calendar.timeInMillis+24*60*60*1000//set min available date to tomorrow in datePicker
            datePickerDialog.show()

            //if date is changed in datepicker:
            datePickerDialog.datePicker.setOnDateChangedListener { _, year, month, day ->

                val calendar2 = Calendar.getInstance()
                calendar2.set(year, month, day)

                //calculate day difference
                val msDiff = calendar2.timeInMillis - calendar.timeInMillis
                dayDiff = TimeUnit.MILLISECONDS.toDays(msDiff).toInt()

                //not very accurate, but it will work for now.
                monthDiff = dayDiff / 31

                //set numPickers to maximum date for reminder

                numPickerMonth.minValue = 0
                numPickerMonth.maxValue = monthDiff

                numPickerDay.minValue = 0
                numPickerDay.maxValue = dayDiff

                checkupSetTextView.text = "set for $month th month and $day day"
            }
        }

        numPickerMonth.setOnValueChangedListener { _, changedFromNum, changedToNum ->
            if (changedFromNum - changedToNum == -1){//value increased by 1
                numPickerDay.maxValue -= 31

            }
            else if (changedFromNum - changedToNum == 1){//value decreased by one
                numPickerDay.maxValue += 31
            }
            else if (changedFromNum == 0 && changedToNum == monthDiff){//value changed from 0 to maxMonth value
                numPickerDay.maxValue = dayDiff - changedToNum * 31
            }
            else if (changedFromNum == monthDiff && changedToNum == 0) {// changed from maxMonth to 0
                numPickerDay.maxValue = dayDiff
            }
        }

        numPickerDay.setOnValueChangedListener { _, changedFromNum, changedToNum ->
            //if changedToNum is dividable by 31 (end of month) and number increased - subtract 1 from month max value
            if(changedToNum % 31 == 0 && changedToNum != 0){
                if (changedFromNum - changedToNum < 0){
                    numPickerMonth.maxValue--
                }
            }
            //if changedToNum is dividable by 31 (end of month) and number decreased - add 1 to month max value
            else if(changedFromNum % 31 == 0 && changedFromNum != 0){
                if (changedFromNum - changedToNum > 0){
                    numPickerMonth.maxValue++
                }
            }
            //if num changed from 0 to maxvalue, set month max to 0
            else if (changedFromNum == 0 && changedToNum == dayDiff){
                numPickerMonth.maxValue = 0
            }
            //if num changed from maxvalue to 0, set month max to dayDiff
            else if (changedFromNum == dayDiff && changedToNum == 0){
                numPickerMonth.maxValue = dayDiff / 31
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}