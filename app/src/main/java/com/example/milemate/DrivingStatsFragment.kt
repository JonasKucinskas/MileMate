package com.example.milemate

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TextView
import androidx.fragment.app.Fragment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DrivingStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrivingStatsFragment : Fragment(), LocationListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var gpsTracker: GPS
    private lateinit var speedText: TextView
    private lateinit var tripCostText: TextView
    private lateinit var fuelText: TextView
    private lateinit var timer: Chronometer


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speedText = view.findViewById(R.id.speedText)
        fuelText = view.findViewById(R.id.fuelText)
        tripCostText = view.findViewById(R.id.KelionesKainaText)
        timer = view.findViewById(R.id.timer)

        timer.start()
    }

    override fun onLocationChanged(p0: Location) {

        //Toast.makeText(requireContext(), "Vieta: $location", Toast.LENGTH_LONG).show()

        val longitude = gpsTracker.getLongitude()
        val latitude = gpsTracker.getLatitude()

        val traveledDistance = gpsTracker.getTraveledDistance(latitude, longitude)
        val fuelUsed = getFuelConsumption(traveledDistance, 7.0)
        val speed = gpsTracker.getSpeed()
        val tripCost = getTripCost(1.5, fuelUsed)
        fuelText.text = "Sunaudota kuro: $fuelUsed"//pajibat iš lempos tą fuel efficiency
        speedText.text = "Greitis: $speed"
        tripCostText.text = "Kelionės kaina: $tripCost"

        Log.d("Distance traveled:", "${gpsTracker.getTraveledDistance(latitude, longitude)}")
    }

    override fun onStart() {
        super.onStart()
        //Gonna update the gps as long as we're in this fragment
        gpsTracker.startLocationUpdates()
    }



    override fun onStop() {
        super.onStop()
        //stopping updating gps when we leave fragment
        gpsTracker.stopLocationUpdates()
    }

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
        gpsTracker = GPS(requireContext(), this)

        return inflater.inflate(R.layout.fragment_driving_stats, container, false)
    }

    private fun getFuelConsumption(distance: Double, fuelEfficiency: Double) : Double {
        return distance / fuelEfficiency
    }

    private fun getTripCost(fuelPrice: Double, fuelUsed: Double) : Double{
        return fuelPrice * fuelUsed
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DrivingStatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DrivingStatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}