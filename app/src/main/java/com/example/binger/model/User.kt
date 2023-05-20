package com.example.binger.model

data class User(var uid: String ?= null,
                var email : String ?= null,
                var username: String ?= null,
                var userContact: String ?= null,
                var cards: ArrayList<PaymentMethod> ?= null,
                var addresses: ArrayList<Address> ?= null,
                var orders: ArrayList<Order> ?= null)
