package com.example.binger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.binger.databinding.FragmentDeleteRestaurantBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DeleteRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentDeleteRestaurantBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeleteRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deleteBtn.setOnClickListener {
            val resName = binding.resName.text.toString()
            if (resName.isNotEmpty())
                deleteData(resName)
            else
                Toast.makeText(requireContext(), "Cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteData(resName: String) {
        database = FirebaseDatabase.getInstance().getReference("restaurantData")
        database.child(resName).removeValue().addOnSuccessListener {
            binding.resName.text.clear()
            Toast.makeText(requireContext(), "The data has been deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
        }
    }
}