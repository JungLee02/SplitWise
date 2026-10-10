package com.techvipin130524.splitmate.feature.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techvipin130524.splitmate.domain.model.Friend
import com.techvipin130524.splitmate.domain.repository.FriendRepository
import com.techvipin130524.splitmate.domain.repository.UserRepository
import com.techvipin130524.splitmate.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendsUiState(
    val currentUserId: String = "",
    val currentUserName: String = "",
    val currentUserEmail: String = "",
    val friends: List<Friend> = emptyList(),
    val isLoading: Boolean = true,
    val isSendingRequest: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            authRepository.authState.collectLatest { authUser ->
                if (authUser != null) {
                    _uiState.value = _uiState.value.copy(
                        currentUserId = authUser.uid,
                        currentUserName = authUser.name ?: "User",
                        currentUserEmail = authUser.email ?: ""
                    )

                    friendRepository.observeFriends(authUser.uid).collectLatest { list ->
                        _uiState.value = _uiState.value.copy(friends = list, isLoading = false)
                    }
                }
            }
        }
    }

    fun addFriendByEmail(targetEmail: String) {
        val state = _uiState.value
        if (targetEmail.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingRequest = true, error = null)
            friendRepository.sendFriendRequest(
                currentUserId = state.currentUserId,
                currentUserName = state.currentUserName,
                currentUserEmail = state.currentUserEmail,
                targetEmail = targetEmail
            ).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSendingRequest = false,
                    message = "Friend added successfully!"
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isSendingRequest = false,
                    error = e.localizedMessage ?: "Failed to add friend"
                )
            }
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.removeFriend(friendId)
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}
