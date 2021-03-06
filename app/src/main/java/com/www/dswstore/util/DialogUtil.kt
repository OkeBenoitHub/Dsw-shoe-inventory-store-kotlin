package com.www.dswstore.util

import android.content.Context
import android.app.ProgressDialog;
import android.content.DialogInterface
import androidx.annotation.MenuRes

import androidx.appcompat.app.AlertDialog;
import com.www.dswstore.R
import org.michaelbel.bottomsheet.BottomSheet

/**
 * Dialog Util :: contain every recurring task dealing with Android Dialog
 */
class DialogUtil {

    private val mLoaderDialog: ProgressDialog? = null

    interface ShowBasicAlertDialogCallback {
        fun onShowBasicAlertDialog(isPositiveBtnTapped: Boolean)
    }

    /**
     * Show alert dialog
     * @param context :: context
     * @param title :: dialog title
     * @param contentMessage :: dialog content message
     * @param positiveBtnText :: dialog positive button text
     * @param negativeBtnText :: dialog negative button text
     */
    fun showBasicAlertDialog(
        context: Context,
        title: String?,
        contentMessage: String?,
        positiveBtnText: String?,
        negativeBtnText: String?,
        isCancelTapOutside: Boolean,
        showBasicAlertDialogCallback: ShowBasicAlertDialogCallback
    ) {
        // Build an AlertDialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            context,
            R.style.Theme_MaterialComponents_DayNight_Dialog_Alert
        )

        // Set a title for alert dialog
        builder.setTitle(title)
        // Ask the final question
        builder.setMessage(contentMessage)
        builder.setCancelable(isCancelTapOutside)

        // Set the alert dialog yes button click listener
        builder.setPositiveButton(positiveBtnText) { dialog, which ->
            // Do something when user clicked the Yes button
            showBasicAlertDialogCallback.onShowBasicAlertDialog(true)
        }

        // Set the alert dialog no button click listener
        builder.setNegativeButton(negativeBtnText) { dialog, which ->
            // Do something when No button clicked
            showBasicAlertDialogCallback.onShowBasicAlertDialog(false)
        }
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on interface
        dialog.show()
    }

    /**
     * Show bottom sheet menu dialog options
     */
    fun showBottomSheetMenuDialogOptions(context: Context, menuResXml: Int, bottomSheetListener: DialogInterface.OnClickListener): BottomSheet.Builder {
        val builder = BottomSheet.Builder(context)
        //builder.setView(R.layout.sort_shoes_bottom_sheet_options)
        builder.setMenu(menuResXml,bottomSheetListener)
        builder.setItemTextColorRes(R.color.edit_text_color)
        builder.setDarkTheme(false)
        builder.setFullWidth(true)
        builder.show()
        return builder
    }
}