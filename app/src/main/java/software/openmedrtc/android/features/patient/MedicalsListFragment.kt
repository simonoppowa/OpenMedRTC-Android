package software.openmedrtc.android.features.patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_medicals_list.*
import org.koin.android.ext.android.get
import software.openmedrtc.android.R
import software.openmedrtc.android.core.platform.BaseFragment
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
        patientViewModel.loadMedicals() // TODO
    }

    private fun initRecyclerView() {
        recyclerViewMedicals.layoutManager = LinearLayoutManager(context)
        recyclerViewMedicals.adapter = medicalsAdapter
        medicalsAdapter.clickListener = { medical ->
            Timber.d("Medical clicked: ${medical.email}")
            patientViewModel.openWebsocketConnection(medical)
        }
    }

    private fun observeMedicalsList() {
        patientViewModel.medicals.observe(this, Observer { medicals ->
            medicalsAdapter.collection = medicals
        })
    }

    private fun observeFailure() {
        patientViewModel.failure.observe(viewLifecycleOwner, Observer { failure ->
            sendErrorToast(failure)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = MedicalsListFragment()
    }
}
