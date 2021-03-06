package com.www.dswstore.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Window
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.www.dswstore.R
import java.util.*


class MainUtil {
    private var mSharedPreferences: SharedPreferences? = null

    /**
     * Detect Night mode
     */
    fun detectNightMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    /**
     * Show toast message
     */
    fun showToastMessage(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(0, 0, 0)
        toast.show()
    }

    /**
     * Set bottom bar navigation background color
     */
    fun setBottomBarNavigationBackgroundColor(
        window: Window,
        context: Context,
        defaultSystemBgColor: Int,
        darkModeSystemColor: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(context, defaultSystemBgColor)
        }


        if (detectNightMode(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(
                context,
                darkModeSystemColor
            )
        }
    }

    /**
     * Write string data to preferences
     * @param keyValue :: key value
     * @param value :: value to be stored
     */
    fun writeDataStringToSharedPreferences(context: Context, keyValue: String?, value: String?) {
        // get shared preferences
        mSharedPreferences = context.getSharedPreferences(
            context.getString(R.string.package_name_text), Context.MODE_PRIVATE
        )

        val editor = mSharedPreferences!!.edit()
        editor.putString(keyValue, value)
        editor.apply()
    }

    /**
     * Get string data from preferences
     * @param keyValue :: key value
     * @return data string
     */
    fun getDataStringFromSharedPreferences(context: Context, keyValue: String?): String? {
        mSharedPreferences = context.getSharedPreferences(
            context.getString(R.string.package_name_text), Context.MODE_PRIVATE
        )
        return mSharedPreferences!!.getString(keyValue, "")
    }

    /**
     * Set Action Bar background color
     * @param context :: context
     * @param actionBar :: action bar
     * @param bgColorRes :: color int from resources
     */
    fun setActionBarBackgroundColor(context: Context, actionBar: ActionBar?, bgColorRes: Int) {
        // Define ActionBar object;
        if (actionBar == null) return
        // Define ColorDrawable object and color res int
        // with color int res code as its parameter
        val colorDrawable = ColorDrawable(ContextCompat.getColor(context, bgColorRes))

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable)
    }

    /**
     * Capitalize each word from string
     * @param stringInput :: the string to transform
     * @return new string with each word capitalized
     */
    fun capitalizeEachWordFromString(stringInput: String): String {
        val strArray = stringInput.split(" ".toRegex()).toTypedArray()
        val builder = StringBuilder()
        for (s in strArray) {
            val cap = s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1)
            builder.append(cap).append(" ")
        }
        return builder.toString()
    }

    /*
     * This method checks for a valid name :: contains only letters
     * @param name :: name input
     * @return true or false
     */
    fun isValidName(name: String): Boolean {
        val nameRegX = Regex("^[\\p{L} .'-]+$")
        return name.matches(nameRegX)
    }

    /**
     * Get number of column width for Grid layout auto fit
     */
    fun getNumbColumnsForGridLayoutAutoFit(context: Context, columnWidthDp: Float): Int {
        // For example columnWidthDp=180
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }

    /**
     * Share Text data through app
     * @param shareAboutTitle :: title of share dialog
     * @param textToShare :: text data to share
     */
    fun shareTextData(context: Context, shareAboutTitle: String?, textToShare: String?) {
        val mimeType = "text/plain"

        // Use ShareCompat.IntentBuilder to build the Intent and start the chooser
        /* ShareCompat.IntentBuilder provides a fluent API for creating Intents */ShareCompat.IntentBuilder /* The from method specifies the Context from which this share is coming from */
            .from((context as Activity?)!!)
            .setType(mimeType)
            .setChooserTitle(shareAboutTitle)
            .setText(textToShare)
            .startChooser()
    }

    /**
     * Display snack bar message
     * @param contextView :: coordinatorLayout root view
     * @param messageResId :: message resource id string
     */
    fun displaySnackBarMessage(
        contextView: CoordinatorLayout?,
        messageResId: Int,
        snackBarDuration: Int
    ): Snackbar {
        val snackBar = Snackbar.make(
            contextView!!,
            messageResId, snackBarDuration
        )
        snackBar.show()
        return snackBar
    }
}