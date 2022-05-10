package com.chandra.chatapp.signin

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chandra.chatapp.MainActivity
import com.chandra.chatapp.R
import com.chandra.chatapp.databinding.ActivitySigninBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.reflect.Executable

class SignIn : AppCompatActivity() {

    private var item = 1
    private lateinit var ttb : Animation
    private lateinit var ltr : Animation
    private lateinit var rtl : Animation

    private lateinit var auth : FirebaseAuth
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var googleSignInClient :GoogleSignInClient
    private val RC_SIGN_IN = 1  // Can be any integer unique to the Activity

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

        playAnimation()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.ccp.registerCarrierNumberEditText(binding.edtPhoneNumber)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

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
                binding.btnGoogle.text = "Sign up with google"
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


        binding.submitBtn.setOnClickListener{

            if(!binding.ccp.isValidFullNumber){
               Toast.makeText(this@SignIn,"Number is not valid", Toast.LENGTH_SHORT).show()
            }
            else {
//                binding.submitBtn.visibility = View.GONE
//                binding.progressBar.visibility = View.VISIBLE
                sendOTP(binding.edtPhoneNumber.text.toString().trim())

            }
        }



        binding.btnGoogle.setOnClickListener{
            // Google sing-up work
//            startActivity(Intent(this@SignIn, OTPVerification::class.java));
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent,RC_SIGN_IN)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {

        try {
            val account = task?.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!.idToken)

        }catch (e: ApiException){
            e.message?.let { Log.v("FirebaseAuth", it) }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        binding.progressBar.visibility = View.VISIBLE
        val credential =  GoogleAuthProvider.getCredential(idToken, null)
        GlobalScope.launch(Dispatchers.IO) {
            val authx = auth.signInWithCredential(credential)
            val firebaseUser = auth.currentUser

            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{
            binding.progressBar.visibility = View.GONE
        }

    }

    private fun sendOTP(ph: String) {

        val dialog = ProgressDialog(this@SignIn)
        with(dialog) {

            setMessage("Sending OTP...")
            setCancelable(false)
            show()
        }

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

                dialog.dismiss()

//                binding.submitBtn.visibility = View.VISIBLE
//                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SignIn,e.message, Toast.LENGTH_SHORT).show()

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                dialog.dismiss()
//                binding.submitBtn.visibility = View.VISIBLE
//                binding.progressBar.visibility = View.GONE

                val intent = Intent(this@SignIn,OTPVerification::class.java)
                intent.putExtra("phone",phoneNumber)
                intent.putExtra("id",verificationId)
                startActivity(intent)

                Toast.makeText(this@SignIn,"OTP sent successfully", Toast.LENGTH_SHORT).show()

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