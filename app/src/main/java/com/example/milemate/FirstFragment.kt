package com.example.milemate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.milemate.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val checkUpPage = Intent(context, CheckUpPage::class.java)
            startActivity(checkUpPage)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}