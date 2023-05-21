package com.example.binger.model

data class Address(var id: String ?= null,
                   var doorNum: String?= null,
                   var addressLine: String ?= null,
                   var city: String ?= null,
                   var postalCode: Int ?= null,
                   var name: String ?= null,
                   var default: Int ?= null){
}
