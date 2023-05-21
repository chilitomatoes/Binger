package com.example.binger.ui.menu

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.binger.R
import com.example.binger.adapter.MenuListAdapter
import com.example.binger.databinding.FragmentMenuBinding
import com.example.binger.model.Menus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class menu : Fragment(), MenuListAdapter.MenuListClickListener {


    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: menuViewModel

    var menuList: ArrayList<Menus> =ArrayList()
    private var cusOrder: ArrayList<Menus>?=ArrayList()
    private var menuListAdapter: MenuListAdapter?=null

    var number:Int=0


    val database = FirebaseDatabase.getInstance()
    val reference = database.getReference("menus")

    private var totalItemINCart=0
    var restaurantName:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentMenuBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(menuViewModel::class.java)

        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurantName=viewModel.restaurantSelected?.name.toString()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val menusDetails: ArrayList<Menus> = ArrayList()

                    for (menuSnap in dataSnapshot.child(restaurantName!!).children) {
                        val name = menuSnap.child("name").getValue(String::class.java)
                        val price = menuSnap.child("price").getValue().toString()
                        val url = menuSnap.child("url").getValue(String::class.java)


                        val menu = Menus(name, price, url,0)

                        menusDetails.add(menu)
                    }

                    viewModel.menus = menusDetails
                    menuList = viewModel.menus
                    menuListAdapter?.updateMenuList(menuList) // Notify the adapter about the updated menuList

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
            }
        })


        binding.menuRecyclerView.layoutManager= GridLayoutManager(requireContext(),2)
        menuListAdapter= MenuListAdapter(menuList,this,viewModel)
        binding.menuRecyclerView.adapter=menuListAdapter


        binding.checkOutButton.setOnClickListener{
            for(item in viewModel!!.menus)
            {
                number+=item!!.totalInCart
            }
            if(number<=0)
            {
                cusOrder=null

                Toast.makeText(requireContext(), "Please add some item into the Cart", Toast.LENGTH_SHORT).show()
            }
            else
            {
                viewModel.itemListInCart=cusOrder

                findNavController().navigate(R.id.payment)
            }

        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(menuViewModel::class.java)
        // TODO: Use the ViewModel
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun addToCartClickListener(menu: Menus, binding: FragmentMenuBinding) {
        if(cusOrder==null)
        {
            cusOrder=ArrayList()

        }
        cusOrder?.add(menu)
        totalItemINCart=0
        for(menu in cusOrder!!)
        {
            totalItemINCart += menu?.totalInCart!!
        }
        binding.checkOutButton.text="Checkout ("+ totalItemINCart+") Items"

    }

    override fun removeFromCartClickListener(menu: Menus,binding:FragmentMenuBinding) {
        if(cusOrder!!.contains(menu))
        {
            cusOrder?.remove(menu)
            totalItemINCart=0
            for(menu in cusOrder!!)
            {
                totalItemINCart+=menu?.totalInCart!!
            }
            binding.checkOutButton.text="Check Out ("+totalItemINCart+") Items"
        }

    }



}