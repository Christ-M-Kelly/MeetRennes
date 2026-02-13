package com.meetrennes.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.meetrennes.app.domain.Lieu
import kotlinx.coroutines.flow.Flow

@Dao
interface LieuDao {

    @Query("SELECT * FROM lieux ORDER BY nom ASC")
    fun getAll(): Flow<List<Lieu>>

    @Query("SELECT * FROM lieux WHERE id = :id")
    suspend fun getById(id: String): Lieu?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lieux: List<Lieu>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lieu: Lieu)

    @Update
    suspend fun update(lieu: Lieu)

    @Query("SELECT COUNT(*) FROM lieux")
    suspend fun count(): Int
}
