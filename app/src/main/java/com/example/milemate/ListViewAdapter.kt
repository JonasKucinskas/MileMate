package com.example.milemate

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.io.File


class ListViewAdapter(private val context : Activity, private val carList : List<com.example.milemate.database.Car>) :
    ArrayAdapter<com.example.milemate.database.Car>(context, R.layout.list_item, carList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.list_item, null)

        val carName = view.findViewById<TextView>(R.id.listitem_car_name)
        val carBrand = view.findViewById<TextView>(R.id.listitem_car_brand)
        val carImage = view.findViewById<ImageView>(R.id.listitem_car_image)

        val deleteButton = view.findViewById<Button>(R.id.buttonDelete)

        carName.text = carList[position].name
        carBrand.text = carList[position].brand

        val ImageFolder = context.getString(R.string.saved_images)
        // Loads image onto carPlaceHolders
        val CurrentCarImagePath = ImageFolder + "/" + carList[position].name + carList[position].id + ".jpg"

        if(File(CurrentCarImagePath).exists()) {
            val bitmap = BitmapFactory.decodeFile(CurrentCarImagePath)
            carImage.setImageBitmap(bitmap)
        }

        deleteButton.setOnClickListener{

        }

        return view;
    }
}