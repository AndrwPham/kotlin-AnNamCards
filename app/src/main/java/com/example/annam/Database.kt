package com.example.annam

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase


@Entity(tableName = "FlashCards", indices = [Index(
    value = ["english_card", "vietnamese_card"],
    unique = true
)])
data class FlashCard(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "english_card") val englishCard: String?,
    @ColumnInfo(name = "vietnamese_card") val vietnameseCard: String?
)

@Dao
interface FlashCardDao {
    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    @Query("SELECT * FROM FlashCards WHERE uid IN (:flashCardIds)")
    suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard>

    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT 3")
    suspend fun loadRandomThree(): List<FlashCard>

    @Query("SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
            "vietnamese_card LIKE :vietnamese LIMIT 1")
    suspend fun findByCards(english: String, vietnamese: String): FlashCard

    @Query(
        "SELECT * FROM FlashCards WHERE " +
                "(CASE WHEN :exactEn THEN english_card LIKE :en  " +
                "WHEN NOT :exactEn  THEN english_card LIKE '%' || :en || '%' END) " +
                "AND " +
                "(CASE WHEN :exactVn THEN vietnamese_card LIKE :vn " +
                "WHEN NOT :exactVn THEN vietnamese_card LIKE '%' || :vn || '%' END)"
    )
    suspend fun getFilteredFlashCards(
        en: String,
        exactEn: Int,
        vn: String,
        exactVn: Int
    ): List<FlashCard>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg flashCard: FlashCard)

    @Delete
    fun delete(flashCard: FlashCard)

    @Query("DELETE FROM FlashCards")
    suspend fun clearAll()
}

@Database(entities = [FlashCard::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao
}

