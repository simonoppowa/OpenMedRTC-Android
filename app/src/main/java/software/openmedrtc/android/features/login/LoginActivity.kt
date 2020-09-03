package software.openmedrtc.android.features.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.get
import software.openmedrtc.android.R
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.dashboard.DashboardActivity


class LoginActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        observeInputFields()
        observeFailure()
        observeAuthStatus()
    }

    private fun observeInputFields() {
        // TODO
    }

    private fun observeFailure() {
        loginViewModel.failure.observe(this, Observer { failure ->
            when (failure) {
                is Failure.AuthFailure -> setErrorText(getString(R.string.login_error_credentials))
            }
        })
    }

    private fun observeAuthStatus() {
        loginViewModel.userAuthenticated.observe(this, Observer { authenticated ->
            if (authenticated) startDashboardActivity()
        })
    }

    fun onLoginButtonClicked(view: View) {
        val inputId = txt_input_id.text
        val inputPassword = txt_input_password.text

        if (isValidInput(inputId, inputPassword)) {
            loginViewModel.authUser(inputId.toString(), inputPassword.toString())

        } else {
            setErrorText(getString(R.string.login_error_input))
        }
    }
    private fun isValidInput(id: Editable, password: Editable): Boolean {
        // TODO check input
        return if (id.isBlank()) {
            false
        } else !password.isBlank()
    }

    private fun startDashboardActivity() {
        startActivity(DashboardActivity.getIntent(this))
        finish()
    }

    private fun setErrorText(errorText: String) {
        txt_input_id.error = errorText
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, LoginActivity::class.java)
    }
}
