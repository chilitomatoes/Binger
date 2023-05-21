package com.example.binger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import androidx.recyclerview.widget.LinearLayoutManager

class RestaurantFragment : Fragment() {

    private lateinit var dbref : DatabaseReference
    private lateinit var restaurantRecyclerView: RecyclerView
    private lateinit var restaurantArrayList: ArrayList<Restaurant>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)

        restaurantRecyclerView = view.findViewById(R.id.restaurantList)
        restaurantRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        restaurantRecyclerView.setHasFixedSize(true)

        restaurantArrayList = arrayListOf<Restaurant>()
        getRestaurantData()

        return view
    }

    private fun getRestaurantData() {

        dbref = FirebaseDatabase.getInstance().getReference("restaurantData")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val restDetails: ArrayList<Restaurant> = ArrayList()
                    for (restaurantSnapshot in snapshot.children) {
                        val restName = restaurantSnapshot.child("name").getValue(String::class.java)
                        val restAddress = restaurantSnapshot.child("address").getValue(String::class.java)
                        val restCharge = restaurantSnapshot.child("delivery_charge").getValue().toString()
                        val restImage = restaurantSnapshot.child("image").getValue(String::class.java)

                        val restaurant = Restaurant(restName, restAddress, restCharge, restImage)
                        restDetails.add(restaurant)
                    }
                    restaurantArrayList = restDetails
                    restaurantRecyclerView.adapter = RestaurantAdapter(restaurantArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

}