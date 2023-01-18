package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.renderscript.RenderScript
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 0
const val PENDING_INTENT_REQUEST_CODE = 1
const val DOWNLOAD_ID = "download_id"


fun NotificationManager.sendNotification(msg: String, id: Long, resourceId: Int, context: Context) {

    val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText(msg)
        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, resourceId))

    val contentIntent = Intent(context, DetailActivity::class.java)
    contentIntent.putExtra(DOWNLOAD_ID, id)

    val pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    builder.addAction(R.drawable.ic_assistant_black_24dp, "OPEN APP" , pendingIntent )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    builder.setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())

}