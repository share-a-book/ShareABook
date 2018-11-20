package edu.uco.ychong.shareabook.book

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.BookStatus


class BookAdapter(private val myDataSet: ArrayList<Book>,
                  private val customItemClickListener: CustomItemClickListener) :
    RecyclerView.Adapter<BookAdapter.MyViewHolder>() {

    private var mFireStore: FirebaseFirestore? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        mFireStore = FirebaseFirestore.getInstance()
        val rowView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_card_layout, parent, false)
        val viewHolder = MyViewHolder(rowView)

        rowView.setOnClickListener {
            customItemClickListener.onItemClick(it, viewHolder.position)
        }
        return viewHolder
    }

    override fun getItemCount(): Int = myDataSet.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val book = myDataSet[position]
        holder.tile.text = book.title
        holder.author.text = "Author: ${book.author}"
        holder.datePosted.text = "Date posted: ${book.datePosted}"
        holder.status.text = "Status: ${book.status}"
        holder.image.setImageResource(R.drawable.emptyphoto)
        holder.postedBy.text = "Posted By: ${book.lenderName}"
        holder.genre.text = "Genre: ${book.genre}"
        holder.description.text = "Description: ${book.description}"
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tile = itemView.findViewById<TextView>(R.id.id_infoBookTitle)
        val author = itemView.findViewById<TextView>(R.id.id_infoBookAuthor)
        val datePosted = itemView.findViewById<TextView>(R.id.id_infoBookDatePosted)
        val status = itemView.findViewById<TextView>(R.id.id_infoBookStatus)
        val image = itemView.findViewById<ImageView>(R.id.id_infoBookImage)
        val postedBy = itemView.findViewById<TextView>(R.id.id_BookPostedBy)
        val genre = itemView.findViewById<TextView>(R.id.id_infoBookGenre)
        val description = itemView.findViewById<TextView>(R.id.id_infoBookDescription)
    }

    fun filter(text: String) {
        val tempList = ArrayList<Book>()
        tempList.addAll(myDataSet)
        myDataSet.clear()
        if (text.isEmpty()) {
            mFireStore?.collection("$BOOKDOC_PATH")
                ?.whereEqualTo("status", BookStatus.AVAILABLE)
                ?.get()
                ?.addOnSuccessListener {
                    myDataSet.clear()
                    for (bookSnapShot in it) {
                        val book = bookSnapShot.toObject(Book::class.java)
                        book.id = bookSnapShot.id
                        myDataSet.add(book)
                    }
                    notifyDataSetChanged()
                }
        } else {
            for (item in tempList) {
                if (bookTitleOrAuthorContainsText(item, text)) {
                    myDataSet.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun bookTitleOrAuthorContainsText(book: Book, text: String): Boolean {
        return (book.title.toLowerCase().contains(text))
    }
}