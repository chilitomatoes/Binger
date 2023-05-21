package com.example.binger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.binger.databinding.FragmentUpdateRestaurantBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentUpdateRestaurantBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateBtn.setOnClickListener {
            val resName = binding.resName.text.toString()
            val resAddress = binding.resAddress.text.toString()
            val resCharge = binding.resCharge.text.toString()
            val resImage = binding.resImage.text.toString()
            if(resName.isNotEmpty()&&resAddress.isNotEmpty()&&resCharge.isNotEmpty()&&resImage.isNotEmpty()) {
                if (resCharge.isDigitsOnly()) {
                    updateData(resName, resAddress, resCharge, resImage)
                }else{
                    Toast.makeText(requireContext(), "Charge should only have digit", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), "Please fill in all the field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateData(resName: String, resAddress: String, resCharge: String, resImage: String) {
        database = FirebaseDatabase.getInstance().getReference("restaurantData")
        val res = mapOf(
            "address" to resAddress,
            "delivery_charge" to resCharge,
            "image" to resImage
        )
        database.child(resName).updateChildren(res).addOnSuccessListener {
            binding.resName.text.clear()
            binding.resAddress.text.clear()
            binding.resCharge.text.clear()
            binding.resImage.text.clear()

            Toast.makeText(requireContext(), "The data has been updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
        }
    }
}