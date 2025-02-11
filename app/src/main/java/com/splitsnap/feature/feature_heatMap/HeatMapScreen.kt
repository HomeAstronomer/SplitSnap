package com.splitsnap.feature.feature_heatMap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberTileOverlayState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
    fun HeatMapScreen(viewModel: HeatMapViewModel= hiltViewModel()) {
        // Define some LatLng points for the heat map
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()


        // Set up weighted points if needed

        // Camera position state

    if(uiState.value.expense.isNotEmpty() &&  uiState.value.builder?.center!=null) {

        // Heatmap tile provider
        val heatmapProvider = remember(uiState.value.expense) {
            HeatmapTileProvider.Builder()
                .weightedData(uiState.value.expense) // Use weighted data
                .radius(30) // Adjust radius for heat map intensity
                .gradient(
                    HeatmapTileProvider.DEFAULT_GRADIENT // You can define a custom gradient if needed
                )
                .build()
        }
        val cameraPositionState = rememberCameraPositionState {
            position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                uiState.value.builder?.center!!,5f
            )
        }
        val isMapLoaded = remember { mutableStateOf(false) }
        LaunchedEffect(isMapLoaded.value) {
            if(isMapLoaded.value) {
                delay(500.milliseconds)
                cameraPositionState.animate(
                    com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(
                        uiState.value.builder!!,
                        300
                    )
                )
            }
        }
        // Remember tile overlay state
        val tileOverlayState = rememberTileOverlayState()

        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                isMapLoaded.value=true
            }
        ) {
            TileOverlay(
                tileProvider = heatmapProvider,
                state = tileOverlayState
            )
        }
    }

}