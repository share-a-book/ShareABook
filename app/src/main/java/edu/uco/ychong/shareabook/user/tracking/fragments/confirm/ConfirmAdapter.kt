package edu.uco.ychong.shareabook.user.tracking.fragments.confirm

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.model.Request
import edu.uco.ychong.shareabook.model.RequestStatus
import kotlinx.android.synthetic.main.book_confirm_row.view.*


class ConfirmAdapter(private val myDataSet: ArrayList<Request>, private val confirmFragment: ConfirmFragment) :
        RecyclerView.Adapter<ConfirmAdapter.MyViewHolder>() {

    private var mFireStore: FirebaseFirestore? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        mFireStore = FirebaseFirestore.getInstance()

        val rowView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_confirm_row, parent, false)

        val viewHolder = MyViewHolder(rowView)

        rowView.id_checkoutButton.setOnClickListener {
            Log.d(TESTTAG, "checkout ${myDataSet[viewHolder.layoutPosition]}")
            Log.d(TESTTAG, "layout position: ${viewHolder.layoutPosition}")
            confirmFragment.checkout(myDataSet[viewHolder.layoutPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val request = myDataSet[position]
        holder.bookTitle.text = request.bookTitle
        holder.bookAuthor.text = "By ${request.bookAuthor}"
        holder.lenderName.text = request.lenderEmail

        if (request.bookImageUrl.isNullOrEmpty()) {
            holder.bookImage.setImageResource(R.drawable.emptyphoto)
        } else {
            Picasso.get()
                    .load(request.bookImageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.bookImage)
        }

        holder.bookId = request.bookId

        val status = request. requestStatus
        if (status == RequestStatus.REQUEST_ACCEPTED)
            holder.status.text = "Request Accepted"
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage = itemView.findViewById<ImageView>(R.id.id_bookImage)
        val lenderName = itemView.findViewById<TextView>(R.id.id_lenderName)
        val bookTitle = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val bookAuthor = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val status = itemView.findViewById<TextView>(R.id.id_status)
        var bookId = ""
    }
}