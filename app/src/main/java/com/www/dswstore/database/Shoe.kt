package com.www.dswstore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "shoe_store_inventory_table")
data class Shoe(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var name: String,
    var short_description: String,
    var price: Double,
    var brand_name: String,
    var in_clearance: Boolean,
    var picture_path: String,
    var section: String,
    var size: String,
    var date_added: Date = Date(),
    var is_favored: Boolean = false)