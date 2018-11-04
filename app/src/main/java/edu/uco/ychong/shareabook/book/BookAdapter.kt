package edu.uco.ychong.shareabook.book

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.model.Book
import java.util.*

class BookAdapter(private val myDataSet: ArrayList<Book>, val customItemClickListener: CustomItemClickListener) :
    RecyclerView.Adapter<BookAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.book_card_layout, parent, false)
        val viewHolder = MyViewHolder(rowView)

        rowView.setOnClickListener {
            customItemClickListener.onItemClick(it, viewHolder.position)
        }

        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val book = myDataSet[position]

//        val dateTime = LocalDate.parse(book.datePosted, DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))

        holder.tile.text = book.title
        holder.author.text = "Author: ${book.author}"
        holder.datePosted.text = "Date posted: ${book.datePosted}"
        holder.status.text = "Status: ${book.status}"
        holder.image.setImageResource(R.drawable.emptyphoto)
        holder.postedBy.text = "Posted By: ${book.lender}"
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tile = itemView.findViewById<TextView>(R.id.info_bookTitle)
        val author = itemView.findViewById<TextView>(R.id.info_bookAuthor)
        val datePosted = itemView.findViewById<TextView>(R.id.info_bookDatePosted)
        val status = itemView.findViewById<TextView>(R.id.info_bookStatus)
        val image = itemView.findViewById<ImageView>(R.id.info_bookImage)
        val postedBy = itemView.findViewById<TextView>(R.id.postedBy)
    }
}