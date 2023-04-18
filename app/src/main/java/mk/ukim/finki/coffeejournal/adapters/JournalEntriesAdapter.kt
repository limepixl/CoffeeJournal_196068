package mk.ukim.finki.coffeejournal.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mk.ukim.finki.coffeejournal.R
import mk.ukim.finki.coffeejournal.room.model.JournalEntry
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
            Glide.with(holder.itemView).load(data.photoPath).into(holder.ivPhoto)
            holder.ivPhoto.visibility = View.VISIBLE
        }
    }
}

class JournalEntriesHeader(private val dataSet: List<JournalEntry> = ArrayList()): RecyclerView.Adapter<JournalEntriesHeader.HeaderViewHolder>() {
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumEntries: TextView

        init {
            tvNumEntries = view.findViewById(R.id.tvNumEntries)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.journal_entries_header, parent, false)

        return HeaderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val string: String = when (dataSet.size) {
            0 -> "No logs yet... Get brewing!"
            1 -> "You've logged a single cup. Nice!"
            else -> "You've logged ${dataSet.size} cups of coffee!"
        }

        holder.tvNumEntries.text = string
    }

    override fun getItemCount(): Int {
        return 1
    }
}