package com.cityflowdemo

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.cityflowdemo.model.Transaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val listOfTransactions = MutableLiveData<ArrayList<Transaction>>()
    private val list = ArrayList<Transaction>()
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = Request.Builder()
            .url("wss://ws.blockchain.info/inv")
            .build()

        // Connection between Websocket API and subscribing channel Data
        val webSocket = OkHttpClient().newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.e(TAG, "Connection Open")
                handler.post {
                    connection_status.text = "Connecting"
                }
                webSocket.send("{\"op\":\"unconfirmed_sub\"}")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.e(TAG, "MEssage Received Bytes")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.e(TAG, "Mesage Received String $text")
                handler.post {
                    connection_status.text = "Connected"
                }
                GlobalScope.launch {
                    getResponse(text)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e(TAG, "Connection Failure ${response.toString()}")
                Log.e(TAG, t.message.toString())
                handler.post {
                    connection_status.text = "Disconnected"
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.e(TAG, "Connection Closed")
            }
        })

        setupLitView()

        // Clear Logs
        clear.setOnClickListener {
            list.clear()
            listOfTransactions.postValue(list)
        }


    }

    // Setup Recyclerview with live data.
    private fun setupLitView() {
        transaction_list.layoutManager = LinearLayoutManager(this)
        val adapter = TransactionAdapter()
        transaction_list.adapter = adapter
        listOfTransactions.observeForever {
            it?.let {
                adapter.setList(it)
            }

        }
    }

    // Taking response and getting response Asynchronously.
    suspend fun getResponse(response: String) {
        val transaction = Transaction()
        val json = JSONObject(response)
        val xObject = json.getJSONObject("x")
        transaction.time = xObject.getLong("time")
        transaction.timeInDisplay = convertLongToTime(transaction.time!!)
        transaction.hash = xObject.getString("hash")
        val inputsArray = xObject.getJSONArray("inputs")
        var value: Long = 0
        for (i in 0 until inputsArray.length()) {
            val iObject = inputsArray.getJSONObject(i)
            val preObject = iObject.getJSONObject("prev_out")
            value += preObject.getLong("value")
        }
        transaction.value = value
        transaction.amount = (value / 8861).toDouble()
        if (transaction.value!! > 890000) {
            //hardcoded.
            list.add(0, transaction)
            listOfTransactions.postValue(list)
        }
    }

    // Converting Long to Display format time
    private fun convertLongToTime(time: Long): String {
        val date = Date(time * 1000L)
        val format = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH)
        return format.format(date)
    }

    
}
