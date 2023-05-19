package com.example.binger.ui.home.ViewModel

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.example.binger.ui.home.Menus
import com.example.binger.ui.home.RestaurantModel

class menuViewModel() : ViewModel(){
    var restaurantSelected:RestaurantModel?=null
    var menus:ArrayList<Menus> = ArrayList()
    var itemListInCart:ArrayList<Menus>? = ArrayList()
    var menuItem:ArrayList<Menus>? = ArrayList()




}


