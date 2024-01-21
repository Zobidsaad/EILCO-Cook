package com.example.eilcooking

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eilcooking.ui.theme.EILcookingTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Context
import androidx.compose.ui.platform.LocalContext


class UserProfileDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EILcookingTheme {
                UserProfileDetailsScreen()
            }
        }
    }
}

@Composable
fun UserProfileDetailsScreen() {
    val context = LocalContext.current // Obtenez le contexte local Composable
    val user = FirebaseAuth.getInstance().currentUser
    val userEmail by remember { mutableStateOf(user?.email ?: "Aucun e-mail") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails du Profil") },
                backgroundColor = Color(0xFF87CEEB)
            )
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(it)) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "EIL Cook Logo Small",
                modifier = Modifier.size(100.dp).padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Email: $userEmail", style = MaterialTheme.typography.h6)

            // Champ de saisie pour le prénom
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Prénom") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            // Champ de saisie pour le nom
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            // Bouton pour enregistrer les données
            Button(
                onClick = {
                    // Appel de la fonction pour enregistrer les données dans Firestore
                    saveUserDataToFirestore(context,firstName, lastName, userEmail)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Enregistrer")
            }
        }
    }
}

fun saveUserDataToFirestore(context: Context, firstName: String, lastName: String, email: String) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val db = Firebase.firestore
        val userData = hashMapOf(
            "prenom" to firstName,
            "nom" to lastName,
            "email" to email
        )

        db.collection("student")
            .document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(context, "Données enregistrées avec succès", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de l'enregistrement des données: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
    }
}
