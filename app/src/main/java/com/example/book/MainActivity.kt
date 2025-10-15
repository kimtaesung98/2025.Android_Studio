package com.example.book

//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.ImageView
//import android.widget.ToggleButton
//import androidx.core.view.WindowInsetsCompat
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.drawable.AnimationDrawable
import android.widget.Button
import android.widget.ImageView

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

//class MyDrawingview(context: Context, attrs: AttributeSet? = null) : View(context, attrs){
//
//    private val redPrint = Paint().apply {
//        color = Color.RED
//        style = Paint.Style.FILL
//    }
//
//    private val blueStrokePaint = Paint().apply{
//        color = Color.BLUE
//        style = Paint.Style.STROKE
//        strokeWidth = 10f
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        val width = width.toFloat()
//        val height = height.toFloat()
//
//        canvas.drawCircle(width / 4,height / 4, 100f,redPrint)
//        canvas.drawCircle(width * 3 / 4,height / 4, 100f,blueStrokePaint)
//        canvas.drawCircle(width - 100f,height -400f, height - 100f,blueStrokePaint)
//    }
//}

class MainActivity : AppCompatActivity() {
    private lateinit var animationDrawable: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // 1. ViewPager2에 어댑터 설정
        viewPager.adapter = ViewPagerAdapter(this)

        // 2. TabLayout과 ViewPager2를 연결
        val tabTitles = listOf("홈", "리스트", "설정")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val animationImageView: ImageView = findViewById(R.id.animationImageView)
        val startButton: Button = findViewById(R.id.startButton)
        val stopButton: Button = findViewById(R.id.stopButton)
        // ImageView의 배경으로 설정된 AnimationDrawable을 가져옵니다.
        // Drawable을 AnimationDrawable 타입으로 캐스팅해야 합니다.

        animationDrawable = animationImageView.background as AnimationDrawable

        // 시작 버튼 클릭 리스너
        startButton.setOnClickListener {
            // 애니메이션 시작
            animationDrawable.start()
        }

        // 정지 버튼 클릭 리스너
        stopButton.setOnClickListener {
            // 애니메이션 정지
            animationDrawable.stop()
        }
    }

    // 액티비티가 다시 시작될 때 애니메이션을 시작할 수도 있습니다. (선택 사항)
    override fun onResume() {
        super.onResume()
        // animationDrawable.start()
    }

    // 액티비티가 일시 정지될 때 애니메이션을 멈춰서 리소스 낭비를 막을 수 있습니다. (선택 사항)
    override fun onPause() {
        super.onPause()
        // animationDrawable.stop()
    }
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        val imageView: ImageView = findViewById(R.id.imageView)
//        val toggleButton: ToggleButton = findViewById(R.id.toggleButton)
//
//        toggleButton.setOnCheckedChangeListener { _, isChecked->
//            if(isChecked){
//                imageView.setImageResource(R.drawable.image_on)
//            }else{
//                imageView.setImageResource(R.drawable.image_off)
//            }
//        }
}
