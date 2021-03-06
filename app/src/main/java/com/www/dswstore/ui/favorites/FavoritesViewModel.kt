package com.www.dswstore.ui.favorites

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.www.dswstore.R
import com.www.dswstore.database.Shoe
import com.www.dswstore.database.ShoeDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesViewModel(shoeDatabase: ShoeDatabaseDao, application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel

    // get all favorite shoes from database
    val favoriteShoes = shoeDatabase.getAllFavoriteShoesFromDb(true)

    val database = shoeDatabase

    /**
     * Track shoe favored
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
     * Track error message to show based on favorite shoes availability in db
     */
    val errorMessage = Transformations.map(favoriteShoes) { favorites ->
        when(favorites.isEmpty()) {
            true -> application.baseContext.getString(R.string.empty_favorites_shoe_store_text)
            else -> ""
        }
    }

    /**
     * Track error message visibility based on favorite shoes availability in db
     */
    val errorMessageVisibility = Transformations.map(favoriteShoes) {
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

}