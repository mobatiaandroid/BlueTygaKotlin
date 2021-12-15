package com.vkc.bluetyga.activity.home

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.lzyzsd.circleprogress.ArcProgress
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealer_redeem_list.RedeemListDealerActivity
import com.vkc.bluetyga.activity.gifts.GiftsActivity
import com.vkc.bluetyga.activity.home.model.app_version.AppVersionMainResponseModel
import com.vkc.bluetyga.activity.home.model.loyalty_points.LoyaltyPointsMainResponseModel
import com.vkc.bluetyga.activity.home.model.loyalty_points.Response
import com.vkc.bluetyga.activity.home.model.register_device.RegisterDeviceMainResponseModel
import com.vkc.bluetyga.activity.inbox.InboxActivity
import com.vkc.bluetyga.activity.issue_point.IssuePointActivity
import com.vkc.bluetyga.activity.issue_point.IssuePointDealerActivity
import com.vkc.bluetyga.activity.point_history.PointHistoryActivity
import com.vkc.bluetyga.activity.profile.ProfileActivity
import com.vkc.bluetyga.activity.shop_image.ShopImageActivity
import com.vkc.bluetyga.activity.sub_dealer_redeem.SubDealerRedeemActivity
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import com.vkc.bluetyga.api.ApiClient
import devlight.io.library.ArcProgressStackView
import retrofit2.Call
import retrofit2.Callback
import java.util.ArrayList

class HomeActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var textPoints: TextView
    lateinit var textNoPoints: TextView
    lateinit var textVersion: TextView
    lateinit var updateAppView: ConstraintLayout
    lateinit var llPoints: LinearLayout
    lateinit var llGifts:LinearLayout
    lateinit var llProfile:LinearLayout
    lateinit var llShop:LinearLayout
    lateinit var llDescription:LinearLayout
    lateinit var llInbox:LinearLayout
    lateinit var arcProgress: ArcProgress
    lateinit var buttonIssue: Button
    lateinit var arcProgressStackView: ArcProgressStackView
    lateinit var progressBarDialog: ProgressBarDialog
    var giftStatus: String? = null
    private val modelCount = 2
    var myPoints: Int = 0
    private val mStartColors = IntArray(modelCount)
    private val mEndColors = IntArray(modelCount)
    var serverVersion: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        context = this
        initialiseUI()
        getAppVersion()
        getMyPoints()
    }
    private fun initialiseUI() {
        textPoints = findViewById(R.id.textPoints)
        textNoPoints = findViewById(R.id.textNoPoints)
        textVersion = findViewById(R.id.textVersion)
        updateAppView = findViewById(R.id.updateAppView)
        llDescription = findViewById(R.id.llDescription)
        llInbox = findViewById(R.id.llInbox)
        llPoints = findViewById(R.id.llPoints)
        llGifts = findViewById(R.id.llGifts)
        llProfile = findViewById(R.id.llProfile)
        llShop = findViewById(R.id.llShop)
        arcProgress = findViewById(R.id.arc_progress)
        arcProgressStackView = findViewById(R.id.arcProgressStackView)
        buttonIssue = findViewById(R.id.buttonIssue)
        progressBarDialog = ProgressBarDialog(context)

        arcProgress.suffixText = ""
        arcProgress.strokeWidth = 15f
        arcProgress.max = 10000000
        arcProgress.bottomTextSize = 80f
        arcProgress.unfinishedStrokeColor = getColor(R.color.white)
        arcProgress.textColor = getColor(R.color.white)
        arcProgress.setBackgroundColor(getColor(R.color.transparent))

        llProfile.setOnClickListener {
            startActivity(Intent(
                this@HomeActivity, ProfileActivity::class.java))
        }
        llPoints.setOnClickListener {
            startActivity(Intent(
                this@HomeActivity, PointHistoryActivity::class.java))
        }
        llShop.setOnClickListener {
            startActivity(Intent(
                this@HomeActivity, ShopImageActivity::class.java))
        }
        llGifts.setOnClickListener {
            if (PreferenceManager.getUserType(context) == "7") {
                startActivity(Intent(
                    this@HomeActivity, SubDealerRedeemActivity::class.java))
            } else if (PreferenceManager.getUserType(context) == "6") {
                startActivity(Intent(
                    this@HomeActivity, RedeemListDealerActivity::class.java))
            } else {
                startActivity(Intent(
                    this@HomeActivity, GiftsActivity::class.java))
            }
        }
        llInbox.setOnClickListener {
            startActivity(Intent(
                this@HomeActivity, InboxActivity::class.java))
        }
        buttonIssue.setOnClickListener {

            if (PreferenceManager.getUserType(context) == "7") {
                startActivity(Intent(
                    this@HomeActivity, IssuePointActivity::class.java))
            } else if (PreferenceManager.getUserType(context) == "6") {
                startActivity(Intent(
                    this@HomeActivity, IssuePointDealerActivity::class.java))
            }
        }

        if (PreferenceManager.getUserType(context) == "7"
            || PreferenceManager.getUserType(context) == "6"
        ) {
            buttonIssue.visibility = View.VISIBLE
            arcProgressStackView.visibility = View.GONE
            arcProgress.visibility = View.VISIBLE
            llDescription.visibility = View.GONE
            // textPoints.setVisibility(View.GONE);
        } else {
            buttonIssue.visibility = View.GONE
            arcProgressStackView.visibility = View.VISIBLE
            arcProgress.visibility = View.GONE
            // textPoints.setVisibility(View.VISIBLE);
            llDescription.visibility = View.VISIBLE
        }
        val startColors = resources.getStringArray(R.array.devlight)
        val bgColors = resources.getStringArray(R.array.bg)
        // Parse colors
        for (i in 0 until modelCount) {
            mStartColors[i] = Color.parseColor(startColors[i])
            mEndColors[i] = Color.parseColor(bgColors[i])
        }

    }
    private fun getAppVersion() {
        var appVersionMainResponse: AppVersionMainResponseModel
        var appVersionResponse: com.vkc.bluetyga.activity.home.model.app_version.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getAppVersionResponse()
                .enqueue(object : Callback<AppVersionMainResponseModel>{
                    override fun onResponse(
                        call: Call<AppVersionMainResponseModel>,
                        response: retrofit2.Response<AppVersionMainResponseModel>
                    ) {
                        progressBarDialog.hide()
                        if (response.body() != null){
                            appVersionMainResponse = response.body()!!
                            appVersionResponse = appVersionMainResponse.response
                            if (appVersionResponse.status == "Success"){
                                serverVersion = appVersionResponse.appversion
                                if (serverVersion == getVersion()) {
                                    deviceRegister()
                                    updateAppView.visibility = View.GONE
                                } else {
                                    updateAppView.visibility = View.VISIBLE
                                    updateApp(context)
                                }
                            }else{
                                CustomToast.customToast(context)
                                CustomToast.show(0)
                            }
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }

                    override fun onFailure(call: Call<AppVersionMainResponseModel>, t: Throwable) {
                        progressBarDialog.hide()
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }

                })
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }

    private fun updateApp(context: Activity) {
        val appPackageName = context.packageName
        val builder = AlertDialog.Builder(context)
        builder.setTitle("New Update Available !")
            .setMessage("Please update the app to avail new features") //
            .setPositiveButton("Ok") { _, _ ->
                try {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")))
                } catch (exception: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }


    private fun deviceRegister() {
        var registerDeviceMainResponse: RegisterDeviceMainResponseModel
        var registerDeviceResponse: com.vkc.bluetyga.activity.home.model.register_device.Response
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result
            PreferenceManager.setToken(context, token)
        })
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getDeviceRegistrationResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context),
                PreferenceManager.getToken(context)
            ).enqueue( object : Callback<RegisterDeviceMainResponseModel>{
                override fun onResponse(
                    call: Call<RegisterDeviceMainResponseModel>,
                    response: retrofit2.Response<RegisterDeviceMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        registerDeviceMainResponse = response.body()!!
                        registerDeviceResponse = registerDeviceMainResponse.response
                        if (registerDeviceResponse.status == "Success"){
                            /***Do Nothing***/
                        }else if (registerDeviceResponse.status == "Empty"){
                            deviceRegister()
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<RegisterDeviceMainResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(context)
                    CustomToast.show(0)
                }
            })
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }

    private fun getVersion(): String? {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(
            packageName, 0
        )
        return packageInfo.versionName
    }

    private fun getMyPoints() {
        var loyaltyPointsMainResponse: LoyaltyPointsMainResponseModel
        var loyaltyPointsResponse: Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getLoyaltyPointsResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context)
            ).enqueue( object : Callback<LoyaltyPointsMainResponseModel> {
                override fun onResponse(
                    call: Call<LoyaltyPointsMainResponseModel>,
                    response: retrofit2.Response<LoyaltyPointsMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        loyaltyPointsMainResponse = response.body()!!
                        loyaltyPointsResponse = loyaltyPointsMainResponse.response
                        if (loyaltyPointsResponse.status == "Success"){
                            val points: String = loyaltyPointsResponse.loyality_point
                            giftStatus = loyaltyPointsResponse.gift_status
                            myPoints = points.toInt()
                            if (points == "0") {
                                textNoPoints.visibility = View.VISIBLE
                                textPoints.visibility = View.GONE
                            } else {
                                textPoints.text = "$points Coupons"
                                textNoPoints.visibility = View.GONE
                            }
                            val mulVal: Int = myPoints * 100
                            val percentValue = mulVal / 1600
                            //    Log.i("percent ", "" + percent_value);
                            //    Log.i("percent ", "" + percent_value);
                            arcProgressStackView.textColor = Color.parseColor("#000000")
                            arcProgressStackView.drawWidthDimension = 150f

                            if (PreferenceManager.getUserType(context) == "7"
                                || PreferenceManager.getUserType(context) == "6"
                            ) {
                                buttonIssue.visibility = View.VISIBLE
                                arcProgressStackView.visibility = View.GONE
                                arcProgress.visibility = View.VISIBLE
                                llDescription.visibility = View.GONE
                                arcProgress.progress = myPoints
                            } else {
                                buttonIssue.visibility = View.GONE
                                arcProgressStackView.visibility = View.VISIBLE
                                arcProgress.visibility = View.GONE
                                llDescription.visibility = View.VISIBLE
                                val models = ArrayList<ArcProgressStackView.Model>()
                                models.add(
                                    ArcProgressStackView.Model(
                                        "",
                                        (100).toFloat(),
                                        mEndColors[0],
                                        mStartColors[0]
                                    )
                                )
                                models.add(
                                    ArcProgressStackView.Model(
                                        "",
                                        percentValue.toFloat(),
                                        mEndColors[1],
                                        mStartColors[1]
                                    )
                                )
                                arcProgressStackView.models = models
                            }
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<LoyaltyPointsMainResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(context)
                    CustomToast.show(0)
                }
            })
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }
}