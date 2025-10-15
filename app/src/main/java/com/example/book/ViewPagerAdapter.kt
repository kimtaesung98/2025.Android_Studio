package com.example.book

// ViewPagerAdapter.kt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    // 페이지 개수
    override fun getItemCount(): Int = 3

    // 각 위치(position)에 어떤 프래그먼트를 보여줄지 결정
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ListFragment() // 우리가 만든 리스트 프래그먼트
//            2 -> SettingsFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}