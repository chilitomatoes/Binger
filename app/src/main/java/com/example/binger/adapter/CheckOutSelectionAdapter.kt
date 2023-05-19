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
import com.example.binger.ui.paymentMethods.PaymentMethodFragment
import com.google.firebase.database.DatabaseReference

class CheckOutSelectionAdapter(val context: Context): RecyclerView.Adapter<CheckOutSelectionAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckOutSelectionAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selection_item,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CheckOutSelectionAdapter.MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 5
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}