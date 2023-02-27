package mk.ukim.finki.coffeejournal.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import mk.ukim.finki.coffeejournal.enums.BrewMethod
import java.util.*

@Entity("journal_entry")
data class JournalEntry(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "date")
    val date: Date,

    @ColumnInfo(name = "brew_method")
    val brewMethod: BrewMethod,

    @ColumnInfo(name = "rating")
    val rating: Int,

    @ColumnInfo(name = "notes")
    val notes: String?
)