package com.techvipin130524.splitmate.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.techvipin130524.splitmate.feature.auth.domain.usecase.LoginUseCase
import com.techvipin130524.splitmate.feature.auth.domain.usecase.LogoutUseCase
import com.techvipin130524.splitmate.feature.auth.domain.usecase.ObserveAuthStateUseCase
import com.techvipin130524.splitmate.feature.auth.domain.usecase.RegisterUseCase
import com.techvipin130524.splitmate.feature.auth.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val sessionState =
        observeAuthStateUseCase()
            .map { user ->
                if (user == null) {
                    SessionState.LoggedOut
                } else {
                    SessionState.LoggedIn(user)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SessionState.Checking
            )

    private val _uiState =
        MutableStateFlow(AuthUiState())

    val uiState =
        _uiState.asStateFlow()

    fun login(
        email: String,
        password: String
    ) {
        if (email.isBlank()) {
            setError("Email is required")
            return
        }

        if (password.isBlank()) {
            setError("Password is required")
            return
        }

        viewModelScope.launch {
            _uiState.value =
                AuthUiState(isLoading = true)

            val result =
                loginUseCase(
                    email.trim(),
                    password
                )

            result
                .onSuccess {
                    _uiState.value =
                        AuthUiState()
                }
                .onFailure { exception ->
                    _uiState.value =
                        AuthUiState(
                            errorMessage = exception.toUserMessage()
                        )
                }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        when {
            name.isBlank() -> {
                setError("Name is required")
                return
            }

            email.isBlank() -> {
                setError("Email is required")
                return
            }

            password.length < 8 -> {
                setError(
                    "Password must contain at least 8 characters"
                )
                return
            }

            password != confirmPassword -> {
                setError("Passwords do not match")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value =
                AuthUiState(isLoading = true)

            val result =
                registerUseCase(
                    name.trim(),
                    email.trim(),
                    password
                )

            result
                .onSuccess {
                    _uiState.value =
                        AuthUiState()
                }
                .onFailure { exception ->
                    _uiState.value =
                        AuthUiState(
                            errorMessage =
                                exception.toUserMessage()
                        )
                }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            setError("Enter your email address")
            return
        }

        viewModelScope.launch {
            _uiState.value =
                AuthUiState(isLoading = true)

            resetPasswordUseCase(email.trim())
                .onSuccess {
                    _uiState.value =
                        AuthUiState(
                            successMessage =
                                "Password reset email sent"
                        )
                }
                .onFailure { exception ->
                    _uiState.value =
                        AuthUiState(
                            errorMessage =
                                exception.toUserMessage()
                        )
                }
        }
    }

    fun logout() {
        logoutUseCase()
    }

    fun clearMessage() {
        _uiState.value = AuthUiState()
    }

    private fun setError(message: String) {
        _uiState.value =
            AuthUiState(
                errorMessage = message
            )
    }
}

private fun Throwable.toUserMessage(): String {
    return when (this) {
        is FirebaseAuthWeakPasswordException ->
            "Password is too weak."

        is FirebaseAuthUserCollisionException ->
            "An account already exists with this email."

        is FirebaseAuthInvalidUserException ->
            "No account was found with these credentials."

        is FirebaseAuthInvalidCredentialsException ->
            "Email or password is incorrect."

        is FirebaseNetworkException ->
            "Network error. Please check your connection."

        else ->
            message ?: "Something went wrong. Please try again."
    }
}
