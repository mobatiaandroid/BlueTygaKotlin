package com.vkc.bluetyga.activity.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealers.DealersActivity
import com.vkc.bluetyga.activity.profile.model.profile.Data
import com.vkc.bluetyga.activity.profile.model.profile.ProfileMainResponseModel
import com.vkc.bluetyga.activity.profile.model.profile.Response
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import com.vkc.loyaltyme.api.ApiClient
import retrofit2.Call
import retrofit2.Callback

class ProfileActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var headerManager: HeaderManager
    lateinit var imageBack: ImageView
    lateinit var imageProfile: ImageView
    lateinit var buttonUpdate: Button
    lateinit var editMobile: EditText
    lateinit var editOwner:EditText
    lateinit var editShop:EditText
    lateinit var editState:EditText
    lateinit var editDistrict:EditText
    lateinit var editPlace:EditText
    lateinit var editPin:EditText
    lateinit var editAddress:EditText
    lateinit var editMobile2:EditText
    lateinit var editEmail:EditText
    lateinit var textCustomerID: TextView
    lateinit var textMyDealers:TextView
    lateinit var textUpdate:TextView
    lateinit var textMyCustomers:TextView
    lateinit var progressBarDialog: ProgressBarDialog
    var filePath = ""
    var ACTIVITY_REQUEST_CODE = 700
    var ACTIVITY_FINISH_RESULT_CODE = 701
    private val mImageCaptureUri: Uri? = null
    var otpValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        context = this
        initialiseUI()
        getProfile()
    }

    private fun getProfile() {
        var profileMainResponse: ProfileMainResponseModel
        var profileResponse: Response
        var profileData: Data
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getProfileResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context)
            ).enqueue(object : Callback<ProfileMainResponseModel> {
                override fun onResponse(
                    call: Call<ProfileMainResponseModel>,
                    response: retrofit2.Response<ProfileMainResponseModel>
                ) {
                    progressBarDialog.hide()
                }

                override fun onFailure(call: Call<ProfileMainResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                }

            })
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        imageProfile = findViewById(R.id.imageProfile)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        editMobile = findViewById(R.id.editMobile)
        editOwner = findViewById(R.id.editOwner)
        editShop = findViewById(R.id.editShop)
        editState = findViewById(R.id.editState)
        editDistrict = findViewById(R.id.editDistrict)
        editPlace = findViewById(R.id.editPlace)
        editPin = findViewById(R.id.editPin)
        editAddress = findViewById(R.id.editAddress)
        editMobile2 = findViewById(R.id.editMobile2)
        editEmail = findViewById(R.id.editEmail)
        textCustomerID = findViewById(R.id.textCustId)
        textMyDealers = findViewById(R.id.textMydealers)
        textMyCustomers = findViewById(R.id.textMyCustomers)
        textUpdate = findViewById(R.id.textUpdate)
        progressBarDialog = ProgressBarDialog(context)
        headerManager = HeaderManager(this@ProfileActivity, resources.getString(R.string.profile))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!

        // editMobile.setEnabled(false);
        editOwner.isEnabled = true
        editShop.isEnabled = false
        editState.isEnabled = false
        editDistrict.isEnabled = false
        editPlace.isEnabled = true
        editPin.isEnabled = false
        editAddress.isEnabled = false
        if (PreferenceManager.getUserType(context) == "6") {
            textMyDealers.visibility = View.GONE
            textMyCustomers.visibility = View.VISIBLE
        } else {
            textMyCustomers.visibility = View.GONE
            textMyDealers.visibility = View.VISIBLE
        }
        buttonUpdate.setOnClickListener {
            updateProfile()
        }
        textMyDealers.setOnClickListener {
            startActivity(
                Intent(
                    this@ProfileActivity,
                    DealersActivity::class.java))
        }
        imageProfile.setOnClickListener {  }
        textUpdate.setOnClickListener {
            if (editMobile.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                if (editMobile.text.toString().trim { it <= ' ' }.length == 10) {
                    if (PreferenceManager.getMobile(context).equals(editMobile.text.toString().trim { it <= ' ' })) {
                        /***Do Nothing***/
                    } else {
                        alertUpdateMobile(context)
                    }
                } else {
                    CustomToast.customToast(context)
                    CustomToast.show(54)
                }
            }else{
                CustomToast.customToast(context)
                CustomToast.show(54)
            }
        }
        textMyCustomers.setOnClickListener {  }
    }
}