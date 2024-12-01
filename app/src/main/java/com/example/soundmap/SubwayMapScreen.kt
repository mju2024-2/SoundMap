package com.example.soundmap.com.example.soundmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import com.example.soundmap.R

import androidx.compose.foundation.background

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter


@Composable
fun SubwayMapScreen(onBackPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Zoomable 이미지 구현
        ZoomableImage(
            painter = painterResource(id = R.drawable.subway_map),
            contentDescription = "노선도 이미지"
        )
    }
}

@Composable
fun ZoomableImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    // 상태를 기억 (확대/축소 및 이동)
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // 확대/축소 처리
                    scale = (scale * zoom).coerceIn(1f, 5f) // 최소 1배, 최대 5배 확대

                    // 이동 처리 (확대 상태에서만 이동 가능)
                    if (scale > 1f) {
                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(-500f, 500f), // 이동 제한 범위
                            y = (offset.y + pan.y).coerceIn(-500f, 500f)
                        )
                    } else {
                        // 기본 상태에서는 이동 불가
                        offset = Offset.Zero
                    }
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .fillMaxSize()
    ) {
        // 노선도 이미지
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}
