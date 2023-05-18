package com.example.binger.adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.binger.R
import com.example.binger.databinding.FragmentPaymentmethodBinding
import com.example.binger.model.PaymentMethod
import com.example.binger.ui.paymentMethods.PaymentMethodFragment
import com.google.firebase.database.DatabaseReference

class AddressAdapter(val context: Context, private val paymentMethodList : ArrayList<PaymentMethod>, private val database: DatabaseReference): RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {

    private var _binding: FragmentPaymentmethodBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.address_item,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddressAdapter.MyViewHolder, position: Int) {
        val currentItem = paymentMethodList[position]

        holder.cardNum.text = currentItem.cardNum.toString()
        holder.holderName.text = currentItem.holderName

        holder.deleteBtn.setOnClickListener(){
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to DELETE?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    database.child(currentItem.id.toString()).removeValue()
                    Toast.makeText(context, "Card Deleted Successfully", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }

        holder.isDefault.setOnClickListener(){

            for(paymentMethod in paymentMethodList){
                paymentMethod.default = 0
                database.child(paymentMethod.id.toString()).child("default").setValue(0)
            }
            database.child(currentItem.id.toString()).child("default").setValue(1)
            Toast.makeText(context, "Set Default Successfully", Toast.LENGTH_SHORT).show()
        }

        holder.isDefault.isEnabled = currentItem.default != 1
    }

    override fun getItemCount(): Int {
        return paymentMethodList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val holderName: TextView = itemView.findViewById(R.id.holderNameTextView)
        val cardNum: TextView = itemView.findViewById(R.id.cardNumTextView)
        val isDefault: Button = itemView.findViewById(R.id.setPaymentDefaultButton)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deletePaymentMethodButton)
    }
}