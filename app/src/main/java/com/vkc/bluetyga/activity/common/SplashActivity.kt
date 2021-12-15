package com.vkc.bluetyga.activity.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealers.DealersActivity
import com.vkc.bluetyga.activity.home.HomeActivity
import com.vkc.bluetyga.manager.PreferenceManager

class SplashActivity : AppCompatActivity() {
    private lateinit var context: Context
    private lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        context = this
        loadSplash()
    }

    private fun loadSplash() {
        val countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (PreferenceManager.getAgreeTerms(context)) {
                    startNextActivity()
                } else {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            TermsAndConditionsActivity::class.java
                        )
                    )
                    activity.overridePendingTransition(0, 0)
                    finish()
                }
            }
        }
        countDownTimer.start()
    }

    private fun startNextActivity() {
        if (isFinishing) return
        if (PreferenceManager.getLoginStatusFlag(context).equals("Y")) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else if (PreferenceManager.getIsVerifiedOTP(context) == "Y") {
            if (PreferenceManager.getUserType(context) == "6") {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                if (PreferenceManager.getDealerCount(context) > 0) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, DealersActivity::class.java))
                    finish()
                }
            }
        } else {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }
}