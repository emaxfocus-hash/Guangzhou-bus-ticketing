package com.guangzhou.busticketing

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MessagingService", "New FCM token: $token")
        // send token to backend
        DeviceRegistrar.register(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: "Guangzhou Bus"
        val body = message.notification?.body ?: "You have a new update"
        Log.d("MessagingService", "Received notification: $title - $body")
        // You could show a local notification here if desired
    }
}
