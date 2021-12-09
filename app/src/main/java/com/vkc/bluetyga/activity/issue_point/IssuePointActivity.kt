package com.vkc.bluetyga.activity.issue_point

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.github.lzyzsd.circleprogress.ArcProgress
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.home.HomeActivity
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import java.util.ArrayList

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
    var sampleList = arrayListOf<String>("Dealer","Retailer","Sub-Dealer")
//    var listUsers: ArrayList<com.vkc.loyaltyme.activity.issue_points.model.user_type.Data>? = null
    var selectedId: String? = null
    var myPoint = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_point)
        context = this
        initialiseUI()
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
    }

    private fun submitPoints() {
        TODO("Not yet implemented")
    }
}