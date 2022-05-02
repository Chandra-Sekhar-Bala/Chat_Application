package com.chandra.chatapp.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chandra.chatapp.R

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        // See: https://developer.android.com/training/basics/intents/result

        supportActionBar?.hide()
    }
}