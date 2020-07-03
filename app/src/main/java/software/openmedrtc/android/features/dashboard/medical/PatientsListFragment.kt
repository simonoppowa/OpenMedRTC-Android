package software.openmedrtc.android.features.dashboard.medical

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_patients_list.*
import org.koin.android.ext.android.get
import software.openmedrtc.android.R
import software.openmedrtc.android.features.video.VideoActivity
import software.openmedrtc.android.core.platform.BaseFragment
import software.openmedrtc.android.features.connection.entity.Patient
import software.openmedrtc.android.features.connection.websocket.PatientAdapter
import timber.log.Timber

class PatientsListFragment : BaseFragment() {

    private var medicalViewModel: MedicalViewModel = get()
    private var patientsAdapter: PatientAdapter = get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patients_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observePatientList()
        observeFailure()
        medicalViewModel.connectToWebsocket()
    }

    private fun initRecyclerView() {
        recyclerViewPatients.layoutManager = LinearLayoutManager(context)
        recyclerViewPatients.adapter = patientsAdapter
        patientsAdapter.clickListener = {patient ->
            Timber.d("Patient clicked: ${patient.email}")
            startVideoActivity(patient)
        }
    }

    private fun startVideoActivity(patient: Patient) {
        startActivity(VideoActivity.getIntent(context!!, patient))
    }

    private fun observePatientList() {
        medicalViewModel.patients.observe(viewLifecycleOwner, Observer { patients ->
            patientsAdapter.collection = patients
        })
    }

    private fun observeFailure() {
        medicalViewModel.failure.observe(viewLifecycleOwner, Observer { failure ->
            sendErrorToast(failure)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PatientsListFragment()
    }
}
