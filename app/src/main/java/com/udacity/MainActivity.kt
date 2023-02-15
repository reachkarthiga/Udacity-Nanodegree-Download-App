package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var link = ""
    private var title = ""
    private var description = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager =
            ContextCompat.getSystemService(this, NotificationManager::class.java)!!

        createNotificationChannel()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (validateLink()) {
                download()
            } else {
                radio_group.clearCheck()
            }
        }

    }

    private fun validateLink(): Boolean {

        when (radio_group.checkedRadioButtonId) {

            R.id.glide_button -> {
                link = GLIDE_URL
                title = "Glide"
                description = getString(R.string.glide_radio_button)
            }

            R.id.app_button -> {
                link = APP_URL
                title = "Udacity Nanodegree Project"
                description = getString(R.string.current_app)

            }

            R.id.retrofit_button -> {
                link = RETROFIT_URL
                title = "Retrofit"
                description = getString(R.string.retrofit_radio_button)
            }

            else -> {
                link = ""
            }
        }


        if (link.isNotEmpty() && url.text?.isEmpty() == false) {
            sendMsg("Choose only 1 option for downloading")
            return false
        }


        if (url.text?.isNotEmpty() == true) {
            try {
                val extension = MimeTypeMap.getFileExtensionFromUrl(url.text.toString())
                if (extension != null) {
                    val type =
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()
                    if (type != "application/zip") {
                        sendMsg("Enter a link with zipped file download format ")
                        return false
                    } else {
                        link = url.text.toString()
                        title = title_description.text.toString()
                        description = title
                    }
                }
            } catch (e: Exception) {
                sendMsg("Enter a valid link")
                return false
            }
        }

        if (link.isEmpty()) {
            sendMsg("Select at least 1 option for downloading")
            return false
        }

        return true

    }

    private fun sendMsg(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel =
                NotificationChannel(
                    CHANNEL_ID, "Downloads",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            notificationChannel.description = "Channel to show File Download Status"
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(link))
                .setTitle(title)
                .setDescription(description)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    System.currentTimeMillis().toString()
                )

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        val query = DownloadManager.Query()
        query.setFilterById(downloadID)
        val cursor = downloadManager.query(query)
        var resourceId: Int = 0

        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

            when (cursor.getInt(index)) {
                DownloadManager.STATUS_FAILED -> resourceId = R.drawable.failure
                DownloadManager.STATUS_PAUSED,
                DownloadManager.STATUS_RUNNING,
                DownloadManager.STATUS_PENDING -> resourceId = R.drawable.inprogress
                DownloadManager.STATUS_SUCCESSFUL -> resourceId = R.drawable.success
            }
        } else {
            resourceId = R.drawable.failure
        }

        notificationManager.cancelAll()
        notificationManager.sendNotification(
            "Check the Download Status",
            downloadID, resourceId, applicationContext
        )

    }

    companion object {
        private const val APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"

        const val CHANNEL_ID = "channelId"
    }

}


