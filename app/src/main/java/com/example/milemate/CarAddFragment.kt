package com.example.milemate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


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

        //Button click event to create car object
        addCarButton.setOnClickListener {
            val carNameContent = view.findViewById<EditText>(R.id.textCarName).text.toString()
            val carBrandContent = view.findViewById<EditText>(R.id.textCarBrand).text.toString()
            val carOdometerContent = view.findViewById<EditText>(R.id.textCarOdometer).text.toString()
            FirstCar = Car(carNameContent, carBrandContent, carOdometerContent)
            Toast.makeText(activity, "Car added successfully!", Toast.LENGTH_SHORT).show()
            //Sends notification
            val notificationHelper = NotificationHelper(requireContext())
            notificationHelper.sendNotification("MileMate", "Car Added successfully!")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }



}