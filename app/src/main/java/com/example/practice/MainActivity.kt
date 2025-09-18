package com.example.practice

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
import kotlin.*

class MainActivity : AppCompatActivity() {

    // UI 요소들을 클래스 레벨 변수로 선언하여 onCreate 및 다른 함수에서 접근 가능하도록 합니다.
    private lateinit var tv: TextView
    private lateinit var tv_t: TextView
    private lateinit var btn: Button
    private lateinit var tv_p: TextView
    private lateinit var tv_ps: TextView

    private var timerTask: Timer? = null
    private var sec: Int = 0
    private var stage = 1
    private var point_list = MutableListOf<Float>()
    private var person = 1
    private var num: Int = 0 // num도 클래스 레벨 변수로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // setContentView를 가장 먼저 호출하는 것이 일반적입니다.

        // UI 요소 초기화
        tv = findViewById(R.id.tv_random)
        tv_t = findViewById(R.id.tv_timer)
        btn = findViewById(R.id.btn_main)
        tv_p = findViewById(R.id.tv_point)
        tv_ps = findViewById(R.id.tv_person)

        val random_box = Random()
        num = random_box.nextInt(1001) // num 초기화

        tv.text = (num.toFloat() / 100).toString()
        btn.text = "시작"

        btn.setOnClickListener {
            stage++
            tv_ps.text = "참가자 $person"
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
                tv_p.text = point.toString()
                btn.text = "다음으로 "
                person ++

                point_list.add(point)
                // stage를 다시 1로 리셋하거나, 다음 단계로 넘어가는 로직을 추가할 수 있습니다.
                // 예: stage = 1; sec = 0; num = random_box.nextInt(1001); tv.text = (num.toFloat() / 100).toString(); tv_t.text = "0.0"; tv_p.text = ""; btn.text = "시작";
            } else if (stage > 3) { // "다음으로" 버튼 클릭 후 초기화 (예시)
                stage = 1
                sec = 0
                num = random_box.nextInt(1001)
                tv.text = (num.toFloat() / 100).toString()
                tv_t.text = "0.0" // 타이머 텍스트 초기화
                tv_p.text = ""    // 포인트 텍스트 초기화
                btn.text = "시작"
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
