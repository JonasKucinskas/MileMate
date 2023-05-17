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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.milemate.database.FireBase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var carMileages: HashMap<Int, String> = HashMap()
var carMileagesSince: HashMap<Int, String> = HashMap()
/**
 * A simple [Fragment] subclass.
 * Use the [DrivingStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrivingStatsFragment : Fragment(), LocationListenerr {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var gpsTracker: GPS
    private lateinit var speedText: TextView
    private lateinit var tripCostText: TextView
    private lateinit var fuelText: TextView
    private lateinit var distanceTraveledText: TextView
    private lateinit var timer: Chronometer
    private var carID = 0
    private var email = "";
    private var tempMileage = 0;
    //private val carMileages: MutableMap<Int, String> = mutableMapOf()
    //private val carMileagesSince: MutableMap<Int, String> = mutableMapOf()




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speedText = view.findViewById(R.id.speedText)
        fuelText = view.findViewById(R.id.fuelText)
        tripCostText = view.findViewById(R.id.KelionesKainaText)
        distanceTraveledText = view.findViewById(R.id.distanceTraveledText)
        timer = view.findViewById(R.id.timer)

        timer.start()
        gpsTracker = GPS(requireContext(), this)
        gpsTracker.startLocationUpdates()



        Firebase.database.setPersistenceEnabled(true)


        var pref = activity?.getSharedPreferences("Preferences", 0); // 0 - for private mode
        email = pref?.getString("loggedInUserEmail", null)!!

        setFragmentResultListener("CarData") { _, bundle ->

            // Getting a selected car id from previous fragment
            val result = bundle.getString("carID")
            carID = result?.toInt()!!

            FireBase().getCarMileage(email, carID){tempMileage = it}
        }
    }

    override fun onDistanceChanged(traveledDistance: Double) {

        if (email != null) {
            FireBase().getUserCars(email){
                val cars = it
                CalculateForOil(cars)
            }
                if (carID != null) {
                    FireBase().setCarMileage(email, carID, tempMileage + traveledDistance.toInt())
                }
        }

        val formattedDistance = String.format("%.2f", traveledDistance)
        var fuelUsed = getFuelConsumption(traveledDistance, 7.0)
        var tripCost = getTripCost(1.5, fuelUsed)
        var tripCostNew = String.format("%.2f", tripCost)
        var fuelUsedNew = String.format("%.2f", fuelUsed)
        fuelText.text = "Sunaudota kuro: $fuelUsedNew"//pajibat iš lempos tą fuel efficiency
        tripCostText.text = "Kelionės kaina: $tripCostNew"
        distanceTraveledText.text = "Nuvažiuotas atstumas: $formattedDistance"
        Log.d("Distance traveled:", "$traveledDistance")

    }

    override fun onSpeedChanged(speed: Double) {
        var speedak = speed.toInt()
        speedText.text = "Greitis: $speedak"
    }

    override fun onLocationChanged(location: Location) {

        Log.d("LocationChanged", "New location received: $location")

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
        //setFragmentResult("CarData", bundleOf("carMileAges" to carMileages.toString()))
        //setFragmentResult("CarData", bundleOf("carMileAgesSince" to carMileagesSince.toString()))

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

        return inflater.inflate(R.layout.fragment_driving_stats, container, false)
    }

    private fun getFuelConsumption(distance: Double, fuelEfficiency: Double) : Double {
        return distance * fuelEfficiency / 100
    }

    private fun getTripCost(fuelPrice: Double, fuelUsed: Double) : Double{
        return fuelPrice * fuelUsed
    }

    /*private fun CalculateForOil(cars : ArrayList<com.example.milemate.database.Car>)
    {

        for (i in 0 until cars.size)
        {
            if(cars[i].id == carID) {
                if (carMileages.contains(carID) == false) {
                    carMileages.put(carID, cars[i].mileage.toString())
                }

                if (carMileagesSince.contains(carID) == false) {
                    carMileagesSince.put(carID, cars[i].mileage.toString())
                } else {
                    Log.d("CARMILEAGES:", "${carMileages.get(carID)}")
                    Log.d("CARMILEAGESINCE:", "${carMileagesSince.get(carID)}")
                    carMileagesSince.replace(carID, cars[i].mileage.toString())
                }
            }
        }
    }*/

    private fun CalculateForOil(cars : ArrayList<com.example.milemate.database.Car>)
    {

        val notificationHelper = NotificationHelper(requireContext())

        for (i in 0 until cars.size)
        {
            if(cars[i].id == carID) {
                if (carMileages.contains(i) == false && cars[i].mileage != 0 ) {
                    carMileages.put(i, cars[i].mileage.toString())
                }

                if (carMileagesSince.contains(i) == false) {
                    carMileagesSince.put(i, cars[i].mileage.toString())
                } else {
                    Log.d("CARMILEAGES:", "${carMileages.get(i)}")
                    Log.d("CARMILEAGESINCE:", "${carMileagesSince.get(i)}")
                    carMileagesSince.replace(i, cars[i].mileage.toString())

                    var vienas = carMileagesSince.get(i)?.toInt()
                    var du = carMileages.get(i)?.toInt()

                    if (vienas != null) {
                        if((vienas - du!!) >= 2) {
                            var carName = cars[i].name
                            notificationHelper.sendNotification("MileMate", "Don't forget to change your oil for your $carName", 99)
                            var new = carMileagesSince.get(i)
                            if (new != null) {
                                carMileages.replace(i, new )
                            }
                        }
                    }
                }
            }
        }

        //OilReminder(cars, carMileages, carMileagesSince)
    }

    private fun OilReminder(cars : ArrayList<com.example.milemate.database.Car>, carMileages : HashMap<Int, String>, carMileagesSince : kotlin.collections.HashMap<Int, String>)
    {
        val notificationHelper = NotificationHelper(requireContext())

        for(i in 0 until carMileages.size)
        {
            var vienas = carMileagesSince.get(i)?.toInt()
            var du = carMileages.get(i)?.toInt()

            if (vienas != null) {

                if((vienas - du!!) >= 2)
                {
                    var carName = cars[i].name
                    notificationHelper.sendNotification("MileMate", "Don't forget to change your oil for your $carName", 99)
                    var new = carMileagesSince.get(i)
                    if (new != null) {
                        carMileages.replace(i, new )
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