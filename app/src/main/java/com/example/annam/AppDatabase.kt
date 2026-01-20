package com.example.annam

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FlashCard::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao
}
