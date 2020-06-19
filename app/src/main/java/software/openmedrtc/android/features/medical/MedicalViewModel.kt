package software.openmedrtc.android.features.medical

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Response
import software.openmedrtc.android.core.interactor.UseCase
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Patient
import software.openmedrtc.android.features.shared.PatientDTO
import software.openmedrtc.android.features.shared.connection.DataMessage
import software.openmedrtc.android.features.shared.connection.GetWebsocketConnection
import software.openmedrtc.android.features.shared.connection.Websocket
import timber.log.Timber

class MedicalViewModel(
    private val getWebsocketConnection: GetWebsocketConnection,
    private val gson: Gson
) : BaseViewModel() {

    var patients: MutableLiveData<List<Patient>> = MutableLiveData()

    private val MESSAGE_TYPE_PATIENTS_LIST = "PATIENTS_LIST" // TODO

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
                            patients.postValue(patientList.map { Patient(it) })
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
