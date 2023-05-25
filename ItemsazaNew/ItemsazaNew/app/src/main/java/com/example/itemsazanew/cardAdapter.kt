package com.example.itemsazanew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class cardAdapter(
    private val cardList: ArrayList<DebitCardModel>
    ) :
    RecyclerView.Adapter<cardAdapter.CardViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener:onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val cardView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardViewHolder(cardView,mListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.cardNumber.text = card.cardNumber
        holder.expireMonth.text = card.month
        holder.expireYear.text = card.year
        holder.cvc.text = card.cvc
        if(card.cardType?.compareTo("Visa")==0){
            holder.cardType.setImageResource(R.drawable.visa_logo)
        }else if (card.cardType?.compareTo("Master Card") == 0){
            holder.cardType.setImageResource(R.drawable.master_card)
        }else if(card.cardType?.compareTo("American Express")==0){
            holder.cardType.setImageResource(R.drawable.american_express)
        }
    }


    override fun getItemCount(): Int {
        return cardList.size
    }

    class CardViewHolder(itemView: View, clickListener:onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val cardNumber : TextView = itemView.findViewById(R.id.card_no)
        val expireMonth : TextView= itemView.findViewById(R.id.month)
        val expireYear : TextView =  itemView.findViewById(R.id.year)
        val cvc : TextView = itemView.findViewById(R.id.addedCvc)
        val cardType : ImageView = itemView.findViewById(R.id.card_type)

        init{
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }

        }

    }
}