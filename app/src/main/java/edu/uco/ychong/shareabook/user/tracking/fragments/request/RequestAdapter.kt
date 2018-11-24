package edu.uco.ychong.shareabook.user.tracking.fragments.request

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
import kotlinx.android.synthetic.main.book_request_row.view.*

class RequestAdapter(private val myDataSet: ArrayList<Request>, private val requestFragment: RequestFragment) :
        RecyclerView.Adapter<RequestAdapter.MyViewHolder>() {

    private var mFireStore: FirebaseFirestore? = null
    lateinit var parentActivity: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        mFireStore = FirebaseFirestore.getInstance()

        val rowView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_request_row, parent, false)

        val viewHolder = MyViewHolder(rowView)

        rowView.id_acceptButton.setOnClickListener {
            Log.d(TESTTAG, "accept ${myDataSet[viewHolder.layoutPosition]}")
            Log.d(TESTTAG, "layout position: ${viewHolder.layoutPosition}")
            requestFragment.acceptRequest(myDataSet[viewHolder.layoutPosition])
        }

        rowView.id_rejectButton.setOnClickListener {
            Log.d(TESTTAG, "reject ${myDataSet[viewHolder.layoutPosition]}")
            Log.d(TESTTAG, "layout position: ${viewHolder.layoutPosition}")
            requestFragment.rejectRequest(myDataSet[viewHolder.layoutPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val request = myDataSet[position]
        holder.tile.text = "Title: ${request.bookTitle}"
        holder.author.text = "Author: ${request.bookAuthor}"
        holder.borrowerName.text = request.borrowerName

        if (request.bookImageUrl.isNullOrEmpty()) {
            holder.bookImage.setImageResource(R.drawable.emptyphoto)
        }
        else {
            Picasso.get().load(request.bookImageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.bookImage)
        }

        holder.bookId = request.bookId
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tile = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val author = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val borrowerName = itemView.findViewById<TextView>(R.id.id_borrower)
        val bookImage = itemView.findViewById<ImageView>(R.id.id_bookImage)
        var bookId = ""
    }
}
