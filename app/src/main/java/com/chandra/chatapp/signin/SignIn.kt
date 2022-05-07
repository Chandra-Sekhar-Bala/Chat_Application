package com.chandra.chatapp.signin

import android.content.Intent
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
import org.w3c.dom.Text

class SignIn : AppCompatActivity() {

    private var item = 1
    private lateinit var ttb : Animation
    private lateinit var ltr : Animation
    private lateinit var rtl : Animation


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

        binding.submitBtn.setOnClickListener{
//            if(binding.submitBtn.isEnabled) {
                startActivity(Intent(this, OTPVerification::class.java))
//            }
        }


        binding.btnGoogle.setOnClickListener{
            // Google sing-up work
        }




    }

    private fun playAnimation() {
        binding.img.startAnimation(ttb)
        binding.btnGoogle.startAnimation(rtl)
        binding.btnEmail.startAnimation(ltr)
        binding.btnPhone.startAnimation(ltr)


    }

}