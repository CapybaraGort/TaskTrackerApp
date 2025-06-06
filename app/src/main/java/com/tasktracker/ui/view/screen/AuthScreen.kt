package com.tasktracker.ui.view.screen

import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tasktracker.R
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.data.remote.auth.AuthResult
import com.tasktracker.domain.entity.User
import com.tasktracker.ui.Screen
import com.tasktracker.ui.component.button.DebouncedButton
import com.tasktracker.viewModel.NavigationEvent
import com.tasktracker.viewModel.NavigationViewModel
import com.tasktracker.viewModel.TaskViewModel
import com.tasktracker.viewModel.remote.UserRemoteViewModel
import com.tasktracker.viewModel.remote.auth.AnonymousAuthState
import com.tasktracker.viewModel.remote.auth.AnonymousViewModel
import com.tasktracker.viewModel.remote.auth.YandexAuthState
import com.tasktracker.viewModel.remote.auth.YandexAuthViewModel
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AuthScreen(navHostController: NavHostController) {
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val navEvent by navigationViewModel.navigation.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_with),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OAuthView({
                    navigationViewModel.popUpTo(Screen.Home.route)
                })
                Spacer(modifier = Modifier.height(8.dp))
                AnonymousView({
                    navigationViewModel.popUpTo(Screen.Home.route)
                })
            }
        }
    }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Уведомления отключены", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(navEvent) {
        when (navEvent) {
            NavigationEvent.Back -> Unit
            NavigationEvent.Idle -> Unit
            is NavigationEvent.Navigate -> {}
            is NavigationEvent.PopUpTo -> {
                navHostController.navigate((navEvent as NavigationEvent.PopUpTo).route) {
                    popUpTo(Screen.AuthScreen.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}

@Composable
private fun AnonymousView(navToHomeScreen: () -> Unit) {
    val scope = rememberCoroutineScope()
    val anonymousViewModel: AnonymousViewModel = hiltViewModel()
    val authState by anonymousViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            anonymousViewModel.validateToken()
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            AnonymousAuthState.Idle -> {}
            AnonymousAuthState.InvalidToken -> {}
            AnonymousAuthState.Loading -> {}
            AnonymousAuthState.Success -> {
                navToHomeScreen()
            }
        }
    }

    Text(
        modifier = Modifier
            .clickable {
                scope.launch {
                    anonymousViewModel.auth()
                }
                navToHomeScreen()
            },
        text = stringResource(R.string.continue_without_login),
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun OAuthView(navToHomeScreen: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val yandexAuthViewModel: YandexAuthViewModel = hiltViewModel()
    val userRemoteViewModel: UserRemoteViewModel = hiltViewModel()
    val authState by yandexAuthViewModel.authState.collectAsStateWithLifecycle()

    val sdk = remember { YandexAuthSdk.create(YandexAuthOptions(context.applicationContext)) }

    val launcher = rememberLauncherForActivityResult(sdk.contract) { result ->
        scope.launch {
            val authResult = yandexAuthViewModel.handleResult(result)
            when (authResult) {
                is AuthResult.Success -> {
                    withContext(Dispatchers.IO) {
                        try {
                            val userJson =
                                yandexAuthViewModel.getUserInfo(authResult.token).getOrThrow()
                            val user = User(
                                name = userJson.getString("display_name"),
                                email = userJson.getString("default_email")
                            )
                            val userFromDb = userRemoteViewModel.getUserByEmail(user.email)
                            if(userFromDb == null) {
                                val usr = userRemoteViewModel.addUser(user)
                                AuthManager.setCurrentUser(usr)
                            } else {
                                AuthManager.setCurrentUser(userFromDb)
                            }
                            yandexAuthViewModel.setAuthState(YandexAuthState.Success)
                        } catch (e: IllegalStateException) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context, context.getString(R.string.server_error), Toast.LENGTH_SHORT
                                ).show()
                                yandexAuthViewModel.setAuthState(YandexAuthState.Error("server error"))
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context, context.getString(R.string.unknown_error), Toast.LENGTH_SHORT
                                ).show()
                                yandexAuthViewModel.setAuthState(YandexAuthState.Error("unknown error"))
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val userJson = yandexAuthViewModel.getUserInfo(yandexAuthViewModel.getToken().toString()).getOrNull()
                if(userJson != null) {
                    val email = userJson.getString("default_email")
                    val user = userRemoteViewModel.getUserByEmail(email)
                    if(user != null) {
                        yandexAuthViewModel.validateToken()
                        AuthManager.setCurrentUser(user)
                    }
                }
            } catch (e: IllegalStateException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.connection_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            YandexAuthState.Success -> {
                navToHomeScreen()
            }

            else -> {}
        }
    }

    DebouncedButton(
        debounceTimeMillis = 1000,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color.Black.copy(alpha = 0.7f)
        ),
        onClick = {
            val loginOptions = YandexAuthLoginOptions()
            launcher.launch(loginOptions)
        }
    ) {
        Text(text = "Yandex ID", style = MaterialTheme.typography.bodyMedium)
    }
}
