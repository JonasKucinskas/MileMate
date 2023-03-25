package com.example.milemate

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.milemate.database.DBManager
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


class CarAddFragment : Fragment() {

    private var FirstCar = Car("", "", "")
    private var carImage: Bitmap? = null

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
        val imageUploadButton = view.findViewById<Button>(R.id.carAddSelectImage)

        imageUploadButton.setOnClickListener(){
            val intent = Intent()
            intent.type = "image/*"
            intent.action = "android.intent.action.PICK"
            startActivityForResult(intent, 100)
        }

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

            writeCarImage(carImage, carNameContent)

            // Insert into database
            val database = ViewModelProvider(this).get(DBManager::class.java)
            database.insertCar(FirstCar)
        }

    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            carImage = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, data?.data)
        }
    }

    fun writeCarImage(carImage: Bitmap?, fileName: String){

        if (carImage != null) {
            val root: String = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).toString()
            val myDir = File(context?.filesDir.toString()+"/saved_images")
            myDir.mkdirs()
            val fname = "$fileName.jpg"
            val file = File(myDir, fname)

            val out = FileOutputStream(file)
            carImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}