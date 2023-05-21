package com.example.binger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.binger.databinding.FragmentUpdateFoodBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateFoodFragment : Fragment() {

    private lateinit var binding: FragmentUpdateFoodBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateBtn.setOnClickListener{
            val resName = binding.resName.text.toString()
            val foodOldName = binding.foodOldName.text.toString()
            val foodNewName = binding.foodNewName.text.toString()
            val foodPrice = binding.foodPrice.text.toString()
            val foodImage = binding.foodImage.text.toString()
            if(resName.isNotEmpty()&&foodOldName.isNotEmpty()&&foodNewName.isNotEmpty()&&foodPrice.isNotEmpty()&&foodImage.isNotEmpty()) {
                if(!foodOldName.isDigitsOnly() && !foodNewName.isDigitsOnly()) {
                    if (foodPrice.isDigitsOnly()) {
                        updateData(resName, foodOldName, foodNewName, foodPrice, foodImage)
                    }else{
                        Toast.makeText(requireContext(), "The price should only have digit", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "The name should contain at least 1 alphabet", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), "Please fill in all the field", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun updateData(resName: String, foodOldName: String, foodNewName: String, foodPrice: String, foodImage: String) {
        database = FirebaseDatabase.getInstance().getReference("menus")
        val food = mapOf(
            "name" to foodNewName,
            "price" to foodPrice,
            "url" to foodImage
        )
        database.child(resName).child(foodOldName).removeValue()


        database.child(resName).child(foodNewName).updateChildren(food).addOnSuccessListener {
            binding.resName.text.clear()
            binding.foodOldName.text.clear()
            binding.foodNewName.text.clear()
            binding.foodPrice.text.clear()
            binding.foodImage.text.clear()

            Toast.makeText(requireContext(), "The data has been updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
        }
    }
}