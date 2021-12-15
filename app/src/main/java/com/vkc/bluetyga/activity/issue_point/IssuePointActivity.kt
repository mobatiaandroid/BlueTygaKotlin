package com.vkc.bluetyga.activity.issue_point

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.lzyzsd.circleprogress.ArcProgress
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.home.model.loyalty_points.LoyaltyPointsMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.fetch_user_data.Data
import com.vkc.bluetyga.activity.issue_point.model.fetch_user_data.FetchUserDataMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.fetch_user_data.Response
import com.vkc.bluetyga.activity.issue_point.model.get_retailers.RetailersMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.submit_points_response.SubmitPointsResponse
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class IssuePointActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var headerManager: HeaderManager
    lateinit var autoSearch: AutoCompleteTextView
    lateinit var textID: TextView
    lateinit var textName: TextView
    lateinit var textAddress: TextView
    lateinit var textPhone: TextView
    lateinit var editPoints: EditText
    lateinit var buttonIssue: ImageView
    lateinit var imageBack: ImageView
    lateinit var llData: LinearLayout
    lateinit var arcProgress: ArcProgress
    lateinit var progressBarDialog: ProgressBarDialog
    var sampleList = arrayListOf("Dealer","Retailer","Sub-Dealer")
    var listRetailers: ArrayList<com.vkc.bluetyga.activity.issue_point.model.get_retailers.Data>? = null
    var selectedId: String? = null
    var myPoint = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_point)
        context = this
        initialiseUI()
        getMyPoints()
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        autoSearch = findViewById(R.id.autoSearch)
        textID = findViewById(R.id.textViewId)
        textName = findViewById(R.id.textViewName)
        textAddress = findViewById(R.id.textViewAddress)
        textPhone = findViewById(R.id.textViewPhone)
        editPoints = findViewById(R.id.editPoints)
        buttonIssue = findViewById(R.id.buttonIssue)
        llData = findViewById(R.id.llData)
        arcProgress = findViewById(R.id.arc_progress)
        progressBarDialog = ProgressBarDialog(context)
        headerManager =
            HeaderManager(this@IssuePointActivity,
                resources.getString(R.string.issue_point))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        headerManager.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        llData.visibility = View.GONE
        arcProgress.suffixText = ""
        arcProgress.strokeWidth = 15f
        arcProgress.bottomTextSize = 50f
        arcProgress.max = 10000000
        autoSearch.setText(sampleList[0])
        /***getColor deprecated***/
        arcProgress.textColor = getColor(R.color.white)
        arcProgress.setBackgroundColor(getColor(R.color.transparent))
        arcProgress.unfinishedStrokeColor = getColor(R.color.white)
        imageBack.setOnClickListener {
            finish()
        }
        buttonIssue.setOnClickListener {
            if (autoSearch.text.toString().trim { it <= ' ' } == "") {
                CustomToast.customToast(context)
                CustomToast.show(14)
            } else if (editPoints.text.toString().trim { it <= ' ' } == "") {
                CustomToast.customToast(context)
                CustomToast.show(17)
            } else if (editPoints.text.toString().trim { it <= ' ' }.toInt() > myPoint) {
                CustomToast.customToast(context)
                CustomToast.show(16)
            } else {
                submitPoints()
            }

        }
        autoSearch.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            val selectedData: String = autoSearch.text.toString()
            for (i in listRetailers!!.indices) {
                if (listRetailers!![i].name == selectedData) {
                    selectedId = listRetailers!![i].id
                    getUserData()
                    break
                } else {
                    selectedId = ""
                }
            }
        }
        autoSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    if (selectedId!!.isNotEmpty()) {
                        llData.visibility = View.VISIBLE
                    }
                } else {
                    selectedId = ""
                    llData.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        autoSearch.setOnTouchListener { _, _ ->
            autoSearch.showDropDown()
            false
        }

        autoSearch.setOnClickListener {
            autoSearch.showDropDown()
        }
    }

    private fun getUserData() {
        var userDataMainResponse: FetchUserDataMainResponseModel
        var userDataResponse: Response
        var userData: Data
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getUserResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context)
            ).enqueue(object : Callback<FetchUserDataMainResponseModel> {
                override fun onResponse(
                    call: Call<FetchUserDataMainResponseModel>,
                    response: retrofit2.Response<FetchUserDataMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        userDataMainResponse = response.body()!!
                        userDataResponse = userDataMainResponse.response
                        if (userDataResponse.status == "Success"){
                            userData = userDataResponse.data
                            val customerID: String = userData.customer_id
                            val address: String = userData.address
                            val name: String = userData.name
                            val phone: String = userData.phone
                            textID.text = ": $customerID"
                            textName.text = ": $name"
                            textAddress.text = ": $address"
                            textPhone.text = ": $phone"
                            llData.visibility = View.VISIBLE
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<FetchUserDataMainResponseModel>, t: Throwable) {
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

    private fun submitPoints() {
        var submitPointsResponse: SubmitPointsResponse
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getSubmitPointsResponse(
                PreferenceManager.getCustomerID(context),
                selectedId.toString(),
                "5",
                editPoints.text.toString(),
                "7"
            ).enqueue(object : Callback<SubmitPointsResponse> {
                override fun onResponse(
                    call: Call<SubmitPointsResponse>,
                    response: retrofit2.Response<SubmitPointsResponse>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        submitPointsResponse = response.body()!!
                        if (submitPointsResponse.response == 1){
                            CustomToast.customToast(context)
                            CustomToast.show(18)
                            autoSearch.setText("")
                            editPoints.setText("")
                            getMyPoints()
                        }else if (submitPointsResponse.response == 5){
                            CustomToast.customToast(context)
                            CustomToast.show(61)
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(67)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<SubmitPointsResponse>, t: Throwable) {
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

    private fun getMyPoints() {
        var pointsMainResponse: LoyaltyPointsMainResponseModel
        var pointsResponse: com.vkc.bluetyga.activity.home.model.loyalty_points.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getLoyaltyPointsResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context)
            ).enqueue(object : Callback<LoyaltyPointsMainResponseModel>{
                override fun onResponse(
                    call: Call<LoyaltyPointsMainResponseModel>,
                    response: retrofit2.Response<LoyaltyPointsMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        pointsMainResponse = response.body()!!
                        pointsResponse = pointsMainResponse.response
                        if (pointsResponse.status == "Success"){
                            val points = pointsResponse.loyality_point
                            myPoint = points.toInt()
                            arcProgress.progress = myPoint
                            getUsers()
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

    private fun getUsers() {
        var retailersMainResponse: RetailersMainResponseModel
        var retailersResponse: com.vkc.bluetyga.activity.issue_point.model.get_retailers.Response
        var retailersData: ArrayList<com.vkc.bluetyga.activity.issue_point.model.get_retailers.Data>
        var tempModel: com.vkc.bluetyga.activity.issue_point.model.get_retailers.Data
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getRetailersResponse(
                PreferenceManager.getCustomerID(context)
            ).enqueue(object : Callback<RetailersMainResponseModel>{
                override fun onResponse(
                    call: Call<RetailersMainResponseModel>,
                    response: retrofit2.Response<RetailersMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        retailersMainResponse = response.body()!!
                        retailersResponse = retailersMainResponse.response
                        if (retailersResponse.status == "Success"){
                            retailersData = retailersResponse.data
                            if (retailersData.size > 0){
                                tempModel =
                                    com.vkc.bluetyga.activity.issue_point.model.get_retailers.Data("","")
                                for (i in retailersData.indices){
                                    tempModel.id = retailersData[i].id
                                    tempModel.name = retailersData[i].name
                                    listRetailers!!.add(tempModel)
                                }
                                val listUser = java.util.ArrayList<String>()
                                for (i in listRetailers!!.indices) {
                                    listUser.add(
                                        listRetailers!![i].name
                                    )
                                }

                                val adapter = ArrayAdapter(
                                    context,
                                    android.R.layout.simple_list_item_1,
                                    listUser
                                )
                                autoSearch.threshold = 1
                                autoSearch.setAdapter<ArrayAdapter<String>>(adapter)
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

                override fun onFailure(call: Call<RetailersMainResponseModel>, t: Throwable) {
                    progressBarDialog.show()
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