package mk.ukim.finki.coffeejournal.room.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entry")
    fun getAll() : List<JournalEntry>

    @Insert
    fun insertAll(vararg entries: JournalEntry)

    @Delete
    fun delete(journalEntry: JournalEntry)
}