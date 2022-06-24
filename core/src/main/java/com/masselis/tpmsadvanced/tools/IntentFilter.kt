package com.masselis.tpmsadvanced.tools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.masselis.tpmsadvanced.interfaces.appContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

fun IntentFilter.asFlow() = callbackFlow {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            launch { send(intent) }
        }
    }
    appContext.registerReceiver(receiver, this@asFlow)
    awaitClose {
        try {
            appContext.unregisterReceiver(receiver)
        } catch (e: Exception) {
            // Calling unregisterReceiver with a receiver already unregistered throws IllegalArgumentException. Everything is fine, fired exception doesn't need to be forwarded to the downstream so I catch it and I do nothing else.
        }
    }
}