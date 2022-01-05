package com.example.trackingapp.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object CONST {
    private const val TAG = "CONST"
    @JvmField
	//val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    var dateTimeFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.GERMAN)
    var dateFormat: DateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.GERMAN)

    const val SP_LOG_EVERYTHING = "sp_log_everything"
    const val SP_Accessibility_LOG_EVERYTHING = "sp_log_everything"
    const val KEY_LOG_EVERYTHING_RUNNING = "key_log_everything_running"
    const val KEY_Accessibility_LOG_EVERYTHING_RUNNING = "key_Accessibility_LOG_everything_running"

    const val firebaseReferenceUsers = "users"
    const val firebaseReferenceLogs = "logs"

    private const val BASE_DIR = "LogEverything"
    var RELATIVE_PATH: String? = null
    var ROOT_FOLDER: File? = null
    fun commonDocumentDirPath(FolderName: String): File {
        val dir: File
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            RELATIVE_PATH =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + FolderName
            RELATIVE_PATH = Environment.DIRECTORY_DOWNLOADS + File.separator + FolderName
        } else {
            RELATIVE_PATH =
                Environment.getExternalStorageDirectory().toString() + File.separator + FolderName
        }
        dir = File(RELATIVE_PATH)
        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            val success = dir.mkdirs()
            if (!success) {
                //dir = null;
                Log.e(TAG, "commonDocumentDirPath failed")
            }
        }
        return dir
    }

    fun setSavePath(pContext: Context?) {

        /*String deviceId = Secure.getString(pContext.getContentResolver(), Secure.ANDROID_ID);
		if(deviceId == null) {
			deviceId = "NULL";
		}
		ModelLog.d(TAG, "DEVICE ID: " + deviceId);*/
        val LOG_DIR = dateTimeFormat.format(Date())
        ROOT_FOLDER = commonDocumentDirPath(BASE_DIR + File.separator + LOG_DIR + File.separator)
        Log.d(TAG, ROOT_FOLDER!!.absolutePath)
        if (!ROOT_FOLDER!!.exists()) {
            if (!ROOT_FOLDER!!.mkdirs()) {
                Log.e(TAG, "error creating the folders")
            }
        }
    }

    fun sdk29AndUp(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    val numberFormat = NumberFormat.getInstance()

    init {
        numberFormat.maximumFractionDigits = Int.MAX_VALUE
        numberFormat.isGroupingUsed = false
    }
}