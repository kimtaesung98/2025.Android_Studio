package com.example.book

// ListFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SettingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // fragment_list.xml을 화면으로 사용
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // 1. 표시할 데이터 생성
        val userList = listOf(
            User("홍길동", "hong@example.com"),
            User("김철수", "kim@example.com"),
            User("이영희", "lee@example.com")
            // ... 더 많은 데이터 추가
        )

        // 2. 어댑터와 레이아웃 매니저 설정
        recyclerView.adapter = UserAdapter(userList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }
}