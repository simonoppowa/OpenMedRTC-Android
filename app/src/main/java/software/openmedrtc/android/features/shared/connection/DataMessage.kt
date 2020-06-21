package software.openmedrtc.android.features.shared.connection

data class DataMessage(val messageType: String, val json: String) {
    companion object {
        const val MESSAGE_TYPE_PATIENTS_LIST = "PATIENTS_LIST"
        const val MESSAGE_TYPE_SDP_OFFER = "SDP_OFFER"
        const val MESSAGE_TYPE_SDP_ANSWER = "SDP_ANSWER"
        const val MESSAGE_TYPE_ICE_CANDIDATE = "ICE_CANDIDATE"
    }
}

data class SdpMessage(
    val fromUser: String,
    val toUser: String,
    val sessionDescriptionString: String
)

data class IceMessage(val fromUser: String, val toUser: String, val iceCandidate: String)