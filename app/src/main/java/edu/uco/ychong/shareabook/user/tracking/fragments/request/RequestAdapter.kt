package edu.uco.ychong.shareabook.user.tracking.fragments.request

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.CustomItemClickListener
import edu.uco.ychong.shareabook.model.Request

class RequestAdapter(private val myDataSet: ArrayList<Request>,
                     private val customItemClickListener: CustomItemClickListener) :
        RecyclerView.Adapter<RequestAdapter.MyViewHolder>() {

    private var mFireStore: FirebaseFirestore? = null
    lateinit var parentActivity: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        mFireStore = FirebaseFirestore.getInstance()

        val rowView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_pending_row, parent, false)

        val viewHolder = MyViewHolder(rowView)

        rowView.setOnClickListener {
            customItemClickListener.onItemClick(it, viewHolder.position)
        }

        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val request = myDataSet[position]
        holder.tile.text = "Title: ${request.bookTitle}"
        holder.author.text = "Author: ${request.bookAuthor}"
        holder.borrowerName.text = request.borrowerName
        holder.imageProfile.setImageResource(R.drawable.emptyphoto)
        holder.bookId = request.bookId
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tile = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val author = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val borrowerName = itemView.findViewById<TextView>(R.id.id_borrower)
        val imageProfile = itemView.findViewById<ImageView>(R.id.id_lenderProfileImage)
        var bookId = ""
    }
}
