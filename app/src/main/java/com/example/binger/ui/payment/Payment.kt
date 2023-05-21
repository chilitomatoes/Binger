package com.example.binger.ui.payment

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.service.autofill.UserData
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binger.R
import com.example.binger.adapter.CheckOutSelectionAdapter
import com.example.binger.databinding.FragmentPaymentBinding
import com.example.binger.ui.menu.menuViewModel
import com.example.binger.adapter.OrderPlacementAdapter
import com.example.binger.model.Address
import com.example.binger.model.Menus
import com.example.binger.model.Order
import com.example.binger.model.PaymentMethod
import com.example.binger.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlin.math.log


class Payment : Fragment() , CheckOutSelectionAdapter.AdapterListener{
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginedUser: User
    private lateinit var selectedCard: PaymentMethod
    var orderPlacement: OrderPlacementAdapter?=null
    var isDeliveryOn:Boolean=true
    var restaurantName:String?="null"
    var food:ArrayList<Menus>?= ArrayList()
    var order: Order=Order()
    private lateinit var database: DatabaseReference
    private lateinit var orderHisData: DatabaseReference

    ///////////////////////////////////////////////////////////////////////
    var tempaddress:Address= Address("AAA","AAA","AA",47830,"banana","URMOM","CHAONIMA",1)


    lateinit var userData: UserData
    lateinit var bottomSheetDialog: Dialog
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: menuViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(menuViewModel::class.java)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

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


        loginedUser= readUserData()

        for(card in loginedUser.cards!!){
            if(card.default==1){
                selectedCard = card
                binding.defaultCardNameTextView.text = selectedCard.holderName
                binding.defaultCardNumTextView.text = selectedCard.cardNum.toString()
            }
        }

        for(item in viewModel.menus!!)
        {
            Log.v("Count",item.javaClass.toString())
            if(item.totalInCart>0)
            {
                food?.add(item)
            }
        }

        bottomSheetDialog = Dialog(requireContext())

        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bottomSheetDialog.setContentView(R.layout.slideup_checkout)
        bottomSheetDialog.window?.setGravity(Gravity.BOTTOM)
        bottomSheetDialog.window?.setWindowAnimations(R.style.slideAnimation)
        bottomSheetDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        recyclerView = bottomSheetDialog.findViewById(R.id.selectionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        binding.addressCardView.setOnClickListener{

            recyclerView.adapter = CheckOutSelectionAdapter(requireContext(), "Address", loginedUser, this)
            bottomSheetDialog.show()
        }

        binding.paymentCardView.setOnClickListener{

            recyclerView.adapter = CheckOutSelectionAdapter(requireContext(), "Cards", loginedUser, this)
            bottomSheetDialog.show()

        }

        binding.buttonPlaceYourOrder.setOnClickListener {
            onPlaceOrderButtonClick(binding)

            restaurantName=viewModel?.restaurantSelected?.name

            database= FirebaseDatabase.getInstance().getReference(restaurantName!!)
            var id=database.push().key.toString()


            /*userData= UserData(id,restaurantName,addrress,food)
            database.child(userData.id!!).setValue(userData)*/


        }
        binding.radioButtonDelivery.setOnCheckedChangeListener{
                buttonView, isChecked ->

            if(isChecked) {
                binding.tvAddress.visibility = View.VISIBLE
                binding.addressCardView.visibility = View.VISIBLE
                binding.tvDeliveryCharge.visibility = View.VISIBLE
                binding.tvDeliveryChargeAmount.visibility = View.VISIBLE
                isDeliveryOn=true

                calculateTotal(food!!,binding,viewModel)


            }
        }
        binding.radioButtonPickup.setOnCheckedChangeListener{
                buttonView, isChecked ->

            if(isChecked) {
                binding.tvAddress.visibility = View.GONE
                binding.addressCardView.visibility = View.GONE
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
        binding.buttonPlaceYourOrder.setOnClickListener{
            onPlaceOrderButtonClick(binding)

            restaurantName=viewModel?.restaurantSelected?.name
            database=FirebaseDatabase.getInstance().getReference("orderPlaced")
            var id=database.push().key.toString()

            if(isDeliveryOn)
            {
                order= Order(tempaddress,food,restaurantName)
            }
            else
            {
                order= Order(null,food,restaurantName)
            }

            orderHisData=FirebaseDatabase.getInstance().getReference("User")
            orderHisData.child(loginedUser.uid!!).child("orders").child(restaurantName!!).child(id).setValue(order)










            if(isDeliveryOn)
            {
                order= Order(tempaddress,food,restaurantName)
                database.child("Delivery").child(id).setValue(order)
            }
            else
            {
                order= Order(null,food,restaurantName)
                database.child("PickUp").child(id).setValue(order)
            }

        }


    }



    private fun calculateTotal(food:List<Menus?>, binding: FragmentPaymentBinding, view: menuViewModel)
    {
        var subTotal=0f
        for(menu in food)
        {
            subTotal+=menu?.price!!.toFloat() * menu?.totalInCart!!
        }

        binding.tvSubtotalAmount.text="RM"+String.format("%.2f",subTotal)
        if(isDeliveryOn)
        {
            binding.tvDeliveryChargeAmount.text="RM"+String.format("%.2f",view?.restaurantSelected?.delivery_charge?.toFloat())

            subTotal+=viewModel?.restaurantSelected?.delivery_charge!!.toFloat()
        }

        binding.tvTotalAmount.text="RM"+String.format("%.2f",subTotal)


    }

    private fun onPlaceOrderButtonClick(binding: FragmentPaymentBinding)
    {


        findNavController().navigate(R.id.orderSuccess)

    }


    private fun readUserData(): User {
        val json = sharedPreferences.getString("loginedUser", null)
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
    }

    override fun onItemSelected(selectedIndex: Int?) {
        bottomSheetDialog.dismiss()
        selectedCard = loginedUser.cards!![selectedIndex!!]
        binding.defaultCardNameTextView.text = selectedCard.holderName
        binding.defaultCardNumTextView.text = selectedCard.cardNum.toString()
    }


}