package software.openmedrtc.android.features.connection.rest

import com.google.gson.*
import software.openmedrtc.android.features.connection.entity.MedicalDTO
import software.openmedrtc.android.features.connection.entity.PatientDTO
import software.openmedrtc.android.features.connection.entity.UserDTO
import java.lang.NullPointerException
import java.lang.reflect.Type

// TODO
class UserDeserializer: JsonDeserializer<UserDTO> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UserDTO {
        if(json == null) throw NullPointerException()

        val jsonObject = json.asJsonObject

        val email: String = jsonObject.get("email").asString
        val title: String = jsonObject.get("title").asString
        val firstName: String = jsonObject.get("firstName").asString
        val lastName: String = jsonObject.get("lastName").asString

        return if(jsonObject.has("description")) {
            val description: String = jsonObject.get("description").asString
            val waitingTime: Int = jsonObject.get("waitingTime").asInt
            MedicalDTO(email, title, firstName, lastName, description, waitingTime)
        } else {
            PatientDTO(email, title, firstName, lastName)
        }
    }
}
