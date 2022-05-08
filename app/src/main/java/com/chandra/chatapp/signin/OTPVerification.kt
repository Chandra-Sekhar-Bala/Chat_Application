package com.chandra.chatapp.signin

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.chandra.chatapp.MainActivity
import com.chandra.chatapp.R
import com.chandra.chatapp.databinding.ActivityOtpverificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OTPVerification : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var binding: ActivityOtpverificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
    }


    override fun onResume() {
        super.onResume()

        keyPressedListener()
        isValidCode()

        val id = intent.getStringExtra("id")

        binding.submitBtn.setOnClickListener{
            val smsCode = binding.code1.text.toString().trim() +
                    binding.code2.text.toString().trim() +
                    binding.code3.text.toString().trim() +
                    binding.code4.text.toString().trim() +
                    binding.code5.text.toString().trim() +
                    binding.code6.text.toString().trim ()


            val credential = PhoneAuthProvider.getCredential(id!!, smsCode)
            signInWithPhoneAuthCredential(credential)
        }


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    startActivity(Intent(this@OTPVerification, MainActivity::class.java))
                    Toast.makeText(this@OTPVerification,"Success", Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }
                    // Update UI
                    Toast.makeText(this@OTPVerification,"Falied", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isValidCode() {

        if( binding.code1.text.toString().trim().isEmpty() ||
            binding.code2.text.toString().trim().isEmpty() ||
            binding.code3.text.toString().trim().isEmpty() ||
            binding.code4.text.toString().trim().isEmpty() ||
            binding.code5.text.toString().trim().isEmpty() ||
            binding.code6.text.toString().trim().isEmpty()  )
        {
//            binding.submitBtn.isEnabled = false
            binding.submitBtn.setBackgroundResource(R.drawable.submit_btn_disabled)

        }
        else{

//            binding.submitBtn.isEnabled = true
            binding.submitBtn.setBackgroundResource(R.drawable.submit_btn_background)
        }

    }


    private fun keyPressedListener() {

        binding.code1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.code2.requestFocus()

            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.code2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.code3.requestFocus()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.code3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.code4.requestFocus()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.code4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.code5.requestFocus()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })


        binding.code5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.code6.requestFocus()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })



    }

}