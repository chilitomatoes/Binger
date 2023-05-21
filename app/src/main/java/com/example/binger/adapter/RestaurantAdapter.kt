package com.example.binger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RestaurantAdapter(private val restaurantList : ArrayList<Restaurant>) : RecyclerView.Adapter<RestaurantAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = restaurantList[position]

        holder.resName.text = currentItem.name
        holder.resAddress.text = currentItem.address
        holder.resCharge.text = currentItem.delivery_charge
        Glide.with(holder.thumbImage).load(currentItem.image).into(holder.thumbImage)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var thumbImage: ImageView = itemView.findViewById(R.id.thumbImage)
        val resName : TextView = itemView.findViewById(R.id.restaurantName)
        val resAddress : TextView = itemView.findViewById(R.id.restaurantAddress)
        val resCharge : TextView = itemView.findViewById(R.id.restaurantCharge)

    }

}