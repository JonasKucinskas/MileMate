package com.example.milemate

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.Switch
import org.json.JSONObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserSettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserSettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    var selectedSpinnerItem: String? = null //Regionas
    var selectedLicenseDate: String = "2000-1-1"
    var regionSwitchBool: Boolean = false
    var licenseSwitchBool: Boolean = false

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
        return inflater.inflate(R.layout.fragment_user_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = File(requireActivity().filesDir,"usersettings.json")
        if(file.canRead()) {
            val fileReader = FileReader(file)
            val jsonString = fileReader.readText()
            val jsonObject = JSONObject(jsonString)

            regionSwitchBool = jsonObject.getBoolean("regionSwitch")
            licenseSwitchBool = jsonObject.getBoolean("licenseSwitch")

            if(jsonObject.has("licenseDate"))
            selectedLicenseDate = jsonObject.getString("licenseDate")

            if(jsonObject.has("userRegion"))
            selectedSpinnerItem = jsonObject.getString("userRegion")

        }


        RegionSelectFun(view)
        LicenceExpiryFun(view)

    }

    private fun RegionSelectFun(view: View)
    {
        val regions = resources.getStringArray(R.array.array_regions)
        val regionSwitch = view.findViewById<Switch>(R.id.switchResidenceUsrSet)

        regionSwitch.isChecked = regionSwitchBool

            //if (regionSwitch.isChecked) {

                val spinner = view.findViewById<Spinner>(R.id.spinnerRegionUsrSet)

                if (spinner != null) {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, regions)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter


                    for ((index, value) in regions.withIndex()) {
                        if (value == selectedSpinnerItem) {
                            spinner.setSelection(index)
                            break
                        }
                    }

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,
                                                    view: View, position: Int, id: Long) {

                            selectedSpinnerItem = parent?.getItemAtPosition(position).toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }

                    }
                }
        //}

        regionSwitch.setOnCheckedChangeListener { _, _ ->
            regionSwitchBool = regionSwitch.isChecked
        }

    }

    private fun LicenceExpiryFun(view: View)
    {

        val licenseSwitch = view.findViewById<Switch>(R.id.switchLicenseUsrSet)

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val calendar = Calendar.getInstance()

        var dateSplit = selectedLicenseDate.split("-")
        var year = dateSplit.get(0).toInt()
        var month = dateSplit.get(1).toInt() - 1
        var day = dateSplit.get(2).toInt()

        calendar.set(year, month, day)
        calendarView.setDate(calendar.timeInMillis)
        licenseSwitch.isChecked = licenseSwitchBool

            if (licenseSwitch.isChecked) {

                calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

                    calendar.set(year, month, dayOfMonth)
                    calendarView.setDate(calendar.timeInMillis)

                    val date = calendar.time
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectedLicenseDate = dateFormat.format(date)
                }
            }

        licenseSwitch.setOnCheckedChangeListener { _, _ ->
            licenseSwitchBool = licenseSwitch.isChecked
        }

        val CurrentDate = Calendar.getInstance()
        val NeededDate = Calendar.getInstance()
        NeededDate.set(year, month, day)

        val difference = NeededDate.timeInMillis - CurrentDate.timeInMillis
        val daysDifference = TimeUnit.MILLISECONDS.toDays(difference)

        if (daysDifference < 7 ){
            val notificationHelper = NotificationHelper(requireContext())
            notificationHelper.sendNotification("MileMate", "Hey! Don’t forget to renew your drivers license! It expires in a week!", 4)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        val jsonObject = JSONObject()
        jsonObject.put("userRegion", selectedSpinnerItem)
        jsonObject.put("licenseDate", selectedLicenseDate)
        jsonObject.put("licenseSwitch", licenseSwitchBool)
        jsonObject.put("regionSwitch", regionSwitchBool)

        val file = File(requireActivity().filesDir,"usersettings.json")

        if(!file.exists())
        file.createNewFile()

        val fileWriter = FileWriter(file, false)
        fileWriter.write(jsonObject.toString())
        fileWriter.close()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserSettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserSettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}