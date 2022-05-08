package com.chandra.chatapp.signin

import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.util.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.chandra.chatapp.R
import com.chandra.chatapp.databinding.ActivitySigninBinding
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import org.w3c.dom.Text
import javax.xml.datatype.DatatypeConstants.SECONDS

class SignIn : AppCompatActivity() {

    private var item = 1
    private lateinit var ttb : Animation
    private lateinit var ltr : Animation
    private lateinit var rtl : Animation

    private lateinit var auth : FirebaseAuth
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks


    lateinit var binding : ActivitySigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // See: https://developer.android.com/training/basics/intents/result
        supportActionBar?.hide()
        ttb = AnimationUtils.loadAnimation(this,R.anim.scale)
        ltr = AnimationUtils.loadAnimation(this,R.anim.ltr)
        rtl = AnimationUtils.loadAnimation(this,R.anim.rtl)

        binding.submitBtn.isEnabled = false
        playAnimation()

        auth = FirebaseAuth.getInstance()


        binding.ccp.registerCarrierNumberEditText(binding.edtPhoneNumber)

        binding.edtPhoneNumber.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.ccp.isValidFullNumber){
                    binding.submitBtn.isEnabled = true

                    binding.submitBtn.setBackgroundResource(R.drawable.submit_btn_background)
                }
                else {
                    binding.submitBtn.isEnabled = false
                    binding.submitBtn.setBackgroundResource(R.drawable.submit_btn_disabled)
                }
            }
            override fun afterTextChanged(p0: Editable?) {}

        })


    }

    override fun onResume() {
        super.onResume()

        // Don't have a account click handling
        binding.btnSignUp.setOnClickListener{
            if(item % 2 == 0){
                binding.btnPhone.text = "Sign in with phone"
                binding.btnGoogle.text = "Sign in with google"
                binding.haveAcc.text = "Don't have a accout?"
                binding.btnSignUp.text = "Sign up"
            }else{
                binding.btnPhone.text = "Sign up with phone"
                binding.btnGoogle.text = "Sign up with phone"
                binding.haveAcc.text = "Have a accout?"
                binding.btnSignUp.text = "Sign in"
            }
            binding.submitBtn.visibility = View.GONE
            binding.edtPhoneNumber.visibility = View.GONE
            binding.ccp.visibility = View.GONE
            binding.btnGoogle.visibility = View.VISIBLE
            binding.btnPhone.visibility = View.VISIBLE

            playAnimation()

            ++item
        }

        binding.btnPhone.setOnClickListener{
            binding.btnGoogle.visibility = View.GONE
            binding.btnPhone.visibility = View.GONE

            binding.submitBtn.visibility = View.VISIBLE
            binding.edtPhoneNumber.visibility = View.VISIBLE
            binding.ccp.visibility = View.VISIBLE
        }

        binding.submitBtn.setOnClickListener {
//            startActivity(Intent(this, OTPVerification::class.java))
            binding.submitBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            sendOTP(binding.edtPhoneNumber.text.toString().trim())

        }



        binding.btnGoogle.setOnClickListener{
            // Google sing-up work
        }







    }

    private fun sendOTP(ph: String) {

        val phoneNumber = "+" + binding.ccp.selectedCountryCode + ph
        Log.e("PHONE",phoneNumber)


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                binding.submitBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            Toast.makeText(this@SignIn,"onVerificationCompleted", Toast.LENGTH_SHORT).show()

            }

            private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {


            }

            override fun onVerificationFailed(e: FirebaseException) {

                binding.submitBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SignIn,e.message, Toast.LENGTH_SHORT).show()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.submitBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE

                val intent = Intent(this@SignIn,OTPVerification::class.java)
                intent.putExtra("phone",phoneNumber)
                intent.putExtra("id",verificationId)
                startActivity(intent)

                Toast.makeText(this@SignIn,"Code Sent", Toast.LENGTH_SHORT).show()

            }
        }


        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun playAnimation() {
        binding.img.startAnimation(ttb)
        binding.btnGoogle.startAnimation(rtl)
        binding.btnEmail.startAnimation(ltr)
        binding.btnPhone.startAnimation(ltr)

    }

}