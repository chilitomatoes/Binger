package com.example.binger.ui.paymentMethods


import android.app.Dialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binger.R
import com.example.binger.adapter.PaymentMethodAdapter
import com.example.binger.databinding.FragmentPaymentmethodBinding
import com.example.binger.model.PaymentMethod
import com.example.binger.ui.home.HomeViewModel
import com.google.firebase.database.*


class PaymentMethodFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var paymentMethodList: ArrayList<PaymentMethod>

    private var _binding: FragmentPaymentmethodBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val paymentMethodViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentPaymentmethodBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("PaymentMethod")

        recyclerView = binding.paymentMethodRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        getPaymentMethodData()



        binding.addPaymentFloatingButton.setOnClickListener{
            val bottomSheetDialog = Dialog(requireContext())

            bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            bottomSheetDialog.setContentView(R.layout.slideup_payment)
            bottomSheetDialog.show()
            bottomSheetDialog.window?.setGravity(Gravity.BOTTOM)
            bottomSheetDialog.window?.setWindowAnimations(R.style.slideAnimation)
            bottomSheetDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            val addPaymentButton:Button = bottomSheetDialog.findViewById(R.id.addPaymentButton)
            addPaymentButton.setOnClickListener(){

                val cardNum: EditText = bottomSheetDialog.findViewById(R.id.cardNumEditText)
                val holderName: EditText = bottomSheetDialog.findViewById(R.id.holderNameEditText)
                val expMon: EditText = bottomSheetDialog.findViewById(R.id.expMonEditText)
                val expYear: EditText = bottomSheetDialog.findViewById(R.id.expYearEditText)
                val cvc: EditText = bottomSheetDialog.findViewById(R.id.cvcEditText)

                if(TextUtils.isEmpty(cardNum.text.toString())) {
                    cardNum.error = "Enter your card number"
                    cardNum.requestFocus()
                }else if(cardNum.text.toString().length != 16) {
                    cardNum.error = "Enter 16 digits"
                    cardNum.requestFocus()
                }else if(TextUtils.isEmpty(expMon.text.toString())) {
                    expMon.error = "Enter card expiry month"
                    expMon.requestFocus()
                }else if(expMon.text.toString().toInt() > 12 || expMon.text.toString().toInt() < 0) {
                    expMon.error = "Invalid month"
                    expMon.requestFocus()
                }else if(TextUtils.isEmpty(expYear.text.toString())) {
                    expYear.error = "Enter card expiry year"
                    expYear.requestFocus()
                }else if(TextUtils.isEmpty(cvc.text.toString())) {
                    cvc.error = "Enter card cvc"
                    cvc.requestFocus()
                }else if(cvc.text.toString().length != 3) {
                    cvc.error = "Enter 3 digits"
                    cvc.requestFocus()
                }else if(TextUtils.isEmpty(holderName.text.toString())) {
                    holderName.error = "Enter card holder name"
                    holderName.requestFocus()
                }else{
                    var id=database.push().key.toString()
                    val newPaymentMethod:PaymentMethod = PaymentMethod(id,cardNum.text.toString().toLong(), cvc.text.toString().toInt(), expMon.text.toString().toInt(), expYear.text.toString().toInt(), holderName.text.toString(), 0)
                    database.child(id).setValue(newPaymentMethod)

                    bottomSheetDialog.dismiss()
                    Toast.makeText(context, "Card Added Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getPaymentMethodData() {
        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                paymentMethodList = arrayListOf<PaymentMethod>()
                if(snapshot.exists()){
                    for (paymentMethodSnapshot in snapshot.children){
                        val paymentMethod = paymentMethodSnapshot.getValue(PaymentMethod::class.java)
                        if(paymentMethod?.default == 1){
                            paymentMethodList.add(0,paymentMethod)
                        }else {
                            paymentMethodList.add(paymentMethod!!)
                        }
                    }
                    recyclerView.adapter = PaymentMethodAdapter(requireContext(),paymentMethodList, database)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}