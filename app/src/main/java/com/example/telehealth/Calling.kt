package com.example.telehealth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import java.util.Collections

class Calling : AppCompatActivity() {

    private lateinit var voiceCallButton: ZegoSendCallInvitationButton
    private lateinit var videoCallButton: ZegoSendCallInvitationButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calling)

        // Initialize buttons
        voiceCallButton = findViewById(R.id.voice_call_button)
        videoCallButton = findViewById(R.id.video_call_button)


        // Initialize Zego Call Service
        val config = ZegoUIKitPrebuiltCallInvitationConfig()
        ZegoUIKitPrebuiltCallService.init(application, AppConstants.appId, AppConstants.appSign, "Meet", "Meet", config)

        // Use the username from the Intent (assuming you pass the username)
        val username = intent.getStringExtra("USERNAME") ?: "Upul"

        // Set up call buttons
        setupVoiceCall(username)
        setupVideoCall(username)
    }

    private fun setupVoiceCall(username: String) {
        voiceCallButton.setIsVideoCall(false)
        voiceCallButton.resourceID = "zego_uikit_call" // Ensure this resource ID is correct
        voiceCallButton.setInvitees(Collections.singletonList(ZegoUIKitUser(username, username)))
    }

    private fun setupVideoCall(username: String) {
        videoCallButton.setIsVideoCall(true)
        videoCallButton.resourceID = "zego_uikit_call" // Ensure this resource ID is correct
        videoCallButton.setInvitees(Collections.singletonList(ZegoUIKitUser(username, username)))
    }
}