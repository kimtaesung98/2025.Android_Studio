package com.example.appname.delivery.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appname.delivery.data.repository.DeliveryRepositoryImpl
import com.example.appname.delivery.domain.usecase.SubmitDeliveryRequestUseCase
import com.example.appname.delivery.ui.viewmodel.DeliveryViewModel
import com.example.appname.ui.theme.AppnameTheme
import kotlinx.coroutines.flow.collectLatest

import androidx.hilt.navigation.compose.hiltViewModel // (1) 🚨 hiltViewModel import
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun DeliveryScreen(
    // 🚨 (2) viewModel() 호출 방식을 Factory를 사용하도록 변경
    deliveryViewModel: DeliveryViewModel = hiltViewModel()
) {
    val uiState by deliveryViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // ViewModel의 이벤트를 구독하고 처리 (Side-effect 처리)
    LaunchedEffect(key1 = true) {
        deliveryViewModel.eventFlow.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "배달 요청 정보 입력",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.Companion.height(16.dp))

        OutlinedTextField(
            value = uiState.restaurantName,
            onValueChange = { deliveryViewModel.onRestaurantNameChange(it) },
            label = { Text("음식점 이름") },
            modifier = Modifier.Companion.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = "음식점 아이콘") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Companion.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Companion.Down) }),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.menu,
            onValueChange = { deliveryViewModel.onMenuChange(it) },
            label = { Text("메뉴") },
            modifier = Modifier.Companion.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "메뉴 아이콘") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Companion.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Companion.Down) }),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.deliveryAddress,
            onValueChange = { deliveryViewModel.onDeliveryAddressChange(it) },
            label = { Text("배달 주소") },
            modifier = Modifier.Companion.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = "주소 아이콘") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Companion.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true
        )

        Spacer(modifier = Modifier.Companion.weight(1f))

        Button(
            onClick = {
                deliveryViewModel.submitDeliveryRequest()
            },
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("요청하기")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeliveryScreenPreview() {
    AppnameTheme {
        DeliveryScreen()
    }
}

annotation class DeliveryScreen