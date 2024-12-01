package com.example.soundmap.model

import android.os.Parcel
import android.os.Parcelable

/**
 * `RouteDetails` 클래스
 * - 특정 경로에 대한 상세 정보를 저장하는 데이터 클래스.
 * - Parcelable 인터페이스를 구현하여 객체를 직렬화/역직렬화할 수 있음 (Intent로 전달 가능).
 *
 * @property title 경로 제목 (예: "최단 경로", "최소 비용" 등)
 * @property travelTime 이동 시간 (예: "22분")
 * @property startingTime 출발 시각 (예: "오전 10:30 출발")
 * @property cost 경로 비용 (예: "1500원")
 * @property routeType 경로 유형 (예: "1호선", "2호선" 등 노선 정보)
 * @property routeDetails 경로에 포함된 주요 역 정보 (예: "수원역 / 고색역 / 망포역")
 * @property stationCount 경로에 포함된 역 개수 (예: "8개 역")
 * @property totalDistance 경로의 총 거리 (예: "총 거리: 10.8km")
 * @property arrivalTime 도착 시각 (예: "오전 11:00")
 * @property travelDuration 소요 시간 (예: "30분")
 * @property departureTime 출발 시각 (예: "오전 10:30")
 */
data class RouteDetails(
    val title: String,           // 경로 제목
    val travelTime: String,      // 이동 시간
    val startingTime: String,    // 출발 시각
    val cost: String,            // 경로 비용
    val routeType: String,       // 경로 유형 또는 노선 정보
    val routeDetails: String,    // 주요 역 정보
    val stationCount: String,    // 경로에 포함된 역 개수
    val totalDistance: String,   // 경로 총 거리
    val arrivalTime: String,     // 도착 시각
    val travelDuration: String,  // 소요 시간
    val departureTime: String    // 출발 시각
) : Parcelable {

    /**
     * Parcelable 생성자를 통해 `Parcel` 객체에서 데이터를 읽어와 `RouteDetails` 객체를 만듭니다.
     */
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "", // 경로 제목
        parcel.readString() ?: "", // 이동 시간
        parcel.readString() ?: "", // 출발 시각
        parcel.readString() ?: "", // 경로 비용
        parcel.readString() ?: "", // 경로 유형
        parcel.readString() ?: "", // 주요 역 정보
        parcel.readString() ?: "", // 역 개수
        parcel.readString() ?: "", // 총 거리
        parcel.readString() ?: "", // 도착 시각
        parcel.readString() ?: "", // 소요 시간
        parcel.readString() ?: ""  // 출발 시각
    )

    /**
     * 데이터를 Parcel 객체에 쓰는 메서드
     * - 객체의 각 필드를 Parcel 객체에 순서대로 직렬화합니다.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(travelTime)
        parcel.writeString(startingTime)
        parcel.writeString(cost)
        parcel.writeString(routeType)
        parcel.writeString(routeDetails)
        parcel.writeString(stationCount)
        parcel.writeString(totalDistance)
        parcel.writeString(arrivalTime)
        parcel.writeString(travelDuration)
        parcel.writeString(departureTime)
    }

    /**
     * Parcel 인터페이스의 기본 메서드
     * - 특별한 경우가 없으므로 0을 반환.
     */
    override fun describeContents(): Int = 0

    /**
     * Companion Object: Parcelable.Creator 구현
     * - Parcelable 인터페이스를 구현하는 클래스에 필요한 메서드.
     * - `createFromParcel`: Parcel 객체에서 RouteDetails 객체를 생성.
     * - `newArray`: RouteDetails 객체 배열을 생성.
     */
    companion object CREATOR : Parcelable.Creator<RouteDetails> {
        override fun createFromParcel(parcel: Parcel): RouteDetails {
            return RouteDetails(parcel)
        }

        override fun newArray(size: Int): Array<RouteDetails?> {
            return arrayOfNulls(size)
        }
    }
}
