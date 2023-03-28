package com.example.milemate

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.milemate.database.DBManager
import com.example.milemate.databinding.FragmentFirstBinding
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.File
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TitleFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    val maxCars = 4;

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Makes all layouts disabled
        //for (num in 0..4) {
        //    val id = resources.getIdentifier(String.format("Car%dLayout", num+1),
        //        "id", activity?.packageName)
        //    view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(id).isVisible = false
        //}

        val navGraphActivity = activity as MainActivity

        val checkUpButton = view.findViewById<Button>(R.id.check_UpBtn)
        checkUpButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_FirstFragment_to_checkUpFragment)
        }
        val carAddButton = binding.root.findViewById<Button>(R.id.add_CarBtn)
        carAddButton.setOnClickListener{
            navGraphActivity.navController.navigate(R.id.action_FirstFragment_to_CarAddFragment)
        }
        reminderChecker()

        /*carAddButton.setOnClickListener {
            val fragment = CarAddFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_content_main, fragment)

            transaction.addToBackStack(null)
            transaction.commit()*/

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


        val viewModel = ViewModelProvider(this).get(DBManager::class.java)
        var carCount = 0;
        disableAllCarRemoveButtons()

        viewModel.getAllCars().observe(viewLifecycleOwner)
        { cars ->
            // TODO: Add car images (hopefully linked to cars) to each of 4 car elements
            // Cia accessint whatever car data
            for (car in cars) {

                if(carCount > maxCars-1){
                    break
                }

                // Loads image onto carPlaceHolders
                val imagePath = getString(R.string.saved_images)
                val layoutID = "Car${car.id}Layout"
                // Makes placeholder visible if car is found
                val carLayout = view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(resources.getIdentifier(layoutID,
                    "id", activity?.packageName))
                carLayout.visibility = View.VISIBLE

                var idCarImageButton = resources.getIdentifier(String.format("carImgPlaceHolder%d", carCount+1), "id", activity?.packageName)
                val idRemoveButton: Int = resources.getIdentifier(String.format("carRemoveImgBtn%d", carCount+1), "id", activity?.packageName)
                view?.findViewById<ImageButton>(idCarImageButton)?.isVisible = true;


                val bitmap = BitmapFactory.decodeFile(imagePath + "/" + car.name + ".jpg")
                val carButton = view.findViewById<ImageButton>(idCarImageButton)
                val xButton = view.findViewById<ImageButton>(idRemoveButton)

                carButton.setImageBitmap(bitmap)

                // When pressed on image X button of car object
                xButton.setOnClickListener{

                }
                // When pressed on image button of car object
                carButton.setOnClickListener{
                    navGraphActivity.navController.navigate(R.id.carFragment)
                }

                carCount++;
            }

            if(carCount > 0){
                val noCarsTextview = view.findViewById<TextView>(R.id.textview_first)
                noCarsTextview.isVisible = false
            }else{
                disableAllCarPlaceholders()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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

    private fun disableAllCarPlaceholders(){
        for(i in 0..maxCars){
            val id: Int = resources.getIdentifier(String.format("carImgPlaceHolder%d", i+1), "id", activity?.packageName)
            view?.findViewById<ImageButton>(id)?.isVisible = false;
        }
    }

    private fun disableAllCarRemoveButtons(){
        for(i in 0..maxCars){
            val id: Int = resources.getIdentifier(String.format("carRemoveImgBtn%d", i+1), "id", activity?.packageName)
            view?.findViewById<ImageButton>(id)?.isVisible = false;
        }
    }

}