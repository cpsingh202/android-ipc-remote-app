package com.assignment.android.remoteapp

import android.app.Service
import android.content.Intent
import android.os.*

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
            val what = message.what

            //Send the response
            val response = Message.obtain(null, 2, 0, 0)
            try {
                val replyTo = message.replyTo
                replyTo.send(response)
            } catch (exc: RemoteException) {
                exc.printStackTrace()
            }
        }
    }
}