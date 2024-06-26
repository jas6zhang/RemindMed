package com.gradle.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.jwt.JWT
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.cs346.remindmed.R
import com.gradle.apiCalls.DoctorApi
import com.gradle.apiCalls.PatientApi
import com.gradle.constants.GlobalObjects
import com.gradle.models.Doctor
import com.gradle.models.Patient
import com.gradle.models.User

class LoginViewModel : ViewModel() {

    var appJustLaunched by mutableStateOf(true)
    var userIsAuthenticated by mutableStateOf(false)
    var userIsComplete by mutableStateOf(false)
    var accessToken by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    private val TAG = "MainViewModel"
    private var account: Auth0 =
        Auth0(R.string.com_auth0_client_id.toString(), R.string.com_auth0_domain.toString())
    private lateinit var context: Context

    var user by mutableStateOf(User())

    fun login() {
        WebAuthProvider
            .login(account)
            .withScheme(context.getString(R.string.com_auth0_scheme))
            .start(context, object : Callback<Credentials, AuthenticationException> {

                override fun onFailure(error: AuthenticationException) {
                    Log.e(TAG, "Error occurred in login(): $error")
                }

                override fun onSuccess(result: Credentials) {
                    val accessToken = result.idToken

                    var jwt = JWT(accessToken).subject ?: "1"
                    if (jwt.startsWith("auth0|")) {
                        jwt = jwt.slice(6 until jwt.length)
                    }

                    val patient: Patient = PatientApi().getPatientbyId(jwt)
                    val doctor: Doctor = DoctorApi().getDoctor(jwt)

                    if (patient.pid != "-1") {
                        user = User(accessToken, patient.name, "patient")
                        userIsComplete = true
                        GlobalObjects.patient = patient
                        GlobalObjects.type = "patient"
                    } else if (doctor.did != "-1") {
                        user = User(accessToken, doctor.name, "doctor")
                        GlobalObjects.doctor = doctor
                        GlobalObjects.type = "doctor"
                        userIsComplete = true
                    } else {
                        user = User(accessToken)
                    }

                    userIsAuthenticated = true
                    appJustLaunched = false
                    isLoading = true
                }

            })
    }

    fun logout() {
        WebAuthProvider
            .logout(account)
            .withScheme(context.getString(R.string.com_auth0_scheme))
            .start(context, object : Callback<Void?, AuthenticationException> {

                override fun onFailure(error: AuthenticationException) {
                    Log.e(TAG, "Error occurred in logout(): $error")
                }

                override fun onSuccess(result: Void?) {
                    user = User()
                    userIsAuthenticated = false
                    userIsComplete = false
                    GlobalObjects.type = ""
                    GlobalObjects.patient = Patient()
                    GlobalObjects.doctor = Doctor()
                }
            })
    }

    fun setContext(activityContext: Context) {
        context = activityContext
        account = Auth0(
            context.getString(R.string.com_auth0_client_id),
            context.getString(R.string.com_auth0_domain)
        )
    }

}
