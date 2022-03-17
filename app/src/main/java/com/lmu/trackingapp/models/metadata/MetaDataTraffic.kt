package com.lmu.trackingapp.models.metadata

class MetaDataTraffic(
    val WIFI_BYTES_TRANSMITTED: Long?,
    val WIFI_BYTES_RECEIVED: Long?,
    val MOBILE_BYTES_TRANSMITTED: Long?,
    val MOBILE_BYTES_RECEIVED: Long?
): MetaType("DATA_TRAFFIC_META")