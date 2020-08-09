package software.openmedrtc.android

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import software.openmedrtc.android.features.dashboard.DashboardActivity
import software.openmedrtc.android.features.login.LoginActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    private lateinit var id: String
    private lateinit var password: String

    @get:Rule
    val activityRule = IntentsTestRule(LoginActivity::class.java)

    @Before
    fun initCredentials() {
        id = "john_doe"
        password = "test"
    }

    @Test
    fun login_and_check_activity_started() {
        onView(withId(R.id.txt_input_id))
            .perform(typeText(id), closeSoftKeyboard())
        onView(withId(R.id.txt_input_password))
            .perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        intended(hasComponent(DashboardActivity::class.java.name))
    }
}
