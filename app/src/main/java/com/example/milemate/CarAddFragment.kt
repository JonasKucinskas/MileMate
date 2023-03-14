package com.example.milemate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.milemate.database.DBManager


class CarAddFragment : Fragment() {

    private var FirstCar = Car("", "", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //getting reference to the variable
        val addCarButton = view.findViewById<Button>(R.id.buttonAddCar)

        //Creating spinner for choosing countries
        val countryOptions = arrayOf("Choose your country", "Lithuania")
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, countryOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        spinner.adapter = adapter
        spinner.setSelection(0)

        //Button click event to create car object
        addCarButton.setOnClickListener {
            val carNameContent = view.findViewById<EditText>(R.id.textCarName).text.toString()
            val carBrandContent = view.findViewById<EditText>(R.id.textCarBrand).text.toString()
            val carMileageContent = view.findViewById<EditText>(R.id.numberCarMileage).text.toString()
            FirstCar = Car(carNameContent, carBrandContent, carMileageContent)
            Toast.makeText(activity, "Car added successfully!", Toast.LENGTH_SHORT).show()
            //Sends notification
            val notificationHelper = NotificationHelper(requireContext())
            notificationHelper.sendNotification("MileMate", "Car Added successfully!", 0)

            // Insert into database
            val database = ViewModelProvider(this).get(DBManager::class.java)
            database.insertCar(FirstCar)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}