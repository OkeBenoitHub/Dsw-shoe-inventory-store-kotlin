package com.www.dswstore.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.www.dswstore.R
import com.www.dswstore.util.*
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView


class ViewPhotoActivity : AppCompatActivity(),PhotoUtil.MyCallback {
    private lateinit var mIntent: Intent
    private var mPhotoEditor: PhotoEditor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_photo)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(R.string.profile_photo_label_text)

        // Change action Bar background color
        MainUtil().setActionBarBackgroundColor(this, supportActionBar, R.color.colorPrimary)

        // Set navigation bottom background color
        if (MainUtil().detectNightMode(this)) {
            MainUtil().setBottomBarNavigationBackgroundColor(
                window = window,
                this,
                R.color.bottom_black_color,
                R.color.colorPrimaryDark
            )
        }

        mIntent = intent
        val photoEditorView = findViewById<PhotoEditorView>(R.id.photoEditorView)
        if (mIntent.hasExtra(SHOE_PHOTO_PATH_EXTRA)) {
            val photoIntentUri: Uri =
                Uri.parse(mIntent.getStringExtra(SHOE_PHOTO_PATH_EXTRA))
            mPhotoEditor = PhotoUtil().photoEditorView(this, photoIntentUri, photoEditorView)
        }

        val saveEditedPhotoButton = findViewById<Button>(R.id.saveEditedPhoto)
        saveEditedPhotoButton.setOnClickListener {
            saveEditedPhoto()
        }
    }

    /**
     * Save photo
     */
    private fun saveEditedPhoto() {
        // check for run external write permission first
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE
            )
            return
        }
        PhotoUtil().savePhotoFile(this, mPhotoEditor, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                // permission was granted, yay!
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                ) {
                    PhotoUtil().savePhotoFile(this, mPhotoEditor,this)
                }
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                MainUtil().showToastMessage(
                    applicationContext,
                    getString(R.string.needs_access_storage_warning)
                )
            }
        }
    }

    private fun returnBackIntent() {
        mIntent = Intent()
        val photoFilePath: String? = PhotoUtil().getPhotoFilePath(this)
        mIntent.putExtra(SHOE_PHOTO_PATH_EXTRA, photoFilePath)
        setResult(RESULT_OK, mIntent)
        finish()
    }

    override fun onSavedPhotoFile(isSuccessful: Boolean) {
        if (isSuccessful) {
            // photo saved to device successfully
            returnBackIntent()
        } else {
            // failed to saved
            MainUtil().showToastMessage(
                applicationContext,
                getString(R.string.failed_to_save_photo_text)
            )
        }
    }
}