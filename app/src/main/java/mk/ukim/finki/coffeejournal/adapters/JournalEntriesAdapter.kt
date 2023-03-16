package mk.ukim.finki.coffeejournal.adapters

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mk.ukim.finki.coffeejournal.R
import mk.ukim.finki.coffeejournal.room.model.JournalEntry
import java.io.File
import java.sql.Date
import java.util.*

class JournalEntriesAdapter(private val dataSet: List<JournalEntry> = ArrayList()) :
    RecyclerView.Adapter<JournalEntriesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView
        val tvBrewMethod: TextView
        val tvNotes: TextView
        val ivPhoto: ImageView
        val ratingBar: RatingBar

        init {
            tvDate = view.findViewById(R.id.tvDate)
            tvBrewMethod = view.findViewById(R.id.tvBrewMethod)
            tvNotes = view.findViewById(R.id.tvNotes)
            ivPhoto = view.findViewById(R.id.ivPhoto)
            ratingBar = view.findViewById(R.id.ratingBar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.journal_entries_card, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataSet[position]

        holder.tvDate.text = "Date: " + Date(data.date).toString()
        holder.tvBrewMethod.text = data.brewMethod.toString()
            .replace('_', ' ').lowercase()
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                else it.toString()
            }
        holder.ratingBar.rating = data.rating.toFloat()

        if (data.notes != null && data.notes != "") {
            holder.tvNotes.text = "Notes: " + data.notes
            holder.tvNotes.visibility = View.VISIBLE
        }

        if (data.photoPath != null) {
            val photoFile = data.photoPath?.let { File(it) }
            if(photoFile != null) {
                val bytes = photoFile.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.ivPhoto.setImageBitmap(bitmap)
                holder.ivPhoto.visibility = View.VISIBLE
            }
        }

    }
}