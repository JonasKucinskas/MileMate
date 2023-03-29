package com.example.milemate.database

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import com.example.milemate.R

private const val ARG_CAR_ID = "arg_car_id"

/**
 * A simple [Fragment] subclass.
 * Use the [CarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CarFragment : Fragment() {
    private var arg_car_id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arg_car_id = it.getInt(ARG_CAR_ID)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_car, container, false)



        view.findViewById<TextView>(R.id.textViewDaysCar)?.text = arg_car_id.toString()
        return view;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment CarFragment.
         */
        @JvmStatic
        fun newInstance(arg_car_id: Int) =
            CarFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CAR_ID, arg_car_id)
                }
            }
    }
}