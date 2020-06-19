package software.openmedrtc.android.features.shared

import retrofit2.Call
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import timber.log.Timber

interface UserRepository {
    fun medicals(): Either<Failure, List<Medical>>


    class Network(private val medicalService: UserService) : UserRepository {
        override fun medicals(): Either<Failure, List<Medical>> {
            return request(
                medicalService.medicals(),
                { it.map { medicalDTO -> Medical(medicalDTO) } },
                emptyList()
            )
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
