package com.example.soundmap.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // 알림을 보내기 위한 코드 작성
        val context = applicationContext
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 생성 (Android 8.0 이상에서 필요)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "도착 알림 채널"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("도착 알림")
            .setContentText("목적지 도착 5분전입니다.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // 알림 표시
        notificationManager.notify(1, builder.build())

        // 작업이 성공적으로 완료되었음을 알림
        return Result.success()
    }

}