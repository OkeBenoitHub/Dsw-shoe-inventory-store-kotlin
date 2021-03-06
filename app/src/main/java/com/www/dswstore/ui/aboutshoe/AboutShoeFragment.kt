package com.www.dswstore.ui.aboutshoe

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.www.dswstore.R
import com.www.dswstore.database.ShoeDatabase
import com.www.dswstore.databinding.AboutShoeFragmentBinding
import com.www.dswstore.ui.main.MainFragmentDirections
import com.www.dswstore.ui.saveshoe.SaveShoeViewModelFactory
import com.www.dswstore.util.DialogUtil
import com.www.dswstore.util.MainUtil

class AboutShoeFragment : Fragment(),DialogUtil.ShowBasicAlertDialogCallback {
    private lateinit var aboutShoeFragmentBinding: AboutShoeFragmentBinding

    companion object {
        fun newInstance() = AboutShoeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private lateinit var aboutShoeViewModel: AboutShoeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        aboutShoeFragmentBinding = AboutShoeFragmentBinding.inflate(layoutInflater,container,false)

        aboutShoeFragmentBinding.lifecycleOwner = this
        return aboutShoeFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // application
        val application = requireNotNull(this.activity).application
        val arguments = AboutShoeFragmentArgs.fromBundle(arguments!!)

        // Create an instance of the ViewModel Factory.
        // shoe database source
        val dataSource = ShoeDatabase.getInstance(application).shoeDatabaseDao
        val aboutShoeViewModelFactory = AboutShoeViewModelFactory(arguments.shoeId, dataSource, application)
        aboutShoeViewModel = ViewModelProvider(this, aboutShoeViewModelFactory).get(AboutShoeViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        aboutShoeFragmentBinding.aboutShoeViewModel = aboutShoeViewModel

        // Track when a shoe by its id gets deleted
        aboutShoeViewModel.shoeDeletedFromDbEvt.observe(viewLifecycleOwner, { event ->
            event?.let {
                if (event) {
                    this.findNavController().navigateUp()
                    MainUtil().showToastMessage(requireContext(),getString(R.string.deleted_successfully))
                    aboutShoeViewModel.shoeDeletedFromDbEventCleared()
                }
            }
        })

        // edit shoe info button tapped
        aboutShoeFragmentBinding.editShoeInfoBtn.setOnClickListener {
            this.findNavController().navigate(
                AboutShoeFragmentDirections.actionAboutShoeFragmentToSaveShoeFragment(arguments.shoeId)
            )
        }

        // share shoe info button
        aboutShoeFragmentBinding.shareShoeInfoBtn.setOnClickListener {
            val shareShoeInfoText = "Shoe Data Info:\n\n${aboutShoeViewModel.getShoeInfoMessage()}"
            MainUtil().shareTextData(requireContext(),getString(R.string.share_shoe_info_via_text),shareShoeInfoText)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.about_shoe_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.delete_shoe_menu_item -> {
                DialogUtil().showBasicAlertDialog(
                    requireContext(),
                    "Delete shoe",
                    "Are you sure?",
                    "yes",
                    "No",
                    true,
                    showBasicAlertDialogCallback = this
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onShowBasicAlertDialog(isPositiveBtnTapped: Boolean) {
        if (isPositiveBtnTapped) {
            // delete shoe info by its id
            aboutShoeViewModel.deleteShoeItemById()
        }
    }

}