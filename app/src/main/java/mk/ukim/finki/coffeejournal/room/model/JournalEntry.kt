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
    var notes: String? = null,

    @ColumnInfo(name = "photo")
    var photo: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JournalEntry

        if (id != other.id) return false
        if (date != other.date) return false
        if (brewMethod != other.brewMethod) return false
        if (rating != other.rating) return false
        if (notes != other.notes) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + date.hashCode()
        result = 31 * result + brewMethod.hashCode()
        result = 31 * result + rating
        result = 31 * result + (notes?.hashCode() ?: 0)
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        return result
    }
}