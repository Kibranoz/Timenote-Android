package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.myapplication.ui.theme.MyApplicationTheme


import java.util.*
import kotlin.concurrent.schedule

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val timenote:Timenote by viewModels()
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
        Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                TextField(value = hours, onValueChange = { hours = it }, modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )
                Text(":", fontSize = 40.sp )
                TextField(value = minutes, onValueChange = { minutes = it }, modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone))
                Text(":", fontSize  = 40.sp)
                TextField(value = seconds, onValueChange = { seconds = it }, modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone))
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                if (timenote.getIfBegin()){
                    timenote.play()
                }

                timenote.adjustTime(
                    hours = hours.toIntOrNull() ?: 0,
                    minutes = minutes.toIntOrNull() ?: 0,
                    seconds = seconds.toIntOrNull() ?:0
                )
                onPlayStatusChange(R.drawable.ic_baseline_pause_24)
                timenote.play()

            }) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_save_24), contentDescription = "adjust new time", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(20.dp))

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
     Timer().schedule(delay = 1000, period = 1000) {
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

        val context = LocalContext.current;


        IconButton(onClick = {

            timenote.receiveTextFromActivity(text=text)
            val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, timenote.sendTextToActivity())
            type = "text/plain"
        }
            val shareIntent = Intent.createChooser(sendIntent, "Share")
            startActivity(context,shareIntent,null)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_share_24), contentDescription = "Save to disk", modifier = Modifier.size(40.dp))

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