package com.lmu.trackingapp.models.metadata

class MetaNotification(
    val priority: Int? = 0,
    val category: String? = null,
    val infoText: String? = null,
    val subText: String? = null
): MetaType("NOTIFICATION_META")