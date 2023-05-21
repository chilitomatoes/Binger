package com.example.binger.adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.text.Layout
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
import com.example.binger.databinding.FragmentAddressBinding
import com.example.binger.model.Address
import com.example.binger.model.User
import com.google.firebase.database.DatabaseReference
import org.w3c.dom.Text

class AddressAdapter(val context: Context, private val addressList : ArrayList<Address>, private val database: DatabaseReference, private val loginedUser: User): RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.address_item,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddressAdapter.MyViewHolder, position: Int) {
        val currentItem = addressList[position]

        holder.addressName.text = currentItem.name.toString()
        holder.addressLine.text = currentItem.addressLine.toString()
        holder.doorNum.text = currentItem.doorNum.toString()

        if(currentItem.default==1){
            holder.itemLayout.setBackgroundColor(Color.WHITE)
        }

        holder.deleteBtn.setOnClickListener(){
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Confirm to DELETE?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    database.child(loginedUser.uid.toString()).child("addresses").child(currentItem.id.toString()).removeValue()
                    Toast.makeText(context, "Address Deleted Successfully", Toast.LENGTH_SHORT).show()
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
                    for(address in addressList){
                        address.default = 0
                        database.child(loginedUser.uid.toString()).child("addresses").child(address.id.toString()).child("default").setValue(0)
                    }
                    database.child(loginedUser.uid.toString()).child("addresses").child(currentItem.id.toString()).child("default").setValue(1)
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
        return addressList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressName: TextView = itemView.findViewById(R.id.addressNameTextView)
        val addressLine: TextView = itemView.findViewById(R.id.addressLineTextView)
        val doorNum: TextView = itemView.findViewById(R.id.doorNumTextView)
        val isDefault: Button = itemView.findViewById(R.id.setAddressDefaultButton)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteAddressButton)
        val itemLayout: View = itemView.findViewById(R.id.addressItemLayout)
    }
}