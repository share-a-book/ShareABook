package edu.uco.ychong.shareabook.user.tracking.fragments.request

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
import kotlinx.android.synthetic.main.book_pending_row.view.*

class BookRequestAdapter(private val myDataSet: ArrayList<Book>,
                         private val customItemClickListener: CustomItemClickListener) :
        RecyclerView.Adapter<BookRequestAdapter.MyViewHolder>() {

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

        rowView.id_rejectButton.setOnClickListener {
            ToastMe.message(parent.context, "Reject request")
            getBorrowRequestId(viewHolder.bookId, false)
            //rejectRequest(viewHolder.bookId)
        }

        rowView.id_acceptButton.setOnClickListener {
            ToastMe.message(parent.context, "Accept request")
            ToastMe.message(parent.context, viewHolder.bookId)
            getBorrowRequestId(viewHolder.bookId, true)
            //acceptRequest(viewHolder.bookId)
        }

        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookRequest = myDataSet[position]
        holder.tile.text = "Title: ${bookRequest.title}"
        holder.author.text = "Author: ${bookRequest.author}"
        holder.borrowerName.text = bookRequest.borrower
        holder.imageProfile.setImageResource(R.drawable.emptyphoto)
        holder.bookId = bookRequest.id
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tile = itemView.findViewById<TextView>(R.id.id_bookTitle)
        val author = itemView.findViewById<TextView>(R.id.id_bookAuthor)
        val borrowerName = itemView.findViewById<TextView>(R.id.id_borrower)
        val imageProfile = itemView.findViewById<ImageView>(R.id.id_lenderProfileImage)
        var bookId = ""
    }

    private fun getBorrowRequestId(bookId: String, acceptanceStatus: Boolean) {
        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)
            ?.collection(REQUESTDOC_PATH)
            ?.whereEqualTo("borrowStatus", BookStatus.REQUEST_PENDING)
            ?.get()
            ?.addOnSuccessListener {
                for (requestSnapShot in it) {
                    if(acceptanceStatus == true) {
                        acceptRequest(bookId, requestSnapShot.id)
                    }
                    else {
                        rejectRequest(bookId, requestSnapShot.id)
                    }
                }
            }
            ?.addOnFailureListener {
            }

    }

    private fun acceptRequest(bookId: String, requestId: String) {
        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)
            ?.collection(REQUESTDOC_PATH)?.document(requestId)
            ?.update("borrowStatus", BookStatus.ACCEPTED)
            ?.addOnSuccessListener {
            }
            ?.addOnFailureListener {
            }

        mFireStore?.collection(BOOKDOC_PATH)
                ?.document(bookId)
                ?.update("status", BookStatus.ACCEPTED)
                ?.addOnSuccessListener {
                    notifyDataSetChanged()
                }?.addOnFailureListener {
                }
    }

    private fun rejectRequest(bookId: String, requestId: String) {
        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)
            ?.collection(REQUESTDOC_PATH)?.document(requestId)
            ?.update("borrowStatus", BookStatus.REJECTED)
            ?.addOnSuccessListener {
            }
            ?.addOnFailureListener {
            }

        mFireStore?.collection(BOOKDOC_PATH)
                ?.document(bookId)
                ?.update("status", BookStatus.AVAILABLE,
                    "borrower", "")
                ?.addOnSuccessListener {
                    notifyDataSetChanged()
                }?.addOnFailureListener {
                }
    }
}
