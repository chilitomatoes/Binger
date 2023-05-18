package com.example.binger.model

data class Address(var id: String ?= null,
                   var line1: String ?= null,
                   var line2: String ?= null,
                   var postalCode: Int ?= null,
                   var town: String ?= null,
                   var state: String?= null,
                   var name: String ?= null,
                   var default: Int ?= null){

}
