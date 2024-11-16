package com.example.aisplitwise.feature.feature_expense_dialog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.aisplitwise.data.local.Expense
import com.example.aisplitwise.data.local.Member
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
fun ExpenseDialog(
    navHostController: NavHostController,
    expenseViewModel: ExpenseDialogViewModel,
    members: List<Member>
) {
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.75f))
    ) {

        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.8f)
                .padding(vertical = 48.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            var amount by remember { mutableStateOf(0) }
            var message by remember { mutableStateOf("") }
            val splitAmongMap = remember {
                mutableStateMapOf<Member, Boolean>().apply {
                    members.forEach { member ->
                        this[member] = false
                    }
                }
            }

            Column(
                Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Create a Expense",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = amount.toString(),
                    onValueChange = { value -> amount = value.toIntOrNull() ?: 0 },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Message TextField
                OutlinedTextField(value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                Row( Modifier.padding(bottom = 16.dp)){
                    Column(Modifier.padding(end = 16.dp).weight(1f)) {
                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = "Lat :- $latitude",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            modifier = Modifier.padding(bottom = 16.dp),
                            text = "Long :- $longitude",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    LocationButton{ lat, lon ->
                        latitude = lat
                        longitude = lon
                        // You can now use latitude and longitude in your expense message
                    }

                }
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Split Among",
                    style = MaterialTheme.typography.bodyMedium
                )
                HorizontalDivider(Modifier.fillMaxWidth())

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

                            Checkbox(checked = isSelected.value ?: false,
                                onCheckedChange = { splitAmongMap.set(item, it) })
                        }

                    }
                }
                Row(Modifier) {
                    val isOkButtonEnabled by remember(splitAmongMap) {
                        derivedStateOf { splitAmongMap.values.contains(true) }
                    }
                    OutlinedButton(
                        onClick = {
                            navHostController.popBackStack()
                        }, modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Text(text = "Cancel")
                    }
                    FilledTonalButton(
                        onClick = {
                            val expense = Expense(
                                id = "",
                                description = message,
                                amount = amount.toDouble(),
                                paidBy = Member(),
                                splitAmong = splitAmongMap.toMap()
                                    .filter { it.value }.keys.toList(),
                                latitude = latitude,
                                longitude = longitude
                            )
                            navHostController.previousBackStackEntry?.savedStateHandle?.set(
                                "expenseKey", expense
                            )
                            navHostController.popBackStack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        enabled = isOkButtonEnabled
                    ) {
                        Text(text = "OK")
                    }

                }

            }

        }
    }

}

@Composable
fun LocationButton(onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted

    }
    var showLoadingRing by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = permissionGranted) {
        if(permissionGranted){
            showLoadingRing=true
            getCurrentLocation(context) { lat, Long ->
                showLoadingRing = false
                onLocationReceived.invoke(lat, Long)

            }
        }
    }
    Box(contentAlignment = Alignment.Center) {



        IconButton(
            modifier = Modifier
                .padding(4.dp)
                .background(
                    if (permissionGranted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    shape = CircleShape
                )
                .clip(CircleShape), onClick = {
                    if(!permissionGranted){
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)}
            }) {
            if (permissionGranted) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.LocationOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

        }
        if(showLoadingRing) {
            CircularProgressIndicator(Modifier.size(56.dp))
        }
    }


}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context, onLocationReceived: (latitude: Double, longitude: Double) -> Unit
) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Create a location request
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, // High accuracy for getting precise current location
        1000 // Request interval in milliseconds (1 second in this case)
    ).setMaxUpdates(1) // Only request one update
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                onLocationReceived(it.latitude, it.longitude)
            }
            // Remove location updates after getting the current location
            fusedLocationClient.removeLocationUpdates(this)
        }
    }

    // Start location updates
    fusedLocationClient.requestLocationUpdates(
        locationRequest, locationCallback, Looper.getMainLooper()
    )
}

@Preview
@Composable
fun LocationButtonPreview() {
    LocationButton{_,_->}

}