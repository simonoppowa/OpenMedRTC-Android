package software.openmedrtc.android.features.shared

abstract class UserDTO(
    val email: String,
    val title: String,
    val firstName: String,
    val lastName: String
)

class MedicalDTO(
    email: String,
    title: String,
    firstName: String,
    lastName: String,
    val description: String,
    val waitingTime: Int
) : UserDTO(email, title, firstName, lastName) {
    constructor(medical: Medical) : this(
        medical.email,
        medical.title,
        medical.firstName,
        medical.lastName,
        medical.description,
        medical.waitingTime
    )
}
