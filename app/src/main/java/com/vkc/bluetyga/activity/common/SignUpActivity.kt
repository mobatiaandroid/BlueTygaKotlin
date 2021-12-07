package com.vkc.bluetyga.activity.common

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings.*
import android.provider.Settings.Secure.*
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.common.api.Api
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.common.model.district.DistrictResponseModel
import com.vkc.bluetyga.activity.common.model.state.State
import com.vkc.bluetyga.activity.common.model.state.StateResponseModel
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
    lateinit var spinnerDistrict:Spinner
    lateinit var spinnerUserType:Spinner
    lateinit var mobileNo: String
    lateinit var selectedState: String
    lateinit var selectedDistrict: String

    lateinit var progressBarDialog: ProgressBarDialog
    lateinit var buttonRegister: Button

    lateinit var arrayListState: ArrayList<String>
    lateinit var arrayListDistrict:ArrayList<String>
    lateinit var userTypeList:ArrayList<String>

    lateinit var stateList: ArrayList<State>
    lateinit var districtList: ArrayList<com.vkc.bluetyga.activity.common.model.district.Response>

    private var userType: String = ""
    private var androidID = ""
    var stateID: String = ""
    var district: String = ""
    var isNewReg = false


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

        editCustomer.setOnKeyListener { v, keyCode, event ->
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
                    if (userTypeList[position] == "Retailer") {
                        userType = "5"
                    } else if (userTypeList[position] == "Subdealer") {
                        userType = "7"
                    } else if (userTypeList[position] == "Dealer") {
                        userType = "6"
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

    private fun registerAPI() {
        TODO("Not yet implemented")
    }

    private fun newRegisterAPI() {
        TODO("Not yet implemented")
    }

    private fun getData() {
        TODO("Not yet implemented")
    }

    private fun getState() {
        stateList.clear()
        arrayListState.clear()
        var stateMainResponse: StateResponseModel
        var i = 0
        if(UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getStateResponse()
                .enqueue(object : Callback<StateResponseModel>{
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
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }

    private fun getDistrict(stateID: String) {
        arrayListDistrict.clear()
        districtList.clear()
        var districtResponse: DistrictResponseModel
        var i = 0
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getDistrictResponse(
                stateID
            ).enqueue(object : Callback<DistrictResponseModel>{
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
                        if (districtList[j].district.uppercase()
                                .equals(district.uppercase())
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
}