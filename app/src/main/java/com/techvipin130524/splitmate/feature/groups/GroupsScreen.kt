package com.techvipin130524.splitmate.feature.groups

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techvipin130524.splitmate.core.designsystem.component.EmptyState
import com.techvipin130524.splitmate.core.designsystem.component.GroupCard
import com.techvipin130524.splitmate.core.designsystem.component.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onCreateGroupClick: () -> Unit,
    onGroupClick: (String) -> Unit,
    viewModel: GroupsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message, uiState.error) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Groups") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateGroupClick) {
                Icon(Icons.Default.Add, contentDescription = "Create Group")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                LoadingState(modifier = Modifier.fillMaxSize())
            } else if (uiState.groups.isEmpty()) {
                EmptyState(
                    title = "No Groups Yet",
                    subtitle = "Create a group for your trip, house, or friends to split expenses easily.",
                    buttonText = "Create Group",
                    onButtonClick = onCreateGroupClick,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
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
