package com.techvipin130524.splitmate.feature.auth.presentation

import com.techvipin130524.splitmate.feature.auth.domain.AuthUser

sealed interface SessionState {

    data object Checking : SessionState

    data object LoggedOut : SessionState

    data class LoggedIn(
        val user: AuthUser
    ) : SessionState
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
