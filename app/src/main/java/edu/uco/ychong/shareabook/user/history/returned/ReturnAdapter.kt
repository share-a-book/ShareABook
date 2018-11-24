package edu.uco.ychong.shareabook.user.history.returned

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.model.History
import edu.uco.ychong.shareabook.model.HistoryStatus


class ReturnAdapter(private val myDataSet: ArrayList<History>,
                    private val returnFragment: ReturnedFragment) :
            RecyclerView.Adapter<ReturnAdapter.MyViewHolder>() {


        private var mAuth: FirebaseAuth?= null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val rowView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.book_returning_row, parent, false)
            val viewHolder = MyViewHolder(rowView)

            viewHolder.confirmReturnButton.setOnClickListener {
                returnFragment.confirmReturn(viewHolder.layoutPosition)
            }
            return viewHolder
        }

        override fun getItemCount(): Int = myDataSet.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val bookReturning = myDataSet[position]
            holder.bookTitle.text = bookReturning.bookTitle
            holder.bookAuthor.text = bookReturning.bookAuthor
            holder.borrowerName.text = bookReturning.borrowerName
            holder.bookImage.setImageResource(R.drawable.emptyphoto)

            if (bookReturning.historyStatus == HistoryStatus.RETURN_PROCESSING)
                holder.status.text = "Returning"

            if (!bookReturning.bookImageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(bookReturning.bookImageUrl)
                    .fit().centerCrop()
                    .into(holder.bookImage)
            } else {
                holder.bookImage.setImageResource(R.drawable.emptyphoto)
            }
        }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage = itemView.findViewById<ImageView>(R.id.id_bookImage)
        val borrowerName = itemView.findViewById<TextView>(R.id.id_borrowerName)
        val bookTitle = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val bookAuthor = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val status = itemView.findViewById<TextView>(R.id.id_status)
        val confirmReturnButton = itemView.findViewById<Button>(R.id.id_confirmReturnButton)
    }
}

