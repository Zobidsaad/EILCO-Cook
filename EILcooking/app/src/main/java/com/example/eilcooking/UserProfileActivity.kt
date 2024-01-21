package com.example.eilcooking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import coil.compose.rememberImagePainter // Importez cette ligne
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eilcooking.ui.theme.EILcookingTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.util.Log



class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EILcookingTheme {
                UserProfileScreen(this@UserProfileActivity)
            }
        }
    }

    fun signOut() {
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            .addOnCompleteListener {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Vous avez été déconnecté.", Toast.LENGTH_SHORT).show()
                navigateToSignIn()
            }
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun UserProfileScreen(userProfileActivity: UserProfileActivity) {
    var showMenu by remember { mutableStateOf(false) }
    var categories by remember { mutableStateOf(listOf<Category>()) }
    val coroutineScope = rememberCoroutineScope()

    // Faire l'appel API lors du premier rendu du composant
    LaunchedEffect(key1 = true) {
        coroutineScope.launch {

            try {
                val response = RetrofitInstance.api.getCategories()
                categories = response.categories
                Log.d("Categories", categories.toString()) // Afficher les catégories dans les logs

            } catch (e: Exception) {
                Log.e("Error", "Erreur lors de la récupération des catégories: ${e.message}")
            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EILCOOK") },
                navigationIcon = {
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            DropdownMenuItem(onClick = {
                                userProfileActivity.startActivity(
                                    Intent(
                                        userProfileActivity,
                                        userProfileActivity::class.java
                                    )
                                )
                                showMenu = false
                            }) {
                                Text("Accueil")
                            }
                            DropdownMenuItem(onClick = {
                                userProfileActivity.startActivity(
                                    Intent(
                                        userProfileActivity,
                                        UserProfileDetailsActivity::class.java
                                    )
                                )
                                showMenu = false
                            }) {
                                Text("Profile")
                            }
                            DropdownMenuItem(onClick = { /* TODO: Action pour Favorites */ }) {
                                Text("Favorites")
                            }
                            DropdownMenuItem(onClick = { userProfileActivity.signOut() }) {
                                Text("Sign Out")
                            }
                        }
                    }
                },
                backgroundColor = Color(0xFF87CEEB) // Couleur bleu ciel
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(it)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "EIL Cook Logo Small",
                modifier = Modifier.size(100.dp).padding(16.dp)
            )
            LazyColumn {

                items(categories) { category ->
                    CategoryCard(category)
                }
            }

        }
    }
}

@Composable
fun CategoryCard(category: Category) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(category.strCategory, style = MaterialTheme.typography.h6)

            Spacer(modifier = Modifier.height(8.dp))

            // Afficher l'image de la catégorie
            Image(
                painter = rememberImagePainter(data = category.strCategoryThumb),
                contentDescription = "Category Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Afficher la description de la catégorie
            Text(
                text = category.strCategoryDescription,
                style = MaterialTheme.typography.body1,
                color = Color.Gray
            )
        }
    }
}





