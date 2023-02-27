package mk.ukim.finki.coffeejournal

import mk.ukim.finki.coffeejournal.room.model.JournalEntry

interface LocalDataSource {

    suspend fun insert(journalEntry: JournalEntry)

    suspend fun saveAll(journalEntries: List<JournalEntry>)

    suspend fun delete(journalEntry: JournalEntry)

    suspend fun getAll(): List<JournalEntry>
}