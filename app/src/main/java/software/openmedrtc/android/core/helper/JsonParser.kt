package software.openmedrtc.android.core.helper

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import software.openmedrtc.android.core.di.USERNAME
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.features.shared.connection.DataMessage
import software.openmedrtc.android.features.shared.connection.IceMessage
import software.openmedrtc.android.features.shared.connection.SdpMessage
import timber.log.Timber
import java.lang.ClassCastException

class JsonParser(private val gson: Gson) {

    fun createSdpDataMessageJson(
        fromUser: String,
        toUser: String,
        sessionDescription: SessionDescription,
        messageType: String
    ): Either<Failure.ParsingFailure, String> {
        return try {
            val sdpMessage = SdpMessage(
                fromUser,
                toUser,
                sessionDescriptionToJson(sessionDescription)
            )
            val dataMessage =
                DataMessage(
                    messageType,
                    sdpMessageToJson(sdpMessage)
                )
            return Either.Right(dataMessageToJson(dataMessage))
        } catch (t: Throwable) {
            Either.Left(Failure.ParsingFailure)
        }
    }

    fun createIceDataMessageJson(
        fromUser: String,
        toUser: String,
        iceCandidate: IceCandidate,
        messageType: String
    ): Either<Failure.ParsingFailure, String> {
        return try {
            val iceJson = iceCandidateToJson(iceCandidate)
            val iceMessage = IceMessage(fromUser, toUser, iceJson)
            val iceMessageJson = iceMessageToJson(iceMessage)
            val dataMessage = DataMessage(messageType, iceMessageJson)

            Either.Right(dataMessageToJson(dataMessage))
        } catch (t: Throwable) {
            Either.Left(Failure.ParsingFailure)
        }
    }

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

    fun sdpMessageToJson(sdpMessage: SdpMessage): String =
        toJson(sdpMessage)

    fun dataMessageToJson(dataMessage: DataMessage): String =
        toJson(dataMessage)

    fun iceMessageToJson(iceMessage: IceMessage): String =
        toJson(iceMessage)

    fun sessionDescriptionToJson(sessionDescription: SessionDescription): String =
        toJson(sessionDescription)

    fun iceCandidateToJson(iceCandidate: IceCandidate): String =
        toJson(iceCandidate)

    private fun <T> parseJson(jsonString: String, classOfT: Class<T>): T =
        gson.fromJson(jsonString, classOfT) ?: throw JsonSyntaxException("Wrong syntax")

    private fun toJson(src: Any): String =
        gson.toJson(src, src::class.java) ?: throw ClassCastException("Wrong syntax")
}
