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
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

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
    var selectedLicenseDate: String? = null
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

        val regions = resources.getStringArray(R.array.array_regions)


        val spinner = view.findViewById<Spinner>(R.id.spinnerRegionUsrSet)
        if (spinner != null) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, regions)


            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {

                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }


        val residenceSwitch = view.findViewById<Switch>(R.id.switchResidenceUsrSet)
        val licenseSwitch = view.findViewById<Switch>(R.id.switchLicenseUsrSet)

        residenceSwitch.setOnCheckedChangeListener { _, _ ->
            if (residenceSwitch.isChecked) {
                val spinner = view.findViewById<Spinner>(R.id.spinnerRegionUsrSet)
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedSpinnerItem = parent?.getItemAtPosition(position).toString()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        //Nieko nevyksta
                    }
                }
            } else {
                // Nieko nevyksta
            }
        }

        licenseSwitch.setOnCheckedChangeListener { _, _ ->
            if (licenseSwitch.isChecked) {
                val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
                calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    val date = calendar.time
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectedLicenseDate = dateFormat.format(date)
                }
            } else {
                // Nieko nevyksta
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

            val jsonObject = JSONObject()
            jsonObject.put("userRegion", selectedSpinnerItem)
            jsonObject.put("licenseDate", selectedLicenseDate)
            val file = File(requireActivity().filesDir, "usersettings.json")
            val fileWriter = FileWriter(file)
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