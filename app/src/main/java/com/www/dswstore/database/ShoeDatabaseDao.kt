package com.www.dswstore.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShoeDatabaseDao {

    @Insert
    suspend fun insertShoeToDb(shoe: Shoe)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param  shoe value to write
     */
    @Update
    suspend fun updateShoeFromDb(shoe: Shoe)

    /**
     * Select a specific shoe by its ID
    */
    @Query("SELECT * from shoe_store_inventory_table WHERE id = :shoeId")
    fun getShoeFromDbById(shoeId: Long): LiveData<Shoe?>

    /**
     * Deletes all shoe info values from the table.
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM shoe_store_inventory_table")
    suspend fun clearAllShoesDataFromDb()


    @Query("UPDATE shoe_store_inventory_table SET is_favored = :isFavored WHERE id = :shoeId")
    suspend fun favoredOrUnfavoredShoe(shoeId: Long, isFavored: Boolean)

    /**
     * Deletes all shoe info values from the table.
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM shoe_store_inventory_table WHERE id = :shoeId")
    suspend fun deleteShoeDataById(shoeId: Long)

    /**
     * Selects and returns all shoe rows in the table,
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM shoe_store_inventory_table ORDER BY id DESC")
    fun getAllShoesFromDb(): LiveData<List<Shoe>>


    /**
     * Selects and returns all shoe rows in the table,
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM shoe_store_inventory_table WHERE is_favored = :is_favored ORDER BY id DESC")
    fun getAllFavoriteShoesFromDb(is_favored: Boolean): LiveData<List<Shoe>>

    /**
     * Selects and returns all shoe rows in the table,
     * by section.
     */
    @Query("SELECT * FROM shoe_store_inventory_table WHERE section = :section ORDER BY id DESC")
    fun getAllShoesFromDbBySection(section: String): LiveData<List<Shoe>>

    /**
     * Get all shoes from db by size
     */
    @Query("SELECT * FROM shoe_store_inventory_table WHERE size = :size ORDER BY id DESC")
    fun getAllShoesFromDbBySize(size: String): LiveData<List<Shoe>>

    /**
     * Get all shoes from db by most expensive or cheapest
     */

    @Query("SELECT * FROM shoe_store_inventory_table ORDER BY price DESC")
    fun getAllShoesByMostExpensive(): LiveData<List<Shoe>>

    @Query("SELECT * FROM shoe_store_inventory_table ORDER BY price ASC")
    fun getAllShoesByCheapest(): LiveData<List<Shoe>>

    /**
     * Get all shoes from db in clearance
     */
    @Query("SELECT * FROM shoe_store_inventory_table WHERE in_clearance = :in_clearance ORDER BY id DESC")
    fun getAllShoesFromDbInClearance(in_clearance: Boolean): LiveData<List<Shoe>>

}