package com.example.metropickerwithmenu

import android.content.Context

open class Storage(context: Context) : MainActivity() {

    private val mPrefs      = context.getSharedPreferences(PREFS, MODE_PRIVATE)
    private val mDefaultMsg = context.resources.getString(R.string.no_station_selected_msg)

    fun getStation(): String {
        val station = mPrefs.getString(KEY_STATION_SELECTED, null)
        return station ?: mDefaultMsg
    }

    fun setStation(stationName: String?) {
        val editor = mPrefs.edit()
        editor.putString(KEY_STATION_SELECTED, stationName)
        editor.apply()
    }

    companion object {
        const val PREFS = "PREFS"
        const val KEY_STATION_SELECTED = "STATION"
    }
}