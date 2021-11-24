package com.example.trackingapp.composables

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.trackingapp.R
import com.example.trackingapp.ui.theme.BackgroundColor
import com.example.trackingapp.ui.theme.TrackingAppTheme


sealed class WelcomeEvent {
    object SignInSignUp : WelcomeEvent()
    object LoginIn : WelcomeEvent()
}

@Composable
fun OnBoardingScreen(onEvent: (WelcomeEvent) -> Unit) {
    var brandingBottom by remember { mutableStateOf(0f) }
    var showBranding by remember { mutableStateOf(true) }
    var heightWithBranding by remember { mutableStateOf(0) }

    val currentOffsetHolder = remember { mutableStateOf(0f) }
    currentOffsetHolder.value = if (showBranding) 0f else -brandingBottom
    val currentOffsetHolderDp =
        with(LocalDensity.current) { currentOffsetHolder.value.toDp() }
    val heightDp = with(LocalDensity.current) { heightWithBranding.toDp() }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
        val offset by animateDpAsState(targetValue = currentOffsetHolderDp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .brandingPreferredHeight(showBranding, heightDp)
                .offset(y = offset)
                .onSizeChanged {
                    if (showBranding) {
                        heightWithBranding = it.height
                    }
                }
        ) {
            Branding(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned {
                        if (brandingBottom == 0f) {
                            brandingBottom = it.boundsInParent().bottom
                        }
                    }
            )
            SignInCreateAccount(
                onEvent = onEvent,
                onFocusChange = { focused -> showBranding = !focused },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

private fun Modifier.brandingPreferredHeight(
    showBranding: Boolean,
    heightDp: Dp
): Modifier {
    return if (!showBranding) {
        this
            .wrapContentHeight(unbounded = true)
            .height(heightDp)
    } else {
        this
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp)
        )
        Text(
            text = stringResource(R.string.app_name_display),
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.ic_logo_placholder),
        modifier = modifier,
        contentDescription = null
    )
}

@Composable
private fun SignInCreateAccount(
    onEvent: (WelcomeEvent) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    //val emailState = remember { EmailState() }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Top) {

        Button(
            onClick = {
                Log.d("xxx","Login pressed")
                onEvent(WelcomeEvent.LoginIn) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login_button),
                style = MaterialTheme.typography.subtitle2
            )
        }

        Button(
            onClick = { onEvent(WelcomeEvent.SignInSignUp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login_signup),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

@Preview(name = "OboardingScreen")
@Composable
fun OnBoardingScreenPreview() {
    TrackingAppTheme() {
        OnBoardingScreen() {}
    }
}