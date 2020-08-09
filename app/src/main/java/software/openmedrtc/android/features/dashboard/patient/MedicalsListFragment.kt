package software.openmedrtc.android.features.dashboard.patient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_medicals_list.*
import org.koin.android.ext.android.get
import software.openmedrtc.android.R
import software.openmedrtc.android.features.video.VideoActivity
import software.openmedrtc.android.core.platform.BaseFragment
import software.openmedrtc.android.features.connection.entity.Medical
import software.openmedrtc.android.features.connection.rest.MedicalsAdapter
import timber.log.Timber

class MedicalsListFragment : BaseFragment() {

    private var patientViewModel: PatientViewModel = get()
    private var medicalsAdapter: MedicalsAdapter = get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medicals_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observeMedicalsList()
        observeFailure()
        loadMedicalsOnline()
    }

    private fun initRecyclerView() {
        recyclerViewMedicals.layoutManager = LinearLayoutManager(context)
        recyclerViewMedicals.adapter = medicalsAdapter
        medicalsAdapter.clickListener = { medical ->
            Timber.d("Medical clicked: ${medical.id}")
            startVideoActivity(medical)
        }
    }

    private fun observeMedicalsList() {
        patientViewModel.medicals.observe(this, Observer { medicals ->
            medicalsAdapter.collection = medicals
        })
    }

    private fun loadMedicalsOnline() {
        patientViewModel.loadMedicals()
    }

    private fun observeFailure() {
        patientViewModel.failure.observe(viewLifecycleOwner, Observer { failure ->
            sendErrorToast(failure)
        })
    }

    private fun startVideoActivity(medical: Medical) {
        startActivity(VideoActivity.getIntent(context!!, medical))
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MedicalsListFragment()
    }
}
