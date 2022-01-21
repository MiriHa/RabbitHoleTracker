package com.example.trackingapp.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson


object SharedPrefManager {

    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(CONST.PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    fun saveLastIntention(intention: String){
        val editor = sharedPrefs.edit()
        editor.putString(CONST.PREFERENCES_INTENTION_NAME, intention)
        editor.apply()
    }

    fun getLastSavedIntention(): String?{
        return sharedPrefs.getString(CONST.PREFERENCES_INTENTION_NAME, "last intention")
    }

    //TODO remove?
    fun saveBoolean(key: String, intention: Boolean){
        val editor = sharedPrefs.edit()
        editor.putBoolean(key, intention)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean{
        return sharedPrefs.getBoolean(key, false)
    }

    fun saveIntentionList(intentionMap: MutableSet<String?>?) {
        val json: String = Gson().toJson(intentionMap)

        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putString(CONST.PREFERENCES_INTENTION_LIST, json)
        editor.apply()
    }

    fun getIntentionList(): MutableSet<String?> {
        val json: String? = sharedPrefs.getString(CONST.PREFERENCES_INTENTION_LIST, "")
        return Gson().fromJson<MutableSet<String?>>(json, MutableSet::class.java)
    }

}