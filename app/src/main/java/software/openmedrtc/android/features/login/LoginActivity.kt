package software.openmedrtc.android.features.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.lifecycle.Observer
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.get
import software.openmedrtc.android.R
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.connection.rest.AuthenticateUser
import software.openmedrtc.android.features.dashboard.DashboardActivity
import kotlin.math.log


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
                is Failure.AuthFailure -> setErrorText("Wrong email or password")
            }
        })
    }

    private fun observeAuthStatus() {
        loginViewModel.userAuthenticated.observe(this, Observer { authenticated ->
            if (authenticated) startDashboardActivity()
        })
    }

    fun onLoginButtonClicked(view: View) {
        val inputEmail = txt_input_email.text
        val inputPassword = txt_input_password.text

        if (isValidInput(inputEmail, inputPassword)) {
            loginViewModel.authUser(inputEmail.toString(), inputPassword.toString())

        } else {
            setErrorText("Wrong input")
        }
    }
    private fun isValidInput(email: Editable, password: Editable): Boolean {
        return if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) {
            false
        } else !password.isBlank()
    }

    private fun startDashboardActivity() {
        startActivity(DashboardActivity.getIntent(this))
        finish()
    }

    private fun setErrorText(errorText: String) {
        txt_input_email.error = errorText
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, LoginActivity::class.java)
    }
}
