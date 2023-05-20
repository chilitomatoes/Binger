package com.example.binger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.binger.R
import com.example.binger.model.User

class CheckOutSelectionAdapter(val context: Context, val mode: String, val loginedUser: User, val listener: AdapterListener): RecyclerView.Adapter<CheckOutSelectionAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckOutSelectionAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selection_item,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CheckOutSelectionAdapter.MyViewHolder, position: Int) {

        if(mode == "Cards"){
            if(loginedUser.cards?.size ?: null != 0){
                val currentItem = loginedUser.cards!![position]
                holder.selectionName.text = currentItem?.holderName
                holder.selection.text = currentItem?.cardNum.toString()
            }

        }else{
            val currentItem = loginedUser.addresses?.get(position)
            if (currentItem != null) {
                holder.selectionName.text = currentItem.name
                holder.selection.text = currentItem.line1
            }
        }

        holder.itemView.setOnClickListener{
            listener.onItemSelected(position)
        }
    }

    override fun getItemCount(): Int {
        var listSize: Int?= 0
        if(mode == "Cards"){
            listSize = loginedUser.cards?.size
        }else{
            listSize = loginedUser.addresses?.size
        }

        return listSize!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val selectionName: TextView = itemView.findViewById(R.id.selectionNameTextView)
        val selection: TextView = itemView.findViewById(R.id.selectionTextView)
    }

    interface AdapterListener {
        fun onItemSelected(selectedIndex: Int?)
    }
}