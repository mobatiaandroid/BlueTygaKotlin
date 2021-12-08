package com.vkc.bluetyga.activity.common

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings.*
import android.provider.Settings.Secure.*
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.common.model.district.DistrictResponseModel
import com.vkc.bluetyga.activity.common.model.new_register.NewRegisterMainResponseModel
import com.vkc.bluetyga.activity.common.model.register.RegisterMainResponseModel
import com.vkc.bluetyga.activity.common.model.resend_otp.ResendOTPMainResponse
import com.vkc.bluetyga.activity.common.model.state.State
import com.vkc.bluetyga.activity.common.model.state.StateResponseModel
import com.vkc.bluetyga.activity.common.model.user_details.Data
import com.vkc.bluetyga.activity.common.model.user_details.UserDetailsMainResponseModel
import com.vkc.bluetyga.activity.common.model.verify_otp.VerifyOTPMainResponseModel
import com.vkc.bluetyga.activity.dealers.DealersActivity
import com.vkc.bluetyga.activity.home.HomeActivity
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import com.vkc.loyaltyme.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SignUpActivity : AppCompatActivity() {

    lateinit var context: Activity
    lateinit var editMobile: EditText
    lateinit var editOwner: EditText
    lateinit var editShop: EditText
    lateinit var editPlace: EditText
    lateinit var editPin: EditText
    lateinit var editCustomer: EditText
    lateinit var editDoor: EditText
    lateinit var editAddress: EditText
    lateinit var editLandmark: EditText
    lateinit var imageGetData: ImageView
    lateinit var imageSearchID: ImageView
    lateinit var llAddress: LinearLayout
    lateinit var llCustomerID: ConstraintLayout
    lateinit var llUserType: LinearLayout
    lateinit var spinnerState: Spinner
    lateinit var spinnerDistrict: Spinner
    lateinit var spinnerUserType: Spinner
    lateinit var mobileNo: String
    lateinit var selectedState: String
    lateinit var selectedDistrict: String

    lateinit var progressBarDialog: ProgressBarDialog
    lateinit var buttonRegister: Button

    lateinit var arrayListState: ArrayList<String>
    lateinit var arrayListDistrict: ArrayList<String>
    lateinit var userTypeList: ArrayList<String>

    lateinit var stateList: ArrayList<State>
    lateinit var districtList: ArrayList<com.vkc.bluetyga.activity.common.model.district.Response>

    private var userType: String = ""
    private var androidID = ""
    var stateID: String = ""
    var district: String = ""
    var isNewReg = false
    var otpValue = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        editMobile = findViewById(R.id.editMobile)
        editOwner = findViewById(R.id.editOwner)
        editShop = findViewById(R.id.editShop)
        editPlace = findViewById(R.id.editPlace)
        editPin = findViewById(R.id.editPin)
        editCustomer = findViewById(R.id.editCustomerID)
        editDoor = findViewById(R.id.editDoor)
        editAddress = findViewById(R.id.editAddress)
        editLandmark = findViewById(R.id.editLandMark)
        imageSearchID = findViewById<View>(R.id.imageSearchID) as ImageView
        imageGetData = findViewById<View>(R.id.imageGetData) as ImageView
        spinnerState = findViewById<View>(R.id.spinnerState) as Spinner
        spinnerUserType = findViewById<View>(R.id.spinnerUserType) as Spinner
        spinnerDistrict = findViewById<View>(R.id.spinnerDistrict) as Spinner
        llCustomerID = findViewById(R.id.llCustId)
        llAddress = findViewById(R.id.llAddress)
        buttonRegister = findViewById(R.id.buttonRegister)
        llUserType = findViewById(R.id.llUserType)

        progressBarDialog = ProgressBarDialog(context)

        llCustomerID.visibility = View.GONE
        llAddress.visibility = View.GONE
        llUserType.visibility = View.GONE
        editOwner.isEnabled = false
        editShop.isEnabled = false
        editPlace.isEnabled = false
        editPin.isEnabled = false

        editOwner.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        editPlace.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        editShop.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        editDoor.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        editAddress.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        editLandmark.filters = arrayOf<InputFilter>(InputFilter.AllCaps())

        stateList = ArrayList()
        districtList = ArrayList()
        userTypeList = ArrayList()
        arrayListState = ArrayList()
        arrayListDistrict = ArrayList()
        editCustomer.setText("")
        userType = ""
        userTypeList.clear()
        editMobile.setText("")
        userTypeList.add("User Type")
        userTypeList.add("Dealer")
        userTypeList.add("Subdealer")
        userTypeList.add("Retailer")


        androidID = getString(contentResolver, ANDROID_ID)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            Log.e("token", token.toString())
        })

        val adapter =
            ArrayAdapter(this@SignUpActivity, android.R.layout.simple_spinner_item, userTypeList)
        spinnerUserType.adapter = adapter
        getState()

        imageGetData.setOnClickListener {
            getData()
        }

        imageSearchID.setOnClickListener {
            getData()
        }

        buttonRegister.setOnClickListener {
            if (editMobile.text.toString().trim { it <= ' ' } == "") {
                editMobile.requestFocus()
                editMobile.error = "Mandatory Field"
            } else if (editOwner.text.toString().trim { it <= ' ' } == "") {
                editOwner.requestFocus()
                editOwner.error = "Mandatory Field"
            } else if (editShop.text.toString().trim { it <= ' ' } == "") {
                editShop.requestFocus()
                editShop.error = "Mandatory Field"
            } else if (userType == "" && isNewReg) {
                CustomToast.customToast(context)
                CustomToast.show(57)
            } else if (editDoor.text.toString().trim { it <= ' ' } == "" && isNewReg) {
                editDoor.requestFocus()
                editDoor.error = "Mandatory Field"
            } else if (editAddress.text.toString().trim { it <= ' ' } == "" && isNewReg) {
                editAddress.requestFocus()
                editAddress.error = "Mandatory Field"
            } else if (selectedState == "") {
                CustomToast.customToast(context)
                CustomToast.show(32)
            } else if (selectedDistrict == "") {
                CustomToast.customToast(context)
                CustomToast.show(33)
            } else if (editPlace.text.toString().trim { it <= ' ' } == "") {
                editPlace.requestFocus()
                editPlace.error = "Mandatory Field"
            } else if (editPin.text.toString().trim { it <= ' ' } == "") {
                editPin.requestFocus()
                editPin.error = "Mandatory Field"
            } else {
                if (isNewReg) {
                    newRegisterAPI()
                } else {
                    registerAPI()
                }
            }
        }

        editCustomer.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        getData()
                        true
                    }
                    else -> {}
                }
            }
            false
        }

        spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    (parent.getChildAt(0) as TextView).textSize = 14f
                    (parent.getChildAt(0) as TextView).isAllCaps = true
                    stateID = stateList[position - 1].state
                    selectedState = stateList[position - 1].state_name
                    getDistrict(stateID)
                } else {
                    stateID = ""
                    selectedState = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedState = ""
                stateID = ""
            }
        }

        spinnerUserType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    (parent.getChildAt(0) as TextView).textSize = 14f
                    (parent.getChildAt(0) as TextView).isAllCaps = true
//                    if (userTypeList[position] == "Retailer") {
//                        userType = "5"
//                    } else if (userTypeList[position] == "Subdealer") {
//                        userType = "7"
//                    } else if (userTypeList[position] == "Dealer") {
//                        userType = "6"
//                    }
                    userType = when (userTypeList[position]) {
                        "Subdealer" -> "7"
                        "Retailer" -> "5"
                        "Dealer" -> "6"
                        else -> ""
                    }

                } else {
                    userType = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                userType = ""
            }
        }

        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    (parent.getChildAt(0) as TextView).isAllCaps = true
                    (parent.getChildAt(0) as TextView).textSize = 14f
                    selectedDistrict = districtList[position - 1].district
                } else {
                    selectedDistrict = ""
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
//        editMobile.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }

    private fun newRegisterAPI() {
        var newRegisterMainResponse: NewRegisterMainResponseModel
        var newRegisterResponse: com.vkc.bluetyga.activity.common.model.new_register.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getNewRegisterResponse(
                editCustomer.text.toString(),
                editShop.text.toString(),
                selectedState,
                selectedDistrict,
                editPlace.text.toString(),
                editPin.text.toString(),
                editOwner.text.toString(),
                editMobile.text.toString(),
                editDoor.text.toString(),
                editAddress.text.toString(),
                editLandmark.text.toString(),
                userType
            ).enqueue( object : Callback<NewRegisterMainResponseModel>{
                override fun onResponse(
                    call: Call<NewRegisterMainResponseModel>,
                    response: Response<NewRegisterMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        newRegisterMainResponse = response.body()!!
                        newRegisterResponse = newRegisterMainResponse.response
                        if (newRegisterResponse.status == "Success"){
                            showAlertDialog(context,
                                "4",
                                "Registration request submitted successfully. Please login to loyalty app after the confirmation from VKC Group.")
                        }else if (newRegisterResponse.status == "Exists"){
                            showAlertDialog(context,
                                "5",
                                "Registration request already submitted. Please contact VKC Group.")
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<NewRegisterMainResponseModel>, t: Throwable) {
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

    private fun registerAPI() {
        var registerMainResponse: RegisterMainResponseModel
        var registerResponse: com.vkc.bluetyga.activity.common.model.register.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getRegisterResponse(
                editMobile.text.toString().trim(),
                PreferenceManager.getUserType(context),
                PreferenceManager.getCustomerID(context),
                editOwner.text.toString().trim(),
                editPlace.text.toString().trim()
            ).enqueue(object : Callback<RegisterMainResponseModel>{
                override fun onResponse(
                    call: Call<RegisterMainResponseModel>,
                    response: Response<RegisterMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        registerMainResponse = response.body()!!
                        registerResponse = registerMainResponse.response
                        if (registerResponse.status == "Success"){
                            showOTPDialog()
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<RegisterMainResponseModel>, t: Throwable) {
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

    private fun showOTPDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_otp_alert)
        val editOTP1 = dialog.findViewById<View>(R.id.editOtp1) as EditText
        val editOTP2 = dialog.findViewById<View>(R.id.editOtp2) as EditText
        val editOTP3 = dialog.findViewById<View>(R.id.editOtp3) as EditText
        val editOTP4 = dialog.findViewById<View>(R.id.editOtp4) as EditText
        val textResend = dialog.findViewById<View>(R.id.textResend) as TextView
        val textCancel = dialog.findViewById<View>(R.id.textCancel) as TextView
        val textOTP = dialog.findViewById<View>(R.id.textOtp) as TextView
        val buttonCancel = dialog.findViewById<View>(R.id.buttonCancel) as Button
        val mob = mobileNo.substring(mobileNo.length - 4, mobileNo.length)
        textOTP.text = "OTP has been sent to  XXXXXX$mob"
        editOTP1.isCursorVisible = false
        textCancel.setOnClickListener {
            editCustomer.setText("")
            llCustomerID.visibility = View.GONE
            dialog.dismiss()
            val intent = Intent(context, SignUpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        textResend.setOnClickListener {
            resendOTP()
            editOTP1.setText("")
            editOTP2.setText("")
            editOTP3.setText("")
            editOTP4.setText("")
        }
        editOTP1.addTextChangedListener(GenericTextWatcher(editOTP1, editOTP2))
        editOTP2.addTextChangedListener(GenericTextWatcher(editOTP2, editOTP3))
        editOTP3.addTextChangedListener(GenericTextWatcher(editOTP3, editOTP4))
        editOTP4.addTextChangedListener(GenericTextWatcher(editOTP4, null))

        editOTP1.setOnKeyListener(GenericKeyEvent(editOTP1, null))
        editOTP2.setOnKeyListener(GenericKeyEvent(editOTP2, editOTP1))
        editOTP3.setOnKeyListener(GenericKeyEvent(editOTP3, editOTP2))
        editOTP4.setOnKeyListener(GenericKeyEvent(editOTP4, editOTP3))

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        editOTP4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1){
                    if (editOTP1.text.isNotEmpty()
                        && editOTP2.text.isNotEmpty()
                        && editOTP3.text.isNotEmpty()
                        && editOTP4.text.isNotEmpty()){
                        otpValue = editOTP1.text.toString().trim() + editOTP2.text.toString().trim() + editOTP3.text.toString().trim() + editOTP4.text.toString().trim()
                        verifyOTP(otpValue, editMobile.text.toString().trim())
                    }
                }
            }
        })
        dialog.show()
    }

    private fun resendOTP() {
        var resendOTPMainResponse: ResendOTPMainResponse
        var resendOTPResponse: com.vkc.bluetyga.activity.common.model.resend_otp.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getResendOTPResponse(
                PreferenceManager.getUserType(context),
                PreferenceManager.getCustomerID(context)
            ).enqueue( object : Callback<ResendOTPMainResponse>{
                override fun onResponse(
                    call: Call<ResendOTPMainResponse>,
                    response: Response<ResendOTPMainResponse>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        resendOTPMainResponse = response.body()!!
                        resendOTPResponse = resendOTPMainResponse.response
                        if (resendOTPResponse.status.equals("Success")){
                            CustomToast.customToast(context)
                            CustomToast.show(34)
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }
                }

                override fun onFailure(call: Call<ResendOTPMainResponse>, t: Throwable) {
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

    private fun verifyOTP(otpValue: String, mobile: String) {
        var verifyOTPMainResponse: VerifyOTPMainResponseModel
        var verifyOTPResponse: com.vkc.bluetyga.activity.common.model.verify_otp.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getVerifyOTPResponse(
                otpValue,
                PreferenceManager.getUserType(context),
                PreferenceManager.getCustomerID(context),
                mobileNo,
                "0"
            ).enqueue( object : Callback<VerifyOTPMainResponseModel>{
                override fun onResponse(
                    call: Call<VerifyOTPMainResponseModel>,
                    response: Response<VerifyOTPMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        verifyOTPMainResponse = response.body()!!
                        verifyOTPResponse = verifyOTPMainResponse.response
                        if (verifyOTPResponse.status == "Success"){
                            PreferenceManager.setIsVerifiedOTP(context,"Y")
                            CustomToast.customToast(context)
                            CustomToast.show(8)
                            if (PreferenceManager.getLoginStatusFlag(context).equals("Y")){
                                startActivity(
                                    Intent(
                                        this@SignUpActivity,
                                        HomeActivity::class.java
                                    )
                                )
                                finish()
                            }else{
                                if (editCustomer.text.toString().trim().equals("")){
                                    startActivity(
                                        Intent(
                                            this@SignUpActivity,
                                            DealersActivity::class.java
                                        )
                                    )
                                    finish()
                                } else{
                                    showUpdateDialog()
                                }
                            }
                        }else{
                            val attempts = verifyOTPResponse.otp_attempt
                            if (attempts == "3"){
                                showAlertDialog(context, "2", "Your OTP attempts exceeded. Kindly fill up the data for new registration")
                                PreferenceManager.setIsVerifiedOTP(context,"N")
                            } else{
                                CustomToast.customToast(context)
                                CustomToast.show(9)
                            }
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<VerifyOTPMainResponseModel>, t: Throwable) {
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

    private fun showUpdateDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_update)
        val textYes = dialog.findViewById<View>(R.id.textYes)
        val textNo = dialog.findViewById<View>(R.id.textNo)
        textYes.setOnClickListener {
            dialog.dismiss()
            updatePhone(otpValue, editMobile.text.toString().trim())
        }
        textNo.setOnClickListener {
            dialog.dismiss()
            if (PreferenceManager.getUserType(context) == "6") {
                startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SignUpActivity, DealersActivity::class.java))
                finish()
            }
        }
        dialog.show()
    }

    private fun updatePhone(otpValue: String, mobile: String) {
        var updatePhoneMainResponse: VerifyOTPMainResponseModel
        var updatePhoneResponse: com.vkc.bluetyga.activity.common.model.verify_otp.Response
        if (UtilityMethods.checkInternet(context)) {
            progressBarDialog.show()
            ApiClient.getApiService().getVerifyOTPResponse(
                otpValue,
                PreferenceManager.getUserType(context),
                PreferenceManager.getCustomerID(context),
                mobileNo,
                "1"
            ).enqueue(object : Callback<VerifyOTPMainResponseModel> {
                override fun onResponse(
                    call: Call<VerifyOTPMainResponseModel>,
                    response: Response<VerifyOTPMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        updatePhoneMainResponse = response.body()!!
                        updatePhoneResponse = updatePhoneMainResponse.response
                        if (updatePhoneResponse.status.equals("Success")){
                            showAlertDialog(context,"3","Mobile number updated successfully. Please login using new mobile number")
                            startActivity(
                                Intent(
                                    this@SignUpActivity,
                                    DealersActivity::class.java
                                )
                            )
                            finish()
                        } else{
                            PreferenceManager.setIsVerifiedOTP(context,"N")
                            CustomToast.customToast(context)
                            CustomToast.show(9)
                        }
                    } else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<VerifyOTPMainResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(context)
                    CustomToast.show(0)
                }
            })
        } else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }

    private fun getData() {
        var userDetailsMainResponse: UserDetailsMainResponseModel
        var userDetailsResponse: com.vkc.bluetyga.activity.common.model.user_details.Response
        var userDetails: Data
        if (UtilityMethods.checkInternet(context)) {
            progressBarDialog.show()
            ApiClient.getApiService().getUserDetailsResponse(
                editMobile.text.toString().trim(),
                editCustomer.text.toString().trim(),
                androidID
            ).enqueue(object : Callback<UserDetailsMainResponseModel> {
                override fun onResponse(
                    call: Call<UserDetailsMainResponseModel>,
                    response: Response<UserDetailsMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null) {
                        userDetailsMainResponse = response.body()!!
                        userDetailsResponse = userDetailsMainResponse.response
                        if (userDetailsResponse.status == "Success") {

                            userDetails = userDetailsResponse.data
                            val owner: String = userDetails.contact_person
                            val shop_name: String = userDetails.name
                            district = userDetails.district
                            val state: String = userDetails.state_name
                            val pin: String = userDetails.pincode
                            val role: String = userDetails.role
                            val cust_id: String = userDetails.cust_id
                            val city: String = userDetails.city.uppercase()
                            val stateCode: String = userDetails.state

                            if (editCustomer.text.toString().trim().isNotEmpty()) {

                            }
                            for (i in stateList.indices) {
                                if (stateList[i].state == stateCode) {
                                    spinnerState.setSelection(i + 1)
                                }
                            }
                            /**Set District from get Data**/
                            for (i in districtList.indices) {
                                if (districtList[i].district == district) {
                                    spinnerDistrict.setSelection(i + 1)
                                }
                            }
                            val isLoggedIn = userDetails.is_logged_in
                            mobileNo = userDetails.phone
                            PreferenceManager.setMobileNo(context, mobileNo)
                            if (isLoggedIn == "0"){
                                val dealerCount = userDetails.dealers_count.toInt()
                                if (dealerCount > 0 ){

                                    PreferenceManager.setDealerCount(context, dealerCount)
                                    CustomToast.customToast(context)
                                    CustomToast.show(35)
                                }else{
                                    PreferenceManager.setDealerCount(context, 0)
                                }
                                UtilityMethods.hideKeyBoard(context)
                                PreferenceManager.setCustomerID(context, cust_id)
                                PreferenceManager.setUserType(context, role)
                                PreferenceManager.setUserID(context, role) // For Dealer

                                editOwner.setText(owner)
                                editShop.setText(shop_name)
                                editPlace.setText(city)
                                editPin.setText(pin)
                                editOwner.isEnabled = true
                                editShop.isEnabled = false
                                spinnerDistrict.setEnabled(false)
                                spinnerState.isEnabled = false
                                editPlace.isEnabled = true
                                editPin.isEnabled = false
                                llCustomerID.visibility = View.GONE
                                llAddress.visibility = View.GONE
                                llUserType.visibility = View.GONE
                                isNewReg = false
                            }else{
                                CustomToast.customToast(context)
                                CustomToast.show(29)
                            }


                        } else if (userDetailsResponse.status.equals("Empty")) {
                            isNewReg = true
                            editOwner.isEnabled = true
                            editShop.isEnabled = true
                            spinnerState.isEnabled = true
                            spinnerDistrict.isEnabled = true
                            editPlace.isEnabled = true
                            editPin.isEnabled = true
                            if (editCustomer.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                                showAlertDialog(
                                    context,
                                    "0",
                                    "You are not a registered user proceed with new registration"
                                )
                            } else {
                                showAlertDialog(
                                        context,
                                        "1",
                                        "You are not registered with this mobile no, kindly search with CUST ID.If you don't know CUST ID please fill up the data for new registration"
                                    )
                            }
                            llCustomerID.visibility = View.VISIBLE
                            llAddress.visibility = View.VISIBLE
                            llUserType.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<UserDetailsMainResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(context)
                    CustomToast.show(0)
                }

            })
        } else {
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }

    private fun showAlertDialog(context: Activity, type: String, message: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_alert)
        val messageText: TextView = dialog.findViewById<View>(R.id.message) as TextView
        val buttonCancel: Button = dialog.findViewById<View>(R.id.buttonCancel) as Button
        val okButton: Button = dialog.findViewById<View>(R.id.buttonOk) as Button
        messageText.text = message
        if (type == "1") {
            editMobile.clearFocus()
            editCustomer.requestFocus()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
            if (type == "1") {
                editCustomer.setText("")
            } else if (type == "2") {
                editCustomer.setText("")
                startActivity(Intent(context, SignUpActivity::class.java))
            } else if (type == "3") {
                startActivity(Intent(context, DealersActivity::class.java))
            } else if (type == "4") {
            } else if (type == "5") {
            } else {
                llUserType.visibility = View.VISIBLE
            }
        }
        okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
/*
    private fun showAlertDialog(context: Activity, type: String, message: String) {

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_alert)
        val messageText: TextView = dialog.findViewById<View>(R.id.message) as TextView
        val buttonCancel: Button = dialog.findViewById<View>(R.id.buttonCancel) as Button
        val okButton: Button = dialog.findViewById<View>(R.id.buttonOk) as Button
        messageText.text = message
        if (type.equals("1")) {
            editMobile.clearFocus()
            editCustomer.requestFocus()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
            if (type == "1") {
                editCustomer.setText("")
            } else if (type == "2") {
                editCustomer.setText("")
                startActivity(Intent(context, SignUpActivity::class.java))
            } else if (type == "3") {
                startActivity(Intent(context, DealersActivity::class.java))
            } else if (type == "4") {
            } else if (type == "5") {
            } else {
                llUserType.visibility = View.VISIBLE
            }
        }
        okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
*/


    private fun getState() {
        stateList.clear()
        arrayListState.clear()
        var stateMainResponse: StateResponseModel
        var i = 0
        if (UtilityMethods.checkInternet(context)) {
            progressBarDialog.show()
            ApiClient.getApiService().getStateResponse()
                .enqueue(object : Callback<StateResponseModel> {
                    override fun onResponse(
                        call: Call<StateResponseModel>,
                        response: Response<StateResponseModel>
                    ) {
                        progressBarDialog.hide()
                        stateMainResponse = response.body()!!
                        while (i < stateMainResponse.states.size) {
                            stateList.add(stateMainResponse.states[i])
                            i++
                        }
                        i = 0
                        arrayListState.add("Select State")
                        while (i < stateList.size) {
                            arrayListState.add(stateList[i].state_name)
                            i++
                        }
                        val adapter = ArrayAdapter(
                            this@SignUpActivity,
                            android.R.layout.simple_spinner_item,
                            arrayListState
                        )
                        spinnerState.adapter = adapter
                    }

                    override fun onFailure(call: Call<StateResponseModel>, t: Throwable) {
                        progressBarDialog.hide()
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                })
        } else {
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }

    private fun getDistrict(stateID: String) {
        arrayListDistrict.clear()
        districtList.clear()
        var districtResponse: DistrictResponseModel
        var i = 0
        if (UtilityMethods.checkInternet(context)) {
            progressBarDialog.show()
            ApiClient.getApiService().getDistrictResponse(
                stateID
            ).enqueue(object : Callback<DistrictResponseModel> {
                override fun onResponse(
                    call: Call<DistrictResponseModel>,
                    response: Response<DistrictResponseModel>
                ) {
                    progressBarDialog.hide()
                    districtResponse = response.body()!!
                    while (i < districtResponse.response.size) {
                        districtList.add(districtResponse.response[i])
                        i++
                    }
                    i = 0
                    arrayListDistrict.add("Select District")
                    while (i < districtList.size) {
                        arrayListDistrict.add(districtList[i].district)
                        i++
                    }
                    val adapter = ArrayAdapter(
                        this@SignUpActivity,
                        android.R.layout.simple_spinner_item,
                        arrayListDistrict
                    )
                    spinnerDistrict.adapter = adapter
                    for (j in districtList.indices) {
                        if (districtList[j].district.uppercase() == district.uppercase()
                        ) {
                            spinnerDistrict.setSelection(j + 1)
                        }
                    }
                }

                override fun onFailure(call: Call<DistrictResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(context)
                    CustomToast.show(0)
                }
            })
        }
    }
    class GenericKeyEvent(private val currentView: EditText, private val previousView: EditText?) :
        View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.editOtp1 && currentView.text.isEmpty()) {
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }

    }

    class GenericTextWatcher(private val currentView: View, private val nextView: View?) :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when (currentView.id) {
                R.id.editOtp1 -> {
                    if (text.length == 1) {
                        nextView!!.requestFocus()
                        currentView.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    } else if (text.isEmpty()){
                        currentView.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
                R.id.editOtp2 -> {
                    if (text.length == 1) {
                        nextView!!.requestFocus()
                        currentView.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    } else if (text.isEmpty()){
                        currentView.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
                R.id.editOtp3 -> {
                    if (text.length == 1) {
                        nextView!!.requestFocus()
                        currentView.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    } else if (text.isEmpty()){
                        currentView.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
                R.id.editOtp4 -> {
                    if (text.length == 1) {
                        currentView.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    } else if (text.isEmpty()){
                        currentView.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
            }
        }
    }
}