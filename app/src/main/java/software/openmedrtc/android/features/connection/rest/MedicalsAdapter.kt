package software.openmedrtc.android.features.connection.rest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_medical.view.*
import software.openmedrtc.android.R
import software.openmedrtc.android.features.connection.entity.Medical
import kotlin.properties.Delegates


class MedicalsAdapter(private val context: Context) :
    RecyclerView.Adapter<MedicalsAdapter.MedicalsViewHolder>() {

    internal var collection: List<Medical> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (Medical) -> Unit = { _ -> }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalsViewHolder =
        MedicalsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_medical,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = collection.size

    override fun onBindViewHolder(holder: MedicalsViewHolder, position: Int) {
        holder.bind(collection[position], clickListener)
    }


    class MedicalsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(medical: Medical, clickListener: (Medical) -> Unit) {
            view.txtFullName.text = "${medical.title}. ${medical.firstName} ${medical.lastName}"
            view.txtJobDescription.text = "General Practitioner"
            view.txtWaitingTime.text = "${medical.waitingTime} min"

            view.btnCall.setOnClickListener { clickListener(medical) }
        }
    }
}
