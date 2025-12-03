package com.example.deliveryapp2.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.deliveryapp2.data.local.TokenManager
import com.example.deliveryapp2.data.network.LoginRequest
import com.example.deliveryapp2.data.network.RegisterRequest
import com.example.deliveryapp2.data.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) { // String: Role (CUSTOMER/OWNER)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("CUSTOMER") } // Default
    var address by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = if (isLoginMode) "Welcome Back" else "Create Account", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        if (!isLoginMode) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address (e.g. 101, Gangnam-daero)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Role Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = role == "CUSTOMER", onClick = { role = "CUSTOMER" })
                Text("Customer")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = role == "OWNER", onClick = { role = "OWNER" })
                Text("Owner")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
                scope.launch {
                    try {
                        if (isLoginMode) {
                            // 로그인
                            val response = RetrofitClient.authService.login(LoginRequest(email, password))
                            if (response.success && response.token != null) {
                                tokenManager.saveToken(response.token)
                                tokenManager.saveUserRole(response.role ?: "CUSTOMER")
                                // [추가] 주소 저장
                                tokenManager.saveUserAddress(response.address ?: "")
                                onLoginSuccess(response.role ?: "CUSTOMER")
                            } else {
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // 회원가입
                            val response = RetrofitClient.authService.register(RegisterRequest(email, password, name, role, address))
                            if (response.success) {
                                Toast.makeText(context, "Account Created! Please Login.", Toast.LENGTH_SHORT).show()
                                isLoginMode = true // 로그인 모드로 전환
                            } else {
                                Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (isLoginMode) "Login" else "Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Login")
        }
    }
}