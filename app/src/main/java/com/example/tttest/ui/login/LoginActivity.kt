package com.example.tttest.ui.login

import android.app.Activity

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.example.tttest.R
import com.example.tttest.ui.login.LoginViewModel
import com.example.tttest.ui.login.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private var loginViewModel: LoginViewModel? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java!!)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login)
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loading)

        loginViewModel!!.loginFormState.observe(this, Observer { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }
            loginButton.isEnabled = loginFormState.isDataValid
            if (loginFormState.usernameError != null) {
                usernameEditText.error = getString(loginFormState.usernameError!!)
            }
            if (loginFormState.passwordError != null) {
                passwordEditText.error = getString(loginFormState.passwordError!!)
            }
        })

        loginViewModel!!.loginResult.observe(this, Observer { loginResult ->
            if (loginResult == null) {
                return@Observer
            }
            loadingProgressBar.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success!!)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel!!.loginDataChanged(usernameEditText.text.toString(),
                        passwordEditText.text.toString())
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel!!.login(usernameEditText.text.toString(),
                        passwordEditText.text.toString())
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel!!.login(usernameEditText.text.toString(),
                    passwordEditText.text.toString())
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int?) {
        Toast.makeText(applicationContext, errorString!!, Toast.LENGTH_SHORT).show()
    }
}
