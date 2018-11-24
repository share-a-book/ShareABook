package edu.uco.ychong.shareabook.user.history.history

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.model.History

class HistoryAdapter(private val myDataSet: ArrayList<History>,
                private val historyFragment: HistoryFragment) :
        RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rowView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_history_row, parent, false)
        val viewHolder = MyViewHolder(rowView)
        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = myDataSet[position]
        holder.bookTitle.text = history.bookTitle
        holder.bookAuthor.text = history.bookAuthor
        holder.borrowerName.text = history.borrowerName
        holder.lenderName.text = history.lenderName

        if (history.bookImageUrl != "") {
            Picasso.get().load(history.bookImageUrl).into(holder.bookImage)
        } else {
            Picasso.get().load(R.drawable.emptyphoto).into(holder.bookImage)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val borrowerName = itemView.findViewById<TextView>(R.id.id_borrowerName)
        val lenderName = itemView.findViewById<TextView>(R.id.id_lenderName)
        val bookTitle = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val bookAuthor = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val bookImage = itemView.findViewById<ImageView>(R.id.id_bookImage)
        val confirmReturn = itemView.findViewById<Button>(R.id.id_confirmReturnButton)
    }
}