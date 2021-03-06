package com.www.dswstore.ui.saveshoe

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.www.dswstore.R
import com.www.dswstore.database.Shoe
import com.www.dswstore.database.ShoeDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SaveShoeViewModel(private val database: ShoeDatabaseDao, application: Application) : AndroidViewModel(application) {
    // Track if we should insert new shoe data or update existing shoe info
    var updateShoeInfo = false

    fun getShoeInfoDataById(shoeId: Long): LiveData<Shoe?> {
         return database.getShoeFromDbById(shoeId)
    }

    // shoe photo path
    var shoePhotoPath: String? = null

    // shoe name value
    var shoeName: String = ""

    // shoe price value
    var shoePrice: Double = 0.0

    // shoe brand name
    var shoeBrandName: String = ""

    // shoe short description
    var shoeShortDescription: String = ""

    // shoe section
    var shoeSection: String?  = null

    /**
     * Shoe section options
     */
    val sectionMen = "men"
    val sectionWomen = "women"
    val sectionKids = "kids"

    /**
     * set shoe section
     */
    fun onShoeSectionSelected(shoeSectionSelected: String) {
        shoeSection = shoeSectionSelected
    }

    // shoe size
    var shoeSize: String?  = null

    /**
     * Shoe sizes options
     */
    val shoeSizeS = "S"
    val shoeSizeM = "M"
    val shoeSizeL = "L"
    val shoeSizeXL = "XL"
    val shoeSizeXXL = "XXL"

    /**
     * Set shoe size selected
     */
    fun onShoeSizeSelected(shoeSizeSelected: String) {
        shoeSize = shoeSizeSelected
    }

    var isShoeInClearance: Boolean = false

    fun setShoeInClearanceOrNot() {
        isShoeInClearance = !isShoeInClearance
    }

    /**
     * Track new shoe info data inserted to database
     */
    private var _shoeInsertedToDbEvt = MutableLiveData<Boolean?>()
    val shoeInsertedToDbEvt: LiveData<Boolean?>
        get() = _shoeInsertedToDbEvt

    /**
     *  Done inserting new shoe info to database
     */
    fun shoeInsertedToDbEventCleared() {
        _shoeInsertedToDbEvt.value = false
    }

    private suspend fun insertShoeInfoToDatabase() {
        _shoeInsertedToDbEvt.value = true
        withContext(Dispatchers.IO) {
            // Create a new shoe
            // and insert it into the database.
            val newShoe = Shoe(0L,
                shoeName,
                shoeShortDescription,
                shoePrice,
                shoeBrandName,
                isShoeInClearance,
                shoePhotoPath.toString(),
                shoeSection.toString(),
                shoeSize.toString())
            database.insertShoeToDb(newShoe)
        }
    }

    fun onInsertNewShoeInfoToDatabase() {
        viewModelScope.launch {
            insertShoeInfoToDatabase()
        }
    }

    /**
     * Track shoe info data updated from database
     */
    private var _shoeUpdatedFromDbEvt = MutableLiveData<Boolean?>()
    val shoeUpdatedFromDbEvt: LiveData<Boolean?>
        get() = _shoeUpdatedFromDbEvt

    var existingShoeId = 0L

    /**
     *  Done updated shoe info from database
     */
    fun shoeUpdatedFromDbEventCleared() {
        _shoeUpdatedFromDbEvt.value = false
    }

    private suspend fun updateShoeInfoFromDatabase(shoeId: Long) {
        _shoeUpdatedFromDbEvt.value = true
        withContext(Dispatchers.IO) {
            // Create a new shoe
            // and insert it into the database.
            val shoe = Shoe(shoeId,
                shoeName,
                shoeShortDescription,
                shoePrice,
                shoeBrandName,
                isShoeInClearance,
                shoePhotoPath.toString(),
                shoeSection.toString(),
                shoeSize.toString())
            database.updateShoeFromDb(shoe)
        }
    }

    fun onUpdateShoeInfoFromDatabase(shoeId: Long) {
        viewModelScope.launch {
            updateShoeInfoFromDatabase(shoeId)
        }
    }

}