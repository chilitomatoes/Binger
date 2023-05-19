package com.example.binger.ui.address

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binger.R
import com.example.binger.adapter.AddressAdapter
import com.example.binger.adapter.PaymentMethodAdapter
import com.example.binger.databinding.FragmentAddressBinding
import com.example.binger.model.Address
import com.example.binger.model.PaymentMethod
import com.google.firebase.database.*

class AddressFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var addressList: ArrayList<Address>

    private var _binding: FragmentAddressBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(AddressViewModel::class.java)

        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("Address")

        recyclerView = binding.addressRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        getPaymentMethodData()

        binding.addAddressFloatingButton.setOnClickListener{
            val bottomSheetDialog = Dialog(requireContext())

            bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            bottomSheetDialog.setContentView(R.layout.slideup_address)
            bottomSheetDialog.show()
            bottomSheetDialog.window?.setGravity(Gravity.BOTTOM)
            bottomSheetDialog.window?.setWindowAnimations(R.style.slideAnimation)
            bottomSheetDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            val addAddressButton: Button = bottomSheetDialog.findViewById(R.id.addAddressButton)
            addAddressButton.setOnClickListener(){

                val addressLine1: EditText = bottomSheetDialog.findViewById(R.id.addressLine1EditText)
                val addressLine2: EditText = bottomSheetDialog.findViewById(R.id.addressLine2EditText)
                val postalCode: EditText = bottomSheetDialog.findViewById(R.id.postalCodeEditText)
                val town: EditText = bottomSheetDialog.findViewById(R.id.townEditText)
                val state: EditText = bottomSheetDialog.findViewById(R.id.stateEditText)
                val addressName: EditText = bottomSheetDialog.findViewById(R.id.addressNameEditText)

                if(TextUtils.isEmpty(addressName.text.toString())) {
                    addressName.error = "Enter the address name"
                    addressName.requestFocus()
                }else if(TextUtils.isEmpty(addressLine1.text.toString())) {
                    addressLine1.error = "Enter your address"
                    addressLine1.requestFocus()
                }else if(TextUtils.isEmpty(addressLine2.text.toString())) {
                    addressLine2.error = "Enter your address"
                    addressLine2.requestFocus()
                }else if(TextUtils.isEmpty(postalCode.text.toString())) {
                    postalCode.error = "Enter your postal code"
                    postalCode.requestFocus()
                }else if(TextUtils.isEmpty(town.text.toString())) {
                    town.error = "Enter the address town"
                    town.requestFocus()
                }else if(TextUtils.isEmpty(state.text.toString())) {
                    state.error = "Enter the address state"
                    state.requestFocus()
                }else{
                    var id=database.push().key.toString()
                    val newAddress: Address = Address(id,addressLine1.text.toString(), addressLine2.text.toString(), postalCode.text.toString().toInt(), town.text.toString(), state.text.toString(), addressName.text.toString(), 0)
                    database.child(id).setValue(newAddress)

                    bottomSheetDialog.dismiss()
                    Toast.makeText(context, "Address Added Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getPaymentMethodData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                addressList = arrayListOf<Address>()
                if(snapshot.exists()){
                    for (addressSnapshot in snapshot.children){
                        val address = addressSnapshot.getValue(Address::class.java)
                        if(address?.default == 1){
                            addressList.add(0,address)
                        }else {
                            addressList.add(address!!)
                        }
                    }
                    recyclerView.adapter = AddressAdapter(requireContext(),addressList, database)
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