package com.splitsnap.feature.feature_ledger

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.splitsnap.data.local.Expense
import com.splitsnap.data.local.Group
import com.splitsnap.data.local.Member
import com.splitsnap.navigation.AddMemberDialogRoute
import com.splitsnap.navigation.HeatMapRoute
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LedgerScreen(
    navHostController: NavHostController,
    ledgerViewModel: LedgerViewModel = hiltViewModel(),
    navigateToExpenseDialog: (List<Member>) -> Unit
) {
    val uiState by ledgerViewModel.uiState.collectAsState()
    val expenseKey =
        navHostController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Expense?>(
            "expenseKey",
            null
        )?.collectAsState()
    LaunchedEffect(key1 = expenseKey) {
        expenseKey?.value?.let { expense ->
            Log.d("Expense", expense.toString())
            ledgerViewModel.setExpense(expense)
            navHostController.currentBackStackEntry?.savedStateHandle?.remove<Expense>("expenseKey")
        }
    }
    LedgerScreenInternal(
        navHostController::popBackStack,
        navigateToExpenseDialog,
        navHostController::navigate,
        uiState.expense,
        uiState.member,
        uiState.group,
        uiState.moneyStatus
    )


}

@Composable
fun LedgerScreenInternal(
    popBackStack: () -> Unit,
    navigateToExpenseDialog: (List<Member>) -> Unit,
    navigate: (Any) -> Unit,
    expense: List<Expense>,
    member: Member?,
    group: Group?,
    moneyStatus: Pair<Double, Double>
) {
    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = { LedgerScreenHeader(group) { popBackStack() } }) { padding ->
        LazyColumn(Modifier) {
            item {
                Column(
                    Modifier
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(modifier=Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "To Pay",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "₹${moneyStatus.first}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                        VerticalDivider(Modifier.height(48.dp).padding(vertical=8.dp), thickness = 1.dp)
                        Column(modifier=Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text =  "To Receive",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "₹${moneyStatus.second}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Amount",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = "Members: ${group?.members?.joinToString(", ") { it.displayName ?: "" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = {
                                navigateToExpenseDialog.invoke(group?.members ?: emptyList())
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(text = "Add Expense")
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Add Expense",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                navigate(AddMemberDialogRoute(groupId = group?.id ?: ""))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(text = "Add Member")
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        IconButton(onClick = {navigate(HeatMapRoute(group?.id?:""))}) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            items(expense) { item ->
                val isMe by remember {
                    mutableStateOf(item.paidBy.uid == member?.uid)
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = if (isMe) Arrangement.Start else Arrangement.End
                ) {
                    ExpenseCard(expense = item, isMe)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

        }
    }
}

@Composable
fun ExpenseCard(expense: Expense, isMe: Boolean, modifier: Modifier = Modifier) {
    // Formatting date
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(expense.createdAt.toDate())

    Card(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start // Align based on isMe
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Align amount based on isMe
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Amount",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "₹${expense.amount}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(8.dp))

            // Expense Description
            Text(
                text = expense.description,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Paid By
            Text(
                text = "Paid by: ${expense.paidBy.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Split Among
            Text(
                text = "Split among: ${expense.splitAmong.joinToString(", ") { it.displayName ?: "" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(8.dp))

            // Created At
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isMe) Arrangement.Start else Arrangement.End // Align date based on isMe
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Created at: $formattedDate",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Created At
            Row(
                modifier = Modifier.clickable {
                    openLocationInMaps(context = context, expense.latitude, expense.longitude)
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isMe) Arrangement.Start else Arrangement.End // Align date based on isMe
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Date",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Location: ${expense.latitude}, ${expense.longitude}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer

                )
            }
        }
    }
}


@Composable
fun LedgerScreenHeader(
    group: Group?, backClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Click",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
                    .clip(CircleShape)
                    .clickable {
                        backClick.invoke()
                    })
//
//            ImageCompose(
//                Modifier
//                    .size(24.dp)
//                    .background(
//                        MaterialTheme.colorScheme.surfaceContainer,
//                        shape = CircleShape
//                    )
//
//                    .clip(CircleShape)
//                    .padding(8.dp), data = group?.groupImg?:""
//            )

            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = group?.name ?: "", style = MaterialTheme.typography.titleLarge
            )
        }
    }


}

fun openLocationInMaps(context: Context, latitude: Double, longitude: Double) {
    val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude"
    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
    mapIntent.setPackage("com.google.android.apps.maps")

    // Check if there's an app that can handle this Intent
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        Toast.makeText(context, "Google Maps not installed", Toast.LENGTH_SHORT).show()
    }
}


@Preview(showBackground = true)
@Composable
fun LedgerScreenInternalPreview() {
    val dummyMember = Member(uid = "user1", displayName = "John Doe")
    val dummyGroup = Group(id = "group1", name = "Trip to Hawaii")
    val dummyExpenses = listOf(
        Expense(
            id = "1",
            description = "Groceries",
            amount = 55.25,
            paidBy = dummyMember,
            splitAmong = listOf(dummyMember),
            groupId = dummyGroup.id
        ),
        Expense(
            id = "2",
            description = "Fuel",
            amount = 60.00,
            paidBy = dummyMember,
            splitAmong = listOf(dummyMember),
            groupId = dummyGroup.id
        )
    )

    LedgerScreenInternal(
        popBackStack = {},
        navigateToExpenseDialog = {},
        navigate = {},
        expense = dummyExpenses,
        member = dummyMember,
        group = dummyGroup,
        moneyStatus = Pair(50.25, 235435.00)
    )
}
