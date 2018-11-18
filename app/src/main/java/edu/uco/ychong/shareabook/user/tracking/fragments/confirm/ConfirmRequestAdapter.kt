package edu.uco.ychong.shareabook.user.tracking.fragments.confirm

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.BookStatus
import edu.uco.ychong.shareabook.book.CustomItemClickListener
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.Book
import kotlinx.android.synthetic.main.book_confirm_row.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class ConfirmRequestAdapter(private val myDataSet: ArrayList<Book>,
                            private val customItemClickListener: CustomItemClickListener) :
        RecyclerView.Adapter<ConfirmRequestAdapter.MyViewHolder>() {

    private var mFireStore: FirebaseFirestore? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        mFireStore = FirebaseFirestore.getInstance()

        val rowView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_confirm_row, parent, false)

        val viewHolder = MyViewHolder(rowView)

        rowView.setOnClickListener {
            customItemClickListener.onItemClick(it, viewHolder.position)
        }

        rowView.id_checkOutButton.setOnClickListener {
            ToastMe.message(parent.context, "Check Out")
            getBorrowRequestId(viewHolder.bookId)
        }

        return viewHolder
    }

    private fun getBorrowRequestId(bookId: String) {
        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)
            ?.collection(REQUESTDOC_PATH)
            ?.whereEqualTo("borrowStatus", BookStatus.ACCEPTED)
            ?.get()
            ?.addOnSuccessListener {
                for (requestSnapShot in it) {
                    checkOutBook(bookId, requestSnapShot.id)
                }
            }
            ?.addOnFailureListener {
            }

    }

    private fun checkOutBook(bookId: String, requestId: String) {
        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)
            ?.collection(REQUESTDOC_PATH)?.document(requestId)
            ?.update("borrowStatus", BookStatus.CHECKED_OUT,
                "borrowDate", getCurrentDateWithFullFormat())
            ?.addOnSuccessListener {
            }
            ?.addOnFailureListener {
            }

        mFireStore?.collection(BOOKDOC_PATH)
            ?.document(bookId)
            ?.update("status", BookStatus.CHECKED_OUT,
                "checkedoutDate", getCurrentDateWithFullFormat())
            ?.addOnSuccessListener {
                notifyDataSetChanged()
            }?.addOnFailureListener {
            }
    }

    private fun getCurrentDateWithFullFormat(): String {
        return LocalDateTime.now()
            .format(
                DateTimeFormatter
                    .ofLocalizedDate(FormatStyle.FULL))
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookRequested = myDataSet[position]
        holder.bookTitle.text = bookRequested.title
        holder.bookAuthor.text = bookRequested.author
        holder.lenderName.text = bookRequested.lender

        if (bookRequested.status == BookStatus.ACCEPTED)
            holder.status.text = "Accepted"

        holder.lenderProfileImage.setImageResource(R.drawable.emptyphoto)
        holder.bookId = bookRequested.id
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lenderProfileImage = itemView.findViewById<ImageView>(R.id.id_lenderProfileImage)
        val lenderName = itemView.findViewById<TextView>(R.id.id_lenderName)
        val bookTitle = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val bookAuthor = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val status = itemView.findViewById<TextView>(R.id.id_status)
        var bookId = ""
    }
}