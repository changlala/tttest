package com.example.tttest.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
internal class LoginResult {
    var success: LoggedInUserView? = null
    var error: Int? = null

    constructor(error: Int?) {
        this.error = error
    }

    constructor(success: LoggedInUserView?) {
        this.success = success
    }
}
