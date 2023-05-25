package com.example.itemsazanew

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class manageCard : AppCompatActivity() {
    private lateinit var cardNumber: EditText
    private lateinit var eMonth: EditText
    private lateinit var eYear: EditText
    private lateinit var cvc: EditText
    private lateinit var rg: RadioGroup
    private lateinit var cardTypeError : TextView

    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_card)

        setTitle("Manage Saved Card")

        cardNumber = findViewById(R.id.m_card_no)
        eMonth = findViewById(R.id.m_month)
        eYear = findViewById(R.id.m_year)
        cvc = findViewById(R.id.m_cvc)
        rg = findViewById(R.id.m_payment_type)
        editButton = findViewById(R.id.edit_card)
        deleteButton = findViewById(R.id.delete_card)
        cardTypeError = findViewById(R.id.edit_card_type_error)


        //bind values to the views
        setValuesToViews()

        //active when clicks add card function
        editButton.setOnClickListener {
            editCardDetails()
        }

        //active when user clicks delete button
        deleteButton.setOnClickListener {
            deleteCard()
        }

    }

    private fun setValuesToViews() {
        cardNumber.setText(intent.getStringExtra("cardNo"))
        eMonth.setText(intent.getStringExtra("expireMonth"))
        eYear.setText(intent.getStringExtra("expireYear"))
        cvc.setText(intent.getStringExtra("cvc"))
        val radioButtonText = intent.getStringExtra("cardType")!!

        for (rbPosition in 0 until rg.childCount) {
            val rb = rg.getChildAt(rbPosition) as RadioButton
            val rbText = rb.text.toString()

            if (rbText.compareTo(radioButtonText) == 0) {
                rb.isChecked = true
            } else if (rbText.compareTo(radioButtonText) == 0) {
                rb.isChecked = true
            } else if (rbText.compareTo(radioButtonText) == 0) {
                rb.isChecked = true
            }
        }
    }


    private fun editCardDetails() {
        val cardNumberText = cardNumber.text.toString()
        val expireMonth = eMonth.text.toString()
        val expireYear = eYear.text.toString()
        val cvc = cvc.text.toString()
        var selectedCardType = rg!!.checkedRadioButtonId

        val cardId = intent.getStringExtra("cardId")

 //database connection for edit
        dbRef = FirebaseDatabase.getInstance().getReference("savedCards")
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid.toString()

//        calling to edit details validation function
        val errorCount = validateInsertCardDetails(
            cardNumberText,
            expireMonth,
            expireYear,
            cvc,
            selectedCardType
        )

        if (errorCount == 0) {
//            if radio button is selected only, we get value here
            val btn = findViewById<RadioButton>(selectedCardType)
            val radioResult = btn.text.toString()

//            create edit class model
            val card =
                DebitCardModel(
                    cardId,
                    uid,
                    radioResult,
                    cardNumberText,
                    expireMonth,
                    expireYear,
                    cvc
                )

            // Update the card details in the database
            card.cardId?.let { it1 ->
                dbRef.child(it1).setValue(card).addOnSuccessListener {
                    // Show success message
                    Toast.makeText(
                        this,
                        "card updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to the card showing activity
                    var intent = Intent(this, showCards::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    // Show error message
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }else{
            Toast.makeText(this,"Plase fill required fields",Toast.LENGTH_LONG).show()
        }

    }

    private fun deleteCard() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete your saved card ?")
        builder.setMessage("Are you sure ?")
        builder.setIcon(R.drawable.delete_bin)
        builder.setCancelable(false)

//        if user click yes to dialog box then this shows
        builder.setPositiveButton("Yes") { _, _ ->
            val cardId = intent.getStringExtra("cardId")

            //database connection for delete
            dbRef = FirebaseDatabase.getInstance().getReference("savedCards")

            // Delete the saved card from the database
            cardId?.let { it1 ->
                dbRef.child(it1).removeValue().addOnSuccessListener {
                    // Show success message
                    Toast.makeText(
                        this,
                        "Card deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to the showing card activity
                    var intent = Intent(this, showCards::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    // Show error message
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

//        otherwise this shows
        builder.setNegativeButton("Cancel") { _, _ ->

        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    //    edit details validation function
    private fun validateInsertCardDetails(
        cardNumberText: String,
        eMonthText: String,
        eYearText: String,
        cvcText: String,
        selectedCardId: Int
    ): Int {

//        set error count to 0
        var errorCount = 0

//        check Card number is empty, If empty error count will be increased by 1 and show an error
        if (cardNumberText.isEmpty()) {
            errorCount++
            cardNumber.error = "Please enter card number"
        }
//        check Card number is not empty and check whether card number length is less than 16
        if (cardNumberText.isNotEmpty() && cardNumberText.length != 16) {
            errorCount++
            cardNumber.error = "Card number should have 16 numbers"
        }
//        check expiration month is empty
        if (eMonthText.isEmpty()) {
            errorCount++
            eMonth.error = "Please enter expiration month"
        }

//        check expiration month is not Empty, and check whether month is valid month or not
        if (eMonthText.isNotEmpty() && (eMonthText.toInt() < 0 || eMonthText.toInt() > 12 || eMonthText.length != 2)) {
            errorCount++
            eMonth.error = "Please enter valid Expiration month"
        }

        //        check expiration year is empty
        if (eYearText.isEmpty()) {
            errorCount++
            eYear.error = "Please enter expiration year"
        }

//        check expiration year is not Empty, and check whether year is valid month or not
        if (eYearText.isNotEmpty() && (eYearText.toInt() < 0 || eYearText.length != 2)) {
            errorCount++
            eYear.error = "Please enter valid expiration year"
        }

        if (cvcText.isEmpty()) {
            errorCount++
            cvc.error = "Plaese enter CVC number"
        }
        if (cvcText.isNotEmpty() && (cvcText.length < 3 || cvcText.length > 4)) {
            errorCount++
            cvc.error = "Please enter valid CVC number"
        }
        if (selectedCardId == -1) {
            errorCount++
            cardTypeError.text = "Please select card type"
            cardTypeError.visibility = View.VISIBLE
        } else {
            cardTypeError.visibility = View.GONE
        }

        return errorCount
    }

}