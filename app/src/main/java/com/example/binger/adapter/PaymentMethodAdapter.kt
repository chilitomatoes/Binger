package com.example.binger.adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.binger.R
import com.example.binger.databinding.FragmentPaymentmethodBinding
import com.example.binger.model.PaymentMethod
import com.example.binger.model.User
import com.example.binger.ui.paymentMethods.PaymentMethodFragment
import com.google.firebase.database.DatabaseReference

class PaymentMethodAdapter(val context: Context, private val paymentMethodList : ArrayList<PaymentMethod>, private val database: DatabaseReference, private val loginedUser: User): RecyclerView.Adapter<PaymentMethodAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentMethodAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.paymentmethod_item,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentMethodAdapter.MyViewHolder, position: Int) {
        val currentItem = paymentMethodList[position]

        holder.cardNum.text = currentItem.cardNum.toString()
        holder.holderName.text = currentItem.holderName

        if(currentItem.default==1){
            holder.itemLayout.setBackgroundColor(Color.WHITE)
            holder.deleteBtn.isVisible = false
        }

        holder.deleteBtn.setOnClickListener(){
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Confirm to DELETE?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    database.child(loginedUser.uid.toString()).child("cards").child(currentItem.id.toString()).removeValue()
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
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Confirm to SET AS DEFAULT?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    for(paymentMethod in paymentMethodList){
                        paymentMethod.default = 0
                        database.child(loginedUser.uid.toString()).child("cards").child(paymentMethod.id.toString()).child("default").setValue(0)
                    }
                    database.child(loginedUser.uid.toString()).child("cards").child(currentItem.id.toString()).child("default").setValue(1)
                    Toast.makeText(context, "Set Default Successfully", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

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
        val itemLayout: View = itemView.findViewById(R.id.paymentMethodItemLayout)
    }
}