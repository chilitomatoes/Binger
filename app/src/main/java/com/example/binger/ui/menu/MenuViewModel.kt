package com.example.binger.ui.menu


import androidx.lifecycle.ViewModel
import com.example.binger.model.Menus
import com.example.binger.model.RestaurantModel


class menuViewModel() : ViewModel(){
    var restaurantSelected: RestaurantModel?=null
    var menus:ArrayList<Menus> = ArrayList()
    var itemListInCart:ArrayList<Menus>? = ArrayList()
    var menuItem:ArrayList<Menus>? = ArrayList()




}


