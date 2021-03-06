package com.www.dswstore.ui.saveshoe

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.theartofdev.edmodo.cropper.CropImage
import com.www.dswstore.R
import com.www.dswstore.database.ShoeDatabase
import com.www.dswstore.databinding.SaveShoeFragmentBinding
import com.www.dswstore.ui.ViewPhotoActivity
import com.www.dswstore.ui.aboutshoe.AboutShoeFragmentArgs
import com.www.dswstore.ui.main.MainFragmentDirections
import com.www.dswstore.util.*
import java.io.File


class SaveShoeFragment : Fragment() {
    private lateinit var saveShoeFragmentBinding: SaveShoeFragmentBinding

    companion object {
        fun newInstance() = SaveShoeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private lateinit var saveShoeViewModel: SaveShoeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        saveShoeFragmentBinding = SaveShoeFragmentBinding.inflate(layoutInflater, container, false)
        saveShoeFragmentBinding.lifecycleOwner = this
        return saveShoeFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // application
        val application = requireNotNull(this.activity).application
        // retrieve shoeId passed to update shoe
        // if insert new shoe shoeId would be 0L
        val arguments = SaveShoeFragmentArgs.fromBundle(arguments!!)

        // shoe database
        val dataSource = ShoeDatabase.getInstance(application).shoeDatabaseDao

        val saveShoeViewModelFactory = SaveShoeViewModelFactory(dataSource, application)

        saveShoeViewModel = ViewModelProvider(this, saveShoeViewModelFactory).get(SaveShoeViewModel::class.java)

        saveShoeFragmentBinding.saveShoeViewModel = saveShoeViewModel

        getShoeInfoDataAlreadySavedFromDb(arguments)

        setShoePhotoFromSavedPreferences()

        // pick shoe photo from gallery button tapped
        saveShoeFragmentBinding.takeShoePictureGallery.setOnClickListener {
            startActivityForResult(
                PhotoUtil().pickPhotoFromGallery(requireContext()),
                PICK_PHOTO_FROM_PHONE_GALLERY
            )
        }

        // take shoe photo from camera
        saveShoeFragmentBinding.takeShoePictureCamera.setOnClickListener {
            val takePictureFromCameraIntent: Intent? = PhotoUtil().capturePhoto(requireContext())
            if (takePictureFromCameraIntent != null) {
                saveShoeViewModel.shoePhotoPath =
                    PhotoUtil().getPhotoFilePath(requireContext()).toString()
                startActivityForResult(takePictureFromCameraIntent, REQUEST_PHOTO_CAPTURE)
            }
        }

        // Keep track whenever new shoe entry inserted in database
        saveShoeViewModel.shoeInsertedToDbEvt.observe(viewLifecycleOwner, { event ->
            event?.let {
                if (event) {
                    MainUtil().showToastMessage(requireContext(), getString(R.string.shoe_added_successfully))
                    // go back to Main Fragment screen
                    this.findNavController().navigateUp()
                    saveShoeViewModel.shoeInsertedToDbEventCleared()
                }
            }
        })

        // Keep track whenever an existing shoe entry gets updated from database
        saveShoeViewModel.shoeUpdatedFromDbEvt.observe(viewLifecycleOwner, { event ->
            event?.let {
                if (event) {
                    MainUtil().showToastMessage(requireContext(), getString(R.string.shoe_updated_successfully))
                    // go back to Main Fragment screen
                    this.findNavController().navigateUp()
                    saveShoeViewModel.shoeUpdatedFromDbEventCleared()
                }
            }
        })
    }

    /**
     * Get shoe data info from database by shoe ID
     */
    private fun getShoeInfoDataAlreadySavedFromDb(arguments: SaveShoeFragmentArgs) {
        if (arguments.shoeId != 0L) {
            val shoeById = saveShoeViewModel.getShoeInfoDataById(arguments.shoeId)
            shoeById.observe(viewLifecycleOwner, { shoe ->
                shoe?.let {
                    // we should update existing shoe info data
                    saveShoeViewModel.updateShoeInfo = true

                    // set existing shoe id
                    saveShoeViewModel.existingShoeId = shoe.id

                    // set shoe picture path
                    saveShoeViewModel.shoePhotoPath = shoe.picture_path
                    PhotoUtil().loadPhotoFileWithGlide(
                        requireContext(),
                        shoe.picture_path,
                        null,
                        saveShoeFragmentBinding.shoeThumbImg,
                        R.drawable.shoe_64)

                    // set shoe name
                    saveShoeViewModel.shoeName = shoe.name
                    saveShoeFragmentBinding.shoeNameEdt.setText(shoe.name)

                    // set shoe price
                    saveShoeViewModel.shoePrice = shoe.price
                    saveShoeFragmentBinding.shoePriceEdt.setText(shoe.price.toInt().toString())

                    // set shoe brand name
                    saveShoeViewModel.shoeBrandName = shoe.brand_name
                    saveShoeFragmentBinding.shoeBrandNameEdt.setText(shoe.brand_name)

                    // set shoe short description
                    saveShoeViewModel.shoeShortDescription = shoe.short_description
                    saveShoeFragmentBinding.shortDescriptionEdt.setText(shoe.short_description)

                    // set shoe section
                    saveShoeViewModel.shoeSection = shoe.section
                    when(shoe.section) {
                        "men" -> saveShoeFragmentBinding.men.isChecked = true
                        "women" -> saveShoeFragmentBinding.women.isChecked = true
                        else -> saveShoeFragmentBinding.kids.isChecked = true
                    }

                    // set shoe size
                    saveShoeViewModel.shoeSize = shoe.size
                    when(shoe.size) {
                        "S" -> saveShoeFragmentBinding.S.isChecked = true
                        "M" -> saveShoeFragmentBinding.M.isChecked = true
                        "L" -> saveShoeFragmentBinding.L.isChecked = true
                        "XL" -> saveShoeFragmentBinding.XL.isChecked = true
                        "XXL" -> saveShoeFragmentBinding.XXL.isChecked = true
                    }

                    // set shoe in clearance
                    saveShoeViewModel.isShoeInClearance = shoe.in_clearance
                    saveShoeFragmentBinding.checkboxShoeClearance.isChecked = shoe.in_clearance
                }
            })
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK) {
            PhotoUtil().cropPhoto(requireContext(), saveShoeViewModel.shoePhotoPath, this)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                // after cropping photo is successful
                val resultUri: Uri = result.uri
                val intent = Intent(activity, ViewPhotoActivity::class.java)
                intent.putExtra(SHOE_PHOTO_PATH_EXTRA, resultUri.toString())
                startActivityForResult(intent, VIEW_PHOTO_REQUEST_CODE)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // failed to crop photo
                val error = result.error
                MainUtil().showToastMessage(requireContext(), error.message.toString())
            }
        } else if (requestCode == PICK_PHOTO_FROM_PHONE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedImage: Uri? = data.data
                CropImage.activity(selectedImage)
                    .start(requireContext(), this)
            }
        } else if (requestCode == VIEW_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            // edit picture result path..
            if (data != null) {
                saveShoeViewModel.shoePhotoPath =
                    data.getStringExtra(SHOE_PHOTO_PATH_EXTRA).toString()
                PhotoUtil().addPhotoToPhoneGallery(
                    requireContext(),
                    Uri.fromFile(File(saveShoeViewModel.shoePhotoPath.toString()))
                )
                // set shoe photo path to ImageView holder
                saveShoeFragmentBinding.shoeThumbImg.setImageURI(
                    Uri.fromFile(File(saveShoeViewModel.shoePhotoPath.toString()))
                )
                SharedPrefUtil().clearPreferenceDataByKey(requireContext(), SHOE_PHOTO_PATH_EXTRA)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_shoe_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // save shoe button clicked
        if (item.itemId == R.id.save_shoe_item) {
            // check for all shoe info data before saving shoe to database
            checkAllShoeInfoDataBeforeSavingToDatabase()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Check all shoe info data
     */
    private fun checkAllShoeInfoDataBeforeSavingToDatabase() {
        // check for shoe name input
        val shoeNameValue = saveShoeFragmentBinding.shoeNameEdt.text.toString().trim()
        if (!checkForShoeNameAndBrand(shoeNameValue, saveShoeFragmentBinding.shoeNameEdt)) {
            return
        } else {
            saveShoeViewModel.shoeName = shoeNameValue
        }

        // check for shoe price
        val shoePriceValue = saveShoeFragmentBinding.shoePriceEdt.text.toString().trim()
        when {
            shoePriceValue.isEmpty() -> {
                // empty shoe price
                saveShoeFragmentBinding.shoePriceEdt.error = getString(R.string.shoe_price_error_text)
                return
            }
            shoePriceValue.toDouble() <= 0 -> {
                // invalid shoe price
                saveShoeFragmentBinding.shoePriceEdt.error = getString(R.string.invalid_price_error_text)
                return
            }
            else -> {
                saveShoeViewModel.shoePrice = shoePriceValue.toDouble()
            }
        }

        // check for shoe brand name
        val shoeBrandNameValue = saveShoeFragmentBinding.shoeBrandNameEdt.text.toString().trim()
        if (!checkForShoeNameAndBrand(shoeBrandNameValue, saveShoeFragmentBinding.shoeBrandNameEdt)) {
            return
        } else {
            saveShoeViewModel.shoeBrandName = shoeBrandNameValue
        }

        // check for shoe short description
        val shoeShortDescriptionValue =
            saveShoeFragmentBinding.shortDescriptionEdt.text.toString().trim()
        if (shoeShortDescriptionValue.isEmpty()) {
            saveShoeFragmentBinding.shortDescriptionEdt.error =
                getString(R.string.shoe_description_error_text)
            return
        } else {
            saveShoeViewModel.shoeShortDescription = shoeShortDescriptionValue
        }

        // check for shoe sections options
        if (saveShoeViewModel.shoeSection == null) {
            MainUtil().showToastMessage(requireContext(), getString(R.string.select_shoe_section_text))
            return
        }

        // check for shoe sizes options
        if (saveShoeViewModel.shoeSize == null) {
            MainUtil().showToastMessage(requireContext(), getString(R.string.select_shoe_size_text))
            return
        }
        // no error found :: save shoe data to database
        // check whether we should insert new shoe or update an existing one
        if (!saveShoeViewModel.updateShoeInfo) {
            saveShoeViewModel.onInsertNewShoeInfoToDatabase()
        } else {
            saveShoeViewModel.onUpdateShoeInfoFromDatabase(saveShoeViewModel.existingShoeId)
        }
    }

    /**
     * Check for shoe name and brand name edit txt values
     */
    private fun checkForShoeNameAndBrand(shoeOrBrandName: String, shoeOrBrandNameEdt: EditText): Boolean {
        if (shoeOrBrandName.isEmpty()) {
            // empty shoe name
            shoeOrBrandNameEdt.error = getString(R.string.empty_name_error_text)
            return false
        } else if (!MainUtil().isValidName(shoeOrBrandName)) {
            // invalid shoe name
            shoeOrBrandNameEdt.error = getString(R.string.invalid_name_error_text)
            return false
        } else if (shoeOrBrandName.length < 3) {
            // short shoe name
            shoeOrBrandNameEdt.error = getString(R.string.too_short_name_error_text)
            return false
        }
        return true
    }

    /**
     * set shoe photo cached from preferences
     */
    private fun setShoePhotoFromSavedPreferences() {
        // get shoe photo path cached from preferences if exists??
        val shoePhotoPathPref: String? = SharedPrefUtil().getDataStringFromSharedPreferences(
            requireContext(),
            SHOE_PHOTO_PATH_EXTRA
        )
        // check if shoe photo has been cached to view model
        if (saveShoeViewModel.shoePhotoPath != null) {
            // set shoe photo path to ImageView holder
            saveShoeFragmentBinding.shoeThumbImg.setImageURI(
                Uri.fromFile(File(saveShoeViewModel.shoePhotoPath.toString()))
            )
        } else if (shoePhotoPathPref != "") { // if shoe photo path has been cached to preferences
            // load it to image view holder
            PhotoUtil().loadPhotoFileWithGlide(
                requireContext(),
                shoePhotoPathPref,
                null,
                saveShoeFragmentBinding.shoeThumbImg,
                R.drawable.shoe_64
            )
        }
    }
}