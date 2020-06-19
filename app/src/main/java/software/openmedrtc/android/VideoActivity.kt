package software.openmedrtc.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.koin.android.ext.android.get
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.medical.MedicalViewModel
import software.openmedrtc.android.features.patient.PatientViewModel
import software.openmedrtc.android.features.shared.Medical
import software.openmedrtc.android.features.shared.Patient
import software.openmedrtc.android.features.shared.User
import timber.log.Timber

class VideoActivity : BaseActivity() {

    private val medicalViewModel: MedicalViewModel = get()
    private val patientViewModel: PatientViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // TODO
        when {
            intent.hasExtra("medical") -> {
                val medical = intent.getSerializableExtra("medical") as Medical
                intiPatientConnection(medical)
            }
            intent.hasExtra("patient") -> {
                val patient = intent.getSerializableExtra("patient") as Patient
                initMedicalConnection(patient)
            }
            else -> {
                Timber.e("No intent passed")
                finish()
            }
        }
    }

    private fun intiPatientConnection(medical: Medical) {
        patientViewModel.openWebsocketConnection(medical)
    }

    private fun initMedicalConnection(patient: Patient) {
        // TODO init peer connection
    }


    companion object {
        private const val PATIENT_KEY = "patient"
        private const val MEDICAL_KEY = "medical"

        fun getIntent(context: Context, user: User) =
            Intent(context, VideoActivity::class.java).apply {
                when (user) {
                    is Patient -> {
                        putExtra(PATIENT_KEY, user)
                    }
                    is Medical -> {
                        putExtra(MEDICAL_KEY, user)
                    }
                }
            }
    }
}
