package com.example.binger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.binger.databinding.FragmentDeleteFoodBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DeleteFoodFragment : Fragment() {

    private lateinit var binding: FragmentDeleteFoodBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeleteFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deleteBtn.setOnClickListener{
            val resName = binding.resName.text.toString()
            val foodName = binding.foodName.text.toString()
            if(foodName.isNotEmpty())
                deleteData(foodName, resName)
            else
                Toast.makeText(requireContext(), "Cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteData(foodName: String, resName: String) {
        database = FirebaseDatabase.getInstance().getReference("menus")
        database.child(resName).child(foodName).removeValue().addOnSuccessListener {
            binding.resName.text.clear()
            binding.foodName.text.clear()
            Toast.makeText(requireContext(), "The data has been deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
        }
    }

}