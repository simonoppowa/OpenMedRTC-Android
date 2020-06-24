package software.openmedrtc.android.features.connection.peerconnection

import org.webrtc.*
import timber.log.Timber

abstract class PeerConnectionObserver: PeerConnection.Observer {
    override fun onIceCandidate(p0: IceCandidate?) {
        Timber.d("New ice candidate created")
    }

    override fun onDataChannel(p0: DataChannel?) {
        Timber.d("Data channel change: ${p0.toString()}")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {}

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Timber.d("Ice connection change ${p0?.name}")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}

    override fun onAddStream(p0: MediaStream?) {
        Timber.d("Added stream")
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Timber.d("Signaling change: ${p0?.name}")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Timber.d("Ice candidate removed")
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Timber.d("Stream removed")
    }

    override fun onRenegotiationNeeded() {}

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
}
