package com.example.itemsazanew

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class showCards : Fragment() {

    private lateinit var addCardbtn : Button
    private lateinit var cardRecyclerView: RecyclerView
    private lateinit var cardList : ArrayList<DebitCardModel>

    private lateinit var dbRef : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_cards, container, false)

//        get the add card button
        addCardbtn = view.findViewById<Button>(R.id.addCardbtn)

        cardRecyclerView = view.findViewById(R.id.recyclerView)//initialize the recycler view
        cardRecyclerView.layoutManager = LinearLayoutManager(activity)// Set layout manager to the recycler view
        cardRecyclerView.setHasFixedSize(true)

        //initalize card list
        cardList = arrayListOf<DebitCardModel>()

//      set the fragment title
        activity?.title = "Saved Cards"


        //getting card data
        getCardData()

        //navigate to add card interface//
        addCardbtn.setOnClickListener {
            val intent = Intent(activity, addCards::class.java)
            startActivity(intent)
        }

        return view

    }
    //getting saved card details from the database
    private fun getCardData(){
        cardRecyclerView.visibility = View.GONE

        //create connection with database//
        dbRef = FirebaseDatabase.getInstance().getReference("savedCards")

        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                //clear existing card details first
                cardList.clear()

                if(snapshot.exists()){
                    for(cardSnap in snapshot.children){
                        val cardData = cardSnap.getValue(DebitCardModel::class.java)//referencing debit card class and getting data
                        cardList.add(cardData!!)
                    }

                    val cAdapter = cardAdapter(cardList)
                    cardRecyclerView.adapter = cAdapter


                    cAdapter.setOnItemClickListener(object  : cardAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            val intent = Intent(activity,manageCard::class.java)

//                            put extra data to next activity
                            intent.putExtra("cardId",cardList[position].cardId)
                            intent.putExtra("cardNo",cardList[position].cardNumber)
                            intent.putExtra("cardType",cardList[position].cardType)
                            intent.putExtra("expireMonth",cardList[position].month)
                            intent.putExtra("expireYear",cardList[position].year)
                            intent.putExtra("cvc",cardList[position].cvc)

                            startActivity(intent)
                        }
                    })

                    cardRecyclerView.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}