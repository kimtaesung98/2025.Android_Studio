package com.example.practice

import android.os.Bundle
import android.widget.Button // Keep if you plan to use it later
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView // Keep if you plan to use it later
import java.util.Random
import java.util.Timer
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            var timerTask: Timer? = null
            var isRunning = false
            val tv: TextView = findViewById(R.id.tv_random)
            val tv_t: TextView = findViewById(R.id.tv_timer)
            val btn: Button = findViewById(R.id.btn_main)
            var sec : Int = 0
            val tv_p: TextView = findViewById(R.id.tv_point)

                val random_box = Random()
                val num = random_box.nextInt(1001)
                tv.text = (num.toFloat() / 100).toString()


            btn.setOnClickListener{
                isRunning = !isRunning
                if(isRunning == true){
                    timerTask = kotlin.concurrent.timer(period = 10) { // This creates a Timer object
                        sec++
                        runOnUiThread {
                            tv_t.text = (sec.toFloat() / 100).toString()
                        }
                    }
                }else{
                    timerTask?.cancel()

                    val point = abs(sec - num).toFloat() / 100
                    tv_p.text = point.toString()
                }
            }
             //Ensure you return the insets object
            insets // This is the required return value
        }
    }
}