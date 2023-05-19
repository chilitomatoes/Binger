package com.example.binger.ui.home

import android.content.Intent
import android.os.Bundle
import android.service.autofill.UserData
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binger.R
import com.example.binger.databinding.FragmentHomeBinding
import com.example.binger.databinding.FragmentPaymentBinding
import com.example.binger.ui.home.ViewModel.menuViewModel
import com.example.binger.ui.home.adapter.OrderPlacementAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class Payment : Fragment() {
    var orderPlacement: OrderPlacementAdapter?=null
    var isDeliveryOn:Boolean=false
    var addrress:String="null"
    var restaurantName:String?="null"
    var food:ArrayList<Menus>?= ArrayList()
    private lateinit var database: DatabaseReference
    lateinit var userData: UserData

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: menuViewModel

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(menuViewModel::class.java)

        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val root: View = binding.root

        //val textView: TextView = binding.textHome
        //homeViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(item in viewModel.menus!!)
        {
            Log.v("Count",item.javaClass.toString())
            if(item.totalInCart>0)
            {
                food?.add(item)
            }
        }



        binding.buttonPlaceYourOrder.setOnClickListener {
            onPlaceOrderButtonClick(binding)
            addrress=String.format("%s,%s,%s,%s",binding.inputAddress.text.toString(),binding.inputCity.text.toString(),binding.inputState.text.toString(),binding.inputZip.text.toString())

            restaurantName=viewModel?.restaurantSelected?.name



            database= FirebaseDatabase.getInstance().getReference(restaurantName!!)
            var id=database.push().key.toString()


            /*userData= UserData(id,restaurantName,addrress,food)
            database.child(userData.id!!).setValue(userData)*/


        }
        binding.radioButtonDelivery.setOnCheckedChangeListener{
                buttonView, isChecked ->

            if(isChecked) {
                binding.inputAddress.visibility = View.VISIBLE
                binding.inputCity.visibility = View.VISIBLE
                binding.inputState.visibility = View.VISIBLE
                binding.inputZip.visibility = View.VISIBLE
                binding.tvDeliveryCharge.visibility = View.VISIBLE
                binding.tvDeliveryChargeAmount.visibility = View.VISIBLE
                isDeliveryOn=true
                Log.v("SUP","SUP")
                calculateTotal(food!!,binding,viewModel)


            }
        }
        binding.radioButtonPickup.setOnCheckedChangeListener{
                buttonView, isChecked ->

            if(isChecked) {
                binding.inputAddress.visibility = View.GONE
                binding.inputCity.visibility = View.GONE
                binding.inputState.visibility = View.GONE
                binding.inputZip.visibility = View.GONE
                binding.tvDeliveryCharge.visibility = View.GONE
                binding.tvDeliveryChargeAmount.visibility = View.GONE
                isDeliveryOn=false
                calculateTotal(food!!,binding,viewModel)


            }
        }




        binding.cartItemsRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        orderPlacement= OrderPlacementAdapter(food)
        binding.cartItemsRecyclerView.adapter=orderPlacement

        calculateTotal(food!!,binding,viewModel)



    }



    private fun calculateTotal(food:List<Menus?>,binding: FragmentPaymentBinding,view:menuViewModel)
    {
        var subTotal=0f
        for(menu in food)
        {
            subTotal+=menu?.price!!.toFloat() * menu?.totalInCart!!
        }

        binding.tvSubtotalAmount.text="RM"+String.format("%.2f",subTotal)
        Log.v("SAD",isDeliveryOn.toString())
        if(isDeliveryOn)
        {
            Log.v("SAD",viewModel?.restaurantSelected?.delivery_charge!!)
            binding.tvDeliveryChargeAmount.text="RM"+String.format("%.2f",view?.restaurantSelected?.delivery_charge?.toFloat())

            subTotal+=viewModel?.restaurantSelected?.delivery_charge!!.toFloat()
        }

        binding.tvTotalAmount.text="RM"+String.format("%.2f",subTotal)


    }

    private fun onPlaceOrderButtonClick(binding: FragmentPaymentBinding)
    {
        if(TextUtils.isEmpty(binding.inputName.text.toString())) {
            binding.inputName.error =  "Enter your name"
            return
        } else if(isDeliveryOn && TextUtils.isEmpty(binding.inputAddress.text.toString())) {
            binding.inputAddress.error =  "Enter your address"
            return
        } else if(isDeliveryOn && TextUtils.isEmpty(binding.inputCity.text.toString())) {
            binding.inputCity.error =  "Enter your City Name"
            return
        } else if(isDeliveryOn && TextUtils.isEmpty(binding.inputZip.text.toString())) {
            binding.inputZip.error =  "Enter your Zip code"
            return
        } else if( TextUtils.isEmpty(binding.inputCardNumber.text.toString())) {
            binding.inputCardNumber.error =  "Enter your credit card number"
            return
        } else if( TextUtils.isEmpty(binding.inputCardExpiry.text.toString())) {
            binding.inputCardExpiry.error =  "Enter your credit card expiry"
            return
        } else if( TextUtils.isEmpty(binding.inputCardPin.text.toString())) {
            binding.inputCardPin.error =  "Enter your credit card pin/cvv"
            return
        }
        //findNavController().navigate(R.id.OrderSuccess)

    }





}