package com.example.trackingapp.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trackingapp.LoginViewModel
import com.example.trackingapp.R
import com.example.trackingapp.ui.theme.BackgroundColor
import com.example.trackingapp.ui.theme.TextFieldColor
import com.example.trackingapp.ui.theme.TextFieldTextColor
import com.example.trackingapp.ui.theme.TrackingAppTheme


//@Preview(showBackground = true)
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    TrackingAppTheme {
        var email by rememberSaveable() { mutableStateOf("") }
        var password by rememberSaveable() { mutableStateOf("") }
        LoginMainCard(
            email,
            password,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onButtonClick = { viewModel.login(email, password)}
        )
    }
}

@Composable
fun LoginMainCard(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onButtonClick: (String) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(color = MaterialTheme.colors.TextFieldColor)

            Spacer(modifier = Modifier.padding(vertical = 40.dp))

            Text(
                text = stringResource(R.string.login_welcome),
                color = Color.White,
                fontSize = 25.sp,
                modifier = Modifier.padding(40.dp),
            )

            Spacer(modifier = Modifier.padding(vertical = 25.dp))

            TextField(
                value = email,
                onValueChange = onEmailChange,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "icon"
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.login_email_placeholder),
                        color = MaterialTheme.colors.TextFieldTextColor
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = modifier,
                shape = shapes.medium,
            )

            Spacer(modifier = Modifier.padding(vertical = 20.dp))

            TextField(
                value = password,
                onValueChange = onPasswordChange,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_password),
                        contentDescription = "icon"
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.login_password_placeholder),
                        color = MaterialTheme.colors.TextFieldTextColor
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = modifier,
                shape = shapes.medium,
            )

            Spacer(modifier = Modifier.padding(vertical = 25.dp))

            Button(
                modifier = modifier,
                shape = shapes.medium,
                contentPadding = PaddingValues(16.dp),
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onButtonClick(email)
                    } else {
                        //TODO make toast?
                    }
                }
            ) {
                Text(text = stringResource(R.string.login_button))
            }

        }
    }


    @Composable
    fun SignUpMainCard(
        email: String,
        password: String,
        onEmailChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onButtonClick: (String) -> Unit
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(color = MaterialTheme.colors.TextFieldColor)

                Spacer(modifier = Modifier.padding(vertical = 40.dp))

                Text(
                    text = stringResource(R.string.login_welcome),
                    color = Color.White,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(40.dp),
                )

                Spacer(modifier = Modifier.padding(vertical = 25.dp))

                TextField(
                    value = email,
                    onValueChange = { onEmailChange },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = "icon"
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.login_email_placeholder),
                            color = MaterialTheme.colors.TextFieldTextColor
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = modifier,
                    shape = shapes.medium,
                )

                Spacer(modifier = Modifier.padding(vertical = 20.dp))

                TextField(
                    value = password,
                    onValueChange = { onPasswordChange },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_password),
                            contentDescription = "icon"
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.login_password_placeholder),
                            color = MaterialTheme.colors.TextFieldTextColor
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = modifier,
                    shape = shapes.medium,
                )

                Spacer(modifier = Modifier.padding(vertical = 25.dp))

                Button(
                    modifier = modifier,
                    shape = shapes.medium,
                    contentPadding = PaddingValues(16.dp),
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            onButtonClick(email)
                        } else {
                            //TODO make toast?
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.login_button))
                }

            }
        }
    }
}
