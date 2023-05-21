package com.example.binger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.binger.databinding.FragmentAddRestaurantBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentAddRestaurantBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addBtn.setOnClickListener {
            val resName = binding.resName.text.toString()
            val resAddress = binding.resAddress.text.toString()
            val resCharge = binding.resCharge.text.toString()
            val resImage = binding.resImage.text.toString()

            database = FirebaseDatabase.getInstance().getReference("restaurantData")
            val restaurant = Restaurant(resName, resAddress, resCharge, resImage)
            if(resName.isNotEmpty()&&resAddress.isNotEmpty()&&resCharge.isNotEmpty()&&resImage.isNotEmpty()){
                if(resCharge.isDigitsOnly()){
                    database.child(resName).setValue(restaurant).addOnSuccessListener {
                        binding.resName.text.clear()
                        binding.resAddress.text.clear()
                        binding.resCharge.text.clear()
                        binding.resImage.text.clear()

                        Toast.makeText(requireContext(), "The data has been created!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to create", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "Charge should be digit only", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), "Please fill in all the field", Toast.LENGTH_SHORT).show()
            }
        }
    }
}