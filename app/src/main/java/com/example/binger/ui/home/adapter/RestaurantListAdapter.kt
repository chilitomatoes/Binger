package com.example.binger.adapter
import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.binger.R
import com.example.binger.ui.home.Hours
import com.example.binger.ui.home.RestaurantModel
import java.text.SimpleDateFormat
import java.util.*

class RestaurantListAdapter(val restaurantList: List<RestaurantModel?>?, val clickListener: RestaurantListClickListener):RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantListAdapter.MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_list_row,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantListAdapter.MyViewHolder, position: Int) {
        holder.bind(restaurantList?.get(position))
        holder.itemView.setOnClickListener{
            clickListener.onItemClick(restaurantList?.get(position))
        }
    }

    override fun getItemCount(): Int {
        return restaurantList?.size!!
    }

    inner class MyViewHolder(view:View):RecyclerView.ViewHolder(view)
    {
        val thumbImage:ImageView=view.findViewById(R.id.thumbImage)
        val tvRestaurantName:TextView=view.findViewById(R.id.tvRestaurantName)
        val tvRestaurantAddress:TextView=view.findViewById(R.id.tvRestaurantAddress)
        fun bind(restaurantModel: RestaurantModel?)
        {
            tvRestaurantName.text=restaurantModel?.name
            tvRestaurantAddress.text="Address: "+restaurantModel?.address

            Glide.with(thumbImage).load(restaurantModel?.image).into(thumbImage)

        }
    }

    private fun getTodatsHours(hours: Hours):String?
    {
        val calender: Calendar= Calendar.getInstance()
        val date:Date=calender.time
        val day:String=SimpleDateFormat("EEEE",Locale.ENGLISH).format(date.time)
        return when(day){
            "Sunday"->hours.Sunday
            "Monday"->hours.Monday
            "Tuesday"->hours.Tuesday
            "Wednesday"->hours.Wednesday
            "Thursday"->hours.Thursday
            "Friday"->hours.Friday
            "Saturday"->hours.Saturday
            else ->hours.Sunday
        }

    }

    interface RestaurantListClickListener
    {
        fun onItemClick(restaurantModel: RestaurantModel?)
    }
}