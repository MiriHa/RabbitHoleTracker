package com.example.trackingapp.sensor.implementation

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.CallLog.Calls
import android.provider.ContactsContract.PhoneLookup
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.PhoneNumberHelper

class CallSensor: AbstractSensor(
    "CALLSENSOR",
    "call"
) {
    private var mReceiver: BroadcastReceiver? = null
    private var m_context: Context? = null

    override fun getSettingsView(context: Context?): View? {
        return null
    }

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
        Log.d(TAG, "StartScreenSensor: ${CONST.dateTimeFormat.format(time)}")

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_CALL)


        //mReceiver = PowerSensor.PowerReceiver()
        try {
            context.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            //Not Registered
        }
        context.registerReceiver(mReceiver, filter)
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            m_context?.unregisterReceiver(mReceiver)
        }
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()

    }


    class CallReceiver : BroadcastReceiver() {
        val TAG = "CallReceiver"
        private val mHandler = Handler()
        private var broadCastTimestamp: Long = 0
        private var sharedPrefs: SharedPreferences? = null
        private var lastCallLogType: String? = null
        private var lastCallLogNumber: String? = null
        private var countryCode: String? = null
        private var lastCallLogTimestamp: Long = 0
        private var lastCallLogDuration: Long = 0
        private var contactName: String? = null
        private var contactUid = 0


        override fun onReceive(context: Context, intent: Intent) {
            if (LoggingManager.isDataRecordingActive == false) {
                return
            }

            broadCastTimestamp = System.currentTimeMillis()

            sharedPrefs = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
            val extras = intent.extras
            val state = extras?.getString(TelephonyManager.EXTRA_STATE)

            // receives cached call event from SharedPreferences
            val cachedCallEvent = callReceiverCache

            // is phone ringing?
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                callReceiverCache = RINGING
            } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                // is phone off the hook?
                // means: user is calling someone (and is waiting = on hold)
                // or user gets called and picked up (phone was ringing before)
                if (cachedCallEvent != null && cachedCallEvent != RINGING) {
                    callReceiverCache = ONHOLD
                }
            } else if (TelephonyManager.EXTRA_STATE_IDLE == state) {
                // is always called after action
                // run CallRunnable with 2000 ms delay to avoid race conditions
                // (CallLog has to be updated first before retrieving the last call entry)
                mHandler.postDelayed(object : CallRunnable(cachedCallEvent) {
                    override fun run() {
                        Log.d(TAG, "run() at " + System.currentTimeMillis())

                        // Get data of last CallLog entry, save them to corresponding instance variables
                        getLastCallLogEntryData(context)
                        getContactNameAndUidFromNumber(context, lastCallLogNumber)
/*

                        // Ringing length (also onhold length)
                        // broadCastTimestamp = in this case: moment when call was rejected / missed / ended in ms
                        // lastCallLogTimestamp = moment when phone started ringing / dialing in ms
                        // lastCallLogDuration = call duration in seconds in s
                        val totalLength = (broadCastTimestamp - lastCallLogTimestamp) / 1000
                        val ringingLength = (totalLength - lastCallLogDuration).toInt()

                        // Save Ringing state to DB
                        if (cachedEvent != null && cachedEvent  == RINGING) {
                            LogHelper.i(TAG, "Ringing duration: $broadCastTimestamp - $lastCallLogTimestamp = $ringingLength")
                            if (!appDatabaseHelper.isAlreadySaved(UsageActivityName.PHONE, RINGING, lastCallLogTimestamp)) {
                                saveEntry(
                                    context, lastCallLogNumber, countryCode, contactName, contactUid,
                                    lastCallLogTimestamp, ringingLength, RINGING
                                )
                            }
                        }
*/

                        // Save on hold State to DB
                   /*     if (cachedEvent != null && cachedEvent == ONHOLD) {
                            LogHelper.i(TAG, "Ringing duration onhold: $broadCastTimestamp - $lastCallLogTimestamp = $ringingLength")
                            if (!appDatabaseHelper.isAlreadySaved(UsageActivityName.PHONE, ONHOLD, lastCallLogTimestamp)) {
                                saveEntry(
                                    context, lastCallLogNumber, countryCode, contactName, contactUid,
                                    lastCallLogTimestamp, ringingLength, ONHOLD
                                )
                            }
                        }
*/

                        // Save last call log entry to db
                       /* val timestamp = lastCallLogTimestamp + ringingLength // Moment when call really started
                        if (!appDatabaseHelper.isAlreadySaved(UsageActivityName.PHONE, lastCallLogType, timestamp)) {
                            if ("OUTGOING" != lastCallLogType || lastCallLogDuration > 0) { // do not log OUTGOING with duration 0 (#301)
                                if (Build.VERSION.SDK_INT < 24 && "INCOMING" == lastCallLogType && lastCallLogDuration == 0L) {
                                    // workaround for SDK < 24, where rejected does not exist: INCOMING with duration 0 corresponds to REJECTED
                                    saveEntry(
                                        context, lastCallLogNumber, countryCode, contactName, contactUid,
                                        timestamp, lastCallLogDuration.toInt(), "REJECTED"
                                    )
                                }
                                Log.d(TAG, "Found no entry for: PHONE $lastCallLogType $timestamp")
                                saveEntry(
                                    context, lastCallLogNumber, countryCode, contactName, contactUid,
                                    timestamp, lastCallLogDuration.toInt(), lastCallLogType
                                )
                            }
                        }*/

                        // Update callReceiverCache in sharedPrefs with last saved call data!
                        callReceiverCache = lastCallLogType
                    }
                }, 2000)
            }
        }

        /**
         * Implements runnable and accepts event name as argument
         */
        open inner class CallRunnable(cachedEvent: String?) : Runnable {
            var cachedEvent: String? = null

            override fun run() {}

            init {
                this.cachedEvent = cachedEvent
            }
        }
        /**
         * Returns the cached event name from SharedPreferences
         *
         * @return      last event name
         */
        /**
         * Caches the last event using SharedPreferences
         *
         * @param event     event name
         */
        private var callReceiverCache: String?
            private get() {
                Log.d(TAG, "getCallReceiverCache()")
                return sharedPrefs!!.getString("CallReceiverEvent", null)
            }
            private set(event) {
                Log.d(TAG, "setCallReceiverCache()")
                val editor = sharedPrefs!!.edit()
                editor.putString("CallReceiverEvent", event)
                editor.apply()
            }

        /**
         * Saves the call data into database.
         *
         * @param phoneNumber       phone number
         * @param countryCode       countryCode of the number
         * @param contactName       contact name
         * @param contactUid        contact uid
         * @param timestamp         start of the call event in milliseconds
         * @param duration          duration / length of the call event in seconds
         * @param event             event name
         */
        private fun saveEntry(
            context: Context, phoneNumber: String?, countryCode: String?, contactName: String?, contactUid: Int,
            timestamp: Long, duration: Int, event: String?
        ) {

            //create MetaCall
            /*val meta = MetaCall()
            val phoneNumberHash: String = hashFactory.getHash(PhoneNumberHelper.formatNumber(phoneNumber))
            meta.phoneNumber(phoneNumberHash).countryCode(countryCode).partner(hashFactory.getHash(contactName)).duration(duration).contactId(contactUid)
            Log.logMetaData(TAG, meta.toJSONString())*/

            //create UsageActivity using MetaData and store to DB
            //val activity = UsageActivity()
            /*activity.activityName(UsageActivityName.PHONE).timestamp(timestamp).event(event).metaData(meta)
            activity.insertIntoDB()*/
        }

        /**
         * Gets the data of the last CallLog entry
         */
        private fun getLastCallLogEntryData(context: Context) {
            Log.d(TAG, "getLastCallLogEntryData()")
            var c: Cursor? = null
            val selection = arrayOf(Calls.TYPE, Calls.DATE, Calls.NUMBER, Calls.DURATION)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    c = context.contentResolver.query(
                        Calls.CONTENT_URI,
                        selection, null, null, Calls.DATE + " DESC"
                    )
                    val type = c!!.getColumnIndex(Calls.TYPE)
                    val date = c.getColumnIndex(Calls.DATE)
                    val number = c.getColumnIndex(Calls.NUMBER)
                    val duration = c.getColumnIndex(Calls.DURATION)
                    if (c.moveToFirst()) {
                        val typeInt = c.getInt(type)
                        lastCallLogType = getReadableType(typeInt)
                        lastCallLogTimestamp = c.getLong(date)
                        lastCallLogNumber = c.getString(number)
                        lastCallLogDuration = c.getLong(duration)
                        countryCode = PhoneNumberHelper.extractCountryCodeFromNumber(lastCallLogNumber)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    c?.close()
                }
            }
        }

        /**
         * Gets contact name and contact uid
         */
        fun getContactNameAndUidFromNumber(context: Context, number: String?) {
            Log.d(TAG, "getContactNameAndUidFromNumber()")
            var c: Cursor? = null
            val selection = arrayOf(PhoneLookup._ID, PhoneLookup.DISPLAY_NAME, PhoneLookup.NUMBER)
            val contact = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
            try {
                c = context.contentResolver.query(contact, selection, null, null, null)
                val displayName = c!!.getColumnIndex(PhoneLookup.DISPLAY_NAME)
                val uid = c.getColumnIndex(PhoneLookup._ID)
                if (c.moveToFirst()) {
                    contactName = c.getString(displayName)
                    contactUid = c.getInt(uid)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                c?.close()
            }
        }

        /**
         * Converts call type integer values into human readable type names
         *
         * @return human-readable type name
         * @see [Android CallLog.Calls TYPE](https://developer.android.com/reference/android/provider/CallLog.Calls.html.TYPE)
         */
        private fun getReadableType(typeInt: Int): String {
            Log.d(TAG, "getReadableType()")
            var type = ""
            type = when (typeInt) {
                1 -> "INCOMING"
                2 -> "OUTGOING"
                3 -> "MISSED"
                4 -> "VOICEMAIL" //SDK LEVEL 21
                5 -> "REJECTED" //SDK LEVEL 24
                6 -> "BLOCKED" //SDK LEVEL 24
                7 -> "ANSWERED_EXTERNALLY" //SDK LEVEL 25
                else -> "UNKNOWN: $type"
            }
            return type
        }

        companion object {
            private const val RINGING = "RINGING"
            private const val ONHOLD = "ONHOLD"
        }
    }

}