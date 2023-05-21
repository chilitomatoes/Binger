package com.example.binger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import androidx.recyclerview.widget.LinearLayoutManager

class FoodFragment : Fragment() {

    private lateinit var dbref : DatabaseReference
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodArrayList: ArrayList<Food>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_food, container, false)

        foodRecyclerView = view.findViewById(R.id.foodList)
        foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        foodRecyclerView.setHasFixedSize(true)

        foodArrayList = arrayListOf()
        getFoodData()
        return view
    }

    private fun getFoodData() {
        dbref = FirebaseDatabase.getInstance().getReference("menus")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val foodDetails: ArrayList<Food> = ArrayList()
                    for (foodSnapshot in snapshot.children){
                        val resName = foodSnapshot.key
                        for (foodSnap in foodSnapshot.children){
                            val name = foodSnap.child("name").getValue(String::class.java)
                            val price = foodSnap.child("price").getValue().toString()
                            val url = foodSnap.child("url").getValue(String::class.java)

                            val food = Food(name, price, url, resName)
                            foodDetails.add(food)
                        }
                    }
                    foodArrayList = foodDetails
                    foodRecyclerView.adapter = FoodAdapter(foodArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}