package software.openmedrtc.android.features.shared.connection

data class DataMessage(val messageType: String , val json: String) {
    companion object {
        const val MESSAGE_TYPE_PATIENTS_LIST = "PATIENTS_LIST"
    }
}
