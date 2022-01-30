package com.example.trackingapp.models.metadata

class MetaSMS(
    var phoneNumber: String? = null,
    val countryCode: String? = null,
    val partner: String? = null,
    val length: Int = 0,
    val messageHash: String? = null,
    val contactId: Int = 0
) : MetaType("SMS_META")