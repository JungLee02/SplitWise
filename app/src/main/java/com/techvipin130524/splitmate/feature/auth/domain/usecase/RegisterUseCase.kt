package com.techvipin130524.splitmate.feature.auth.domain.usecase

import com.techvipin130524.splitmate.feature.auth.domain.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ) = repository.register(
        name,
        email,
        password
    )
}