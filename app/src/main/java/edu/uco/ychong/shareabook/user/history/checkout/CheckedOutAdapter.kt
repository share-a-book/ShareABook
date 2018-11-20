package edu.uco.ychong.shareabook.user.history.checkout

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.model.History
import edu.uco.ychong.shareabook.model.HistoryStatus

class CheckedOutAdapter(private val myDataSet: ArrayList<History>,
                               private val checkedOutFragment: CheckedOutFragment) :
    RecyclerView.Adapter<CheckedOutAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rowView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_checkedout_row, parent, false)

        val viewHolder = MyViewHolder(rowView)

        rowView.setOnClickListener {

        }

        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookCheckedOut = myDataSet[position]
        holder.bookTitle.text = bookCheckedOut.bookTitle
        holder.bookAuthor.text = bookCheckedOut.bookAuthor
        holder.lenderName.text = bookCheckedOut.lenderName
        holder.lenderProfile.setImageResource(R.drawable.emptyphoto)

        if (bookCheckedOut.historyStatus == HistoryStatus.CHECKED_OUT)
            holder.status.text = "Checked Out"
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lenderProfile = itemView.findViewById<ImageView>(R.id.id_lenderProfileImage)
        val lenderName = itemView.findViewById<TextView>(R.id.id_lenderName)
        val bookTitle = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val bookAuthor = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val status = itemView.findViewById<TextView>(R.id.id_status)
    }
}
