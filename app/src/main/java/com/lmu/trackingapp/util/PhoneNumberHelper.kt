package com.lmu.trackingapp.util

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber

object PhoneNumberHelper {
    private const val TAG = "PhoneNumberHelper"
    private const val DEFAULT_CC = "DE"

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

    fun extractCountryCodeFromNumber(ownNumber: String?): String {
        val number: PhoneNumber
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            number = phoneUtil.parse(PhoneNumberUtil.convertAlphaCharactersInNumber(ownNumber), DEFAULT_CC)
            phoneUtil.getRegionCodeForNumber(number)
        } catch (e: NumberParseException) {
            e.printStackTrace()
            DEFAULT_CC
        }
    }
}
