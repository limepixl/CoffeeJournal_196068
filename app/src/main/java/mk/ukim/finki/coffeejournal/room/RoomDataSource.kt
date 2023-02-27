package mk.ukim.finki.coffeejournal.room

import mk.ukim.finki.coffeejournal.LocalDataSource
import mk.ukim.finki.coffeejournal.room.model.JournalEntry
import mk.ukim.finki.coffeejournal.room.model.JournalEntryDao

class RoomDataSource(private val journalEntryDao: JournalEntryDao) : LocalDataSource {
    override suspend fun insert(journalEntry: JournalEntry) {
        journalEntryDao.insertAll(journalEntry)
    }

    override suspend fun saveAll(journalEntries: List<JournalEntry>) {
        for(journalEntry in journalEntries) {
            journalEntryDao.insertAll(journalEntry)
        }
    }

    override suspend fun delete(journalEntry: JournalEntry) {
        journalEntryDao.delete(journalEntry)
    }

    override suspend fun getAll(): List<JournalEntry> {
        return journalEntryDao.getAll()
    }
}