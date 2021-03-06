package com.www.dswstore.util

import android.content.Context
import android.content.SharedPreferences
import com.www.dswstore.R


/**
 * Shared Preferences Util :: contain every recurring task dealing shared preferences
 */
class SharedPrefUtil {
    private var mSharedPreferences: SharedPreferences? = null

    /**
     * Set up shared preferences
     * @param context :: context
     */
    private fun setUpSharedPreferences(context: Context) {
        mSharedPreferences = context.getSharedPreferences(
            context.getString(R.string.package_name_text), Context.MODE_PRIVATE
        )
    }

    /**
     * Write string data to preferences
     * @param keyValue :: key value
     * @param value :: value to be stored
     */
    fun writeDataStringToSharedPreferences(context: Context, keyValue: String?, value: String?) {
        setUpSharedPreferences(context)
        val editor = mSharedPreferences!!.edit()
        editor.putString(keyValue, value)
        editor.apply()
    }

    /**
     * Get string data from preferences
     * @param keyValue :: key value
     * @return data string
     */
    fun getDataStringFromSharedPreferences(context: Context?, keyValue: String?): String? {
        setUpSharedPreferences(context!!)
        return mSharedPreferences!!.getString(keyValue, "")
    }

    /**
     * Write int data to preferences
     * @param keyValue :: data key value
     * @param value :: int value to be stored
     */
    fun writeDataIntToSharedPreferences(context: Context?, keyValue: String?, value: Int) {
        setUpSharedPreferences(context!!)
        val editor = mSharedPreferences!!.edit()
        editor.putInt(keyValue, value)
        editor.apply()
    }

    /**
     * Get int data from preferences
     * @param keyValue :: key for preference
     * @return data int
     */
    fun getDataIntFromSharedPreferences(context: Context?, keyValue: String?): Int {
        setUpSharedPreferences(context!!)
        return mSharedPreferences!!.getInt(keyValue, 0)
    }

    /**
     * Write boolean data to preferences
     * @param keyValue :: boolean key data
     * @param value :: boolean data to be stored
     */
    fun writeDataBooleanToSharedPreferences(context: Context?, keyValue: String?, value: Boolean) {
        setUpSharedPreferences(context!!)
        val editor = mSharedPreferences!!.edit()
        editor.putBoolean(keyValue, value)
        editor.apply()
    }

    /**
     * Get boolean data from preferences
     * @param keyValue :: key for preference
     * @return boolean data
     */
    fun getDataBooleanFromSharedPreferences(context: Context?, keyValue: String?): Boolean {
        setUpSharedPreferences(context!!)
        return mSharedPreferences!!.getBoolean(keyValue, false)
    }

    /**
     * Write array list data to preferences
     * @param keyValue :: key of array list data
     * @param arrayListValue :: array list value to be stored
     */
    fun writeDataArrayListStringToSharedPreferences(
        context: Context?,
        keyValue: String?,
        arrayListValue: ArrayList<String>
    ) {
        setUpSharedPreferences(context!!)
        val setValue: Set<String?> = HashSet(arrayListValue)
        val editor = mSharedPreferences!!.edit()
        editor.putStringSet(keyValue, setValue)
        editor.apply()
    }

    /**
     * Get array list data from preferences
     * @param keyValue :: key for preference
     * @return array list data
     */
    fun getDataArrayListStringFromSharedPreferences(
        context: Context?,
        keyValue: String?
    ): Set<String?>? {
        setUpSharedPreferences(context!!)
        return mSharedPreferences!!.getStringSet(keyValue, null)
    }

    /**
     * Delete all data from preferences
     */
    fun clearAllPreferencesData(context: Context?) {
        setUpSharedPreferences(context!!)
        mSharedPreferences!!.edit().clear().apply()
    }

    /**
     * Delete a specific preference by key value
     * @param keyName :: key for preference
     */
    fun clearPreferenceDataByKey(context: Context?, keyName: String?) {
        setUpSharedPreferences(context!!)
        mSharedPreferences!!.edit().remove(keyName).apply()
    }
}