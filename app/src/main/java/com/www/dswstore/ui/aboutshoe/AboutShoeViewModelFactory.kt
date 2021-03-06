package com.www.dswstore.ui.aboutshoe

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.www.dswstore.database.ShoeDatabaseDao

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the id for the shoe and the AboutShoeViewModel to the ViewModel.
 */
class AboutShoeViewModelFactory(
    private val shoeId: Long,
    private val dataSource: ShoeDatabaseDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AboutShoeViewModel::class.java)) {
            return AboutShoeViewModel(shoeId, dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}