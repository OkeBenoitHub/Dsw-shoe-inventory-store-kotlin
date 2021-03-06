package com.www.dswstore.ui.aboutshoe

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.www.dswstore.R
import com.www.dswstore.database.ShoeDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepQualityFragment.
 * @param shoeId The id of the current shoe we are working on.
 */
class AboutShoeViewModel(private val shoeId: Long = 0L,
                         dataSource: ShoeDatabaseDao, application: Application) : AndroidViewModel(application) {

    /**
     * Get shared shoe info message
     */
    fun getShoeInfoMessage(): String {
        return shoeName.value + "\n" +
                shoePriceText.value + "\n" +
                shoeBrandName.value + "\n" +
                shoeDescription.value + "\n" +
                shoeSection.value + "\n" +
                shoeSize.value + "\n" +
                shoeInClearance.value
    }

    /**
     * Hold a reference to ShoeDatabase via its SleepDatabaseDao.
     */
    val database = dataSource

    private val shoeById = dataSource.getShoeFromDbById(shoeId)

    /**
     * Track shoe info data deleted from database
     */
    private var _shoeDeletedFromDbEvt = MutableLiveData<Boolean?>()
    val shoeDeletedFromDbEvt: LiveData<Boolean?>
        get() = _shoeDeletedFromDbEvt

    /**
     *  Done deleted shoe info from database
     */
    fun shoeDeletedFromDbEventCleared() {
        _shoeDeletedFromDbEvt.value = false
    }

    private suspend fun deleteShoeItemFromDb() {
        _shoeDeletedFromDbEvt.value = true
        withContext(Dispatchers.IO) {
            if (shoeId != 0L)
                database.deleteShoeDataById(shoeId)
        }
    }

    /**
     * Delete shoe info by its id
     */
    fun deleteShoeItemById() {
        viewModelScope.launch {
            deleteShoeItemFromDb()
        }
    }

    // shoe name
    val shoeName = Transformations.map(shoeById) { shoeById ->
        application.baseContext.getString(R.string.shoe_name_holder) + " " + shoeById?.name
    }

    // shoe brand name
    val shoeBrandName = Transformations.map(shoeById) { shoeById ->
        "Brand name: " + shoeById?.brand_name
    }

    // shoe short description
    val shoeDescription = Transformations.map(shoeById) { shoeById ->
        "Description: " + shoeById?.short_description
    }

    // shoe section
    val shoeSection = Transformations.map(shoeById) { shoeById ->
        "Section: " + shoeById?.section
    }

    // shoe size
    val shoeSize = Transformations.map(shoeById) { shoeById ->
        "Size: " + shoeById?.size
    }

    // shoe image path
    val shoePicturePath = Transformations.map(shoeById) { shoeById ->
        shoeById?.picture_path
    }

    // shoe price
    val shoePriceText = Transformations.map(shoeById) { shoeById ->
        "Price: $" + shoeById?.price?.toInt().toString()
    }

    // shoe in clearance?
    val shoeInClearance = Transformations.map(shoeById) { shoeById ->
        shoeById?.let {
            when(shoeById.in_clearance) {
                true -> "In clearance: Yes"
                false -> "In clearance: No"
            }
        }
    }
}