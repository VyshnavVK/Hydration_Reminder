package com.app.myapplication.util

import android.content.Context
import android.preference.PreferenceManager

typealias sp = SharedPreferenceHelper
class SharedPreferenceHelper(private val mContext: Context) {

    companion object {
        fun with(context: Context): SharedPreferenceHelper {
            return SharedPreferenceHelper(context)
        }

        const val INTERVALS_VALUE_KEY = "intervalsValueKey"
        const val NOTIFICATION_KEY = "notificationKey"
        const val NOTIFICATION_VALUE_KEY = "notificationValueKey"
        const val SOUND_KEY = "soundKey"
    }

    fun putInt(key: String?, value: Int): SharedPreferenceHelper {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putInt(key, value)
        edit.apply()
        return this
    }

    fun putBoolean(key: String?, `val`: Boolean): SharedPreferenceHelper {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putBoolean(key, `val`)
        edit.apply()
        return this
    }

    fun putString(key: String?, `val`: String?): SharedPreferenceHelper {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putString(key, `val`)
        edit.apply()
        return this
    }

    fun putFloat(key: String?, `val`: Float): SharedPreferenceHelper {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putFloat(key, `val`)
        edit.apply()
        return this
    }

    fun putLong(key: String?, `val`: Long): SharedPreferenceHelper {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putLong(key, `val`)
        edit.apply()
        return this
    }

    fun getLong(key: String?, _default: Long): Long {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getLong(key, _default)
    }

    fun getFloat(key: String?, _default: Float): Float {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getFloat(key, _default)
    }

    fun getString(key: String?, _default: String?): String? {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getString(key, _default)
    }

    fun getInt(key: String?, _default: Int): Int {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getInt(key, _default)
    }

    fun getBoolean(key: String?, _default: Boolean): Boolean {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getBoolean(key, _default)
    }

    fun hasKey(key: String?): Boolean {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.contains(key)
    }

    fun clearPreferences(): SharedPreferenceHelper {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(mContext)
        preferences.edit().clear().apply()
        return this
    }


}