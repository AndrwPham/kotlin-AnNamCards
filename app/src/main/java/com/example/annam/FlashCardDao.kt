package com.example.annam

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FlashCardDao {
    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    @Query("SELECT * FROM FlashCards WHERE uid IN (:flashCardIds)")
    suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard>

    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT 3")
    suspend fun loadRandomThree(): List<FlashCard>

    @Query(
        "SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
            "vietnamese_card LIKE :vietnamese LIMIT 1"
    )
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

    @Update
    suspend fun update(flashCard: FlashCard)

    @Delete
    fun delete(flashCard: FlashCard)

    @Query("DELETE FROM FlashCards")
    suspend fun clearAll()

    @Query("SELECT * FROM FlashCards WHERE uid = :uid")
    suspend fun getFlashCardById(uid: Int): FlashCard
}
