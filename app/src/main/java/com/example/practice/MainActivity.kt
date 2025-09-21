package com.example.practice

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import java.util.Random
import java.util.Timer // Timer를 사용하려면 java.util.Timer를 임포트해야 합니다.
import kotlin.concurrent.timer // kotlin.concurrent.timer를 사용하려면 임포트해야 합니다.
import kotlin.math.abs
import kotlin.collections.*
import kotlin.collections.minus
import kotlin.inc
import kotlin.text.toFloat

class MainActivity : AppCompatActivity() {

    var p_num = 3
    var k = 1
    val point_list = mutableListOf<Float>()

    fun main() {
        setContentView(R.layout.activity_main) // setContentView를 가장 먼저 호출하는 것이 일반적입니다.

        // UI 요소들을 클래스 레벨 변수로 선언하여 onCreate 및 다른 함수에서 접근 가능하도록 합니다.
        var timerTask: Timer? = null
        var sec: Int = 0
        var stage = 1
        val tv: TextView = findViewById(R.id.tv_random)
        val tv_t: TextView = findViewById(R.id.tv_timer)
        val btn: Button = findViewById(R.id.btn_main)
        val tv_p: TextView = findViewById(R.id.tv_point)
        val tv_people: TextView = findViewById(R.id.tv_people)
        val random_box = Random()
        val num = random_box.nextInt(1001) // num 초기화

        tv.text = (num.toFloat() / 100).toString()
        btn.text = "시작"
        tv_people.text = "참가자 $k"

        btn.setOnClickListener {
            stage++
            if (stage == 2) {
                timerTask = timer(period = 10) { // kotlin.concurrent.timer 사용
                    sec++
                    runOnUiThread {
                        tv_t.text = (sec.toFloat() / 100).toString()
                    }
                }
                btn.text = "정지"
            } else if (stage == 3) {
                timerTask?.cancel()
                val point = abs(sec - num).toFloat() / 100
                point_list.add(point)
                tv_p.text = point.toString()
                btn.text = "다음으로 "
                stage = 0
            } else if (stage == 1) { // "다음으로" 버튼 클릭 후 초기화 (예시)
                if(k < p_num){
                    k++
                    main()
                }
            } else {
                println(point_list)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        main()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
