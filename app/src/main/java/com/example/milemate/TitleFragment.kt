package com.example.milemate

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.milemate.database.DBManager
import com.example.milemate.database.FireBase
import com.example.milemate.databinding.FragmentFirstBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TitleFragment : Fragment() {


    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    private var _binding: FragmentFirstBinding? = null

    private var carsCount = 0;
    private var oldMileage = ArrayList<Int>()
    private var shouldRemind = false

   // public val carMileages: MutableMap<Int, String> = mutableMapOf()
    //public val carMileagesSince: MutableMap<Int, String> = mutableMapOf()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        PermissionFunction()

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize gps here

        val navGraphActivity = activity as MainActivity

        val carAddButton = binding.root.findViewById<Button>(R.id.add_CarBtn)
        carAddButton.setOnClickListener{
            navGraphActivity.navController.navigate(R.id.action_FirstFragment_to_CarAddFragment)
        }

        //reminderChecker()
        TyresReminder()

        Firebase.database.setPersistenceEnabled(true)

        val viewModel = ViewModelProvider(this).get(DBManager::class.java)
        val noCarsTextview = view.findViewById<TextView>(R.id.textview_first)

        var pref = activity?.getSharedPreferences("Preferences", 0); // 0 - for private mode
        var email = pref?.getString("loggedInUserEmail", null)
        if (email != null) {
            FireBase().getUserCars(email){
                val listView: ListView = view.findViewById(R.id.carlist)
                val cars = it
               // OilReminder(cars, carMileages, carMileagesSince)
                carsCount = cars.count()
                listView.adapter = ListViewAdapter(context as Activity, cars)
                listView.setOnItemClickListener { parent, view, position, id ->
                    setFragmentResult("CarData", bundleOf("carID" to cars[position].id.toString()))
                    navGraphActivity.navController.navigate(R.id.carFragment)
                }

                if(cars.isNotEmpty()){
                    noCarsTextview.isVisible = false
                }
            }
        }



        /*viewModel.getAllCars().observe(viewLifecycleOwner)
        { cars ->
            // Cia accessint whatever car data

            val listView: ListView = view.findViewById(R.id.carlist)
            listView.adapter = ListViewAdapter(context as Activity, cars)
            listView.setOnItemClickListener { parent, view, position, id ->
                setFragmentResult("CarData", bundleOf("carID" to cars[position].id.toString()))
                navGraphActivity.navController.navigate(R.id.carFragment)
            }

            if(cars.isNotEmpty()){
                noCarsTextview.isVisible = false
            }

        }*/
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Rewrite this shit cleaner later and add more countries dates depending on law
    private fun TyresReminder(){

        //Temp method for sending notification 1 month and 14days before changing tires
        val todayDate = Calendar.getInstance()
        if ((todayDate.get(Calendar.MONTH) + 1 == 3 && todayDate.get(Calendar.DAY_OF_MONTH) == 1) || (todayDate.get(Calendar.MONTH) + 1 == 3 && todayDate.get(Calendar.DAY_OF_MONTH) == 14) ||
            (todayDate.get(Calendar.MONTH) + 1 == 4 && todayDate.get(Calendar.DAY_OF_MONTH) == 1)){
            val notificationHelper = NotificationHelper(requireContext())
            notificationHelper.sendNotification("MileMate", "Don't forget that you need to change tires from winter to summer until April 10th", 2)
        }
        //Temp method
        if ((todayDate.get(Calendar.MONTH) + 1 == 10 && todayDate.get(Calendar.DAY_OF_MONTH) == 1) || (todayDate.get(Calendar.MONTH) + 1 == 10 && todayDate.get(Calendar.DAY_OF_MONTH) == 14) ||
            (todayDate.get(Calendar.MONTH) + 1 == 11 && todayDate.get(Calendar.DAY_OF_MONTH) == 1)){
            val notificationHelper = NotificationHelper(requireContext())
            notificationHelper.sendNotification("MileMate", "Don't forget that you need to change tires from summer to winter until November 10th", 3)
        }

    }

    /*private fun OilReminder(cars : ArrayList<com.example.milemate.database.Car>, carMileages : HashMap<Int, String>, carMileagesSince : kotlin.collections.HashMap<Int, String>)
    {
        val notificationHelper = NotificationHelper(requireContext())

        for(i in 0 until carMileages.size)
        {
            var vienas = carMileagesSince.get(cars[i].id)?.toInt()
            var du = carMileages.get(cars[i].id)?.toInt()

            if (vienas != null) {

                if((vienas - du!!) >= 2)
                {
                    var carName = cars[i].name
                    notificationHelper.sendNotification("MileMate", "Don't forget to change your oil for your $carName", 99)
                    var new = carMileagesSince.get(cars[i].id)
                    if (new != null) {
                        carMileages.replace(cars[i].id, new )
                    }
                }

            }
        }
    }*/

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

    private fun PermissionFunction()
    {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            // Permission is not granted, request it
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        // Check if the notification permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            // Permission is not granted, request it
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }




    /*
    private fun reminderChecker(){//check if reminder file exists, if yes, check date, if date is today, send notification.
        val file = File(context?.filesDir.toString() + "/reminder.json")

        //read json object, check if reminder date is today, if yes, send notification.
        if (file.exists()){
            val inputStream = file.readLines()
            val jsonObject = JsonParser.parseString(inputStream[0]).asJsonObject
            val reminder = Gson().fromJson(jsonObject, Reminder::class.java)

            //send notification if reminder date is today
            if (DateUtils.isToday(reminder.reminderDate.time)){
                val notificationHelper = NotificationHelper(requireContext())
                notificationHelper.sendNotification("Reminder", "You have check-up in ${reminder.checkUpDate}", 1)
            }
            //send notification 1 day before checkupDate
            else if (DateUtils.isToday(reminder.checkUpDate.time - 86400000)){//86400000 = 1 day in ms
                val notificationHelper = NotificationHelper(requireContext())
                notificationHelper.sendNotification("Reminder", "You have check-up in ${reminder.checkUpDate}", 1)
            }
            //send notification 14 days before checkupDate
            else if (DateUtils.isToday(reminder.checkUpDate.time - 1209600000)){//1209600000 = 2 weeks in ms
                val notificationHelper = NotificationHelper(requireContext())
                notificationHelper.sendNotification("Reminder", "You have check-up in ${reminder.checkUpDate}", 1)
            }
        }
    }
    */


}



