package com.example.trackingapp.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.trackingapp.models.SmsEventType
import com.example.trackingapp.util.PhoneNumberHelper
import com.example.trackingapp.util.SmsHelper

class SmsObserver(handler: Handler?, context: Context) : ContentObserver(handler) {
    private val context: Context
    private var lastOnChangeCall = System.currentTimeMillis()

    override fun onChange(selfChange: Boolean) {
        if (LoggingManager.isDataRecordingActive == false) {
            return
        }

        //don't understand what this does
        if (System.currentTimeMillis() - lastOnChangeCall < 3000) {
            Log.d(TAG, "no URI provided in onChange call")
            this.onChange(selfChange, null)
        }
        lastOnChangeCall = System.currentTimeMillis()
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if (LoggingManager.isDataRecordingActive == false) {
            return
        }
        if (System.currentTimeMillis() - lastOnChangeCall < 2000) {
            Log.d(TAG, "last onChange less than 2s ago, aborted")
            return
        }
        lastOnChangeCall = System.currentTimeMillis()
        //TODO maybe to with coroutine?

        val t = HandlerThread("HANDLER")
        t.start()
        val handler = Handler(t.looper)
        handler.postDelayed({
            Log.d(TAG, "In Runnable ")
            getSMS(context)
        }, 1000)
//        val scope = MainScope()
//        scope.launch {  }

//        CoroutineScope(Dispatchers.IO).launch {
//            delay(1000)
//            getSMS(context)
//        }
    }

    /**
     * Queries the sms content provider using the internal sms id
     *
     * @param context context
     */
    private fun getSMS(context: Context) {
        Log.d(TAG, "getSMS()")
        var c: Cursor? = null
        val projection: Array<String>? = null
        val sortOrder: String = SmsHelper.DATE.toString() + " desc"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                c = context.contentResolver.query(
                    Uri.parse(SmsHelper.CONTENT_URI),
                    projection, null, null, sortOrder
                )
                if (c != null) {
                    val addressIndex = c.getColumnIndexOrThrow(SmsHelper.ADDRESS) //Telephony.TextBasedSmsColumns.ADDRESS
                    val bodyIndex = c.getColumnIndexOrThrow(SmsHelper.BODY) //Telephony.TextBasedSmsColumns.BODY
                    val dateIndex = c.getColumnIndexOrThrow(SmsHelper.DATE) //Telephony.TextBasedSmsColumns.DATE
                    val typeIndex = c.getColumnIndexOrThrow(SmsHelper.TYPE) //Telephony.TextBasedSmsColumns.TYPE

                    if (c.moveToFirst()) {
                        val type = c.getInt(typeIndex)
                        val eventType: SmsEventType = SmsHelper.getTypeByConstant(type)
                        val address = c.getString(addressIndex)
                        val body = c.getString(bodyIndex)
                        val timestamp = c.getLong(dateIndex)

                        //generate ID
                        val smsID: String = SmsHelper.generateSmsID(address, timestamp)
                        val partnerName: String? = SmsHelper.getContactNameByNumber(context, address)
                        val partnerUID: Int = SmsHelper.getContactUidByNumber(context, address) //if this is 0, then partner number is not saved in contacts
                        val messageLength = body.length
                        val countryCode = getCountryCode(address)
                        val hashedNumber: String = PhoneNumberHelper.formatNumber(address).hashCode().toString()


                        //check if entry wasn't saved yet: save / dont save
                        if (!SmsHelper.wasSavedIDBased(smsID)) {
                            Log.i(TAG, "Already Saved sms")
                        } else {
                            val save = true
                            SmsHelper.saveEntry(
                                context,
                                save,
                                eventType,
                                timestamp,
                                address,
                                hashedNumber,
                                countryCode,
                                partnerName.hashCode().toString(),
                                body.hashCode().toString(),
                                messageLength,
                                partnerUID,
                                smsID
                            )
                            Log.i(TAG, "Checked sms was already saved, updated")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Problem with extracting data from sms. ")
            } finally {
                c?.close()
            }
        }
    }

    override fun deliverSelfNotifications(): Boolean {
        Log.d(TAG, "deliverSelfNotifications()")
        return true
    }

    private fun getCountryCode(number: String): String {

        //Extract Own number
        /*AssetManager assetManager1 = getContext().getAssets();
        String defaultcc = PhoneNumberHelper.getOwnCountryCode(context);//  CountryCodeHelper.extractCountryCodeFromNumber(assetManager1, CountryCodeHelper.getOwnPhonenumber(context), null);
        */
        return PhoneNumberHelper.extractCountryCodeFromNumber(number)
    }

    companion object {
        var TAG = "SmsObserver"
    }

    /**
     * Constructor.
     *
     * @param handler
     * @param context
     */
    init {
        Log.d(TAG, "constructor")
        this.context = context
    }
}
