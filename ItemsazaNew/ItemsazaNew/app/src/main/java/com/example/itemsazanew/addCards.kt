package com.example.itemsazanew

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class addCards : AppCompatActivity() {

    private lateinit var cardNumber: EditText
    private lateinit var eMonth: EditText
    private lateinit var eYear: EditText
    private lateinit var cvc: EditText
    private lateinit var saveCard: Button
    private lateinit var rg: RadioGroup
    private lateinit var cardTypeError : TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cards)

        rg = findViewById(R.id.m_payment_type)
        cardNumber = findViewById(R.id.m_card_no)
        eMonth = findViewById(R.id.m_month)
        eYear = findViewById(R.id.m_year)
        cvc = findViewById(R.id.m_cvc)
        saveCard = findViewById(R.id.edit_card)
        cardTypeError = findViewById(R.id.card_type_error)

//        set Acitivity to Add card Details
        setTitle("Add a Card")

        saveCard.setOnClickListener {
            saveCardDetails()
        }

    }

    private fun saveCardDetails() {

        var cardNumberText = cardNumber.text.toString()
        var eMonthText = eMonth.text.toString()
        var eYearText = eYear.text.toString()
        var cvcText = cvc.text.toString()
        var selectedCardType = rg!!.checkedRadioButtonId


        //        firebase database reference
        dbRef = FirebaseDatabase.getInstance().getReference("savedCards")
        val uid = FirebaseAuth.getInstance()?.uid.toString()
        val cardId = dbRef.push().key.toString() //generate unique id for each card

//        create variable called errorCount for validation the form
        var errorCount = validateInsertCardDetails(cardNumberText, eMonthText, eYearText, cvcText,selectedCardType)

        if (errorCount == 0) {

            val btn = findViewById<RadioButton>(selectedCardType)
            val radioResult = btn.text.toString()

            //create card model
            val cardDetails = DebitCardModel(
                cardId,
                uid,
                radioResult,
                cardNumberText,
                eMonthText,
                eYearText,
                cvcText
            )

            dbRef.child(cardId).setValue(cardDetails).addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Card Added Successfully!",
                    Toast.LENGTH_LONG
                ).show()

                //clear text boxes
                cardNumber.text.clear()
                eMonth.text.clear()
                eYear.text.clear()
                cvc.text.clear()

                //back to the previous screen after adding the card in to database
                finish()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this,"Please fill all the required fields",Toast.LENGTH_LONG).show()
        }
    }

    private fun validateInsertCardDetails(
        cardNumberText: String,
        eMonthText: String,
        eYearText: String,
        cvcText: String,
        selectedCardId : Int
    ): Int {

        var errorCount = 0

        if (cardNumberText.isEmpty()) {
            errorCount++
            cardNumber.error = "Please enter card number"
        }
        if (cardNumberText.isNotEmpty() && cardNumberText.length != 16) {
            errorCount++
            cardNumber.error = "Card number should have 16 numbers"
        }
        if (eMonthText.isEmpty()) {
            errorCount++
            eMonth.error = "Please enter expiration month"
        }

        if (eMonthText.isNotEmpty() && (eMonthText.toInt() < 0 || eMonthText.toInt() > 12 || eMonthText.length != 2)) {
            errorCount++
            eMonth.error = "Please enter valid Expiration month"
        }

        if (eYearText.isEmpty()) {
            errorCount++
            eYear.error = "Please enter expiration year"
        }
        if (eYearText.isNotEmpty() && (eYearText.toInt() < 0 || eYearText.length != 2)) {
            errorCount++
            eYear.error = "Please enter valid expiration year"
        }

        if (cvcText.isEmpty()) {
            errorCount++
            cvc.error = "Please enter CVC number"
        }
        if (cvcText.isNotEmpty() && (cvcText.length < 3 || cvcText.length > 4)) {
            errorCount++
            cvc.error = "Please enter valid CVC number"
        }
        if(selectedCardId == -1){
            errorCount++
            cardTypeError.text = "Please select card type"
            cardTypeError.visibility = View.VISIBLE
        }else{
            cardTypeError.visibility = View.GONE
        }

        return errorCount
    }
}