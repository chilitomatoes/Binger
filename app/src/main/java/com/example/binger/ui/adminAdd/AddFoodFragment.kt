package com.example.binger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.binger.databinding.FragmentAddFoodBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddFoodFragment : Fragment() {

    private lateinit var binding: FragmentAddFoodBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addBtn.setOnClickListener{
            val resName = binding.resName.text.toString()
            val foodName = binding.foodName.text.toString()
            val foodPrice = binding.foodPrice.text.toString()
            val foodImage = binding.foodImage.text.toString()

            database = FirebaseDatabase.getInstance().getReference("menus")
            val food = Food(foodName, foodPrice, foodImage, resName)
            if(resName.isNotEmpty()&&foodName.isNotEmpty()&&foodPrice.isNotEmpty()&&foodImage.isNotEmpty())
            {
                if(!foodName.isDigitsOnly()){
                    if(foodPrice.isDigitsOnly()){
                        database.child(resName).child(foodName).setValue(food).addOnSuccessListener {
                            binding.resName.text.clear()
                            binding.foodName.text.clear()
                            binding.foodPrice.text.clear()
                            binding.foodImage.text.clear()
                            Toast.makeText(requireContext(), "The data has been created!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to create", Toast.LENGTH_SHORT).show()
                        }
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
}