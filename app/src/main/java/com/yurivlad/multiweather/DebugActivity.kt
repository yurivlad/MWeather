package com.yurivlad.multiweather

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.yurivlad.multiweather.core.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader


class DebugActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var logUpdateJob: Job
    private val workerScope = CoroutineScope(get<DispatchersProvider>().workerDispatcher)
    private val mainScope = CoroutineScope(get<DispatchersProvider>().mainDispatcher)

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = ""
        textView = findViewById(R.id.text)
        textView.movementMethod = ScrollingMovementMethod()
        logUpdateJob = workerScope.launch {
            while (true) {
                try {
                    val process =
                        Runtime.getRuntime().exec(arrayOf("logcat", "-d"))
                    val bufferedReader = BufferedReader(
                        InputStreamReader(process.inputStream)
                    )
                    val log = StringBuilder(10_000)
                    var line: String?

                    while (bufferedReader.readLine().also {
                            line = it
                        } != null) {
                        log.append(line)
                        log.append("\n")
                    }

                    getLogsFile().writeText(log.toString())

                    log.trimToSize()
                    mainScope.launch {
                        textView.setTextKeepState(log)
                        textView.run {
                            val scrollAmount =
                                textView.layout.getLineTop(textView.lineCount) - textView.height

                            if (scrollAmount > 0)
                                textView.scrollTo(0, scrollAmount)
                            else
                                textView.scrollTo(0, 0)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                delay(30_000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logUpdateJob.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.debug_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.send_logs) {
            val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, getLogsFile())

            val intent = Intent(Intent.ACTION_SEND).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "text/plain"
            }

            startActivity(intent)

            true

        } else super.onOptionsItemSelected(item)

    }

    private fun getLogsFile() = File(filesDir, "log.txt")
}
