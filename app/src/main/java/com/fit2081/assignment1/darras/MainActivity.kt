package com.fit2081.assignment1.darras

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fit2081.assignment1.darras.ui.theme.Assignment1Theme
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign

/**
 * Welcome Screen Activity that displays the welcome screen of the app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
//        sharedPref.edit().clear().apply()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(modifier = Modifier.padding(innerPadding), sharedPref)
                }
            }
        }
    }
}

/**
 * Welcome Screen that displays the welcome screen of the app
 */
@Composable
fun WelcomeScreen(modifier: Modifier = Modifier, sharedPref:SharedPreferences) {
    val context = LocalContext.current

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(150.dp))

            Text("NutriTrack", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)

            Image(
                painter = painterResource(id = R.drawable.logo_f),
                contentDescription = "Welcome Screen Logo",
                modifier = Modifier.size(200.dp),
                colorFilter = ColorFilter.tint(Color(0xFF580DE5))
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text("This app provides general health and nutrition information \nfor" +
                    "educational purposes only. It is not intended as \nmedical advice," +
                    "diagnosis, or treatment. Always consult a \nqualified healthcare" +
                    "professional before making any \nchanges to your diet, exercise, or" +
                    "health regimen.\n" +
                    "Use this app at your own risk.\n" +
                    "If you’d like to an Accredited Practicing Dietitian (APD),\n please" +
                    "visit the Monash Nutrition/Dietetics Clinic\n(discounted rates for" +
                    "students):\n" +
                    "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    val id = sharedPref.getString("user_id", "")

                    if(id.isNullOrEmpty()){
                        context.startActivity(Intent(context,Login_Screen::class.java))
                    }
                    else{
                        AuthManager.login(id)
                        context.startActivity(Intent(context,Food_Intake::class.java))
//                        sharedPref.edit().remove("user_id").apply()
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5))
            ) {
                Text("Login",
                    fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(80.dp))

            Text("Designed with ❤️ by Jamil Darras")
        }
    }
}