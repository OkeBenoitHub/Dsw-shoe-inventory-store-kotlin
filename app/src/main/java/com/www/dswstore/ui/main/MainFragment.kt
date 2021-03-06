package com.www.dswstore.ui.main

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.www.dswstore.R
import com.www.dswstore.adapters.ShoeInventoryAdapter
import com.www.dswstore.adapters.ShoeItemClickListener
import com.www.dswstore.database.ShoeDatabase
import com.www.dswstore.databinding.MainFragmentBinding
import com.www.dswstore.util.DialogUtil
import com.www.dswstore.util.MainUtil
import org.michaelbel.bottomsheet.BottomSheet

const val SORT_SHOES_BY = "sort_shoes_by"
class MainFragment : Fragment(), ShoeInventoryAdapter.OnShoeMarkAsFavoriteListener, DialogInterface.OnClickListener {
    private lateinit var mainFragmentBinding: MainFragmentBinding

    companion object {
        fun newInstance(sort_shoes_by: String): MainFragment {
            val args = Bundle().apply {
                putSerializable(SORT_SHOES_BY, sort_shoes_by)
            }
            return MainFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var shoeInventoryAdapter: ShoeInventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainFragmentBinding = MainFragmentBinding.inflate(layoutInflater, container, false)
        // bind lifecycle owner
        mainFragmentBinding.lifecycleOwner = this
        return mainFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // application
        val application = requireNotNull(this.activity).application

        // shoe database
        val dataSource = ShoeDatabase.getInstance(application).shoeDatabaseDao

        val viewModelFactory = MainViewModelFactory(dataSource, application)

        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        mainFragmentBinding.mainViewModel = mainViewModel

        // Observe for navigation to About Shoe Fragment
        mainViewModel.navigateToAboutShoeFragment.observe(viewLifecycleOwner, { shoe ->

            if (shoe != null) {
                this.findNavController().navigate(
                    MainFragmentDirections
                        .actionMainFragmentToAboutShoeFragment(shoe.id)
                )
            }
            mainViewModel.onAboutShoeFragmentNavigated()
        })

        // Observe for navigation to Save Shoe Fragment :: add new shoe
        mainViewModel.navigateToSaveShoeFragment.observe(viewLifecycleOwner, { navigate ->
            if (navigate == true) {
                this.findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToSaveShoeFragment(0L)
                )
                // set navigation as done to prevent unwanted live data events
                mainViewModel.onNavigateToSaveShoeFragmentDone()
            }
        })

        // Track whenever a shoe get marked as favorite
        mainViewModel.shoeFavoredEvt.observe(viewLifecycleOwner, { event ->
            event?.let {
                val messageResId: Int = if (event) {
                    R.string.shoe_added_to_favorites
                } else {
                    R.string.shoe_removed_from_favorites
                }
                // show snack bar
                MainUtil().displaySnackBarMessage(
                    mainFragmentBinding.mainLayout,
                    messageResId, Snackbar.LENGTH_LONG
                )

                mainViewModel.shoeFavoredEventCleared()
            }
        })

        // set up shoe inventory adapter
        setUpShoeInventoryAdapter()
    }

    /**
     * Set up shoe inventory adapter to recycler view
     */
    private fun setUpShoeInventoryAdapter() {
        // set up Grid layout manager with auto fit columns
        val manager = GridLayoutManager(
            requireContext(),
            MainUtil().getNumbColumnsForGridLayoutAutoFit(
                requireContext(),
                180f
            )
        )

        mainFragmentBinding.recyclerViewShoes.layoutManager = manager

        shoeInventoryAdapter = ShoeInventoryAdapter(ShoeItemClickListener { shoeId ->
            mainViewModel.onShoeItemClicked(shoeId)
            // go to About Shoe Fragment screen with shoeId parameter
            this.findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToAboutShoeFragment(shoeId)
            )
        }, this)
        mainFragmentBinding.recyclerViewShoes.adapter = shoeInventoryAdapter

        // submit list of shoes from view model to adapter
        mainViewModel.shoes.observe(viewLifecycleOwner, { shoes ->
            shoes?.let {
                // hide progress bar
                mainFragmentBinding.progressBar.visibility = View.INVISIBLE
                shoeInventoryAdapter.submitList(shoes)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sort_by -> {
                // sort by
                DialogUtil().showBottomSheetMenuDialogOptions(requireContext(),R.menu.sort_shoes_bottom_sheet_menu,this)
            }
            R.id.add_new_shoe -> {
                // add a new shoe
                this.findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToSaveShoeFragment(0L)
                )
            }
            R.id.favorites -> {
                // navigate to Favorites fragment
                this.findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToFavoritesFragment()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onShoeMarkAsFavorite(shoeId: Long, isFavored: Boolean) {
        mainViewModel.saveShoeAsFavorite(shoeId, !isFavored)
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        val sortedShoesByOption = mainViewModel.getSortedShoesByOption(p1)
        sortedShoesByOption.observe(viewLifecycleOwner, { sortedShoes ->
            sortedShoes?.let {
                shoeInventoryAdapter.submitList(sortedShoes)
            }
        })
    }
}