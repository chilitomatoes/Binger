package com.example.binger.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binger.R
import com.example.binger.adapter.RestaurantListAdapter
import com.example.binger.databinding.FragmentHomeBinding
import com.example.binger.model.RestaurantModel
import com.example.binger.ui.menu.menuViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), RestaurantListAdapter.RestaurantListClickListener {


    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("restaurantData")


    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: menuViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(requireActivity()).get(menuViewModel::class.java)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textHome
        //homeViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRestaurantData { restaurantModelList ->
            initRecyclerView(restaurantModelList)
        }
    }
    private fun getRestaurantData(callback: (ArrayList<RestaurantModel>) -> Unit) {
        val restaurantDetails: ArrayList<RestaurantModel> = ArrayList()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (restauranntSnap in dataSnapshot.children) {
                        val name = restauranntSnap.child("name").getValue(String::class.java)
                        val address = restauranntSnap.child("address").getValue(String::class.java)
                        val deliveryCharge =
                            restauranntSnap.child("delivery_charge").getValue().toString()
                        val image = restauranntSnap.child("image").getValue(String::class.java)

                        val restaurant = RestaurantModel(name, address, deliveryCharge, image)
                        restaurantDetails.add(restaurant)
                    }
                }


                // Call the callback and pass the restaurantDetails list
                callback.invoke(restaurantDetails)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
            }
        })
    }

    private fun initRecyclerView(restaurantList: ArrayList<RestaurantModel>?) {
        val recyclerViewRestaurant = binding?.recyclerViewRestaurantList
        recyclerViewRestaurant?.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RestaurantListAdapter(restaurantList ?: ArrayList(),this) // Provide an empty list if restaurantList is null
        recyclerViewRestaurant?.adapter = adapter
    }

    override fun onItemClick(restaurantModel: RestaurantModel?) {
        viewModel.restaurantSelected=restaurantModel
        findNavController().navigate(R.id.menu)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}