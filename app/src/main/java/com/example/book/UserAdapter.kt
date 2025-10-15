package com.example.book
// UserAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val userList: List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // 뷰홀더: 화면에 표시될 아이템 뷰를 저장하는 객체
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
    }

    // 뷰홀더를 생성하고 뷰를 붙여주는 부분
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    // 뷰홀더에 데이터를 바인딩(연결)하는 부분
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.nameTextView.text = user.name
        holder.emailTextView.text = user.email
    }

    // 리스트의 총 아이템 개수
    override fun getItemCount() = userList.size
}