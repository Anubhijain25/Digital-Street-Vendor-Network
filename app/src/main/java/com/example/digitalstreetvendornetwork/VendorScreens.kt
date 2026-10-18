package com.example.digitalstreetvendornetwork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp

// Moved from MainActivity to keep domain logic organized
data class Product(val id: Int, val name: String, val price: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorAppMainScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    // Added "Profile" to the vendor tabs
    val tabs = listOf("Catalogue", "Location", "Orders", "Profile")
    val productList = remember { mutableStateListOf(
        Product(1, "Fresh Vada Pav", "₹15", "Hot and spicy Mumbai style vada pav served with green chutney."),
        Product(2, "Cutting Chai", "₹10", "Strong ginger tea, perfect for the evening."),
        Product(3, "Misal Pav", "₹40", "Spicy sprout curry with bread, garnished with onions and farsan.")
    )}

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(tabs[selectedTab], fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            val icon = when(index) {
                                0 -> Icons.Filled.Home
                                1 -> Icons.Filled.LocationOn
                                2 -> Icons.Filled.ShoppingCart
                                else -> Icons.Filled.Person
                            }
                            Icon(imageVector = icon, contentDescription = title)
                        },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> VendorCatalogueScreen(productList)
                1 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Map Integration Pending") }
                2 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No Active Orders") }
                3 -> VendorProfileScreen(onLogout = onLogout)
            }
        }
    }
}

@Composable
fun VendorCatalogueScreen(productList: MutableList<Product>) {
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Product") },
            text = {
                Column {
                    OutlinedTextField(value = newName, onValueChange = { newName = it; nameError = false }, label = { Text("Product Name") }, isError = nameError, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newPrice, onValueChange = { newPrice = it; priceError = false }, label = { Text("Price (e.g. 50)") }, isError = priceError, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newDesc, onValueChange = { newDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newName.isNotBlank() && newPrice.isNotBlank()) {
                        productList.add(Product(productList.size + 1, newName, if (newPrice.startsWith("₹")) newPrice else "₹$newPrice", newDesc))
                        newName = ""; newPrice = ""; newDesc = ""; showDialog = false
                    } else { nameError = !newName.isNotBlank(); priceError = !newPrice.isNotBlank() }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Filled.Add, contentDescription = "Add Product", tint = Color.White) } }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            items(productList) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Fastfood, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(product.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(product.price, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            IconButton(onClick = { productList.remove(product) }) { Icon(Icons.Filled.Delete, contentDescription = "Delete Product", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VendorProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Storefront, contentDescription = "Business Profile", modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Raju's Fresh Vends", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Verified Vendor", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Earnings & Reviews", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Business Settings", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Close Shop (Logout)", fontSize = MaterialTheme.typography.titleMedium.fontSize)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}