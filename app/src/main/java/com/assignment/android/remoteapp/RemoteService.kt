package com.assignment.android.remoteapp

import android.app.Service
import android.content.Intent
import android.os.*
import com.assignment.android.remoteapp.Constants.METHOD_TYPE
import com.assignment.android.remoteapp.Constants.REMOTE_URL
import com.assignment.android.remoteapp.Constants.TIMEOUT_DURATION
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class RemoteService : Service() {

    private val TAG = RemoteService::class.java.simpleName

    private var messenger: Messenger? = null


    override fun onBind(p0: Intent?): IBinder? {

        if (this.messenger == null) {
            synchronized(RemoteService::class.java) {
                if (this.messenger == null) {
                    this.messenger = Messenger(ClientRequestHandler())
                }
            }
        }

        //Return the proper IBinder instance
        return this.messenger?.binder
    }

    private inner class ClientRequestHandler : Handler() {

        override fun handleMessage(message: Message) {
            val data = message.data

            // Make server call and return the response
            ServerRequestTask(message.replyTo).execute(
                REMOTE_URL,
                data.getString(getString(R.string.key_input))
            )
        }
    }

    private inner class ServerRequestTask(
        private val replyTo: Messenger
    ) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg strings: String): String {

            val url = URL(strings[0])
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = TIMEOUT_DURATION
            urlConnection.connectTimeout = TIMEOUT_DURATION
            urlConnection.requestMethod = METHOD_TYPE
            urlConnection.doInput = true
            urlConnection.doOutput = true
            urlConnection.setRequestProperty("Content-Type", "application/json")

            val jsonData = JSONObject()
            jsonData.put("data", strings[1])
            val postData = jsonData.toString()

            val outputStream = urlConnection.outputStream
            outputStream.write(postData.toByteArray())

            var result = ""
            val responseCode = urlConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(urlConnection.inputStream))
                while (true) {
                    val line = br.readLine() ?: break;
                    result += line
                }
            }

            if (result.isNotEmpty()) {
                val resData = JSONObject(result)
                val jsonResult = resData.getJSONObject("json")
                return jsonResult.getString("data")
            }

            return "No Response!"
        }

        override fun onPostExecute(result: String) {

            //Prepare the response object
            val data = Bundle()
            data.putString(getString(R.string.key_result), result)

            try {
                val response = Message()

                //Set the response data
                response.data = data

                //Send the response
                replyTo.send(response)

            } catch (exc: RemoteException) {
                exc.printStackTrace()
            }
        }
    }
}

object Constants {
    const val REMOTE_URL = "https://postman-echo.com/post"
    const val METHOD_TYPE = "POST"
    const val TIMEOUT_DURATION = 30000
}