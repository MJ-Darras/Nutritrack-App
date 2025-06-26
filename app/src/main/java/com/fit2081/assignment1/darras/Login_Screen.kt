@file:OptIn(ExperimentalMaterial3Api::class)

package com.fit2081.assignment1.darras

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.assignment1.darras.data.Patient
import com.fit2081.assignment1.darras.data.PatientsViewModel
import com.fit2081.assignment1.darras.ui.theme.Assignment1Theme
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import java.io.BufferedReader
import java.io.InputStreamReader

class Login_Screen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val context = this
        val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val firstLaunch = sharedPref.getBoolean("firstLaunch", true)

        val patientViewModel: PatientsViewModel = ViewModelProvider(
            this, PatientsViewModel.PatientsViewModelFactory(application)
        )[PatientsViewModel::class.java]

//        println(firstLaunch)

        if(firstLaunch) {
            val patients = parsePatientsData(context)
            patientViewModel.insertPatients(patients)
            sharedPref.edit().putBoolean("firstLaunch", false).apply()
        }

        setContent {
            Assignment1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    loginBottomSheet(context = LocalContext.current, sharedPref)
                }
            }
        }
    }
}

fun parsePatientsData(context: Context): List<Patient> {
    val patients = mutableListOf<Patient>()
    val inputStream = context.assets.open("user_details.csv")
    val reader = BufferedReader(InputStreamReader(inputStream))

    reader.readLine()

    reader.forEachLine { line ->
        val sections = line.split(",")

        val sex = sections[2]
        val isMale = sex.equals("male", ignoreCase = true)
        patients.add(Patient(
            userID = sections[1],
            phoneNumber = sections[0],
            password = "",
            name = "",
            registered = false,
            sex = sex,
            heifaTotalscore = if (isMale) sections[3].toFloat() else sections[4].toFloat(),
            discretionaryHEIFAscore = if (isMale) sections[5].toFloat() else sections[6].toFloat(),
            vegetablesHEIFAscore = if (isMale) sections[8].toFloat() else sections[9].toFloat(),
            fruitHEIFAscore = if (isMale) sections[19].toFloat() else sections[20].toFloat(),
            grainsandcerealsHEIFAscore = if (isMale) sections[29].toFloat() else sections[30].toFloat(),
            wholegrainsHEIFAscore = if (isMale) sections[33].toFloat() else sections[34].toFloat(),
            meatandalternativesHEIFAscore = if (isMale) sections[36].toFloat() else sections[37].toFloat(),
            dairyandalternativesHEIFAscore = if (isMale) sections[40].toFloat() else sections[41].toFloat(),
            sodiumHEIFAscore = if (isMale) sections[43].toFloat() else sections[44].toFloat(),
            alcoholHEIFAscore = if (isMale) sections[46].toFloat() else sections[47].toFloat(),
            waterHEIFAscore = if (isMale) sections[49].toFloat() else sections[50].toFloat(),
            sugarHEIFAscore = if (isMale) sections[54].toFloat() else sections[55].toFloat(),
            saturatedFatHEIFAscore = if (isMale) sections[57].toFloat() else sections[58].toFloat(),
            unsaturatedFatHEIFAscore = if (isMale) sections[61].toFloat() else sections[62].toFloat(),
        ))
    }
    return patients
}

//This function was mainly inspired by the examples at the Android Developers website.
@Composable
fun loginBottomSheet(context: Context, sharedPref: SharedPreferences) {

    var currentScreen by rememberSaveable { mutableStateOf("login") }
    val viewModel: PatientsViewModel = viewModel(
        factory = PatientsViewModel.PatientsViewModelFactory(context.applicationContext as Application)
    )

    if(currentScreen == "login"){
        loginUI(
            context,
            onSwitch = { currentScreen = "register" },
            viewModel,
            sharedPref
        )
    } else  {
        registerUI(
            context,
            onSwitch = {currentScreen = "login" },
            viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun loginUI(context: Context, onSwitch: () -> Unit, viewModel: PatientsViewModel, sharedPref: SharedPreferences){
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val allRegisteredIds by viewModel.allRegisteredPatients.collectAsState()
    val idElems = allRegisteredIds.map { it.userID }


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
        ) {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Log in",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {expanded = !expanded},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                ) {
                    OutlinedTextField(
                        value = selectedId,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("My ID (Provided by your clinician)", fontSize = 14.sp)},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = {expanded = false},
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if(idElems.isEmpty()){
                            DropdownMenuItem(
                                text = { Text("No Users have registered yet") },
                                onClick = {},
                                enabled = false
                            )
                        } else{
                            idElems.forEach {item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedId = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", fontSize = 14.sp)},
                    placeholder = {Text("Enter your password")},
                    shape = RoundedCornerShape(10.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("This app is only for pre-registered users. Please have\n" +
                        "your ID and phone number handy before continuing.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button( onClick = {
                    viewModel.getPatientById(selectedId) {
                        user ->
//                        println(user?.password)
                        if(user != null){
                            if(!user.registered){
                                Toast.makeText(context, "User has not registered yet" +
                                        "", Toast.LENGTH_LONG).show()
                            }
                            else if(user.registered && BCrypt.checkpw(password, user.password)){
                                //go to next page
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                                val intent = Intent(context, Food_Intake::class.java)

//                                intent.putExtra("ID", selectedId)
                                AuthManager.login(selectedId)
                                sharedPref.edit().putString("user_id", selectedId).apply()
                                context.startActivity(intent)
                            }
                            else {
                                //print err
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG).show()
                            }
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
                    Text("Continue",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button( onClick = {
                    onSwitch()
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5))
                ) {
                    Text("Register",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }

            }
        }
    }
}

@Composable
fun registerUI(context: Context, onSwitch: () -> Unit, viewModel: PatientsViewModel) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var phone_number by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm_password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var selectedId by rememberSaveable { mutableStateOf("") }

    var expanded by rememberSaveable { mutableStateOf(false) }

    val allUnregisteredIds by viewModel.allUnregisteredPatients.collectAsState()
    val idElems = allUnregisteredIds.map { it.userID }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Register",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                ) {
                    OutlinedTextField(
                        value = selectedId,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("My ID (Provided by your clinician)", fontSize = 14.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                            .fillMaxWidth()
//                            .padding(horizontal = 40.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
//                            .padding(horizontal = 40.dp)
                    ) {
                        if(idElems.isEmpty()){
                            DropdownMenuItem(
                                text = { Text("No Users to registered at the moment") },
                                onClick = {},
                                enabled = false
                            )
                        } else{
                            idElems.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedId = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { input ->
                        if(input.all {it.isLetter() || it.isWhitespace()})
                            name = input
                                    },
                    label = { Text("Name", fontSize = 14.sp) },
                    placeholder = { Text("Enter your name") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = phone_number,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() })
                            phone_number = input
                    },
                    label = { Text("Phone Number", fontSize = 14.sp) },
                    placeholder = { Text("Enter your number") },
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {password = it},
                    label = { Text("Password", fontSize = 14.sp) },
                    placeholder = { Text("Enter your password") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = confirm_password,
                    onValueChange = {confirm_password = it},
                    label = { Text("Confirm Password", fontSize = 14.sp) },
                    placeholder = { Text("Enter your password again") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("This app is only for pre-registered users. Please select\n" +
                        "your ID and enter your phone number and password to claim your account.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                val coroutineScope = rememberCoroutineScope()

                Button( onClick = {
                    //Set new data
                    if(selectedId.isBlank()){
                        Toast.makeText(context, "Please select an ID", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (name.isBlank()){
                        Toast.makeText(context, "Please enter your name", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if(phone_number.isBlank()){
                        Toast.makeText(context, "Please enter your phone number", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if(phone_number.length != 11){
                        Toast.makeText(context, "Phone number must be 11 digits", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if(password.length < 6){
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if(password != confirm_password){
                        Toast.makeText(context, "Passwords should match", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (name.isNotBlank() && password == confirm_password){
                        coroutineScope.launch {
                            // get chosen user id's data
                            viewModel.getPatientById(selectedId) {
                                patientData ->
                                if(patientData != null && phone_number == patientData.phoneNumber){
                                    viewModel.insertPatient(
                                        patientData.copy(
                                            name = name,
                                            password = BCrypt.hashpw(password, BCrypt.gensalt()),
                                            registered = true
                                        )
                                    )
                                    Toast.makeText(context, "Register Successful", Toast.LENGTH_LONG).show()
                                    phone_number = ""
                                    password = ""
                                    confirm_password = ""
                                    name = ""
                                    selectedId = ""
                                }
                                else{
                                    Toast.makeText(context, "Login Failed, check phone number or ID", Toast.LENGTH_LONG).show()
                                }
                            }
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
                    Text("Register",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button( onClick = {
                    onSwitch()
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5))
                ) {
                    Text("Login",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }

            }
        }
    }
}