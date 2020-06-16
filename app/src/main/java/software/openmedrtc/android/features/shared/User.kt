package software.openmedrtc.android.features.shared


abstract class User(
    val email: String,
    val title: String,
    val firstName: String,
    val lastName: String
)


class Patient(
    email: String,
    title: String,
    firstName: String,
    lastName: String
) :
    User(email, title, firstName, lastName)

class Medical(
    email: String,
    title: String,
    firstName: String,
    lastName: String,
    val description: String,
    val waitingTime: Int = 15
) : User(email, title, firstName, lastName) {

    constructor(medicalDTO: MedicalDTO) : this(
        medicalDTO.email,
        medicalDTO.title,
        medicalDTO.firstName,
        medicalDTO.lastName,
        description = medicalDTO.description,
        waitingTime = medicalDTO.waitingTime
    )
}
