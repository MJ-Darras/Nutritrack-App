@file:Suppress("DEPRECATION")

package com.fit2081.assignment1.darras

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.fit2081.assignment1.darras.ui.theme.Assignment1Theme
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.assignment1.darras.data.FoodIntake
import com.fit2081.assignment1.darras.data.FoodIntakeViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Food Intake Questionnaire Screen
 */
class Food_Intake : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user_id = AuthManager.getPatientId() ?: ""

        setContent {
            val viewModel: FoodIntakeViewModel = viewModel(
                factory = FoodIntakeViewModel.FoodIntakeViewModelFactory(application)
            )
            Assignment1Theme {
                questionnaire(user_id, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun questionnaire(id: String, viewModel: FoodIntakeViewModel) {
    val mContext = LocalContext.current
    // Get the food intake by ID
    val foodIntake by produceState<FoodIntake?>(initialValue = null, id){
        viewModel.getFoodIntakeById(id) {
            value = it
        }
    }

    val fruitsChecked = rememberSaveable { mutableStateOf(false) }
    val veggiesChecked = rememberSaveable { mutableStateOf(false) }
    val grainsChecked = rememberSaveable { mutableStateOf(false) }
    val redMeatChecked = rememberSaveable { mutableStateOf(false) }
    val seaFoodChecked = rememberSaveable { mutableStateOf(false) }
    val poultryChecked = rememberSaveable { mutableStateOf(false) }
    val fishChecked = rememberSaveable { mutableStateOf(false) }
    val eggsChecked = rememberSaveable { mutableStateOf(false) }
    val nutsChecked = rememberSaveable { mutableStateOf(false) }

    val selectedPersona = rememberSaveable { mutableStateOf("") }

    val mTimeEat = rememberSaveable { mutableStateOf(FoodIntake.DEFAULT_TIME) }
    var mTimeEatPickerDialog = TimePickerFun(mTimeEat)

    val mTimeSleep = rememberSaveable { mutableStateOf(FoodIntake.DEFAULT_TIME) }
    var mTimeSleepPickerDialog = TimePickerFun(mTimeSleep)

    val mTimeWake = rememberSaveable { mutableStateOf(FoodIntake.DEFAULT_TIME) }
    var mTimeWakePickerDialog = TimePickerFun(mTimeWake)

    // Load the food intake when it changes
    LaunchedEffect(foodIntake) {
        foodIntake?.let {
            fruitsChecked.value = it.fruit
            veggiesChecked.value = it.vegetables
            grainsChecked.value = it.grains
            redMeatChecked.value = it.redMeat
            seaFoodChecked.value = it.seaFood
            poultryChecked.value = it.poultry
            fishChecked.value = it.fish
            eggsChecked.value = it.eggs
            nutsChecked.value = it.nuts
            selectedPersona.value = it.persona
            mTimeEat.value = it.timeEat
            mTimeSleep.value = it.timeSleep
            mTimeWake.value = it.timeWake
        }
    }

    Scaffold(
        topBar = {
            IntakeTopAppBar(mContext)
        },

        bottomBar = {
            val mContext = LocalContext.current
            BottomAppBar(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Button( modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5)),
                        onClick = {
                            if(selectedPersona.value.isBlank()){
                                Toast.makeText(mContext, "Please select a persona", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if(mTimeEat.value.isBlank() || mTimeSleep.value.isBlank() || mTimeWake.value.isBlank()) {
                                Toast.makeText(mContext, "Please set all the time fields required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val formatter = DateTimeFormatter.ofPattern("HH:mm")

                            val sleepTime = LocalTime.parse(mTimeSleep.value.trim(), formatter)
                            val wakeTime = LocalTime.parse(mTimeWake.value.trim(), formatter)
                            val eatTime = LocalTime.parse(mTimeEat.value.trim(), formatter)

                            if(wakeTime == sleepTime){
                                Toast.makeText(mContext, "Wake up time should be different than" +
                                        " sleep time",
                                    Toast.LENGTH_SHORT).show()
                                return@Button

                            }

                            if(eatTime.isBefore(wakeTime) || eatTime.isAfter(sleepTime)) {
                                Toast.makeText(mContext, "Eat time must be after wake up time" +
                                        " and before sleep time",
                                    Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val minOneSelected = listOf(
                                fruitsChecked.value,
                                veggiesChecked.value,
                                grainsChecked.value,
                                redMeatChecked.value,
                                seaFoodChecked.value,
                                poultryChecked.value,
                                fishChecked.value,
                                eggsChecked.value,
                                nutsChecked.value
                            ).any{ it }

                            if (!minOneSelected){
                                Toast.makeText(mContext, "Please select at least one food" +
                                        " group", Toast.LENGTH_SHORT).show()
                                return@Button
                            }


                            val newFoodIntake = FoodIntake(
                                patientUserID = id,
                                fruit = fruitsChecked.value,
                                vegetables = veggiesChecked.value,
                                grains = grainsChecked.value,
                                redMeat = redMeatChecked.value,
                                seaFood = seaFoodChecked.value,
                                poultry = poultryChecked.value,
                                fish = fishChecked.value,
                                eggs = eggsChecked.value,
                                nuts = nutsChecked.value,
                                persona = selectedPersona.value,
                                timeEat = mTimeEat.value,
                                timeSleep = mTimeSleep.value,
                                timeWake = mTimeWake.value,

                            )

                            viewModel.insertFoodIntake(newFoodIntake)
                            val intent = Intent(mContext, HomeScreen::class.java)
                            mContext.startActivity(intent)

                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically)
                        {
                            Icon(Icons.Rounded.Done, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Save")
                        }
                    }
                }
            }
        },
        containerColor = Color.White,
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            //FOOD CATEGORIES
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
            ) {
                Text("Tick all the food categories you can eat", fontWeight = FontWeight.Bold)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
            ) {
                Column (
                    modifier = Modifier
                        .padding(end = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ){
                    LabelledCheckBox("Fruits",fruitsChecked)
                    LabelledCheckBox("Red Meat",redMeatChecked)
                    LabelledCheckBox("Fish",fishChecked)
                }
                Column (
                    modifier = Modifier
                        .padding(end = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    LabelledCheckBox("Vegetables",veggiesChecked)
                    LabelledCheckBox("Sea Food",seaFoodChecked)
                    LabelledCheckBox("Eggs",eggsChecked)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    LabelledCheckBox("Grains",grainsChecked)
                    LabelledCheckBox("Poultry",poultryChecked)
                    LabelledCheckBox("Nuts/Seeds",nutsChecked)
                }
            }

            //PERSONA ONWARDS
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
            ){
                Text("Your Persona", fontWeight = FontWeight.Bold)
                Text("People can be broadly classified into 6 different types based on their "
                        + "eating preferences. Click on each button below to find out the different"
                        + " types, and select the type that best fits you!", fontSize = 12.sp)
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ){
                    Column (
                        horizontalAlignment = Alignment.Start
                    ){
                        PersonaButton(
                            "Health Devotee",
                            image = painterResource(id = R.drawable._761037),
                            text = "I’m passionate about healthy eating & health plays a big part" +
                                    " in my life. I use social media to follow active lifestyle " +
                                    "personalities or get new recipes/exercise ideas. I may even " +
                                    "buy superfoods or follow a particular type of diet. I like to " +
                                    "think I am super healthy."
                        )
                        PersonaButton(
                            "Balance Seeker",
                                image = painterResource(id = R.drawable.screenshot_2025_03_28_145321),
                                text = "I try and live a balanced lifestyle, and I think that all " +
                                        "foods are okay in moderation. I shouldn’t have to feel " +
                                        "guilty about eating a piece of cake now and again. I get" +
                                        " all sorts of inspiration from social media like finding " +
                                        "out about new restaurants, fun recipes and sometimes healthy" +
                                        " eating tips."
                        )
                    }

                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally

                    ){
                        PersonaButton(
                            "Mindful Eater",
                            image = painterResource(id = R.drawable._002_i203_005_healthy_lifestyle_cartoon),
                            text = "I’m health-conscious and being healthy and eating healthy is" +
                                    " important to me. Although health means different things to" +
                                    " different people, I make conscious lifestyle decisions about " +
                                    "eating based on what I believe healthy means. I look for new" +
                                    " recipes and healthy eating information on social media.")

                        PersonaButton("Health Procrastinator",
                            image = painterResource(id = R.drawable.screenshot_2025_03_28_145430),
                            text = "I’m contemplating healthy eating but it’s not a priority " +
                                    "for me right now. I know the basics about what it means " +
                                    "to be healthy, but it doesn’t seem relevant to me right" +
                                    " now. I have taken a few steps to be healthier but I am" +
                                    " not motivated to make it a high priority because I have" +
                                    " too many other things going on in my life.")
                    }

                    Column (
                        horizontalAlignment = Alignment.End
                    ){
                        PersonaButton("Wellness Striver",
                            image = painterResource(id = R.drawable._360755),
                            text = "I aspire to be healthy (but struggle sometimes). Healthy" +
                                    " eating is hard work! I’ve tried to improve my diet, " +
                                    "but always find things that make it difficult to stick" +
                                    " with the changes. Sometimes I notice recipe ideas or " +
                                    "healthy eating hacks, and if it seems easy enough," +
                                    " I’ll give it a go.")

                        PersonaButton("Food Carefree",
                            image = painterResource(id = R.drawable.screenshot_2025_03_28_145608),
                            text = "\tI’m not bothered about healthy eating." +
                                    " I don’t really see the point and I don’t think about it. " +
                                    "I don’t really notice healthy eating tips or recipes and " +
                                    "I don’t care what I eat.")
                    }
                }

                //PERSONA SELECTION
                Spacer(modifier = Modifier.height(15.dp))
                Text("Which persona best fits you?", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(5.dp))


                SimpleDropdown(selectedPersona)

                //Timings
                Spacer(modifier = Modifier.height(30.dp))

                Column (
                ){
                    Text("Timings", fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(5.dp))

                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)

                    ){

                        Text("What time of day approx. do you\nnormally eat your biggest meal?"
                        , fontSize = 14.sp, textAlign = TextAlign.Left)
                        Spacer(modifier = Modifier.width(30.dp))
                        Button(
                            onClick = {mTimeEatPickerDialog.show()},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp), // Remove elevation for a flat look
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date Logo")
                            Text(text = mTimeEat.value,
                                Modifier.padding(start = 10.dp)
                            )
                        }
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ){
                        Text("What time of day approx. do you\ngo to sleep at night?"
                            , fontSize = 14.sp, textAlign = TextAlign.Left)
                        Spacer(modifier = Modifier.width(30.dp))
                        Button(
                            onClick = {mTimeSleepPickerDialog.show()},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp), // Remove elevation for a flat look
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date Logo")
                            Text(text = mTimeSleep.value, Modifier.padding(start = 10.dp))
                        }
                    }
                    Row (

                    ){
                        Text("What time of day approx. do you\nwake up in the morning?"
                            , fontSize = 14.sp, textAlign = TextAlign.Left)
                        Spacer(modifier = Modifier.width(30.dp))
                        Button(
                            onClick = {mTimeWakePickerDialog.show()},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp), // Remove elevation for a flat look
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date Logo")
                            Text(text = mTimeWake.value, Modifier.padding(start = 10.dp))
                        }
                    }
                }

            }
        }
    }
}

/**
 * Time Picker Function
 */
@Composable
fun TimePickerFun(mTime: MutableState<String>): TimePickerDialog {
    val mContext = LocalContext.current
    val mCalendar = Calendar.getInstance()

    val mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
    val mMinute = mCalendar.get(Calendar.MINUTE)

    mCalendar.time = Calendar.getInstance().time

    return TimePickerDialog(
        mContext,
        { _, mHour: Int, mMinute: Int ->
            mTime.value = String.format("%02d:%02d", mHour, mMinute)
        }, mHour, mMinute, false
    )
}

/**
 * Top App Bar helper function
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntakeTopAppBar(context : Context) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    CenterAlignedTopAppBar(
                modifier = Modifier.padding(bottom = 10.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Food Intake Questionnaire",
                        color =  Color(0xFF000000),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
//                        onBackPressedDispatcher?.onBackPressed()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
        )
}

/**
 * Labelled Checkbox helper function
 */
@Composable
fun LabelledCheckBox(label: String, checked: MutableState<Boolean>) {
    Row(verticalAlignment = Alignment.CenterVertically){
        Checkbox(checked = checked.value, onCheckedChange = {checked.value = it},
            modifier = Modifier
                .size(28.dp)
        )
        Text("$label", fontSize = 14.sp)
    }
}

/**
 * Persona Button helper function
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaButton(label: String, image: Painter, text: String) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    Button(
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5)),
        onClick = {showDialog = true},
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically)
        {
            Text("$label", fontSize = 12.sp)
        }
    }
    if(showDialog){
        AlertDialog(
            onDismissRequest = {showDialog = false},
            title = {},
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                    .fillMaxWidth(),

                    ) {
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier.size(130.dp)
                    )
                    Text("$label", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text, textAlign = TextAlign.Center)

                }
            },
            confirmButton = {
            },
            dismissButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Button(
                        onClick = {showDialog = false},
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF580DE5)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),

                        ) {
                        Text("Dismiss")
                    }
                }
            }
        )
    }
}

/**
 * Dropdown helper function
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdown(selectedText: MutableState<String>) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val personas = listOf("Health Devotee", "Balance Seeker", "Mindful Eater",
        "Health Procrastinator", "Wellness Striver", "Food Carefree")

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedText.value,
                onValueChange = {},
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                ),
                readOnly = true,
                placeholder = {
                    Box(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopStart // Forces text to top
                    ) {
                        Text(
                            "Select Option",
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(all = 0.dp)
                        )
                    }
                },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "down arrow icon")
                },
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Start),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(all = 0.dp)
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()

            )
            {
                personas.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona, fontSize = 14.sp) },
                        onClick = {
                            selectedText.value = persona
                            expanded = false
                        },
                        modifier = Modifier.padding(all = 0.dp)
                    )
                }
            }
        }
    }
}