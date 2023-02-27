package mk.ukim.finki.coffeejournal.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import mk.ukim.finki.coffeejournal.enums.BrewMethod
import java.util.*

@Entity("journal_entry")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    var date: Long = Date().time,

    @ColumnInfo(name = "brew_method")
    var brewMethod: BrewMethod = BrewMethod.ESPRESSO,

    @ColumnInfo(name = "rating")
    var rating: Int = 0,

    @ColumnInfo(name = "notes")
    var notes: String? = null
)