package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CharacterDao {

    @Insert
    suspend fun insert(character: CharacterEntity)

    @Query("SELECT * FROM characters")
    suspend fun getAll(): List<CharacterEntity>

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteById(id: Int)
}