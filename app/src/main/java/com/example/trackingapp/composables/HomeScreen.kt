package com.example.trackingapp.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trackingapp.R
import com.example.trackingapp.ui.theme.BackgroundColor
import com.example.trackingapp.ui.theme.TrackingAppTheme

sealed class HomeEvent {
    object clickInfo: HomeEvent()
}


@Composable
fun HomeScreen(onEvent: (HomeEvent) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(R.string.home_titleText),
                    style = MaterialTheme.typography.h3,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.padding(top = 64.dp, bottom = 12.dp)
                )
            }
        }
    }
}


@Preview(name = "Welcome light theme")
@Composable
fun WelcomeScreenPreview() {
    TrackingAppTheme {
        HomeScreen {}
    }
}