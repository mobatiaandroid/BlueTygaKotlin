package com.vkc.bluetyga.activity.issue_point

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.home.model.loyalty_points.LoyaltyPointsMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.get_users.Data
import com.vkc.bluetyga.activity.issue_point.model.get_users.GetUsersMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.submit_points_response.SubmitPointsResponse
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IssuePointDealerActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    private lateinit var headerManager: HeaderManager
    private lateinit var textPoints: TextView
    lateinit var editPoints: EditText
    private lateinit var buttonReset: Button
    private lateinit var buttonSubmit: Button
    private lateinit var llUserType: LinearLayout
    lateinit var userTypeSpinner: Spinner
    lateinit var autoSearch: AutoCompleteTextView
    lateinit var llData: LinearLayout
    private lateinit var textType: TextView
    private lateinit var textID: TextView
    private lateinit var textName: TextView
    private lateinit var textAddress: TextView
    private lateinit var textPhone: TextView
    private lateinit var imageBack: ImageView
    lateinit var progressBarDialog: ProgressBarDialog
    var categories: List<String> = arrayListOf("Select User Type","Retailer","Sub Dealer")
    var selectedID = ""
    var userType = ""
    var listUsers = ArrayList<Data>()
    private var point = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_point_dealer)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        textPoints = findViewById(R.id.textPoints)
        editPoints = findViewById(R.id.editPoints)
        buttonReset = findViewById(R.id.buttonReset)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        llUserType = findViewById(R.id.llUserType)
        userTypeSpinner = findViewById(R.id.spinnerUserType)
        autoSearch = findViewById(R.id.autoSearch)
        llData = findViewById(R.id.llData)
        textName = findViewById(R.id.textViewName)
        textID = findViewById(R.id.textViewId)
        textType = findViewById(R.id.textViewType)
        textAddress = findViewById(R.id.textViewAddress)
        textPhone = findViewById(R.id.textViewPhone)
        headerManager =
            HeaderManager(this@IssuePointDealerActivity,
                resources.getString(R.string.issue_point))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        headerManager.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        progressBarDialog = ProgressBarDialog(context)
        llData.visibility = View.GONE
//        categories.clear()
//        categories.add("Select User Type")
//        categories.add("Retailer")
//        categories.add("Sub Dealer")
        imageBack.setOnClickListener {
            finish()
        }
        buttonSubmit.setOnClickListener {
            if (userType == "") {
                CustomToast.customToast(context)
                CustomToast.show(49)
            } else if (autoSearch.text.toString().trim { it <= ' ' } == "") {
                CustomToast.customToast(context)
                CustomToast.show(51)
            } else if (editPoints.text.toString().trim { it <= ' ' } == "") {
                // VKCUtils.textWatcherForEditText(mEditPoint,
                // "Mandatory field");
                CustomToast.customToast(context)
                CustomToast.show(52)
            } else if (editPoints.text.toString().trim { it <= ' ' }.toInt() > point) {
                // FeedbackSubmitApi();
                CustomToast.customToast(context)
                CustomToast.show(48)
            } else {
                submitPoints()
            }
        }
        buttonReset.setOnClickListener {
            userTypeSpinner.setSelection(0)
            userType = ""
            selectedID = ""
            autoSearch.setText("")
            editPoints.setText("")
            llData.visibility = View.GONE
        }
        autoSearch.setOnClickListener {
            autoSearch.showDropDown()
        }
        autoSearch.onItemClickListener = OnItemClickListener { _, _, _, _ -> // TODO Auto-generated method stub
            val selectedData: String = autoSearch.text.toString()
            for (i in listUsers.indices) {
                if (listUsers[i].name == selectedData) {
                    selectedID = listUsers[i].id
                    println("Selected Id : $selectedID")
                    getUserData()
                    break
                } else {
                    selectedID = ""
                }
            }
        }
        autoSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    /***Do Nothing***/
                } else {
                    selectedID = ""
                    llData.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        val dataAdapter = ArrayAdapter(
            context, android.R.layout.simple_spinner_item, categories
        )

        // Drop down layout style - list view with radio button

        // Drop down layout style - list view with radio button
        dataAdapter
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner

        // attaching data adapter to spinner
        userTypeSpinner.adapter = dataAdapter
        userTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View, pos: Int,
                arg3: Long
            ) {
                // TODO Auto-generated method stub
                if (pos > 0) {
                    if (pos == 1) {
                        userType = "5"
                        selectedID = ""
                        autoSearch.setText("")
                        // mEditPoint.setText("");
                        getUsers(userType)
                    } else {
                        userType = "7"
                        selectedID = ""
                        autoSearch.setText("")
                        getUsers(userType)
                    }
                } else {
                    userType = ""
                }
                println("User Type : $userType")
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }
        getMyPoints()

    }

    private fun getUsers(userType: String) {
        var getUsersMainResponseModel: GetUsersMainResponseModel
        var getUsersResponse: com.vkc.bluetyga.activity.issue_point.model.get_users.Response
        var getUsersData: ArrayList<Data>
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getUsersListResponse(
                PreferenceManager.getCustomerID(context),
                userType
            ).enqueue( object : Callback<GetUsersMainResponseModel>{
                override fun onResponse(
                    call: Call<GetUsersMainResponseModel>,
                    response: Response<GetUsersMainResponseModel>
                ) {
                    progressBarDialog.show()
                    if (response != null){
                        getUsersMainResponseModel = response.body()!!
                        getUsersResponse = getUsersMainResponseModel.response
                        if (getUsersResponse.status == "Success"){
                            listUsers = getUsersResponse.data
                            if (listUsers.size > 0){
                                val listUser = ArrayList<String>()
                                for (i in listUsers.indices) {
                                    listUser.add(
                                        listUsers[i].name)
                                }
                                val adapter = ArrayAdapter(
                                    context,
                                    android.R.layout.simple_list_item_1,
                                    listUser
                                )
                                autoSearch.threshold = 1
                                autoSearch.setAdapter<ArrayAdapter<String>>(adapter)
                            }else{
                                CustomToast.customToast(context)
                                CustomToast.show(17)
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

                override fun onFailure(call: Call<GetUsersMainResponseModel>, t: Throwable) {
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
                    response: Response<LoyaltyPointsMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response != null){
                        pointsMainResponse = response.body()!!
                        pointsResponse = pointsMainResponse.response
                        if (pointsResponse.status.equals("Success")){
                            val points: String = pointsResponse.loyality_point
                            point = points.toInt()
                            textPoints.text = points
                        }else{
                            /***
                             * Do Nothing
                             * ***/
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

    private fun submitPoints() {
        var submitPointsResponse: SubmitPointsResponse
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getSubmitPointsResponse(
                PreferenceManager.getCustomerID(context),
                selectedID, userType, editPoints.text.toString(),
                PreferenceManager.getUserType(context)
            ).enqueue(object : Callback<SubmitPointsResponse>{
                override fun onResponse(
                    call: Call<SubmitPointsResponse>,
                    response: Response<SubmitPointsResponse>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        submitPointsResponse = response.body()!!
                        if (submitPointsResponse.response == 1){
                            CustomToast.customToast(context)
                            CustomToast.show(18)
                            autoSearch.setText("")
                            editPoints.setText("")

                            val dataAdapter = ArrayAdapter(
                                context,
                                android.R.layout.simple_spinner_item,
                                categories
                            )
                            dataAdapter
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            userTypeSpinner.adapter = dataAdapter
                            getMyPoints()
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

    private fun getUserData() {
        TODO("Not yet implemented")
    }
}