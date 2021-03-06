package com.www.dswstore.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.theartofdev.edmodo.cropper.CropImage
import com.www.dswstore.R
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.PhotoEditorView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Photo util :: contain every recurring task dealing with Photo
 */
class PhotoUtil {

    /**
     * Create a new file
     * @return :: nothing
     */
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            SharedPrefUtil().writeDataStringToSharedPreferences(context,SHOE_PHOTO_PATH_EXTRA,absolutePath)
        }
    }

    fun getPhotoFilePath(context: Context): String? {
        return SharedPrefUtil().getDataStringFromSharedPreferences(context, SHOE_PHOTO_PATH_EXTRA)
    }

    /**
     * Pick existing photo from phone gallery
     */
    fun pickPhotoFromGallery(context: Context): Intent? {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val chooserIntent =
            Intent.createChooser(getIntent, context.getString(R.string.choose_photo_from_text))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        return chooserIntent
    }

    /**
     * Add photo to phone gallery
     * @param context :: context
     * @param photoUri :: photo Uri
     */
    fun addPhotoToPhoneGallery(context: Context, photoUri: Uri?) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = photoUri
        context.sendBroadcast(mediaScanIntent)
    }

    /**
     * Capture photo from camera
     */
    fun capturePhoto(context: Context): Intent? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile(context)
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    context,
                    "com.www.dswstore.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                return takePictureIntent
            }
        }
        return null
    }

    /**
     * Crop photo after taking it from gallery or camera
     */
    fun cropPhoto(context: Context?, currentPhotoPath: String?, fragment: Fragment?) {
        if (currentPhotoPath != null) {
            val f = File(currentPhotoPath)
            val contentUri = Uri.fromFile(f)
            // start cropping activity for pre-acquired image saved on the device
            if (fragment != null) {
                if (context != null) {
                    CropImage.activity(contentUri)
                        .start(context, fragment)
                }
            }
        }
    }

    /**
     * load photo file with Glide
     * @param context :: context
     * @param photoFilePathUri :: photo file path URI
     * @param photoFilePathUrl :: photo file path URl
     * @param profilePhotoHolder :: photo image holder
     * @param errorDrawableImgFailed :: error drawable image failed
     */
    fun loadPhotoFileWithGlide(
        context: Context?,
        photoFilePathUri: String?,
        photoFilePathUrl: String?,
        profilePhotoHolder: ImageView?,
        errorDrawableImgFailed: Int
    ) {
        if (profilePhotoHolder != null) {
            if (photoFilePathUri != null) {
                // load photo from device
                Glide.with(context!!)
                    .load(File(photoFilePathUri))
                    .error(errorDrawableImgFailed)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(ColorDrawable(Color.TRANSPARENT))
                    .into(profilePhotoHolder)
            } else {
                // load photo from external url
                Glide.with(context!!)
                    .load(photoFilePathUrl)
                    .error(errorDrawableImgFailed)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(ColorDrawable(Color.TRANSPARENT))
                    .into(profilePhotoHolder)
            }
        }
    }

    /**
     * Photo editor view
     * @param context :: context
     * @param photoIntentUri :: Uri of the photo file
     * @param photoEditorView :: photo editor view
     * @return :: Photo editor object
     */
    fun photoEditorView(
        context: Context?,
        photoIntentUri: Uri?,
        photoEditorView: PhotoEditorView?
    ): PhotoEditor? {
        if (photoEditorView != null) {
            photoEditorView.source.adjustViewBounds = true
            photoEditorView.source.scaleType = ImageView.ScaleType.FIT_CENTER
            photoEditorView.source.setImageURI(photoIntentUri)
            // Photo editor
            return PhotoEditor.Builder(context, photoEditorView)
                .setPinchTextScalable(true)
                .build()
        }
        return null
    }

    interface MyCallback {
        fun onSavedPhotoFile(isSuccessful: Boolean)
    }

    /**
     * save photo file to user device
     * @param context :: context
     * @param photoEditor :: photo editor
     * @param myCallback :: call back method
     */
    fun savePhotoFile(context: Context?, photoEditor: PhotoEditor?, myCallback: MyCallback) {
        if (photoEditor != null) {
            try {
                createImageFile(context!!)
                // check for write permission granted
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    photoEditor.saveAsFile(getPhotoFilePath(context).toString(), object : OnSaveListener {
                        override fun onSuccess(@NonNull imagePath: String) {
                            myCallback.onSavedPhotoFile(true)
                        }

                        override fun onFailure(@NonNull exception: Exception) {
                            myCallback.onSavedPhotoFile(false)
                        }
                    })
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}