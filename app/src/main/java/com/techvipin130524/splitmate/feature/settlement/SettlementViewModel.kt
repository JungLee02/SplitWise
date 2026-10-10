package com.techvipin130524.splitmate.feature.settlement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techvipin130524.splitmate.core.utils.CurrencyUtils
import com.techvipin130524.splitmate.domain.model.Group
import com.techvipin130524.splitmate.domain.model.Settlement
import com.techvipin130524.splitmate.domain.repository.GroupRepository
import com.techvipin130524.splitmate.domain.repository.SettlementRepository
import com.techvipin130524.splitmate.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettlementUiState(
    val currentUserId: String = "",
    val currentUserName: String = "",
    val group: Group? = null,
    val fromUserId: String = "",
    val toUserId: String = "",
    val amountString: String = "",
    val note: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class SettlementViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    private val settlementRepository: SettlementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettlementUiState())
    val uiState: StateFlow<SettlementUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.authState.collectLatest { authUser ->
                if (authUser != null) {
                    _uiState.value = _uiState.value.copy(
                        currentUserId = authUser.uid,
                        currentUserName = authUser.name ?: "User",
                        fromUserId = authUser.uid
                    )
                }
            }
        }
    }

    fun loadGroup(groupId: String) {
        viewModelScope.launch {
            groupRepository.getGroupDetails(groupId).onSuccess { group ->
                val otherMember = group.members.find { it.userId != _uiState.value.currentUserId }
                _uiState.value = _uiState.value.copy(
                    group = group,
                    toUserId = otherMember?.userId ?: ""
                )
            }
        }
    }

    fun updateFromUser(userId: String) {
        _uiState.value = _uiState.value.copy(fromUserId = userId)
    }

    fun updateToUser(userId: String) {
        _uiState.value = _uiState.value.copy(toUserId = userId)
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amountString = amount)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun recordSettlement(onSettled: () -> Unit) {
        val state = _uiState.value
        val group = state.group ?: return

        val amountPaise = CurrencyUtils.parseToPaise(state.amountString)
        if (amountPaise == null || amountPaise <= 0) {
            _uiState.value = state.copy(error = "Please enter a valid amount")
            return
        }

        if (state.fromUserId == state.toUserId) {
            _uiState.value = state.copy(error = "Payer and recipient cannot be the same person")
            return
        }

        val fromUserName = group.members.find { it.userId == state.fromUserId }?.userName ?: ""
        val toUserName = group.members.find { it.userId == state.toUserId }?.userName ?: ""

        val settlement = Settlement(
            groupId = group.id,
            fromUserId = state.fromUserId,
            fromUserName = fromUserName,
            toUserId = state.toUserId,
            toUserName = toUserName,
            amount = amountPaise,
            note = state.note.trim(),
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            settlementRepository.recordSettlement(settlement).onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, message = "Settlement recorded!")
                onSettled()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.localizedMessage ?: "Failed to record settlement")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}
