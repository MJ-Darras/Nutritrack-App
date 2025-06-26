package com.fit2081.assignment1.darras

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fit2081.assignment1.darras.Ai.GenAiViewModel
import com.fit2081.assignment1.darras.Ai.Uistate
import com.fit2081.assignment1.darras.data.FoodIntakeViewModel
import com.fit2081.assignment1.darras.data.Patient
import com.fit2081.assignment1.darras.data.PatientsViewModel
import com.fit2081.assignment1.darras.network.FruitRepository
import com.fit2081.assignment1.darras.ui.theme.Assignment1Theme
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Home Screen Activity that displays the home screen of the app
 */
class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val mContext = this

        setContent {
            Assignment1Theme {
                val navController: NavHostController = rememberNavController()
                var selectedItem = rememberSaveable { mutableStateOf(0) }
                val user_id = AuthManager.getPatientId()
                val patientViewModel: PatientsViewModel = viewModel(
                    factory = PatientsViewModel.PatientsViewModelFactory(application)
                )
                val foodIntakeViewModel: FoodIntakeViewModel = viewModel(
                    factory = FoodIntakeViewModel.FoodIntakeViewModelFactory(application)
                )
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MyBottomAppBar(navController,selectedItem)
                    }
                ) { innerPadding ->
                    Column (modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                    ){
                        MyNavHost(innerPadding,
                            navController,
                            selectedItem,
                            user_id?:"",
                            patientViewModel,
                            foodIntakeViewModel,
                            sharedPref,
                            mContext
                        )
                    }
                }
            }
        }
    }
}

/**
 * Navigation Host that contains all the screens in the app
 */
@Composable
fun MyNavHost(innerPadding: PaddingValues,
              navController: NavHostController,
              selectedItem: MutableState<Int>, id :String,
              patientViewModel: PatientsViewModel,
              foodIntakeViewModel: FoodIntakeViewModel,
              sharedPref: SharedPreferences,
              mContext: Context
)
{
    // Create a NavHost to navigate between screens
    NavHost(
        navController = navController,
        startDestination = "Home",
    ) {
        // Define the different screens in the app
        composable("Home") {
            HomeScreenSection(innerPadding, navController, selectedItem, id, mContext) }

        composable("Insights"){
            InsightsScreen(innerPadding, id, navController, selectedItem, mContext)
        }

        composable("NutriCoach") {
            NutriCoachScreen(innerPadding,foodIntakeViewModel, patientViewModel)
        }

        composable("Settings") {
            SettingsScreen(innerPadding,
                patientViewModel,
                sharedPref,
                mContext
            )
        }
    }

}

/**
 * Home Screen that displays the home screen of the app
 */
@Composable
fun HomeScreenSection(innerPadding: PaddingValues, navController: NavHostController,
                      selectedItem: MutableState<Int>, id : String, mContext: Context)
{
    var context = mContext

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)

    ){
        Text(text = "Hello,", fontSize = 16.sp, color = Color.Gray)

        Text(
            text = "User $id",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(){
            Text(
                text = "You've already filled in your Food Intake\nQuestionnaire, but you can change details here:",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(40.dp))

            Button(
                onClick = {
                    val intent = Intent(context, Food_Intake::class.java)
                    intent.putExtra("ID", id)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(60.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit", color = Color.White, fontSize = 12.sp)
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 15.dp),
        ){
            Image(
                painter = painterResource(id = R.drawable.food),
                contentDescription = null,
                modifier = Modifier.size(250.dp),
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "My Score",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = {
                    navController.navigate("Insights")
                    selectedItem.value = 1
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray),

            ) {
                Text("See all scores ", color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "See all scores",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 15.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.up_arrow_svgrepo_com),
                        contentDescription = "Upward Arrow",
                        tint = Color.DarkGray.copy(0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Your Food Quality score",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
            Text(findScore(id,"HEIFAtotalscore").toString() + "/100",
                color = Color(0xFF61ac37),
                fontWeight = FontWeight.Medium)
        }

        Column (
            modifier = Modifier
                .padding(top = 20.dp)
        ){
            Text("What is the Food Quality Score?", fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text("Your Food Quality Score provides a snapshot of how well your eating patterns " +
                    "align with established food guidelines, helping you identify both strengths " +
                    "and opportunities for improvement in your diet.",
                fontSize = 14.sp)

            Text("\nThis personalized measurement considers various food groups including" +
                    " vegetables, fruits, whole grains, and proteins to give you practical" +
                    " insights for making healthier food choices.",
                fontSize = 14.sp)
        }
    }
}

/**
 * Composable function for displaying the Reports screen.
 */
@Composable
fun InsightsScreen(innerPadding: PaddingValues, id: String, navController: NavHostController,
                   selectedItem: MutableState<Int>, mContext: Context) {

    var context = mContext

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ){
        Spacer(Modifier.height(24.dp))

        Text("Insights: Food Score", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(20.dp))

        SliderRow("Vegetables", 5f, findScore(id,"VegetablesHEIFAscore"))

        SliderRow("Fruits", 5f, findScore(id,"FruitHEIFAscore"))

        SliderRow("Grains & Cereals", 5f, findScore(id,"GrainsandcerealsHEIFAscore"))

        SliderRow("Whole Grains", 5f, findScore(id,"WholegrainsHEIFAscore"))

        SliderRow("Meat & Alternatives", 10f, findScore(id,"MeatandalternativesHEIFAscore"))

        SliderRow("Dairy", 10f, findScore(id,"DairyandalternativesHEIFAscore"))

        SliderRow("Water", 5f, findScore(id,"WaterHEIFAscore"))

        SliderRow("Unsaturated Fats", 5f, findScore(id,"UnsaturatedFatHEIFAscore"))

        SliderRow("Saturated Fats", 5f, findScore(id,"SaturatedFatHEIFAscore"))

        SliderRow("Sodium", 10f, findScore(id,"SodiumHEIFAscore"))

        SliderRow("Sugar", 10f, findScore(id,"SugarHEIFAscore"))

        SliderRow("Alcohol", 5f, findScore(id,"AlcoholHEIFAscore"))



        SliderRow("Discretionary Foods", 10f, findScore(id,"DiscretionaryHEIFAscore"))

        Spacer(modifier = Modifier.height(50.dp))

        Column (
            modifier = Modifier
                .fillMaxWidth(),
        ){
            Text("Total Food Quality Score",
                fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

            val finalScore = findScore(id,"HEIFAtotalscore")

            SliderRow("", 100f, finalScore)

            Spacer(modifier = Modifier.height(30.dp))

            val message = String.format ("Sup, my HEIFA score is %.2f!", finalScore)

            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val shareIntent = Intent(ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                        context.startActivity(Intent.createChooser(shareIntent, "Share Results Via"))
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Share with someone")
                }

                Button(
                    onClick = {
                        navController.navigate("NutriCoach")
                        selectedItem.value = 2
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Improve my diet!")
                }
            }
        }
    }
}

/**
 * Composable function for displaying the Settings screen.
 */
@Composable
fun SettingsScreen(innerPadding: PaddingValues,
                   viewModel: PatientsViewModel,
                   sharedPref: SharedPreferences,
                   mContext: Context
) {
    val currentSettingScreen = rememberSaveable { mutableStateOf("settings") }

    // Navigate to the different screens based on the current screen
    when(currentSettingScreen.value){
        // Navigate to the settings screen
        "settings" -> {
            val patient = remember { mutableStateOf<Patient?>(null) }
            LaunchedEffect(Unit) {
                viewModel.getPatientById(AuthManager.getPatientId().toString(), {data ->
                    if (data != null) {
                        patient.value = data
                    }
                })
            }

            Column (modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
            ){
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("ACCOUNT", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically){
                    Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                        Image(
                            painter = painterResource(id = R.drawable.person_svgrepo_com),
                            contentDescription = "User Icon",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(patient.value?.name ?: "")
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically){
                    Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                        Image(
                            painter = painterResource(id = R.drawable.phone_svgrepo_com),
                            contentDescription = "Phone Icon",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(patient.value?.phoneNumber ?: "")
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically){
                    Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.TopStart) {
                        Image(
                            painter = painterResource(id = R.drawable.id_horizontal_svgrepo_com),
                            contentDescription = "Id Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(patient.value?.userID ?: "")
                }

                Spacer(modifier = Modifier.height(80.dp))

                Text("OTHER SETTINGS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(40.dp))
                val showDialog = rememberSaveable { mutableStateOf(false) }
                SettingButton("Logout", R.drawable.logout_svgrepo_com,"Log-out Icon",
                    onClick = {
                        showDialog.value = true
                    }
                )
                LogoutPopUp(showDialog.value,
                    onConfirm = {
                        showDialog.value = false
                        sharedPref.edit().remove("user_id").apply()
                        AuthManager.logout()
                        mContext.startActivity(Intent(mContext,MainActivity::class.java))
                    },
                    onDismiss = {
                        showDialog.value = false
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                SettingButton("Clinicial Login", R.drawable.person_svgrepo_com, "User Icon",
                    onClick = {
                        currentSettingScreen.value = "clinician_login"
                    }
                )
            }

        }
        // Navigate to the clinician login screen
        "clinician_login" -> {
            ClinicianLoginScreen(currentSettingScreen, mContext)
        }
        // Navigate to the admin screen
        "admin" -> {
            AdminScreen(currentSettingScreen, patientViewModel = viewModel)
        }
    }
}

/**
 * Composable function for displaying the Admin screen.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AdminScreen(
    currentSettingScreen: MutableState<String>,
    patientViewModel: PatientsViewModel,
    genAiViewModel: GenAiViewModel = viewModel()
    ) {

    var mAvg by rememberSaveable { mutableStateOf("") }
    var fAvg by rememberSaveable { mutableStateOf("") }

    var pattern by rememberSaveable { mutableStateOf("") }

    // Get the current UI state
    val uiState by genAiViewModel.uiState.collectAsState()

    // Convert the patient data to a JSON string
    val gson = Gson()
    val samplePatientData = gson.toJson(patientViewModel.allPatients.value)

    // Could have added a mutable state flow val in the Patients View Model for each gender,
    // but decided against that because adding too many could make the refreshing patients data slower,
    // hence it is done locally here
    LaunchedEffect(Unit) {
        patientViewModel.allPatients.collect {
            patientList ->
            var mSum = 0.0
            var mCount = 0

            var fSum = 0.0
            var fCount = 0
            for(patient in patientList){
                if(patient.sex.lowercase() == "male"){
                    mSum += patient.heifaTotalscore
                    mCount++
                } else if(patient.sex.lowercase() == "female"){
                    fSum += patient.heifaTotalscore
                    fCount++
                }
            }
            // calculate the average for both genders
            mAvg = if(mCount > 0) String.format("%.1f", (mSum / mCount)) else "N/A"
            fAvg = if(fCount > 0) String.format("%.1f", (fSum / fCount)) else "N/A"
        }
    }

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
    ){
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text("Clinician Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier
            .padding(4.dp)
        ) {
            Text("Average HEIFA (Male)     :   ", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Text(mAvg)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier
            .padding(4.dp)
        ) {
            Text("Average HEIFA (Female) :   ", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Text(fAvg)
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Generate an answer pattern button
        Button(
            onClick = {
                genAiViewModel.sendPrompt("""
                    Find 3 interesting or unexpected patterns in the following patient dataset.
                    Focus on HEIFA scores, sex, and registration status and the 
                    ranges that these values come in. 
                    Each pattern output should be made up of around 50
                    words with a title at the start that summarizes what its about.
                    
                    Titles should be numbered and should have a ":" after them.
                    After every title, place the findings on the next line. 
                    Do not use font modifiers on any of the text, i.e. do not make anything bold.
                    
                    Do not include anything besides the actual patterns in the output.
                    
                    Sample Data: $samplePatientData
                """.trimIndent())
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5)),
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
                ) {
                Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                    Image(
                        painter = painterResource(id = R.drawable.icons8_search),
                        contentDescription = "Search Icon",
                        modifier = Modifier.size(25.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
                Text("Find Data Pattern",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Get the current UI state and decide what to display
        if(uiState is Uistate.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            var textColor = MaterialTheme.colorScheme.onSurface

            if( uiState is Uistate.Error) {
                textColor = MaterialTheme.colorScheme.error
                pattern = (uiState as Uistate.Error).errorMessage
            }
            else if (uiState is Uistate.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                pattern = (uiState as Uistate.Success).outputText
            }

            Box(
                modifier = Modifier
                    .height(350.dp)
                    .border(2.dp, Color.LightGray)
            ){
                val scrollState = rememberScrollState()

                Text(
                    text = pattern,
                    textAlign = TextAlign.Start,
                    color = textColor,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.LightGray)
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Return to the settings screen
        Button(
            onClick = {
                currentSettingScreen.value = "settings"
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5))
        ) {
            Text("Done",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Composable function for displaying the Clinician Login screen.
 */
@Composable
fun ClinicianLoginScreen(
    currentSettingScreen: MutableState<String>,
    context: Context
){
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
    ){
        var enteredClinicianKey by rememberSaveable { mutableStateOf("") }

        val clinicianKey = AuthManager.getClinicianKey()

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text("Clinician Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(50.dp))

        OutlinedTextField(
            value = enteredClinicianKey,
            onValueChange = { enteredClinicianKey = it },
            label = { Text("Clinician Key", fontSize = 14.sp)},
            placeholder = {Text("Enter your clinician key")},
            shape = RoundedCornerShape(10.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Login button that checks if the entered clinician key is correct
        // and navigates to the admin screen if it is
        Button(
            onClick = {
            if(enteredClinicianKey.isNotEmpty()){
                if(enteredClinicianKey == clinicianKey){
                    //go to next page
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                    currentSettingScreen.value = "admin"
                }
                else {
                    //print err
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG).show()
                }
            }

        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5))
        ) {
            Text("Clinician Login",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Composable function for displaying the NutriCoach screen.
 */
@Composable
fun NutriCoachScreen(
    innerPadding: PaddingValues,
    foodIntake: FoodIntakeViewModel,
    patientViewModel: PatientsViewModel,
    genAiViewModel: GenAiViewModel = viewModel(),
) {
    var showDailog by rememberSaveable { mutableStateOf(false) }
    var responses by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var triggered by rememberSaveable { mutableStateOf(false) }
    var alreadySaved by rememberSaveable { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),

    ){
        val userId = AuthManager.getPatientId()
        var patient by remember { mutableStateOf<Patient?>(null) }

        // Get the patient by ID when the user ID changes
        LaunchedEffect(userId) {
            patientViewModel.getPatientById(userId?:""){
                patient = it
            }
        }

        val fruitScore = patient?.fruitHEIFAscore

        // Display the fruit search screen
        FruitSearch()

        Spacer(modifier = Modifier.height(20.dp))

        val placeholderResult = stringResource(R.string.results_placeholder)

        // Generate a motivational message
        val basicPrompt = """
            Generate a short encouraging message to help someone improve their fruit intake.
            Their fruit intake score is ${fruitScore} with 5 being the max score, hence based 
            on their score out of 5 change your tone slightly.
            You do not need to mention the score in your message, only use it as a reference to 
            give an answer.
            Add some emojis to the short encouraging message.
        """.trimIndent()

        var result by rememberSaveable { mutableStateOf(placeholderResult)}

        // Get the current UI state
        val uiState by genAiViewModel.uiState.collectAsState()


        Column(modifier = Modifier.fillMaxWidth()) {

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                genAiViewModel.sendPrompt(basicPrompt)
                    alreadySaved = false },
                modifier = Modifier
                    .align(Alignment.Start),
                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                Text(text = "Motivational Message")
            }
            // React based on the current UI state
            if(uiState is Uistate.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                var textColor = MaterialTheme.colorScheme.onSurface

                if( uiState is Uistate.Error) {
                    textColor = MaterialTheme.colorScheme.error
                    result = (uiState as Uistate.Error).errorMessage
                }
                else if (uiState is Uistate.Success) {
                    textColor = MaterialTheme.colorScheme.onSurface
                    result = (uiState as Uistate.Success).outputText
                }

                val scrollState = rememberScrollState()

                Text(
                    text = result,
                    textAlign = TextAlign.Start,
                    color = textColor,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(2.dp)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                )
            }

            // If uiState is success, save the AI response to the database
            LaunchedEffect(uiState) {
                if(uiState is Uistate.Success && !alreadySaved){
                    userId?.let {
                        patientViewModel.updateAiResponses(it, result)
                        alreadySaved = true
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Show the AI responses dialog if showDailog is true
            if(showDailog) {
                AiResponsesDialog(responses, onDismiss = {showDailog = false})
            }

            // Show all previous tips button
            Button(
                onClick = {
                    triggered = true
                },
                modifier = Modifier
                    .align(Alignment.Start),
                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                Text(text = "Show all previous tips")
            }

            if(triggered){
                userId?.let{
                    patientViewModel.getAiResponsesById(it) { answer ->
                        responses = answer
                        showDailog = true
                    }
                }
                triggered = false
            }
        }
    }
}

/**
 * Composable function for displaying the AI responses dialog.
 */
@Composable
fun AiResponsesDialog(
    responses: List<String>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Done")
            }
        },
        title = { Text("Previous AI motivational messages") },
        text = {
            Column (modifier = Modifier.verticalScroll(rememberScrollState())){
                if(responses.isEmpty()){
                    Text("No Responses yet")
                } else{
                    responses.forEachIndexed{
                            index, response ->
                        Text("${index + 1}. $response",
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
            }
        }
    )
}

/**
 * Composable function for displaying the bottom navigation bar.
 */
@Composable
fun MyBottomAppBar(navController: NavHostController, selectedItem : MutableState<Int>) {
    // State to track the currently selected item in the bottom navigation bar.
    // List of navigation items: "home", "reports", "settings".
    val items = listOf(
        "Home",
        "Insights",
        "NutriCoach",
        "Settings",
    )
    // NavigationBar composable to define the bottom navigation bar.
    NavigationBar (
        containerColor = Color.White
    ){
        // Iterate through each item in the 'items' list along with its index.
        items.forEachIndexed { index, item ->
            // NavigationBarItem for each item in the list.
            val iconSize = 25.dp
            NavigationBarItem(
                // Define the icon based on the item's name.
                icon = {
                    when (item) {
                        "Home" -> Icon(Icons.Filled.Home, contentDescription = "Home",
                            tint = Color(0xFF580DE5),
                            modifier = Modifier.size(iconSize))
                        "Insights" -> Icon(Icons.Filled.Info, contentDescription = "Insights",
                            tint = Color(0xFF580DE5),
                            modifier = Modifier.size(iconSize))
                        "NutriCoach" -> Icon(painter = painterResource(id = R.drawable.head_side),
                            contentDescription = "NutriCoach",
                            tint = Color(0xFF580DE5),
                            modifier = Modifier.size(iconSize))
                        "Settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings",
                            tint = Color(0xFF580DE5),
                            modifier = Modifier.size(iconSize))
                    }
                },
                // Display the item's name as the label.
                label = { Text(item) },
                // Determine if this item is currently selected.
                selected = selectedItem.value == index,
                // Actions to perform when this item is clicked.
                onClick = {
                    // Update the selectedItem state to the current index.
                    selectedItem.value = index
                    // Navigate to the corresponding screen based on the item's name.
                    navController.navigate(item)
                }
            )
        }
    }
}

/**
 * Composable function for displaying a slider row.
 */
@Composable
fun SliderRow(name: String, limit: Float, positionValue: Float){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)
    ){
        val textWidth = if(name.isEmpty()) 80 else 50
        val sliderWidth = if(name.isEmpty()) 300 else 180

        if (name != ""  ){
            Text(
                text = "$name",fontSize = 14.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
            )
        }
        Slider(
            value = positionValue,
            onValueChange = {},
            valueRange = 0f..limit,
            colors = SliderDefaults.colors(
                thumbColor =  Color(0xFF580DE5), activeTrackColor = Color(0xFF580DE5)
            ),
            modifier = Modifier.width(sliderWidth.dp)
        )
        Text(
            text =
            if (positionValue % 1 == 0f) {
                "${positionValue.toInt()}/${limit.toInt()}"
            } else {
                String.format("%.2f/%d", positionValue, limit.toInt())
            },
            modifier = Modifier
                .width(textWidth.dp),
            textAlign = TextAlign.Right,
            fontSize = 14.sp
        )
    }
}

/**
 * Composable function for displaying a labelled checkbox.
 */
@Composable
fun findScore(rowValue: String, intitColumnName : String): Float {
    val context = LocalContext.current
    val assets = context.assets
    var result = "0"

    val fileName = "user_details.csv"

    try {
        val inputStream = assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        val header = lines.first().split(",")

        val row = lines.drop(1).find { it.split(",")[1] == rowValue }

        if (row != null){
            val columnName = intitColumnName + row.split(",")[2]
            val columnIndex = header.indexOf(columnName)
            result = row.split(",")[columnIndex]
        }

    } catch (e: Exception) {
        //wtv
    }
    return result.toFloat()
}

/**
 * Composable function for displaying a setting button.
 */
@Composable
fun SettingButton(title: String, icon: Int, iconDesc: String, onClick: () -> Unit){
    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(10.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ){
        Row(modifier =
            Modifier
                .fillMaxWidth()
                .padding(10.dp),

            verticalAlignment = Alignment.CenterVertically
        ){
            Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = iconDesc,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(title, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Arrow Icon",
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

/**
 * Composable function for displaying a logout button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutPopUp(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
){
    if(show){
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {Text("Confirm Logout")},
            text = { Text("Are you sure you want to log out?")},
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

}

/**
 * Composable function for displaying a fruit search screen.
 */
@Composable
fun FruitSearch() {
    var chosenFruit by rememberSaveable { mutableStateOf("") }
    val defaultFruitInfo = mapOf(
        "Family" to "",
        "Calories" to "",
        "Fat" to "",
        "Sugar" to "",
        "Carbs" to "",
        "Protein" to ""
    )
    var fruitInfo by rememberSaveable { mutableStateOf(defaultFruitInfo) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val repository = remember { FruitRepository() }

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        Text("NutriCoach", fontWeight = FontWeight.Bold, fontSize = 25.sp)
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text("Fruit Name", fontWeight = FontWeight.SemiBold)

    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth()){
        OutlinedTextField(
            value = chosenFruit,
            onValueChange = { chosenFruit = it },
            placeholder = { Text("Enter a Fruit's name") },
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            modifier = Modifier
                .padding(vertical = 0.dp)
                .height(55.dp)
        )

        Spacer(modifier = Modifier.width(25.dp))

        Button(
            onClick = {
                error = null
                if(chosenFruit.isBlank()){
                    error = "Please enter a fruit's name"
                    return@Button
                }

                scope.launch {
                    val result = repository.fetchFruit(chosenFruit)
                    result.onSuccess {
                        data ->
                        fruitInfo = mapOf(
                            "Family" to data.family,
                            "Calories" to data.nutritions.calories.toString(),
                            "Fat" to data.nutritions.fat.toString(),
                            "Sugar" to data.nutritions.sugar.toString(),
                            "Carbs" to data.nutritions.carbohydrates.toString(),
                            "Protein" to data.nutritions.protien.toString()
                        )
                    }.onFailure {
                        error = "Oopsies, this fruit might not exist, try again please"
                    }
                }
            },
            modifier = Modifier
                .width(100.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            Text(text = "Search")
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    error?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error)
    }

    Column (modifier = Modifier
        .fillMaxWidth()
    ){
        fruitInfo.forEach{ (label,value) ->
            Row(modifier = Modifier
                .padding(4.dp)
            ) {
                Text("$label:")
                Spacer(modifier = Modifier.width(10.dp))
                Text(value)
            }
        }
    }
}