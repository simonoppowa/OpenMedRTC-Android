package software.openmedrtc.android.features.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import software.openmedrtc.android.R
import software.openmedrtc.android.core.platform.BaseActivity

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        observeInputFields()
    }

    private fun observeInputFields() {
        // TODO
    }

    fun onLoginButtonClicked(view: View) {
        val inputEmail = txt_input_email.text
        val inputPassword = txt_input_password.text

        if (isValidInput(inputEmail, inputPassword)) {
            // TODO api call
        } else {
            txt_input_email.error = "Error"
        }
    }

    private fun isValidInput(email: Editable, password: Editable): Boolean {
        return if (!email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) {
            false
        } else password.isBlank()
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, LoginActivity::class.java)

    }
}
