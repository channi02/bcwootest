package com.channi02.bcwoo.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.channi02.bcwoo.databinding.FragmentHomeBinding
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val itemList = mutableListOf<String>() // 원본 리스트 데이터
    private val filteredList = mutableListOf<String>() // 검색된 데이터 리스트
    private lateinit var adapter: ListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var selectedPosition: Int = -1 // 선택한 아이템 위치 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // RecyclerView 설정
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true // 🚀 항상 리스트의 끝부분이 보이도록 설정
        binding.recyclerView.layoutManager = layoutManager
        // 기존 데이터 불러오기
        loadData()

        adapter = ListAdapter(filteredList) { position ->
            selectedPosition = position // 선택한 항목 위치 저장
        }
        binding.recyclerView.adapter = adapter

        // 등록 버튼 클릭 이벤트
        binding.buttonAdd.setOnClickListener {
            val text = binding.editText.text.toString().trim()
            if (text.isNotEmpty()) {
                val formattedText = "[${getCurrentTime()}] $text" // 🚀 시간 추가된 텍스트
                itemList.add(formattedText) // 리스트에 추가
                updateFilteredList()// 검색 리스트에도 추가
                adapter.notifyItemInserted(itemList.size - 1) // UI 갱신

                saveData() // 데이터 저장

                // 🚀 추가된 아이템으로 부드럽게 스크롤
                binding.recyclerView.postDelayed({
                    binding.recyclerView.smoothScrollToPosition(itemList.size - 1)
                }, 100)

                binding.editText.text.clear() // 입력창 초기화
            } else {
                Toast.makeText(requireContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 검색 버튼 클릭 이벤트
        binding.buttonSearch.setOnClickListener {
            val query = binding.editText.text.toString().trim()
            filterList(query);

        }

        // 삭제 버튼 클릭 이벤트
        binding.buttonDelete.setOnClickListener {
            if (selectedPosition >= 0 && selectedPosition < filteredList.size) {
                val itemToRemove = filteredList[selectedPosition]
                itemList.remove(itemToRemove) // 원본 리스트에서 삭제
                updateFilteredList() // 검색 리스트 업데이트 후 RecyclerView 갱신
                saveData() // 삭제 후 데이터 저장
                selectedPosition = -1 // 선택 초기화
            } else {
                Toast.makeText(requireContext(), "삭제할 항목을 선택하세요", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }
    // ✅ 리스트 업데이트 함수
    private fun updateFilteredList() {
        filteredList.clear()
        filteredList.addAll(itemList)
        adapter.notifyDataSetChanged()
    }

    // ✅ 검색 기능
    private fun filterList(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(itemList) // 검색어 없으면 전체 리스트 표시
        } else {
            filteredList.addAll(itemList.filter { it.contains(query, ignoreCase = true) })
        }
        adapter.notifyDataSetChanged() // 변경사항 적용
    }

    // 🚀 현재 시간 가져오기 (월/일(요일) 시:분)
    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM/dd(E) HH:mm", Locale.KOREAN)
        val dateString = dateFormat.format(calendar.time)

        // 요일을 한글로 변환
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }

        return dateString.replace(Regex("\\((.)\\)"), "($dayOfWeek)") // 요일을 한글로 변환
    }

    // 🚀 SharedPreferences를 사용하여 데이터 저장
    private fun saveData() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 리스트를 JSON 배열 형태로 변환하여 저장
        val jsonArray = JSONArray(itemList)
        editor.putString("itemList", jsonArray.toString())
        editor.apply()
    }

    // 🚀 SharedPreferences에서 데이터 불러오기
    private fun loadData() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("itemList", null)

        if (jsonString != null) {
            val jsonArray = JSONArray(jsonString)

            itemList.clear() // ✅ 기존 리스트 초기화 (중복 방지)
            filteredList.clear() // ✅ 필터 리스트도 초기화

            for (i in 0 until jsonArray.length()) {
                itemList.add(jsonArray.getString(i)) // 데이터 복원
            }

            updateFilteredList() // ✅ RecyclerView 갱신
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
