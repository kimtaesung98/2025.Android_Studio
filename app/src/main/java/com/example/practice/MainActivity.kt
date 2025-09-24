package com.example.practice

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Random
import java.util.Timer // Timer를 사용하려면 java.util.Timer를 임포트해야 합니다.
import kotlin.concurrent.timer // kotlin.concurrent.timer를 사용하려면 임포트해야 합니다.
import kotlin.math.abs
import kotlin.collections.*

class MainActivity : AppCompatActivity() {
    var p_num = 3
    var k = 1
    val point_list = mutableListOf<Float>()

    var isBlind = false
    fun start() {
        setContentView(R.layout.activity_start)
        val tv_pnum: TextView = findViewById(R.id.tv_pnum)
        val btn_plus: Button = findViewById(R.id.btn_plus)
        val btn_minus: Button = findViewById(R.id.btn_minus)
        val btn_start: Button = findViewById(R.id.btn_start)
        val btn_Blind: Button = findViewById(R.id.btn_i)

        btn_Blind.setOnClickListener {
            isBlind = !isBlind
            if(isBlind == true){
                btn_Blind.text = "BLIND모드 ON"
            }else{
                btn_Blind.text = "BLIND모드 OFF"
            }
        }

        tv_pnum.text = p_num.toString()

        btn_minus.setOnClickListener {
            p_num--
            if (p_num == 0) {
                p_num = 1
            }
            tv_pnum.text = p_num.toString()
        }

        btn_plus.setOnClickListener {
            p_num++
            tv_pnum.text = p_num.toString()
        }
        btn_start.setOnClickListener {
            main()
        }

    }

    fun main() {
        setContentView(R.layout.activity_main) // setContentView를 가장 먼저 호출하는 것이 일반적입니다.

        // UI 요소들을 클래스 레벨 변수로 선언하여 onCreate 및 다른 함수에서 접근 가능하도록 합니다.
        var timerTask: Timer? = null
        var sec: Int = 0
        var stage = 1
        val tv: TextView = findViewById(R.id.tv_pnum)
        val tv_t: TextView = findViewById(R.id.tv_timer)
        val btn: Button = findViewById(R.id.btn_Blind2)
        val tv_p: TextView = findViewById(R.id.tv_point)
        val tv_people: TextView = findViewById(R.id.tv_people)
        val btn_i: TextView = findViewById(R.id.btn_i)
        val random_box = Random()
        val num = random_box.nextInt(1001) // num 초기화
        val bg_main : ConstraintLayout = findViewById(R.id.bg_main)
        val color_list = mutableListOf<String>("#CEF0A3","#CBF498","#C6F889","#BDF675","#B9F86B","#B0F45C","#ADF654","#ADF654")
        var color_index = k % 8 - 1
        if(color_index == -1){
            color_index = k
        }
        val color_sel = color_list.get(color_index)
        bg_main.setBackgroundColor(Color.parseColor(color_sel))

        tv.text = (num.toFloat() / 100).toString()
        btn.text = "시작"
        tv_people.text = "참가자 $k"

        btn_i.setOnClickListener {
            point_list.clear()
            k = 1
            start()
        }

        btn.setOnClickListener {
            stage++
            if (stage == 2) {
                timerTask = timer(period = 10) { // kotlin.concurrent.timer 사용
                    sec++
                    runOnUiThread {
                        if (isBlind == false) {
                            tv_t.text = (sec.toFloat() / 100).toString()
                        } else if (isBlind == true && stage == 2) {
                            tv_t.text = "???"
                        }
                    }
                }
                btn.text = "정지"
            } else if (stage == 3) {
                tv_t.text = (sec.toFloat() / 100).toString()

                timerTask?.cancel()
                val point = abs(sec - num).toFloat() / 100
                point_list.add(point)
                tv_p.text = point.toString()
                btn.text = "다음으로 "
                stage = 0
            } else if (stage == 1) { // "다음으로" 버튼 클릭 후 초기화 (예시)
                if (k < p_num) {
                    k++
                    main()
                } else {
                    end()
                }
            }
        }
    }

    fun end() {
        setContentView(R.layout.activity_end)
        val btn_init: Button = findViewById(R.id.btn_init)
        val tv_last: TextView = findViewById(R.id.tv_last)
        val tv_lpoint: TextView = findViewById(R.id.tv_lpoint)

        tv_lpoint.text = point_list.maxOrNull().toString()
        var index_last = point_list.indexOf(point_list.maxOrNull())
        tv_last.text = "참가자 " + (index_last + 1).toString()

        btn_init.setOnClickListener {
            point_list.clear()
            k = 1
            start()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        start()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bg_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
