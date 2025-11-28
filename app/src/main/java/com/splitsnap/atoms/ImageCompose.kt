package com.splitsnap.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.splitsnap.R

@Composable
fun ImageCompose(modifier: Modifier=Modifier,
                 data:Any="",
                 loadingImg:Int= R.drawable.ic_hourglass_bottom,
                 errorImg:Int=R.drawable.ic_broken_image,
){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .crossfade(true)
            .crossfade(400)
//            .placeholder(loadingImg)
            .error(errorImg)
            .build(),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier =modifier
    )

}