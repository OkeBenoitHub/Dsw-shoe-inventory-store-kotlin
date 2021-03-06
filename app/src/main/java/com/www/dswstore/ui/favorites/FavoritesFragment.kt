package com.www.dswstore.ui.favorites

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.www.dswstore.R
import com.www.dswstore.adapters.ShoeInventoryAdapter
import com.www.dswstore.adapters.ShoeItemClickListener
import com.www.dswstore.database.ShoeDatabase
import com.www.dswstore.databinding.FavoritesFragmentBinding
import com.www.dswstore.ui.main.MainFragmentDirections
import com.www.dswstore.ui.main.MainViewModelFactory
import com.www.dswstore.util.MainUtil

class FavoritesFragment : Fragment(), ShoeInventoryAdapter.OnShoeMarkAsFavoriteListener {
    private lateinit var favoritesFragmentBinding: FavoritesFragmentBinding

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoritesFragmentBinding = FavoritesFragmentBinding.inflate(layoutInflater,container,false)
        favoritesFragmentBinding.lifecycleOwner = this

        return favoritesFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // application
        val application = requireNotNull(this.activity).application

        // shoe database
        val dataSource = ShoeDatabase.getInstance(application).shoeDatabaseDao

        val favoritesViewModelFactory = FavoritesViewModelFactory(dataSource, application)

        favoritesViewModel = ViewModelProvider(this, favoritesViewModelFactory).get(FavoritesViewModel::class.java)

        favoritesFragmentBinding.favoritesViewModel = favoritesViewModel

        // Track whenever a shoe get marked as favorite
        favoritesViewModel.shoeFavoredEvt.observe(viewLifecycleOwner, { event ->
            event?.let {
                val messageResId: Int = if (event) {
                    R.string.shoe_added_to_favorites
                } else {
                    R.string.shoe_removed_from_favorites
                }
                // show snack bar
                MainUtil().displaySnackBarMessage(
                    favoritesFragmentBinding.mainLayout,
                    messageResId, Snackbar.LENGTH_LONG)

                favoritesViewModel.shoeFavoredEventCleared()
            }
        })

        // set up shoe inventory adapter for favorite shoes
        setUpShoeInventoryAdapter();
    }

    /**
     * Set up shoe inventory adapter to recycler view
     */
    private fun setUpShoeInventoryAdapter() {
        // set up Grid layout manager with auto fit columns
        val manager = GridLayoutManager(requireContext(),
            MainUtil().getNumbColumnsForGridLayoutAutoFit(requireContext(),
                180f))

        favoritesFragmentBinding.recyclerViewFavoriteShoes.layoutManager = manager

        val adapter = ShoeInventoryAdapter(ShoeItemClickListener { shoeId ->
            favoritesViewModel.onShoeItemClicked(shoeId)
            // go to About Shoe Fragment screen with shoeId parameter
            this.findNavController().navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToAboutShoeFragment(shoeId)
            )
        },this)
        favoritesFragmentBinding.recyclerViewFavoriteShoes.adapter = adapter

        // submit list of shoes from view model to adapter
        favoritesViewModel.favoriteShoes.observe(viewLifecycleOwner, { fvShoes ->
            fvShoes?.let {
                // hide progress bar
                favoritesFragmentBinding.progressBar.visibility = View.INVISIBLE
                adapter.submitList(fvShoes)
            }
        })
    }

    override fun onShoeMarkAsFavorite(shoeId: Long, isFavored: Boolean) {
        favoritesViewModel.saveShoeAsFavorite(shoeId, !isFavored)
    }

}