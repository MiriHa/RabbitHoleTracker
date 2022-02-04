package com.example.trackingapp.sensor.usage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.InstallEventType
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST

class InstalledAppSensor : AbstractSensor(
    "INSTALLED_APP_SENSOR",
    "Installed Apps"
) {

    private lateinit var mContext: Context
    private lateinit var mReceiver: BroadcastReceiver

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")


        //InstAppReceiver
        val filter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED)
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        filter.addDataScheme("package")

        mContext = context
        mReceiver = InstAppReceiver()
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
            mContext.unregisterReceiver(mReceiver)
        }
    }

    private fun saveEntry(type: InstallEventType, packageName: String, appLabel: String, timestamp: Long) {
        //generating UsageActivity and inserting it into db
        LogEvent(LogEventName.APPS_INSTALL, timestamp, packageName = packageName, event = type.name, name = appLabel).saveToDataBase()
    }

    inner class InstAppReceiver : BroadcastReceiver() {
        private val LABEL_UNKNOWN = "UNKNOWN"
        override fun onReceive(context: Context, intent: Intent) {
            if (LoggingManager.isDataRecordingActive == false) {
                return
            }

            //get package name from intent
            val uri = intent.data
            val packageName = uri?.schemeSpecificPart
            val action = intent.action
            val timestamp = System.currentTimeMillis()
            if (packageName != null) {
                //get event type
                var type: InstallEventType = InstallEventType.UNKNOWN
                if (action == Intent.ACTION_PACKAGE_ADDED) {
                    Log.i(TAG, "$packageName Package was added")
                    // checkIfSpotiyGetInstalled(context, packageName);
                    type = InstallEventType.INSTALLED
                } else if (action == Intent.ACTION_PACKAGE_REPLACED) {
                    Log.i(TAG, "Package was replaced")
                    type = InstallEventType.UPDATED
                    //   setFlagForSpotifyInSharedPref();
                } else if (action == Intent.ACTION_PACKAGE_REMOVED) {
                    Log.i(TAG, "Package was removed")
                    type = InstallEventType.UNINSTALLED
                    //  removeFlagForSpotifyInSharedPref(packageName);
                } else if (action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {
                    Log.i(TAG, "Package was removed and data removed")
                    type = InstallEventType.UNINSTALLED_AND_DATA_REMOVED
                } else if (action == Intent.ACTION_PACKAGE_DATA_CLEARED) {
                    Log.i(TAG, "Package data was cleared")
                    type = InstallEventType.DATA_CLEARED
                } else {
                    Log.i(TAG, "Unkown action was detected: $action")
                }

                //get application label
                var label = LABEL_UNKNOWN
                if (type.equals(InstallEventType.UNINSTALLED) || type.equals(InstallEventType.UNINSTALLED_AND_DATA_REMOVED)) {
                    //TODO someway get uninstalled app packagename?
                } else {
                    try {
                        val packageManager = context.packageManager
                        val info = packageManager.getApplicationInfo(packageName, 0)
                        label = packageManager.getApplicationLabel(info).toString()
                        Log.i(TAG, "Resolved app name: $label")
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        Log.e(TAG, "Couldn't resolve ApplicationInfo from supplied packageName")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e(TAG, "Unexpected Exception")
                    }
                }
                saveEntry(type, packageName, label, timestamp)
            } else {
                Log.e(TAG, "Package name couldn't be resolved - ignoring event")
            }
        }
    }
}

        // TODO move this to music module if needed
        //
        //    private void checkIfSpotiyGetInstalled(Context context, String packageName) {
        //        if(packageName.equals("com.spotify.music")){
        //                if(!spotifyAlreadyInstalledAndRegisteredInSharedPref()){
        //                    WarningHelper.getInstance().promptWarning(context, "Spotify Einstellungen", "Bitte aktivieren Sie den Übertragungstatus", "Spotify",
        //                            "Bitte aktivieren Sie den Übertragungstatus\n" +
        //                                    "1.) Öffne Spotify\n" +
        //                                    "2.) Gehe zu Bibliothek \n" +
        //                                    "3.) Gehe zu Einstellungen\n" +
        //                                    "4.) Aktiviere Übertragungsstatus");
        //                }
        //                setFlagForSpotifyInSharedPref();
        //        }
        //    }
        //
        //    private boolean spotifyAlreadyInstalledAndRegisteredInSharedPref(){
        //        //proof flat if flag is already set
        //        //read shared prefs
        //
        //        SharedPreferences pref = getContext().getSharedPreferences("spotifyFlag", 0); // 0 - for private mode
        //
        //        LogHelper.i(TAG, "flag was returned");
        //        int returnvalue = pref.getInt("installed", -1);
        //
        //        if(returnvalue == 0) {
        //            LogHelper.i(TAG, "Spotify was installed before (Flag = 0)");
        //            return true;
        //        }else if(returnvalue == 1){
        //            LogHelper.i(TAG, "Spotify already installed (Flag = 1)");
        //            return true;
        //        }else {
        //            LogHelper.i(TAG, "Flag not installed before (Flag = -1)");
        //            return false;
        //        }
        //    }
        //
        //    private void setFlagForSpotifyInSharedPref(){
        //        SharedPreferences pref = getContext().getSharedPreferences("spotifyFlag", 0); // 0 - for private mode
        //        SharedPreferences.Editor editor = pref.edit();
        //
        //        LogHelper.i(TAG, "flag was set");
        //        editor.putInt("installed", 1);
        //
        //        editor.apply();
        //
        //    }
        //
        //    private void removeFlagForSpotifyInSharedPref(String packageName){
        //        LogHelper.i(TAG, "removeFlagForSpotifyInSharedPref was called");
        //        if(packageName.equals("com.spotify.music")) {
        //            SharedPreferences pref = getContext().getSharedPreferences("spotifyFlag", 0); // 0 - for private mode
        //            SharedPreferences.Editor editor = pref.edit();
        //
        //            LogHelper.i(TAG, "flag was removed");
        //            editor.putInt("installed", 0);
        //            editor.apply();
        //        }
        //
        //    }