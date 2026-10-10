package com.techvipin130524.splitmate.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techvipin130524.splitmate.domain.model.Expense
import com.techvipin130524.splitmate.domain.model.Group
import com.techvipin130524.splitmate.domain.repository.ExpenseRepository
import com.techvipin130524.splitmate.domain.repository.GroupRepository
import com.techvipin130524.splitmate.domain.repository.SettlementRepository
import com.techvipin130524.splitmate.domain.usecase.balance.DebtSimplifier
import com.techvipin130524.splitmate.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentUserId: String = "",
    val currentUserName: String = "",
    val youOwePaise: Long = 0L,
    val youAreOwedPaise: Long = 0L,
    val recentExpenses: List<Expense> = emptyList(),
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository,
    private val settlementRepository: SettlementRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            authRepository.authState.collectLatest { authUser ->
                if (authUser != null) {
                    _uiState.value = _uiState.value.copy(
                        currentUserId = authUser.uid,
                        currentUserName = authUser.name ?: "User"
                    )

                    val uid = authUser.uid

                    launch {
                        expenseRepository.observeAllExpenses().collectLatest { expenses ->
                            _uiState.value = _uiState.value.copy(recentExpenses = expenses.take(5))
                            calculateOverallBalance(uid, expenses)
                        }
                    }

                    launch {
                        groupRepository.observeGroups(uid).collectLatest { groups ->
                            _uiState.value = _uiState.value.copy(groups = groups, isLoading = false)
                        }
                    }
                }
            }
        }
    }

    private fun calculateOverallBalance(uid: String, expenses: List<Expense>) {
        var oweSum = 0L
        var owedSum = 0L

        for (expense in expenses) {
            if (expense.isDeleted) continue

            val isPayer = expense.createdBy == uid || expense.payers.any { it.userId == uid }
            val myParticipant = expense.participants.find { it.userId == uid }
            val myShare = myParticipant?.amount ?: 0L

            if (isPayer) {
                // If I paid, total minus my share is owed to me
                val amountLent = expense.totalAmount - myShare
                if (amountLent > 0) owedSum += amountLent
            } else if (myParticipant != null) {
                // I am a participant, so I owe my share
                oweSum += myShare
            }
        }

        _uiState.value = _uiState.value.copy(
            youOwePaise = oweSum,
            youAreOwedPaise = owedSum
        )
    }
}
