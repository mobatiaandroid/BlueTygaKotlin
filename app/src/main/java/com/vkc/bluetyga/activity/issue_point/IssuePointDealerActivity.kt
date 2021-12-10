package com.vkc.bluetyga.activity.issue_point

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.vkc.bluetyga.R
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.utils.ProgressBarDialog
import org.w3c.dom.Text

class IssuePointDealerActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var headerManager: HeaderManager
    lateinit var textPoints: TextView
    lateinit var editPoints: EditText
    lateinit var buttonReset: Button
    lateinit var buttonSubmit: Button
    lateinit var llUserType: LinearLayout
    lateinit var userTypeSpinner: Spinner
    lateinit var autoSearch: AutoCompleteTextView
    lateinit var llData: LinearLayout
    lateinit var textType: TextView
    lateinit var textID: TextView
    lateinit var textName: TextView
    lateinit var textAddress: TextView
    lateinit var textPhone: TextView
    lateinit var imageBack: ImageView
    lateinit var progressBarDialog: ProgressBarDialog
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

    }
}