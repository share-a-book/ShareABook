package edu.uco.ychong.shareabook.user.tracking.fragments.pending

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.CustomItemClickListener
import edu.uco.ychong.shareabook.model.Request
import edu.uco.ychong.shareabook.model.RequestStatus

const val CANCEL_BOOK_REQUEST = "CANCEL_BOOK_REQUEST"
const val CANCEL_BOOK_REQUEST_ID = "CANCEL_BOOK_REQUEST_ID"

class PendingAdapter(private val myDataSet: ArrayList<Request>,
                     private val customItemClickListener: CustomItemClickListener) :
        RecyclerView.Adapter<PendingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rowView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_pending_borrower_row, parent, false)

        val viewHolder = MyViewHolder(rowView)

        rowView.setOnClickListener {
            customItemClickListener.onItemClick(it, viewHolder.position)

            val intent = Intent(parent.context, CancelBookRequestActivity::class.java)
            val theBook = myDataSet[viewHolder.position]
            intent.putExtra(CANCEL_BOOK_REQUEST, theBook)
            intent.putExtra(CANCEL_BOOK_REQUEST_ID, theBook.id)

            parent.context.startActivity(intent)
        }

        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookRequested = myDataSet[position]
        holder.bookTitle.text = bookRequested.bookTitle
        holder.bookAuthor.text = bookRequested.bookAuthor
        holder.lenderName.text = bookRequested.lenderEmail

        if (bookRequested.bookImageUrl.isNullOrEmpty()) {
            holder.bookImage.setImageResource(R.drawable.emptyphoto)
        } else {
            Picasso.get().load(bookRequested.bookImageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.bookImage)
        }

        if (bookRequested.requestStatus == RequestStatus.REQUEST_PENDING)
            holder.status.text = "Waiting for owner response..."
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage = itemView.findViewById<ImageView>(R.id.id_bookImage)
        val lenderName = itemView.findViewById<TextView>(R.id.id_lenderName)
        val bookTitle = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val bookAuthor = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val status = itemView.findViewById<TextView>(R.id.id_status)
    }
}