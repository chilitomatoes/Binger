package com.example.binger.model


data class Order(
                 val  address: Address?= null,
                 val food:ArrayList<Menus>?= null,
                 val resName:String?= null
)
