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

        val id: String = jsonObject.get("id").asString
        val title: String = jsonObject.get("title").asString
        val firstName: String = jsonObject.get("firstName").asString
        val lastName: String = jsonObject.get("lastName").asString
        val profilePicUrl: String = jsonObject.get("profilePicUrl").asString

        return if(jsonObject.has("description")) {
            val description: String = jsonObject.get("description").asString
            val waitingTime: Int = jsonObject.get("waitingTime").asInt
            MedicalDTO(id, title, firstName, lastName, profilePicUrl, description, waitingTime)
        } else {
            PatientDTO(id, title, firstName, lastName, profilePicUrl)
        }
    }
}
