package com.example.milemate

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.milemate.databinding.FragmentFirstBinding
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.File
import java.util.Calendar


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TitleFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

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

        val tempButton = view.findViewById<Button>(R.id.tempButton)

        tempButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_FirstFragment_to_checkUpFragment)
        }

        val carAddButton = binding.root.findViewById<Button>(R.id.add_CarBtn)

        /*carAddButton.setOnClickListener {
            val fragment = CarAddFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_content_main, fragment)

            transaction.addToBackStack(null)
            transaction.commit()*/
        val navGraphActivity = activity as MainActivity

        carAddButton.setOnClickListener{
            navGraphActivity.navController.navigate(R.id.action_FirstFragment_to_CarAddFragment)
        }


        //Didn't test but should work
        val file = File(context?.filesDir.toString() + "/reminder.json")

        //read json object, check if reminder date is today, if yes, send notification.

        if (file.exists()){
            val inputStream = file.readLines()
            val jsonObject = JsonParser.parseString(inputStream[0]).asJsonObject
            val reminder = Gson().fromJson(jsonObject, Reminder::class.java)

            if (DateUtils.isToday(reminder.reminderDate.time)){
                val notificationHelper = NotificationHelper(requireContext())
                notificationHelper.sendNotification("Reminder", "You have check-up in ${reminder.checkUpDate}")
            }
        }

        //Temp method for sending notification 1 month and 14days before changing tires
        val todayDate = Calendar.getInstance()
        if ((todayDate.get(Calendar.MONTH) + 1 == 3 && todayDate.get(Calendar.DAY_OF_MONTH) == 1) || (todayDate.get(Calendar.MONTH) + 1 == 3 && todayDate.get(Calendar.DAY_OF_MONTH) == 14) ||
            (todayDate.get(Calendar.MONTH) + 1 == 4 && todayDate.get(Calendar.DAY_OF_MONTH) == 1)){
            val notificationHelper = NotificationHelper(requireContext())
            notificationHelper.sendNotification("MileMate", "Don't forget that you need to change tires from winter to summer until April 10th")
        }
        //Temp method
        if ((todayDate.get(Calendar.MONTH) + 1 == 10 && todayDate.get(Calendar.DAY_OF_MONTH) == 1) || (todayDate.get(Calendar.MONTH) + 1 == 10 && todayDate.get(Calendar.DAY_OF_MONTH) == 14) ||
            (todayDate.get(Calendar.MONTH) + 1 == 11 && todayDate.get(Calendar.DAY_OF_MONTH) == 1)){
            val notificationHelper = NotificationHelper(requireContext())
            notificationHelper.sendNotification("MileMate", "Don't forget that you need to change tires from summer to winter until November 10th")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}