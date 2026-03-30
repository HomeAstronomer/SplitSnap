package com.splitsnap.feature.feature_Image_Acceptor

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.Timestamp
import com.splitsnap.R
import com.splitsnap.atoms.ImageCompose
import com.splitsnap.atoms.WaveGradient
import com.splitsnap.data.local.Expense
import com.splitsnap.data.local.Group
import com.splitsnap.data.local.Member
import com.splitsnap.feature.feature_expense_dialog.LocationButton
import com.splitsnap.utils.drawableToBitmap
import com.splitsnap.utils.formatToDate
import com.splitsnap.utils.formatToIsoString
import com.splitsnap.utils.updateDateFromMillis
import com.splitsnap.utils.updateTimeFromMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.System
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.set
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageAcceptorScreen(
    imageUri: String, imageAcceptorViewModel: ImageAcceptorViewModel
) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    val uiState by imageAcceptorViewModel.uiState.collectAsStateWithLifecycle()
    var recognizedText by remember { mutableStateOf("") }


    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    )
    LaunchedEffect(recognizedText.isEmpty()) {
        if (recognizedText.isNotBlank()) {
            bottomSheetState.bottomSheetState.expand()
        }
    }
    var saveExpenseBottomSheet by remember { mutableStateOf(false) }
    val clickedGroups = rememberSaveable { mutableStateOf<Group?>(null) }


    BottomSheetScaffold(
        modifier = Modifier.background(Color.Transparent),
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        sheetContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        scaffoldState = bottomSheetState,
        sheetDragHandle = {},
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (recognizedText.isEmpty()) {


            } else {
                uiState.transactionDetails?.let {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            modifier = Modifier.padding(24.dp),
                            text = "Select a group to add expense to", style = MaterialTheme.typography.bodyMedium
                        )
                        uiState.groupList.forEach {
                            Row() {
                                TextButton({
                                    saveExpenseBottomSheet=true
                                    clickedGroups.value = it
                                    coroutine.launch {
                                        bottomSheetState.bottomSheetState.hide()
                                    }
                                },
                                    modifier = Modifier.fillMaxWidth()) {
                                    ImageCompose(
                                        Modifier
                                            .size(48.dp)
                                            .background(
                                                MaterialTheme.colorScheme.surfaceContainer,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clip(RoundedCornerShape(12.dp)),
                                        data = it.groupImg
                                    )
                                    Text(it.name, modifier = Modifier.weight(1f).padding(start = 8.dp))
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.AutoMirrored.Default.NavigateNext,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                }
                            }
                        }
                    }
                }
            }
        },) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent), contentAlignment = Alignment.Center
        ) {
            Box(Modifier.padding(48.dp)) {

                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context).data(imageUri).build()

                val painter = rememberAsyncImagePainter(
                    model = request
                )
                var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
                LaunchedEffect(request) {
                    val result = (loader.execute(request) as? SuccessResult)?.drawable
                    imageBitmap = drawableToBitmap(result)
                    imageBitmap?.let { bitmap ->
                        recognizedText = imageAcceptorViewModel.detectImageData(bitmap)
                    }
                }

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Transparent)
                )
            }
            if (recognizedText.isEmpty()) {
                WaveGradient()

                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("ai_stars.lottie")
                )
                Column() {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(200.dp)
                    )
                    Text(
                        modifier = Modifier.padding(24.dp),
                        text = "Recognizing Text ... ", style = MaterialTheme.typography.bodyMedium
                    )
                }

            }
            AnimatedVisibility(
                visible = saveExpenseBottomSheet,
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                enter = slideInVertically(
                    initialOffsetY = { it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut()
            ) {
                uiState.transactionDetails?.let {
                    EditTransactionDetails(
                        initialDetails = it,
                        clickedGroup = clickedGroups.value!!,
                        onSave = { expense, group ->
                            imageAcceptorViewModel.setExpense(
                                expense,
                                group,
                                onDone = { saveExpenseBottomSheet = false
                                coroutine.launch {
                                    delay(1.seconds)
                                    (context as Activity).finish()
                                }})

                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDetails(
    initialDetails: TransactionDetails,
    clickedGroup: Group,
    onSave: (Expense, Group) -> Unit,
) {
    var receiverName by remember { mutableStateOf(initialDetails.receiver.name) }
    var receiverUpiId by remember { mutableStateOf(initialDetails.receiver.upiId) }
    var amount by remember { mutableStateOf(initialDetails.amount.toString()) }
    var time by remember { mutableStateOf(initialDetails.time) }
    var transactionId by remember { mutableStateOf(initialDetails.transactionId) }
    var platform by remember { mutableStateOf(initialDetails.platform) }
    val scrollState = rememberScrollState()
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(16.dp)
            .scrollable(scrollState, orientation = Orientation.Vertical),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TransactionTimePicker(initialTime = initialDetails.time, updateDate = { time = it })
        var isLocationEmpty by remember { mutableStateOf(true) }
        AnimatedContent(isLocationEmpty) {
            if (it){
                LocationButton { lat, lon ->
                    isLocationEmpty=false
                    latitude = lat
                    longitude = lon
                    // You can now use latitude and longitude in your expense message
                }
            }else{
                Text("Latitude: $latitude, Longitude: $longitude")
            }
        }


        // Receiver Name
        OutlinedTextField(
            value = receiverName,
            onValueChange = { receiverName = it },
            label = { Text("Receiver Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Receiver UPI ID
        OutlinedTextField(
            value = receiverUpiId,
            onValueChange = { receiverUpiId = it },
            label = { Text("Receiver UPI ID") },
            modifier = Modifier.fillMaxWidth()
        )

        // Amount
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Transaction Time (ISO 8601 Format)


        // Transaction ID
        OutlinedTextField(
            value = transactionId,
            onValueChange = { transactionId = it },
            label = { Text("Transaction ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = platform,
            onValueChange = { platform = it },
            label = { Text("Platform") },
            modifier = Modifier.fillMaxWidth()
        )

        // Platform

        val splitAmongMap = remember {
            mutableStateMapOf<Member, Boolean>().apply {
                clickedGroup.members.forEach { member ->
                    this[member] = false
                }
            }
        }


        LazyColumn(Modifier.padding(bottom = 16.dp)) {
            items(splitAmongMap.keys.toList()) { item ->
                Row(
                    Modifier, verticalAlignment = Alignment.CenterVertically
                ) {
                    val isSelected = remember(splitAmongMap.values) {
                        derivedStateOf {
                            splitAmongMap[item]
                        }
                    }
                    Text(
                        modifier = Modifier.weight(1f),
                        text = item.displayName ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Checkbox(
                        checked = isSelected.value ?: false,
                        onCheckedChange = { splitAmongMap.set(item, it) })
                }

            }
        }

        // Save Button
        Button(
            onClick = {
                val expense = Expense(
                    "", description = transactionId,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    splitAmong = splitAmongMap.toMap()
                        .filter { it.value }.keys.toList(),
                    createdAt =  System.currentTimeMillis(),
                    groupId = clickedGroup.id,
                    latitude = latitude,
                    longitude = longitude
                )
                onSave.invoke(expense, clickedGroup)
            }, modifier = Modifier
        ) {
            Text("Save")
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTimePicker(
    initialTime: String,
    updateDate: (String) -> Unit,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(formatToDate(initialTime)) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { showDatePicker = true }
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Amount",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
        }
        // Date Picker

        Spacer(modifier = Modifier.width(8.dp))

        // Time Picker
        Row(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { showTimePicker = true }
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Timelapse,
                contentDescription = "Amount",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
        }
    }
    val timePickerState = rememberTimePickerState()
    val datePickerState = rememberDatePickerState()


    if (showTimePicker) {
        Dialog({ showTimePicker = false }) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(24.dp)
                    )

            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        modifier = Modifier,
                        state = timePickerState
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                    ) {
                        Button(
                            modifier = Modifier.weight(0.5f),
                            onClick = { showTimePicker = false }) {
                            Text("Dismiss")
                        }
                        Button(modifier = Modifier.weight(0.5f), onClick = {
                            showTimePicker = false
                            date = date.updateTimeFromMillis(timePickerState)
                            updateDate.invoke(formatToIsoString(date))

                        }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        Dialog(
            onDismissRequest = {
                showDatePicker = false
            },

            ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(24.dp)
                    )

            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DatePicker(
                        state = datePickerState, showModeToggle = false
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                    ) {
                        Button(
                            modifier = Modifier.weight(0.5f),
                            onClick = { showDatePicker = false }) {
                            Text("Dismiss")
                        }
                        Button(modifier = Modifier.weight(0.5f), onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let {
                                date = date.updateDateFromMillis(it)
                            }
                        }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}