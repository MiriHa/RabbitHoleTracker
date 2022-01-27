package com.example.trackingapp.models

class ContentChangeEvent(var content: String?, inputMode: EventInputMode?, override val dataKey: String? = content?.hashCode().toString()) : Event(Type.CONTENT_CHANGE) {

    var contentLength: Int? = null
    var inputMode: String? = null


    init {
        if (content != null) {
            contentLength = content?.length
        }
        if (inputMode != null) {
            this.inputMode = inputMode.name
        }
    }
}
