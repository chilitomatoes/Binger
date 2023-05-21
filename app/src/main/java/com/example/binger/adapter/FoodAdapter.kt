package com.example.binger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FoodAdapter (private val foodList : ArrayList<Food>) : RecyclerView.Adapter<FoodAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.food_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = foodList[position]

        holder.foodName.text = currentItem.name
        holder.foodPrice.text = currentItem.price
        holder.restName.text = currentItem.resName
        Glide.with(holder.thumbImage).load(currentItem.url).into(holder.thumbImage)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var thumbImage: ImageView = itemView.findViewById(R.id.thumbImage)
        val foodName : TextView = itemView.findViewById(R.id.foodName)
        val foodPrice : TextView = itemView.findViewById(R.id.foodPrice)
        val restName : TextView = itemView.findViewById(R.id.restName)

    }

}