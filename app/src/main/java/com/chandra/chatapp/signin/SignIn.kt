package com.chandra.chatapp.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.chandra.chatapp.R
import com.chandra.chatapp.databinding.ActivitySigninBinding

class SignIn : AppCompatActivity() {

    lateinit var binding : ActivitySigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // See: https://developer.android.com/training/basics/intents/result
        supportActionBar?.hide()

        binding.btnGoogle.setOnClickListener{
            startActivity(Intent(this,OTPVerification::class.java))
        }

        val ttb = AnimationUtils.loadAnimation(this,R.anim.scale)
        val ltr = AnimationUtils.loadAnimation(this,R.anim.ltr)
        val rtl = AnimationUtils.loadAnimation(this,R.anim.rtl)
        binding.img.startAnimation(ttb)
        binding.btnGoogle.startAnimation(rtl)
        binding.btnEmail.startAnimation(ltr)
        binding.btnPhone.startAnimation(rtl)


    }
}