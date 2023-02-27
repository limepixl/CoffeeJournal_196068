package mk.ukim.finki.coffeejournal.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mk.ukim.finki.coffeejournal.room.model.JournalEntry
import mk.ukim.finki.coffeejournal.room.model.JournalEntryDao

@Database(entities = [JournalEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context, AppDatabase::class.java, "journal_database"
                ).build()
                instance = inst
                inst
            }
        }
    }
}