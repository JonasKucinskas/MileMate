package com.example.milemate
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace


class CheckUpPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_up_page)

    }
    fun on_Click_NextButton(view: View) {//switch to set reminder fragment

        supportFragmentManager.commit {
            replace<CheckUpReminderFragment>(R.id.fragmentContainerView)
            setReorderingAllowed(true)
            addToBackStack("name")
        }

    }
}
