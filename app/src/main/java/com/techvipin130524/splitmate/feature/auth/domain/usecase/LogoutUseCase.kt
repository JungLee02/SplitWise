package com.techvipin130524.splitmate.feature.auth.domain.usecase

import com.techvipin130524.splitmate.feature.auth.domain.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    operator fun invoke() {
        repository.logout()
    }
}