package software.openmedrtc.android.features.connection.entity

import java.io.Serializable


abstract class User(
    val id: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val profilePicUrl: String
) : Serializable


class Patient(
    id: String,
    title: String,
    firstName: String,
    lastName: String,
    profilePicUrl: String
) :
    User(id, title, firstName, lastName, profilePicUrl) {

    constructor(patientDTO: PatientDTO) : this(
        patientDTO.id,
        patientDTO.title,
        patientDTO.firstName,
        patientDTO.lastName,
        patientDTO.profilePicUrl
    )
}

class Medical(
    id: String,
    title: String,
    firstName: String,
    lastName: String,
    profilePicUrl: String,
    val description: String,
    val waitingTime: Int = 15
) : User(id, title, firstName, lastName, profilePicUrl) {

    constructor(medicalDTO: MedicalDTO) : this(
        medicalDTO.id,
        medicalDTO.title,
        medicalDTO.firstName,
        medicalDTO.lastName,
        medicalDTO.profilePicUrl,
        description = medicalDTO.description,
        waitingTime = medicalDTO.waitingTime
    )
}
