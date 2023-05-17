package com.example.milemate.database

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.milemate.NotificationHelper
import com.example.milemate.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
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


    private var carID: Int? = null
    private var selectedDatePicker: String = "2000-1-1"
    private var selectedNumPickerMonth = 0
    private var selectedNumPickerDay = 0
    private var selectedNumPickerMonthMax = 0
    private var selectedNumPickerDayMax = 0



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

        //ReadEverything()

        val selectedCalendar = java.util.Calendar.getInstance()


        val driveButton = view.findViewById<Button>(R.id.DriveButton)
        driveButton.setOnClickListener{//WTF IS THIS BULL SHIT
            findNavController().navigate(R.id.action_carFragment_to_drivingStatsFragment)
        }

        val checkupSetTextView = view.findViewById<TextView>(R.id.textViewExpiryCar)
        var selectedDateYear = 0
        var selectedDateMonth = 0
        var selectedDateDay = 0

        // Database init
        val fbDatabase = FireBase()

        setFragmentResultListener("CarData") { _, bundle ->

            // Getting a selected car id from previous fragment
            val result = bundle.getString("carID")
            carID = result?.toInt()

            var pref = activity?.getSharedPreferences("Preferences", 0); // 0 - for private mode
            var email = pref?.getString("loggedInUserEmail", null)

            if (carID != null && email != null) {
                fbDatabase.getCar(email, carID!!){ car ->
                    view.findViewById<TextView>(R.id.editTextCarName)?.text = car.name
                    view.findViewById<TextView>(R.id.editTextCarModel)?.text = car.brand
                    view.findViewById<TextView>(R.id.editTextNumberDecimal)?.text = car.mileage.toString()

                    val escapedEmail = FireBase().escapeEmail(email)
                    val path = FireBase().dbRef.child(escapedEmail).child("cars").child(carID.toString())

                    path.get().addOnSuccessListener{
                        //Log.e("test", "test: ${it.child(date)}")
                        selectedDatePicker = it.child("datePicker").value.toString()
                        selectedNumPickerMonth = Integer.parseInt(it.child("numPickerMonth").value.toString())
                        selectedNumPickerDay = Integer.parseInt(it.child("numPickerDay").value.toString())
                        selectedNumPickerMonthMax = Integer.parseInt(it.child("numPickerMonthMax").value.toString())
                        selectedNumPickerDayMax = Integer.parseInt(it.child("numPickerDayMax").value.toString())

                        val dateSplit = selectedDatePicker.split("-")
                        selectedDateYear = dateSplit.get(0).toInt()
                        selectedDateMonth = dateSplit.get(1).toInt() - 1
                        val selMonth = selectedDateMonth + 1
                        selectedDateDay = dateSplit.get(2).toInt()

                        selectedCalendar.set(selectedDateYear, selectedDateMonth, selectedDateDay)
                        checkupSetTextView.text = "Expiry date set at: $selMonth/$selectedDateDay"
                    }

                    //if (car.checkupReminder != null){
                    //    checkupSetTextView.text = car.checkupReminder.toString()
                    //}


                    val imageFolder = getString(R.string.saved_images)
                    val currentCarImagePath = imageFolder + "/" + car.name + ".jpg"

                    if(File(currentCarImagePath).exists()) {
                        val bitmap = BitmapFactory.decodeFile(currentCarImagePath)
                        view.findViewById<ImageButton>(R.id.imageButtonCar).setImageBitmap(bitmap)
                    }
                }
            }

            val deleteButton = view.findViewById<Button>(R.id.DeleteButton)
            deleteButton.setOnClickListener{
                val escapedEmail = email?.let { it1 -> fbDatabase.escapeEmail(it1) }
                carID?.let { it1 ->
                    if (escapedEmail != null) {
                        fbDatabase.removeCar(escapedEmail, it1)
                        findNavController().navigate(R.id.action_carFragment_to_FirstFragment)
                    }
                }
            }
        }

        val numPickerMonth = view.findViewById<NumberPicker>(R.id.numberPickerMonth)
        val numPickerDay = view.findViewById<NumberPicker>(R.id.numberPickerDay)

        numPickerMonth.maxValue = selectedNumPickerMonthMax
        numPickerDay.maxValue = selectedNumPickerDayMax
        numPickerMonth.value = selectedNumPickerMonth
        numPickerDay.value = selectedNumPickerDay

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

                var realmonth = month+1
                checkupSetTextView.text = "Expiry date set at: $realmonth/$day"


                val date = calendar2.time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDatePicker = dateFormat.format(date)

                selectedNumPickerDay = numPickerDay.value
                selectedNumPickerDayMax = numPickerDay.maxValue
                selectedNumPickerMonth = numPickerMonth.value
                selectedNumPickerMonthMax = numPickerMonth.maxValue

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


            selectedNumPickerDay = numPickerDay.value
            selectedNumPickerDayMax = numPickerDay.maxValue
            selectedNumPickerMonth = numPickerMonth.value
            selectedNumPickerMonthMax = numPickerMonth.maxValue
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

            selectedNumPickerDay = numPickerDay.value
            selectedNumPickerDayMax = numPickerDay.maxValue
            selectedNumPickerMonth = numPickerMonth.value
            selectedNumPickerMonthMax = numPickerMonth.maxValue
        }

        val CurrentDate = java.util.Calendar.getInstance()
        val ExpiryDate = java.util.Calendar.getInstance()
        val NeededDate = java.util.Calendar.getInstance()
        ExpiryDate.set(selectedDateYear, selectedDateMonth, selectedDateDay)
        NeededDate.set(0, numPickerMonth.value, numPickerDay.value)

        val difference = ExpiryDate.timeInMillis - CurrentDate.timeInMillis
        val daysDifference = TimeUnit.MILLISECONDS.toDays(difference)

        val NeededDateInMilis = NeededDate.timeInMillis

        if (daysDifference <= TimeUnit.MILLISECONDS.toDays(NeededDateInMilis) ){
            val notificationHelper = NotificationHelper(requireContext())
            notificationHelper.sendNotification("MileMate", "Hey! Don’t forget to check up your car!", 5)
        }


        SaveEverything(view)
    }



    override fun onDestroyView() {
        super.onDestroyView()



    }

    private fun ReadEverything()
    {
        /*val file = File(requireActivity().filesDir,"carfragment.json")
        if(file.canRead()) {

            val fileReader = FileReader(file)
            val jsonString = fileReader.readText()
            val jsonObject = JSONObject(jsonString)

            if(jsonObject.has("datePicker$carID")) {
                selectedDatePicker = jsonObject.getString("datePicker$carID")
                Log.e("read", "reading...")
            }
            if(jsonObject.has("numPickerMonth$carID"))
                selectedNumPickerMonth = jsonObject.getInt("numPickerMonth$carID")

            if(jsonObject.has("numPickerDay$carID"))
                selectedNumPickerDay = jsonObject.getInt("numPickerDay$carID")

            if(jsonObject.has("numPickerDayMax$carID"))
                selectedNumPickerDayMax = jsonObject.getInt("numPickerDayMax$carID")

            if(jsonObject.has("numPickerMonthMax$carID"))
                selectedNumPickerMonthMax = jsonObject.getInt("numPickerMonthMax$carID")

        }*/
    }

    private fun SaveEverything(view: View)
    {
        val database = FireBase()
        val saveButton = view.findViewById<Button>(R.id.saveButton)


        saveButton.setOnClickListener(){

            val name = view.findViewById<TextView>(R.id.editTextCarName)?.text.toString()
            val brand = view.findViewById<TextView>(R.id.editTextCarModel)?.text.toString()
            val mileage = view.findViewById<TextView>(R.id.editTextNumberDecimal)?.text.toString()

            val pref = activity?.getSharedPreferences("Preferences", 0); // 0 - for private mode
            val email = pref?.getString("loggedInUserEmail", null)
            val newCar = carID?.let { it1 -> Car(it1, name, brand, Integer.parseInt(mileage), "","") }
            if (email != null && newCar != null) {
                database.updateCar(email, newCar)
            }

            val database = FireBase()

            if (email != null) {
                val escapedEmail = database.escapeEmail(email)
                val path = database.dbRef.child(escapedEmail).child("cars").child(carID.toString())
                path.child("datePicker").setValue(selectedDatePicker)
                path.child("numPickerMonth").setValue(selectedNumPickerMonth)
                path.child("numPickerDay").setValue(selectedNumPickerDay)
                path.child("numPickerMonthMax").setValue(selectedNumPickerMonthMax)
                path.child("numPickerDayMax").setValue(selectedNumPickerDayMax)
            }

            /*val jsonObject = JSONObject()
            jsonObject.put("datePicker$carID", selectedDatePicker)
            jsonObject.put("numPickerMonth$carID", selectedNumPickerMonth)
            jsonObject.put("numPickerDay$carID", selectedNumPickerDay)
            jsonObject.put("numPickerMonthMax$carID", selectedNumPickerMonthMax)
            jsonObject.put("numPickerDayMax$carID", selectedNumPickerDayMax)

            val file = File(requireActivity().filesDir,"carfragment.json")

            if(!file.exists())
                file.createNewFile()

            val fileWriter = FileWriter(file, true)
            fileWriter.write(jsonObject.toString())
            fileWriter.close()*/


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