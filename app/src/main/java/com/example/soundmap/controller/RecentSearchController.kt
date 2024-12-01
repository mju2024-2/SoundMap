package com.example.soundmap.controller

import android.content.Context

class RecentSearchController(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("recentSearches", Context.MODE_PRIVATE)

    // 검색 기록 추가
    fun addRecentSearch(start: String, end: String) {
        val recentSearches = sharedPreferences.getStringSet("searches", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        // 새 검색 기록 추가
        recentSearches.add("$start → $end")

        // 최근 검색 기록이 4개를 초과하면 가장 오래된 기록 제거
        while (recentSearches.size > 4) {
            val iterator = recentSearches.iterator()
            if (iterator.hasNext()) {
                iterator.next() // 가장 오래된 데이터 가져오기
                iterator.remove() // 가장 오래된 데이터 삭제
            }
        }

        // 저장
        sharedPreferences.edit().putStringSet("searches", recentSearches).apply()
    }

    // 검색 기록 불러오기 (최대 4개)
    fun getRecentSearches(): List<String> {
        val recentSearches = sharedPreferences.getStringSet("searches", emptySet())?.toList() ?: emptyList()

        // 최근 검색 기록을 최대 4개까지만 반환
        return recentSearches.takeLast(4) // 최신 4개를 반환
    }

    // 검색 기록 초기화
    fun resetRecentSearches() {
        sharedPreferences.edit().remove("searches").apply()
    }
}
