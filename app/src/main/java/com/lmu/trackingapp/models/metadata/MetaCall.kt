package com.lmu.trackingapp.models.metadata

class MetaCall(
    val META_TYPE_NAME: String = "APP_META",
    private val phoneNumber: String? = null,
    private val countryCode: String? = null,
    private val partner: String? = null,
    private val duration: Int = 0,
    private val contactId: Int = 0
): MetaType("CALL_META")