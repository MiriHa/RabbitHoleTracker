package com.example.trackingapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber


object PhoneNumberHelper {
    private const val TAG = "PhoneNumberHelper"
    private const val DEFAULT_CC = "DE"

    //E: CHECKED! WORKS ->
    fun formatNumber(number: String?): String {
        val phoneNumber: PhoneNumber
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            phoneNumber = phoneUtil.parse(PhoneNumberUtil.convertAlphaCharactersInNumber(number), DEFAULT_CC)
            phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (e: NumberParseException) {
            e.printStackTrace()
            number ?: ""
        }
    }

    fun getOwnPhonenumber(context: Context): String? {
        var number = ""
        val phoneMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "could not get own phone number - permission not granted")
            return null
        }
        number = phoneMgr.line1Number
        return number
    }

    fun getOwnCountryCode(context: Context?): String? {
        val ownNumber = getOwnPhonenumber(context!!) ?: return DEFAULT_CC
        return extractCountryCodeFromNumber(ownNumber)
    }

    fun extractCountryCodeFromNumber(ownNumber: String?): String {
        val number: PhoneNumber
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            number = phoneUtil.parse(PhoneNumberUtil.convertAlphaCharactersInNumber(ownNumber), DEFAULT_CC)
            //return String.valueOf(phoneUtil.getCountryCodeForRegion((phoneUtil.getRegionCodeForNumber(number))));
            phoneUtil.getRegionCodeForNumber(number)
        } catch (e: NumberParseException) {
            e.printStackTrace()
            DEFAULT_CC
        }
    }

    fun extractCountryCodesFromNumbers(phoneNumbers: Array<String?>): Array<String?>? {
        val countryCodes = arrayOfNulls<String>(phoneNumbers.size)
        for (i in phoneNumbers.indices) {
            countryCodes[i] = extractCountryCodeFromNumber(phoneNumbers[i])
        }
        return countryCodes
    }
}