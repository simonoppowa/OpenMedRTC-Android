package software.openmedrtc.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_video.*
import org.koin.android.ext.android.get
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SurfaceTextureHelper
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.shared.Medical
import software.openmedrtc.android.features.shared.Patient
import software.openmedrtc.android.features.shared.User
import software.openmedrtc.android.features.shared.connection.MedicalConnectionViewModel
import software.openmedrtc.android.features.shared.connection.PatientConnectionViewModel
import timber.log.Timber

class VideoActivity : BaseActivity() {

    private val rootEglBase: EglBase = get()
    private val mediaConstraints: MediaConstraints = get()
    private val surfaceTextureHelper: SurfaceTextureHelper = get()

    private val medicalConnectionViewModel: MedicalConnectionViewModel = get()
    private val patientConnectionViewModel: PatientConnectionViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        initVideoViews()
        // TODO
        when {
            intent.hasExtra(MEDICAL_KEY) -> {
                val medical = intent.getSerializableExtra(MEDICAL_KEY) as Medical
                intiPatientConnection(medical)
            }
            intent.hasExtra(PATIENT_KEY) -> {
                val patient = intent.getSerializableExtra(PATIENT_KEY) as Patient
                initMedicalConnection(patient)
            }
            else -> {
                Timber.e("No intent passed")
                finish()
            }
        }
    }

    private fun initVideoViews() {
        surface_view_local.init(rootEglBase.eglBaseContext, null)
        surface_view_remote.init(rootEglBase.eglBaseContext, null)
        surface_view_local.setZOrderMediaOverlay(true)
        surface_view_remote.setZOrderMediaOverlay(true)
    }

    private fun intiPatientConnection(medical: Medical) {
        patientConnectionViewModel.initMedicalConnection(medical)
        observePatientConnection()
    }

    private fun initMedicalConnection(patient: Patient) {
        medicalConnectionViewModel.initPatientConnection(patient)
        observeMedicalConnection()
    }

    // TODO duplicate code
    private fun observePatientConnection() {
        patientConnectionViewModel.peerConnection.observe(
            this,
            Observer { peerConnection ->
                initVideoCapture(peerConnection)
            }
        )
    }

    private fun observeMedicalConnection() {
        medicalConnectionViewModel.peerConnection.observe(
            this,
            Observer { peerConnection ->
                initVideoCapture(peerConnection)
            }
        )
    }

    private fun initVideoCapture(peerConnection: PeerConnection) {
        // TODO
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
