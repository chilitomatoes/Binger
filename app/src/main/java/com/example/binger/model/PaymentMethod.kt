package com.example.binger.model

data class PaymentMethod(var id: String ?= null,
                         var cardNum: Long ?= null,
                         var cvc: Int ?= null,
                         var expMon: Int ?= null,
                         var expYear: Int ?= null,
                         var holderName: String ?= null,
                         var default: Int ?= null){

}
