package com.techvipin130524.splitmate.feature.auth.domain.usecase

import com.techvipin130524.splitmate.feature.auth.domain.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        email: String
    ) = repository.resetPassword(email)
}