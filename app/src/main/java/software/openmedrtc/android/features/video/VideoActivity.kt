package software.openmedrtc.android.features.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.activity_video_call.*
import kotlinx.android.synthetic.main.activity_video_waiting_room.*
import org.koin.android.ext.android.get
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SurfaceTextureHelper
import software.openmedrtc.android.R
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.helper.FrontVideoCapturer
import software.openmedrtc.android.core.helper.ImageUrls.WAITING_ROOM_IMAGE
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.connection.entity.Medical
import software.openmedrtc.android.features.connection.entity.Patient
import software.openmedrtc.android.features.connection.entity.User
import software.openmedrtc.android.features.connection.ConnectionViewModel
import software.openmedrtc.android.features.connection.MedicalConnectionViewModel
import software.openmedrtc.android.features.connection.PatientConnectionViewModel

class VideoActivity : BaseActivity() {

    private val rootEglBase: EglBase = get()
    private val mediaConstraints: MediaConstraints = get()
    private val surfaceTextureHelper: SurfaceTextureHelper = get()
    private val frontVideoCapturer: FrontVideoCapturer = get()

    private lateinit var connectionViewModel: ConnectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        initVideoViews()
        when {
            intent.hasExtra(MEDICAL_KEY) -> {
                // Init Patient Connection
                val medical = intent.getSerializableExtra(MEDICAL_KEY) as Medical
                connectionViewModel = get() as PatientConnectionViewModel
                showWaitingRoom()
                initConnection(medical)
            }
            intent.hasExtra(PATIENT_KEY) -> {
                // Init Medical Connection
                val patient = intent.getSerializableExtra(PATIENT_KEY) as Patient
                connectionViewModel = get() as MedicalConnectionViewModel
                initConnection(patient)
            }
            else -> finishWithFailure(Failure.IntentFailure)
        }
    }

    private fun initVideoViews() {
        surface_view_local.init(rootEglBase.eglBaseContext, null)
        surface_view_remote.init(rootEglBase.eglBaseContext, null)
        surface_view_local.setZOrderMediaOverlay(true)
        surface_view_remote.setZOrderMediaOverlay(true)
    }

    private fun initConnection(user: User) {
        connectionViewModel.initConnection(user)
        observeConnection()
    }

    private fun observeConnection() {
        connectionViewModel.peerConnection.observe(
            this,
            Observer { peerConnection ->
                initVideoCapture(peerConnection)
            }
        )
        connectionViewModel.remoteMediaStream.observe(
            this,
            Observer { mediaStream ->
                mediaStream.videoTracks[0].addSink(surface_view_remote)
            }
        )
        connectionViewModel.iceConnectionState.observe(
            this,
            Observer { connectionState ->
                when (connectionState) {
                    PeerConnection.IceConnectionState.CONNECTED -> view_flipper.displayedChild =
                        view_flipper.indexOfChild(findViewById(R.id.video_call_layout))
                    PeerConnection.IceConnectionState.FAILED -> finishWithFailure(Failure.IceFailure)
                    PeerConnection.IceConnectionState.DISCONNECTED -> finish()
                }
            }
        )
    }

    private fun showWaitingRoom() {
        view_flipper.displayedChild =
            view_flipper.indexOfChild(findViewById(R.id.waiting_room_layout))
        waiting_time_chronometer.start()

        // Load waiting room pic with Glide
        Glide
            .with(this)
            .load(WAITING_ROOM_IMAGE)
            .centerCrop()
            .placeholder(R.drawable.ic_logo)
            .circleCrop()
            .into(img_waiting)
    }

    private fun initVideoCapture(peerConnection: PeerConnection) {
        val videoCapturerAndroid = frontVideoCapturer.createCameraCapturer()

        if(videoCapturerAndroid == null) {
            finishWithFailure(Failure.CameraFailure)
            return
        }

        val videoSource =
            connectionViewModel.peerConnectionFactory
                .createVideoSource(videoCapturerAndroid.isScreencast)
        videoCapturerAndroid.initialize(
            surfaceTextureHelper,
            this,
            videoSource.capturerObserver
        )
        val localVideoTrack =
            connectionViewModel.peerConnectionFactory
                .createVideoTrack("100", videoSource)

        val audioSource = connectionViewModel.peerConnectionFactory
            .createAudioSource(mediaConstraints)
        val localAudioTrack = connectionViewModel.peerConnectionFactory
            .createAudioTrack("101", audioSource)

        val stream = connectionViewModel.peerConnectionFactory
            .createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        peerConnection.addStream(stream)

        videoCapturerAndroid.startCapture(1024, 720, 30) // TODO

        localVideoTrack.addSink(surface_view_local)

        surface_view_local.setMirror(true)
        surface_view_remote.setMirror(true)
    }

    fun onHangupButtonClicked(view: View) {
        finish()
    }

    override fun onDestroy() {
        connectionViewModel.closeConnection()
        rootEglBase.release()
        surface_view_local.release()
        surface_view_remote.release()
        super.onDestroy()
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
