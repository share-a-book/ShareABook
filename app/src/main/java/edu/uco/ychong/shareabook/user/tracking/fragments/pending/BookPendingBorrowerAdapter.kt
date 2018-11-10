package edu.uco.ychong.shareabook.user.tracking.fragments.pending

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.BookStatus
import edu.uco.ychong.shareabook.book.CustomItemClickListener
import edu.uco.ychong.shareabook.model.Book

class BookPendingBorrowerAdapter(private val myDataSet: ArrayList<Book>,
                                 private val customItemClickListener: CustomItemClickListener) :
        RecyclerView.Adapter<BookPendingBorrowerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rowView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_pending_borrower_row, parent, false)

        val viewHolder = MyViewHolder(rowView)

        rowView.setOnClickListener {
            customItemClickListener.onItemClick(it, viewHolder.position)
        }

        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookRequested = myDataSet[position]
        holder.bookTitle.text = bookRequested.title
        holder.bookAuthor.text = bookRequested.author
        holder.lenderName.text = bookRequested.lender

        if (bookRequested.status == BookStatus.REQUEST_PENDING)
            holder.status.text = "Waiting for owner response..."
        holder.lenderProfile.setImageResource(R.drawable.emptyphoto)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lenderProfile = itemView.findViewById<ImageView>(R.id.id_lenderProfileImage)
        val lenderName = itemView.findViewById<TextView>(R.id.id_lenderName)
        val bookTitle = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val bookAuthor = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val status = itemView.findViewById<TextView>(R.id.id_status)
    }
}