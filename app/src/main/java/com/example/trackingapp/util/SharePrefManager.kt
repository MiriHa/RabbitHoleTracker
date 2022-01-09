package com.example.trackingapp.util

import android.content.Context

object SharePrefManager {

    fun saveLastIntention(context: Context, intention: String){
        val sharedPref = context.getSharedPreferences(CONST.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(CONST.PREFERENCES_INTENTION_NAME, intention)
        editor.apply()
    }

    fun getLastSavedIntention(context: Context): String?{
        val sharedPref = context.getSharedPreferences(CONST.PREFERENCES_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString(CONST.PREFERENCES_INTENTION_NAME, "last intention")
    }
}