package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import java.io.File
import kotlin.properties.Delegates


class DetailActivity : AppCompatActivity() {

    private var id by Delegates.notNull<Long>()
    private lateinit var fileTitle: String
    private var downloadStatus by Delegates.notNull<Int>()
    private lateinit var fileLocation: String
    private lateinit var fileMediaType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        id = intent.extras?.getLong(DOWNLOAD_ID) ?: 0
        queryDownloadManager(id)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

    }

    fun queryDownloadManager(id: Long) {

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterById(id)
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            if (DownloadManager.STATUS_FAILED == cursor.getInt(index)) {
                open_file.isEnabled = false
                image.setImageResource(R.drawable.failure)
                downloadStatus = DownloadManager.STATUS_FAILED
                fileLocation = " "
                fileTitle =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
            }

            if (DownloadManager.STATUS_PAUSED == cursor.getInt(index)) {
                open_file.isEnabled = false
                image.setImageResource(R.drawable.inprogress)
                downloadStatus = DownloadManager.STATUS_PAUSED
                fileLocation = " "
                fileTitle =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
            }

            if (DownloadManager.STATUS_PENDING == cursor.getInt(index)) {
                open_file.isEnabled = false
                image.setImageResource(R.drawable.inprogress)
                downloadStatus = DownloadManager.STATUS_PENDING
                fileLocation =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                fileTitle =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
            }

            if (DownloadManager.STATUS_RUNNING == cursor.getInt(index)) {
                open_file.isEnabled = false
                image.setImageResource(R.drawable.inprogress)
                downloadStatus =DownloadManager.STATUS_RUNNING
                fileLocation =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                fileTitle =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
            }

            if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(index)) {
                open_file.isEnabled = true
                image.setImageResource(R.drawable.success)
                downloadStatus =DownloadManager.STATUS_SUCCESSFUL
                fileLocation =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                fileTitle =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
                fileMediaType =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))

            }

            status.text = when (downloadStatus) {
                DownloadManager.STATUS_RUNNING -> "Running"
                DownloadManager.STATUS_PENDING -> "Pending"
                DownloadManager.STATUS_SUCCESSFUL -> "Complete"
                DownloadManager.STATUS_FAILED -> "Failure"
                DownloadManager.STATUS_PAUSED -> "Paused"
                else -> {"Error"}
            }
            file_location.text = fileLocation
            file_name.text = fileTitle

        } else {

            image.setImageResource(R.drawable.failure)

        }

    }

    fun toMainPage(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun fileOpen(view: View) {

        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
            val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
            startActivity(intent)
        }
    }

    fun mailDetails(view: View) {

        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = fileMediaType
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION )
                putExtra(Intent.EXTRA_SUBJECT, "Udacity Project Download")
                putExtra(Intent.EXTRA_TEXT, "File Name : $fileTitle")
               putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(applicationContext,
                   applicationContext.packageName + ".provider", File(fileLocation)))
            }

            startActivity(intent)
        }

    }

    fun refreshData(view: View) {
        queryDownloadManager(id)
    }

}
