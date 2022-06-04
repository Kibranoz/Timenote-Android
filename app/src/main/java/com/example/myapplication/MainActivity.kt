package com.example.myapplication
import com.example.myapplication.Timenote
import android.os.Bundle
import android.provider.SyncStateContract
import android.widget.Chronometer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme


import java.util.*
import kotlin.concurrent.schedule

class MainActivity : ComponentActivity() {
    var timenote = Timenote()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                var text by rememberSaveable{ mutableStateOf("")}
                var pauseOrPlayIcon by rememberSaveable {mutableStateOf(R.drawable.ic_baseline_play_arrow_24)}
                var showTimeAdjustView by remember { mutableStateOf(false)}
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TimeView(timenote = timenote)

                        if (showTimeAdjustView) {

                            TimeChange(
                                timenote = timenote,
                                onPlayStatusChange = { pauseOrPlayIcon = it },
                                showTimeAdjustView = false
                            )
                        }

                        MenuBar(timenote = timenote, text=text, onTextChange = {text=it}, pauseOrPlayIcon = pauseOrPlayIcon, onPlayStatusChange = {pauseOrPlayIcon = it},
                            showTimeAdjustView = showTimeAdjustView,  onTimeAdjust = { showTimeAdjustView = it});
                        SimpleTextField(text=text, onTextChange = {text = it})


                    }
                }
            }
        }
    }

}




@Composable
fun SimpleTextField(text:String, onTextChange:(String)->Unit){
    TextField(value = text , onValueChange = {onTextChange(it)}, modifier = Modifier.fillMaxSize())
}

@Composable
fun TimeChange(timenote: Timenote, onPlayStatusChange: (Int) -> Unit, showTimeAdjustView: Boolean){
    var hours by remember{ mutableStateOf("")}
    var minutes by remember{ mutableStateOf("")}
    var seconds by remember{ mutableStateOf("")}
        Column {


            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                TextField(value = hours, onValueChange = { hours = it }, modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )
                Text(":", fontSize = 40.sp )
                TextField(value = minutes, onValueChange = { minutes = it }, modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone))
                Text(":")
                TextField(value = seconds, onValueChange = { seconds = it }, modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone))
            }
            Button(onClick = {
                if (timenote.getIfBegin()){
                    timenote.play()
                }

                timenote.adjustTime(
                    hours = hours.toInt(),
                    minutes = minutes.toInt(),
                    seconds = seconds.toInt()
                )
                onPlayStatusChange(R.drawable.ic_baseline_pause_24)
                timenote.play()

            }) {
                Text("Enregistrer")
            }
        }



}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
@Composable

fun TimeView(timenote: Timenote){
    var timeStr by remember { mutableStateOf("")}
     Timer().schedule(delay = 10000, period = 1000) {
         timeStr = timenote.getStrTime()
    }
    Text(timeStr, fontSize  = 40.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}
@Composable
fun MenuBar(timenote: Timenote, text:String, onTextChange: (String) -> Unit, pauseOrPlayIcon:Int, onPlayStatusChange: (Int) -> Unit, onTimeAdjust: (Boolean)->Unit,
showTimeAdjustView:Boolean){
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()){
        IconButton(onClick = {
            onTimeAdjust(!showTimeAdjustView)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_history_24), contentDescription = "Go back", modifier = Modifier.size(40.dp))

        }

        IconButton(onClick = { timenote.receiveTextFromActivityAndUpdateWithNewTimestamp(text = text)
        onTextChange(timenote.sendTextToActivity())}) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_playlist_add_24), contentDescription = "Add timenote",modifier = Modifier.size(40.dp))

        }

        IconButton(onClick = {
            if (timenote.getSiEnPause()){
                onPlayStatusChange(R.drawable.ic_baseline_pause_24)
                timenote.play()
            }
            else {
                onPlayStatusChange(R.drawable.ic_baseline_play_arrow_24)
                timenote.pause()
            }
        }) {
            Icon(painter = painterResource(id = pauseOrPlayIcon), contentDescription = "Play ", modifier = Modifier.size(40.dp))

        }

        IconButton(onClick = { /*TODO*/ }) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_save_24), contentDescription = "Save to disk", modifier = Modifier.size(40.dp))

        }



    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}