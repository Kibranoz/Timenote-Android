package com.example.myapplication

import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.floor

class Timenote {
    var text = ""
    var time:Long = 0;
    var timeBeginning:Long=0;
    var pauseBeggining:Long = 0;
    var enPause:Boolean = true;
    var begin = true;
    var pauseTime:Long = 0

    constructor(){
        Timer().schedule(delay = 1000, period = 1000){


            if (!this@Timenote.enPause){
                        this@Timenote.time = System.currentTimeMillis() - this@Timenote.timeBeginning
                    }
            if (this@Timenote.enPause && !this@Timenote.begin){
                this@Timenote.pauseTime = System.currentTimeMillis()
            }
            }


    }
    fun getStrTime():String{
        val hr = floor((this.time/3600000).toDouble()).toLong()
        val min = floor((this.time/60000)%60.toDouble()).toLong()
        val sec = floor((this.time/1000)%60.toDouble()).toLong()
        return "$hr:$min:$sec"

    }
    fun play(){
        if (this.begin){
            this.timeBeginning = System.currentTimeMillis()
            this.begin = false;
        }
        if (this.pauseTime != 0.toLong()){
            var deltaPause = this.pauseTime - this.pauseBeggining;
            this.timeBeginning += deltaPause
        }

        this.enPause = false
    }

    fun pause(){
        this.pauseBeggining = System.currentTimeMillis()
        this.enPause = true
    }

    fun getIfBegin():Boolean{
        return this.begin
    }

    fun getSiEnPause():Boolean{
        return this.enPause
    }
    fun sendTextToActivity():String{
        return this.text;
    }
    fun receiveTextFromActivity(text:String){
        this.text = text
    }
    fun receiveTextFromActivityAndUpdateWithNewTimestamp(text:String){
        this.text = text;
        this.text += "\n" + "-" + getStrTime() + " : "
    }
    fun adjustTime(hours:Int, minutes:Int, seconds:Int){
        this.timeBeginning = System.currentTimeMillis() - (hours * 3600 * 1000 + minutes * 60 * 1000 + seconds * 1000)
    }

}