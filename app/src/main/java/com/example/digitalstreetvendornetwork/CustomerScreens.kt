package com.example.digitalstreetvendornetwork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

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
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
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
            onClick = onLogout, modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(12.dp)
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
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}