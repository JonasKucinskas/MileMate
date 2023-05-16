package com.example.milemate

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.milemate.database.FireBase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerButton = view.findViewById<Button>(R.id.actualregisterbutton)
        registerButton.setOnClickListener{
            val enteredUsername: EditText = view.findViewById(R.id.registerusernamelEditText)
            val enteredPassword: EditText = view.findViewById(R.id.registerpasswordEditText)
            val repeatedPassword: EditText = view.findViewById(R.id.registerpasswordEditText2)
            val username = enteredUsername.text.toString().trim()
            val password = enteredPassword.text.toString()
            val secondPassword = repeatedPassword.text.toString()

            val database = FireBase()

            /*if (!isEmail(email)){
                Toast.makeText(activity, "Enter a valid email!", Toast.LENGTH_SHORT).show()
            }*/
            if (password.length < 8){
                Toast.makeText(activity, "Enter a password of at least 8 characters!", Toast.LENGTH_SHORT).show()
            }
            else if (!isPassword(password)){
                Toast.makeText(activity, "Enter a password with at least 1 uppercase letter and 1 number!", Toast.LENGTH_LONG).show()
            }
            else if (secondPassword != password){
                Toast.makeText(activity, "Passwords do not match!", Toast.LENGTH_LONG).show()
            }
            database.userExist(username){ exist ->
                if(exist){
                    Toast.makeText(activity, "Username is already taken!", Toast.LENGTH_SHORT).show()
                }
                else{
                    database.addUser(username, password)
                    Toast.makeText(activity, "Registration successful!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun isPassword(password: String): Boolean{
        val pattern = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$"
        return password.matches(pattern.toRegex())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}