package com.example.digitalstreetvendornetwork

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFFE65100),
                    secondary = Color(0xFF2E7D32),
                    surfaceVariant = Color(0xFFFFF3E0),
                    error = Color(0xFFD32F2F)
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var currentScreen by remember { mutableStateOf("LOGIN") }
                    var loggedInRole by remember { mutableStateOf("") }

                    when (currentScreen) {
                        "LOGIN" -> LoginScreen(
                            onLoginSuccess = { role -> loggedInRole = role; currentScreen = "MAIN" },
                            onNavigateToSignup = { currentScreen = "SIGNUP" }
                        )
                        "SIGNUP" -> SignupScreen(
                            onSignupSuccess = { role -> loggedInRole = role; currentScreen = "MAIN" },
                            onNavigateToLogin = { currentScreen = "LOGIN" }
                        )
                        "MAIN" -> {
                            val handleLogout = {
                                currentScreen = "LOGIN"
                                loggedInRole = ""
                            }
                            if (loggedInRole == "VENDOR") {
                                // This now strictly calls the UI from VendorScreens.kt
                                VendorAppMainScreen(onLogout = handleLogout)
                            } else {
                                CustomerAppMainScreen(onLogout = handleLogout)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit, onNavigateToSignup: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("VENDOR") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Storefront, contentDescription = "App Logo", modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Local Vends", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Your neighbourhood market, digitized.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth().selectableGroup().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedRole == "VENDOR", onClick = { selectedRole = "VENDOR" })
                        Text("Vendor", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedRole == "CUSTOMER", onClick = { selectedRole = "CUSTOMER" })
                        Text("Customer", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it; phoneError = false }, label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) }, singleLine = true, isError = phoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { if (phone.length == 10 && phone.all { it.isDigit() } && password.isNotBlank()) onLoginSuccess(selectedRole) else phoneError = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)
                ) { Text("Secure Login", fontSize = MaterialTheme.typography.titleMedium.fontSize) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToSignup) { Text("Create a new account") }
    }
}

@Composable
fun SignupScreen(onSignupSuccess: (String) -> Unit, onNavigateToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("VENDOR") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Join Local Vends", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth().selectableGroup().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = selectedRole == "VENDOR", onClick = { selectedRole = "VENDOR" }); Text("Vendor") }
                    Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = selectedRole == "CUSTOMER", onClick = { selectedRole = "CUSTOMER" }); Text("Customer") }
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { if (name.isNotBlank() && phone.length == 10 && password.isNotBlank()) onSignupSuccess(selectedRole) },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)
                ) { Text("Create Account") }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToLogin) { Text("Already have an account? Login") }
    }
}

// --- CUSTOMER DASHBOARD ---
@Composable
fun CustomerAppMainScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Discover", "My Orders", "Profile")

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = when(index) { 0 -> Icons.Filled.Search; 1 -> Icons.Filled.List; else -> Icons.Filled.Person }, contentDescription = title) },
                        label = { Text(title) }, selected = selectedTab == index, onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTab) {
                0 -> CustomerDiscoverScreen()
                1 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Track My Pickups & Deliveries") }
                2 -> CustomerProfileScreen(onLogout = onLogout)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDiscoverScreen() {
    val mumbaiLocation = LatLng(19.0760, 72.8777)
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(mumbaiLocation, 12f) }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("All", "Vegetables", "Fruits", "Street Food")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
            Marker(state = MarkerState(position = LatLng(19.1070, 72.8377)), title = "Raju Vegetables", snippet = "Open till 8PM")
            Marker(state = MarkerState(position = LatLng(19.0860, 72.8877)), title = "Vada Pav Stall", snippet = "Hot Snacks")
        }

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter)) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text("Search for vendors or items...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") }, modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.Transparent, focusedBorderColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter, onClick = { selectedFilter = filter }, label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = "Profile", modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Local Shopper", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("+91 9876543210", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
        ProfileOptionCard(icon = Icons.Filled.History, title = "Order History")
        ProfileOptionCard(icon = Icons.Filled.Star, title = "My Ratings & Reviews")
        ProfileOptionCard(icon = Icons.Filled.Settings, title = "Account Settings")
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontSize = MaterialTheme.typography.titleMedium.fontSize)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileOptionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}