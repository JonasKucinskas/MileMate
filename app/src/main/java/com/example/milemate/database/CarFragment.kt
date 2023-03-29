package com.example.milemate.database

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.milemate.R
import com.example.milemate.databinding.FragmentFirstBinding
import java.io.File


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

        setFragmentResultListener("CarData") { _, bundle ->

            // Getting a selected car id from previous fragment
            var result = bundle.getString("carID")
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

                    val imageFolder = getString(R.string.saved_images)
                    val currentCarImagePath = imageFolder + "/" + car.name + ".jpg"

                    if(File(currentCarImagePath).exists()) {
                        var bitmap = BitmapFactory.decodeFile(currentCarImagePath)
                        view.findViewById<ImageButton>(R.id.imageButtonCar).setImageBitmap(bitmap)
                    }

                }
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