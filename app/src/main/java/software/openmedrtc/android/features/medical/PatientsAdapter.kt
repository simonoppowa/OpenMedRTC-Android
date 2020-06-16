package software.openmedrtc.android.features.medical

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_patient.view.*
import software.openmedrtc.android.R
import software.openmedrtc.android.features.shared.Patient
import kotlin.properties.Delegates

class PatientAdapter(private val context: Context): RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    internal var collection: List<Patient> by Delegates.observable(emptyList()) {
            _, _, _ -> notifyDataSetChanged()
    }

    internal var clickListener: (Patient) -> Unit = { _-> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder =
        PatientViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_patient,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = collection.size

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        holder.bind(collection[position], clickListener)
    }


    class PatientViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(patient: Patient, clickListener: (Patient) -> Unit) {
            view.txtOnlineStatus.text = view.context.getString(R.string.user_online)
            view.txtFullName.text = "${patient.firstName} ${patient.lastName}"

            view.btnCall.setOnClickListener { clickListener(patient) }
        }
    }
}
