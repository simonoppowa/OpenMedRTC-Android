package software.openmedrtc.android.features.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.get
import software.openmedrtc.android.R
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.connection.rest.AuthenticateUser
import software.openmedrtc.android.features.dashboard.DashboardActivity


class LoginActivity : BaseActivity() {

    private val authenticateUser: AuthenticateUser = get()
    private val authenticator: Authenticator = get()
    private val gson: Gson = get()

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
            authUser(inputEmail.toString(), inputPassword.toString())

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

    private fun authUser(email: String, password: String) {
        authenticateUser(AuthenticateUser.Params(email, password)) {
            it.fold({
                // TODO handle failure
                setErrorText("Wrong email or password")
            }, { user ->
                authenticator.loggedInUser.value = user
                saveCredentials(user.email, password)
                startDashboardActivity()
            })
        }
    }

    private fun startDashboardActivity() {
        startActivity(DashboardActivity.getIntent(this))
        finish()
    }

    private fun saveCredentials(email: String, password: String) {
        // TODO encrypt credentials
        val sharedPreferences =
            getSharedPreferences("Login", Context.MODE_PRIVATE)
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString("email", email)
        sharedPreferencesEditor.putString("password", password)
        sharedPreferencesEditor.apply()
    }

    private fun setErrorText(errorText: String) {
        txt_input_email.error = errorText
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, LoginActivity::class.java)

    }
}
