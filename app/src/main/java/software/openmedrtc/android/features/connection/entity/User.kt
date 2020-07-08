package software.openmedrtc.android.features.connection.entity

import java.io.Serializable


abstract class User(
    val email: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val profilePicUrl: String
) : Serializable


class Patient(
    email: String,
    title: String,
    firstName: String,
    lastName: String,
    profilePicUrl: String
) :
    User(email, title, firstName, lastName, profilePicUrl) {

    constructor(patientDTO: PatientDTO) : this(
        patientDTO.email,
        patientDTO.title,
        patientDTO.firstName,
        patientDTO.lastName,
        patientDTO.profilePicUrl
    )
}

class Medical(
    email: String,
    title: String,
    firstName: String,
    lastName: String,
    profilePicUrl: String,
    val description: String,
    val waitingTime: Int = 15
) : User(email, title, firstName, lastName, profilePicUrl) {

    constructor(medicalDTO: MedicalDTO) : this(
        medicalDTO.email,
        medicalDTO.title,
        medicalDTO.firstName,
        medicalDTO.lastName,
        medicalDTO.profilePicUrl,
        description = medicalDTO.description,
        waitingTime = medicalDTO.waitingTime
    )
}
