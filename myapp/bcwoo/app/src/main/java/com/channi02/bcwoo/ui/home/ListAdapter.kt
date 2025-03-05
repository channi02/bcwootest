package com.channi02.bcwoo.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.channi02.bcwoo.R

class ListAdapter(
    private val items: MutableList<String>,
    private val onItemClick: (Int) -> Unit // ✅ 클릭 이벤트 추가
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var selectedPosition: Int = -1 // 현재 선택된 아이템 위치

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]

        // 선택한 아이템의 배경색 변경
        if (position == selectedPosition) {
            holder.textView.setBackgroundResource(R.color.teal_200) // 선택된 상태
        } else {
            holder.textView.setBackgroundResource(android.R.color.transparent) // 기본 상태
        }

        // ✅ 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            selectedPosition = position
            onItemClick(position) // 콜백 실행
            notifyDataSetChanged() // UI 갱신
        }
    }

    override fun getItemCount(): Int = items.size
}
