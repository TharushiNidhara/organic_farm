package com.example.itemsazanew

data class DebitCardModel(
    val cardId : String? = null,
    val userId: String? = null,
    val cardType : String? = null,
    val cardNumber: String? = null,
    val month: String? = null,
    val year: String? = null,
    val cvc: String? = null
)