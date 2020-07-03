package software.openmedrtc.android.features.dashboard.medical

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Response
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.connection.entity.Patient
import software.openmedrtc.android.features.connection.entity.PatientDTO
import software.openmedrtc.android.features.connection.websocket.DataMessage
import software.openmedrtc.android.features.connection.websocket.DataMessage.Companion.MESSAGE_TYPE_PATIENTS_LIST
import software.openmedrtc.android.features.connection.websocket.GetWebsocketConnection
import software.openmedrtc.android.features.connection.websocket.Websocket
import timber.log.Timber

class MedicalViewModel(
    private val getWebsocketConnection: GetWebsocketConnection,
    private val gson: Gson
) : BaseViewModel() {

    var patients: MutableLiveData<List<Patient>> = MutableLiveData()

    fun connectToWebsocket() {
        getWebsocketConnection(GetWebsocketConnection.Params()) {
            it.fold(::handleFailure, ::handleWebsocketConnection)
        }
    }

    private fun handleWebsocketConnection(websocket: Websocket) {
        websocket.addListener(object : Websocket.SocketListener {
            override fun onOpen(websocket: Websocket, response: Response) {}

            override fun onFailure(websocket: Websocket, t: Throwable, response: Response?) {}

            override fun onMessage(websocket: Websocket, text: String) {
                try {
                    val dataMessage = gson.fromJson(text, DataMessage::class.java)

                    when(dataMessage.messageType) {
                        MESSAGE_TYPE_PATIENTS_LIST -> {
                            val patientList = gson.fromJson(dataMessage.json, Array<PatientDTO>::class.java)
                            patients.postValue(patientList.map {
                                Patient(
                                    it
                                )
                            })
                        }
                    }
                } catch (syntaxException: JsonSyntaxException) {
                  Timber.e("Got wrong message format")
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        })
    }

}
