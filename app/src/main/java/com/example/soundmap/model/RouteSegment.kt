package com.example.soundmap.model

/**
 * `RouteSegment` 클래스
 * - 특정 경로 구간에 대한 정보를 저장하는 데이터 클래스.
 * - 경로 알고리즘과 연결하여 노선 구간별 정보를 처리하는 데 사용됩니다.
 *
 * @property lineType 노선 이름 (예: "1호선", "2호선", 등)
 * @property stationCount 노선에 포함된 역 개수
 * @property stations 노선에 포함된 각 역의 이름을 순서대로 저장한 리스트
 * @property lineColor 노선의 색상 (16진수 색상 코드 형식: 예 "#FF0000" - 빨강)
 */
data class RouteSegment(
    val lineType: String,        // 노선 이름, 경로를 구분하는 이름 (예: "1호선")
    val stationCount: Int,       // 이 노선에 포함된 역의 개수
    val stations: List<String>,  // 역 이름 리스트 (경로의 역 이름을 순서대로 나열)
    val lineColor: String        // 이 노선을 나타내는 색상 코드 (UI에서 사용)
)
