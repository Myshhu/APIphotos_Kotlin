package com.example.apiphotos_kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ExpandableListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var photoInfoJSONArray: JSONArray
    //private lateinit var expandableListView: ExpandableListView
    private var expandableListAdapter: MyExpandableListAdapter = MyExpandableListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expandableListView.setAdapter(expandableListAdapter)

        downloadPhotoInfoJSONArray()
    }

    private fun downloadPhotoInfoJSONArray() {
        Thread {
            val connection: HttpURLConnection = createConnection()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                photoInfoJSONArray = createJSONArrayFromConnectionInputStream(connection)
                runOnUiThread { btnLoadDownloadedData.isEnabled = true }
            } else {
                runOnUiThread {
                    btnLoadDownloadedData.text = "Failed to connect - restart app"
                    makeToast("Failed to connect - restart app")
                }
            }
        }.start()
    }

    private fun createConnection(): HttpURLConnection {
        val connection: HttpURLConnection
        val url = URL("https://jsonplaceholder.typicode.com/photos")

        connection = url.openConnection() as HttpURLConnection
        connection.connect()
        return connection
    }

    private fun createJSONArrayFromConnectionInputStream(connection: HttpURLConnection): JSONArray {
        val connectionInputStream = connection.inputStream
        val bufferedReader = BufferedReader(InputStreamReader(connectionInputStream))
        val result = StringBuilder()
        bufferedReader.forEachLine {
            result.append(it).append("\n")
        }
        return JSONArray(result.toString())
    }

    private fun MainActivity.makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    fun btnLoadDownloadedDataClick(v: View) {
        expandableListAdapter.setPhotoInfoJSONArray(photoInfoJSONArray)
        expandableListAdapter.notifyDataSetChanged()
    }
}
