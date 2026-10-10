package com.techvipin130524.splitmate.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techvipin130524.splitmate.core.designsystem.component.BalanceCard
import com.techvipin130524.splitmate.core.designsystem.component.ExpenseRow
import com.techvipin130524.splitmate.core.designsystem.component.GroupCard
import com.techvipin130524.splitmate.core.designsystem.component.LoadingState
import com.techvipin130524.splitmate.core.designsystem.component.PersonAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddExpenseClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onGroupClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PersonAvatar(name = uiState.currentUserName, size = 32)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "SplitMate",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                LoadingState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    item {
                        BalanceCard(
                            youOwePaise = uiState.youOwePaise,
                            youAreOwedPaise = uiState.youAreOwedPaise
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onAddExpenseClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Expense")
                            }

                            OutlinedButton(
                                onClick = onCreateGroupClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Create Group")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (uiState.recentExpenses.isNotEmpty()) {
                            Text(
                                text = "Recent Expenses",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(uiState.recentExpenses, key = { it.id }) { expense ->
                        val myShare = expense.participants.find { it.userId == uiState.currentUserId }?.amount ?: 0L
                        val isPayer = expense.createdBy == uiState.currentUserId || expense.payers.any { it.userId == uiState.currentUserId }

                        ExpenseRow(
                            description = expense.description,
                            createdByName = expense.createdByName,
                            totalAmountPaise = expense.totalAmount,
                            userSharePaise = myShare,
                            isPayer = isPayer,
                            category = expense.category,
                            onClick = {},
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Your Groups",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(uiState.groups, key = { it.id }) { group ->
                        GroupCard(
                            name = group.name,
                            description = group.description,
                            memberCount = group.members.size,
                            onClick = { onGroupClick(group.id) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
