package com.example.trackingapp.sensor

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.example.trackingapp.util.CONST
import java.io.*

abstract class AbstractSensor protected constructor() : Serializable {
    @JvmField
    protected var TAG: String? = null
    var sensorName: String? = null
        protected set
    var isEnabled = true
    protected var fileName: String? = null

    @JvmField
    protected var m_FileHeader: String? = null
    var settings = ""

    @JvmField
    protected var m_isSensorAvailable = false

    @JvmField
    protected var m_OutputStream: OutputStream? = null
    var isRunning = false
        protected set
    val filePath: String
        get() = CONST.ROOT_FOLDER.absolutePath + "/" + fileName
    val settingsState: Int
        get() = 0

    abstract fun getSettingsView(context: Context?): View?
    abstract fun isAvailable(context: Context?): Boolean
    open fun start(context: Context) {
        m_isSensorAvailable = isAvailable(context)
        if (!m_isSensorAvailable) Log.i(TAG, "Sensor not available")
        initFile(context)
    }

    fun initFile(context: Context) {
        /*try {
				File file = new File(getFilePath());
				if(!file.exists()){
					m_FileWriter = new FileWriter(getFilePath(), true);
					m_FileWriter.write(m_FileHeader + "\n");
					Log.w(TAG, "File created " + getFilePath() );
				} else {
					Log.w(TAG, "File exits " + getFilePath() );
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName) // file name
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, CONST.RELATIVE_PATH)
            val extVolumeUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,  // unused (for verification use only)
                MediaStore.MediaColumns.RELATIVE_PATH,  // unused (for verification use only)
                MediaStore.MediaColumns.DATE_MODIFIED //used to set signature for Glide
            )
            val selection =
                (MediaStore.MediaColumns.RELATIVE_PATH + "='" + CONST.RELATIVE_PATH + "' AND "
                        + MediaStore.MediaColumns.DISPLAY_NAME + "='" + fileName + "'")
            var fileUri: Uri? = null
            val cursor =
                context.contentResolver.query(extVolumeUri, projection, selection, null, null)
            if (cursor!!.count > 0) {
                if (cursor.moveToFirst()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    fileUri = ContentUris.withAppendedId(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, id
                    )
                }
            } else {
            }
            cursor.close()
            if (fileUri == null) fileUri = context.contentResolver.insert(extVolumeUri, values)
            try {
                m_OutputStream = context.contentResolver.openOutputStream(fileUri!!, "wa")
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "Error #001: $e")
            }
        } else {
            val path = CONST.RELATIVE_PATH
            val file = File(path, fileName)
            //Log.d(TAG, "saveFile: file path - " + file.getAbsolutePath());
            try {
                m_OutputStream = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    abstract fun stop()

    companion object {
        private const val serialVersionUID = 1L
    }
}