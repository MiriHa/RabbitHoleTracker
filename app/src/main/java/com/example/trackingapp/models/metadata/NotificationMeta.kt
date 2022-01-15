package com.example.trackingapp.models.metadata

class NotificationMeta(
    val priority: Int? = 0,
    val category: String? = null
): MetaType("NOTIFICATION_META") {

    override val dataKey: String = "appMeta"

}