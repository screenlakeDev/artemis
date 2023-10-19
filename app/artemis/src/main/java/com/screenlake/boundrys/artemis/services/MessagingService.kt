package com.screenlake.boundrys.artemis.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.WordTopic
import com.screenlake.boundrys.artemis.repository.GenOp
import timber.log.Timber

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        GenOp(this)
        // Handle the received message here
        val topic = remoteMessage.data["topic"]
        Timber.tag("FCM").d(topic.toString())

        val typeToken = object : TypeToken<List<WordTopic>>() {}.type
        val wordTopics = Gson().fromJson<List<WordTopic>>(topic, typeToken)

        // You can process the message and take appropriate actions
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Timber.d( "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        // sendRegistrationToServer(token)
    }
}