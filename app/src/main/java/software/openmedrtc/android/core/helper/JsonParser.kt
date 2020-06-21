package software.openmedrtc.android.core.helper

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import software.openmedrtc.android.features.shared.connection.DataMessage
import software.openmedrtc.android.features.shared.connection.IceMessage
import software.openmedrtc.android.features.shared.connection.SdpMessage
import timber.log.Timber
import java.lang.ClassCastException

class JsonParser(private val gson: Gson) {

    fun parseDataMessage(jsonString: String): DataMessage? =
        parseJson(jsonString, DataMessage::class.java)

    fun parseSdpMessage(jsonString: String): SdpMessage? =
        parseJson(jsonString, SdpMessage::class.java)

    fun parseIceMessage(jsonString: String): IceMessage? =
        parseJson(jsonString, IceMessage::class.java)

    fun parseSessionDescription(jsonString: String): SessionDescription? =
        parseJson(jsonString, SessionDescription::class.java)

    fun parseIceCandidate(jsonString: String): IceCandidate? =
        parseJson(jsonString, IceCandidate::class.java)

    fun sdpMessageToJson(sdpMessage: SdpMessage): String? =
        toJson(sdpMessage)

    fun dataMessageToJson(dataMessage: DataMessage): String? =
        toJson(dataMessage)

    fun iceMessageToJson(iceMessage: IceMessage): String? =
        toJson(iceMessage)

    fun sessionDescriptionToJson(sessionDescription: SessionDescription): String? =
        toJson(sessionDescription)

    fun iceCandidateToJson(iceCandidate: IceCandidate): String? =
        toJson(iceCandidate)

    private fun <T> parseJson(jsonString: String, classOfT : Class<T>): T? {
        return try {
            gson.fromJson(jsonString, classOfT)
        } catch (jsonSyntaxException : JsonSyntaxException) {
            Timber.e("Failed parsing Json: $jsonSyntaxException")
            null
        }
    }

    private fun toJson(src: Any): String? {
        return try {
            gson.toJson(src, src::class.java)
        } catch (classCastException: ClassCastException) {
            Timber.e("Failed creating json: $classCastException")
            null
        }
    }
}
