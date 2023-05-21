package com.example.binger.ui.address

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binger.R
import com.example.binger.adapter.AddressAdapter
import com.example.binger.adapter.PaymentMethodAdapter
import com.example.binger.databinding.FragmentAddressBinding
import com.example.binger.model.Address
import com.example.binger.model.GeocoderData
import com.example.binger.model.PaymentMethod
import com.example.binger.model.User
import com.example.binger.ui.map.MapsFragment
import com.google.firebase.database.*
import com.google.gson.Gson

class AddressFragment : Fragment(), MapsFragment.ButtonClickListener {
    private lateinit var childFragmentContainer: FrameLayout

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginedUser: User
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var addressList: ArrayList<Address>
    private var geocoderData: GeocoderData? = null

    private var _binding: FragmentAddressBinding? = null
    private val childFragment = MapsFragment()
    private lateinit var bottomSheetDialog: Dialog
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        val root: View = binding.root
        childFragmentContainer = root.findViewById(R.id.childFragmentContainer)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragment.buttonClickListener = this

        loginedUser= readUserData()

        database = FirebaseDatabase.getInstance().getReference("User")

        recyclerView = binding.addressRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        getAddressData()

        bottomSheetDialog = Dialog(requireContext())
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bottomSheetDialog.setContentView(R.layout.slideup_address)
        bottomSheetDialog.window?.setGravity(Gravity.BOTTOM)
        bottomSheetDialog.window?.setWindowAnimations(R.style.slideAnimation)
        bottomSheetDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bottomSheetDialog.setOnDismissListener(){
            resetBottomSheetDialog()
        }

        val addressLine: EditText = bottomSheetDialog.findViewById(R.id.addressLineEditText)
        val doorNum: EditText = bottomSheetDialog.findViewById(R.id.doorNumEditText)
        val postalCode: EditText = bottomSheetDialog.findViewById(R.id.postalCodeEditText)
        val city: EditText = bottomSheetDialog.findViewById(R.id.cityEditText)
        val addressName: EditText = bottomSheetDialog.findViewById(R.id.addressNameEditText)

        val mapbutton: ImageButton = bottomSheetDialog.findViewById(R.id.mapButton)
        mapbutton.setOnClickListener{
            // Check if the MapFragment is already added
            val isMapFragmentAdded = childFragmentManager.findFragmentById(R.id.childFragmentContainer) != null

            if (!isMapFragmentAdded) {
                openChildFragment(childFragment)
                bottomSheetDialog.dismiss()
                binding.addAddressFloatingButton.isVisible = false
                binding.addressRecyclerView.isVisible = false
            }
        }


        binding.addAddressFloatingButton.setOnClickListener{
            bottomSheetDialog.show()

            val addAddressButton: Button = bottomSheetDialog.findViewById(R.id.addAddressButton)
            addAddressButton.setOnClickListener(){

                if(TextUtils.isEmpty(addressName.text.toString())) {
                    addressName.error = "Enter the address name"
                    addressName.requestFocus()
                }else if(TextUtils.isEmpty(addressLine.text.toString())) {
                    addressLine.error = "Enter your address"
                    addressLine.requestFocus()
                }else if(TextUtils.isEmpty(postalCode.text.toString())) {
                    postalCode.error = "Enter your postal code"
                    postalCode.requestFocus()
                }else if(TextUtils.isEmpty(city.text.toString())) {
                    city.error = "Enter the address city"
                    city.requestFocus()
                }else if(TextUtils.isEmpty(doorNum.text.toString())) {
                    doorNum.error = "Enter your floor/unit number"
                    doorNum.requestFocus()
                }else{
                    var id=database.child(loginedUser.uid.toString()).child("addresses").push().key.toString()
                    val newAddress: Address = Address(id,doorNum.text.toString(), addressLine.text.toString(), city.text.toString(), postalCode.text.toString().toInt(), addressName.text.toString(), 0)
                    database.child(loginedUser.uid.toString()).child("addresses").child(id).setValue(newAddress)

                    bottomSheetDialog.dismiss()

                    Toast.makeText(context, "Address Added Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getAddressData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                addressList = arrayListOf<Address>()
                if(snapshot.exists()){
                    for (addressSnapshot in snapshot.child(loginedUser.uid.toString()).child("addresses").children){
                        val address = addressSnapshot.getValue(Address::class.java)
                        if(address?.default == 1){
                            addressList.add(0,address)
                        }else {
                            addressList.add(address!!)
                        }
                    }
                }

                loginedUser.addresses = addressList
                if(loginedUser.addresses!=null && isAdded){
                    recyclerView.adapter = AddressAdapter(requireContext(),
                        loginedUser.addresses!!, database, loginedUser)
                }

                saveUserData(loginedUser)
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

    private fun saveUserData(loginedUser: User) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(loginedUser)
        editor.putString("loginedUser", json)
        editor.apply()
    }

    private fun readUserData(): User {
        val json = sharedPreferences.getString("loginedUser", null)
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
    }

    private fun openChildFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Check if MapFragment is already added
        val isMapFragmentAdded = fragmentManager.findFragmentById(R.id.childFragmentContainer) != null

        if (isMapFragmentAdded) {
            // If MapFragment is already added, replace it with the new fragment
            fragmentTransaction.replace(R.id.childFragmentContainer, fragment)
        } else {
            // If MapFragment is not added, add it to the container
            fragmentTransaction.add(R.id.childFragmentContainer, fragment)
        }

        fragmentTransaction.commit()

    }

    private fun closeChildFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment)
        fragmentTransaction.commit()
        MapsFragment() // Create a new instance of the MapFragment

        bottomSheetDialog.show()
        binding.addAddressFloatingButton.isVisible = true
        binding.addressRecyclerView.isVisible = true
    }

    override fun onButtonClicked(selectedGeocoderData:GeocoderData) {
        closeChildFragment(childFragment)
        geocoderData= selectedGeocoderData
        bottomSheetDialog.findViewById<EditText>(R.id.addressNameEditText).setText(geocoderData!!.name)
        bottomSheetDialog.findViewById<EditText>(R.id.addressLineEditText).setText(geocoderData!!.addressLine)
        bottomSheetDialog.findViewById<EditText>(R.id.postalCodeEditText).setText(geocoderData!!.postalCode)
        bottomSheetDialog.findViewById<EditText>(R.id.cityEditText).setText(geocoderData!!.city)
    }

    private fun resetBottomSheetDialog(){
        bottomSheetDialog.findViewById<EditText>(R.id.addressNameEditText).setText("")
        bottomSheetDialog.findViewById<EditText>(R.id.addressLineEditText).setText("")
        bottomSheetDialog.findViewById<EditText>(R.id.postalCodeEditText).setText("")
        bottomSheetDialog.findViewById<EditText>(R.id.cityEditText).setText("")
        bottomSheetDialog.findViewById<EditText>(R.id.doorNumEditText).setText("")
    }
}