package com.www.dswstore.ui.favorites

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.www.dswstore.database.ShoeDatabaseDao

class FavoritesViewModelFactory(
    private val dataSource: ShoeDatabaseDao,
    private val application: Application
): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}