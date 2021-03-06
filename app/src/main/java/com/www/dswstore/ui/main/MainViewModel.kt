package com.www.dswstore.ui.main

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.www.dswstore.R
import com.www.dswstore.database.Shoe
import com.www.dswstore.database.ShoeDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(shoeDatabase: ShoeDatabaseDao, application: Application) : AndroidViewModel(application) {

    val shoes = shoeDatabase.getAllShoesFromDb()

    private val shoesBySection = shoeDatabase.getAllShoesFromDbBySection("men")

    private val shoesBySize = shoeDatabase.getAllShoesFromDbBySize("L")

    private val shoesByPriceMostExpensive = shoeDatabase.getAllShoesByMostExpensive()

    // private val shoesByPriceCheapest = shoeDatabase.getAllShoesByCheapest()

    private val shoesInClearance = shoeDatabase.getAllShoesFromDbInClearance(true)

    val database = shoeDatabase

    fun getSortedShoesByOption(option: Int): LiveData<List<Shoe>> {
        return when(option) {
            0 -> shoes
            1 -> shoesBySection
            2 -> shoesBySize
            3 -> shoesByPriceMostExpensive
            4 -> shoesInClearance
            else -> shoes
        }
    }

    /**
     * Track shoe favored event
     */
    private var _shoeFavoredEvt = MutableLiveData<Boolean?>()
    val shoeFavoredEvt: LiveData<Boolean?>
        get() = _shoeFavoredEvt

    /**
     *  Done when marked shoe as favorites
     */
    fun shoeFavoredEventCleared() {
        _shoeFavoredEvt.value = null
    }

    fun saveShoeAsFavorite(shoeId: Long, isFavored: Boolean) {
        viewModelScope.launch {
            setShoeAsFavoriteInDb(shoeId, isFavored)
        }
    }

    private suspend fun setShoeAsFavoriteInDb(shoeId: Long, isFavored: Boolean) {
        _shoeFavoredEvt.value = isFavored
        withContext(Dispatchers.IO) {
            database.favoredOrUnfavoredShoe(shoeId, isFavored)
        }
    }

    /**
     * Track error message to show based on shoes availability in db
     */
    val errorMessage = Transformations.map(shoes) {
        when(it.isEmpty()) {
            true -> application.baseContext.getString(R.string.empty_shoe_store_text)
            else -> ""
        }
    }

    /**
     * Track error message visibility based on shoes availability in db
     */
    val errorMessageVisibility = Transformations.map(shoes) {
        when(it.isEmpty()) {
            true -> View.VISIBLE
            else -> View.INVISIBLE
        }
    }

    /**
     * Variable that tells the Fragment to navigate to a specific AboutShoeFragment
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _navigateToAboutShoeFragment = MutableLiveData<Shoe?>()
    val navigateToAboutShoeFragment: LiveData<Shoe?>
        get() = _navigateToAboutShoeFragment

    fun onShoeItemClicked(shoeId: Long) {
        _navigateToAboutShoeFragment.value?.id = shoeId
    }

    fun onAboutShoeFragmentNavigated() {
        _navigateToAboutShoeFragment.value = null
    }

    /**
     * Variable that tells the Fragment to navigate to SaveShoeFragment
     * to add a new shoe to store
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private val _navigateToSaveShoeFragment = MutableLiveData<Boolean?>()
    val navigateToSaveShoeFragment
        get() = _navigateToSaveShoeFragment

    fun onNavigateToSaveShoeFragment() {
        _navigateToSaveShoeFragment.value = true
    }

    fun onNavigateToSaveShoeFragmentDone() {
        _navigateToSaveShoeFragment.value = false
    }
}