package software.openmedrtc.android.features.connection.rest

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import retrofit2.Call
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.features.connection.entity.*
import timber.log.Timber

interface UserRepository {
    fun medicals(): Either<Failure, List<Medical>>
    fun authenticate() : Either<Failure, User>

    class Network(private val userService: UserService) :
        UserRepository {
        override fun medicals(): Either<Failure, List<Medical>> {
            return request(
                userService.medicals(),
                { it.map { medicalDTO ->
                    Medical(
                        medicalDTO
                    )
                } },
                emptyList()
            )
        }

        // TODO
        override fun authenticate(): Either<Failure, User> {
            try {
                val response = userService.authenticate().execute()
                Timber.d("Server responded: ${response.body()}")
                return when (response.isSuccessful) {
                    true -> {
                        when (val body = response.body()) {
                            is MedicalDTO -> {
                                Either.Right(Medical(body))
                            }
                            is PatientDTO -> {
                                Either.Right(Patient(body))
                            }
                            else -> {
                                Either.Left(Failure.ParsingFailure)
                            }
                        }
                    }
                    else -> Either.Left(Failure.ServerFailure)
                }
            } catch (exception: Throwable) {
                return Either.Left(Failure.ServerFailure)
            }
        }

        private fun <T, R> request(
            call: Call<T>,
            transform: (T) -> R,
            default: T
        ): Either<Failure, R> {
            return try {
                val response = call.execute()
                Timber.d("Server responded: ${response.body()}")
                when (response.isSuccessful) {
                    true -> Either.Right(transform((response.body() ?: default)))
                    else -> Either.Left(Failure.ServerFailure)
                }
            } catch (exception: Throwable) {
                Timber.e(exception)
                Either.Left(Failure.ServerFailure)
            }
        }

    }
}
