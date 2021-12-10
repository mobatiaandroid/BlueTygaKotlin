package com.vkc.bluetyga.activity.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.vkc.bluetyga.BuildConfig
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.common.SignUpActivity
import com.vkc.bluetyga.activity.common.model.verify_otp.VerifyOTPMainResponseModel
import com.vkc.bluetyga.activity.customers.CustomersActivity
import com.vkc.bluetyga.activity.dealers.DealersActivity
import com.vkc.bluetyga.activity.profile.model.phone_update_otp.UpdatePhoneOTPMainResponseModel
import com.vkc.bluetyga.activity.profile.model.profile.Data
import com.vkc.bluetyga.activity.profile.model.profile.ProfileMainResponseModel
import com.vkc.bluetyga.activity.profile.model.profile.Response
import com.vkc.bluetyga.activity.profile.model.update_profile.UpdateProfileMainResponseModel
import com.vkc.bluetyga.utils.GenericKeyEvent
import com.vkc.bluetyga.utils.GenericTextWatcher
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import com.vkc.bluetyga.api.ApiClient
import id.zelory.compressor.Compressor
import id.zelory.compressor.FileUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.IOException

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
    var otpValue = ""
    var name: String = ""
    var owner: String = ""
    var district: String = ""
    var city: String = ""
    var state: String = ""
    var pincode: String = ""
    var phone: String = ""
    var url: String = ""
    var mobile2: String = ""
    var email: String = ""
    var outputFileUri: Uri? = null
    var profileData: Data? = null
    lateinit var fileCameraResult: File
    lateinit var fileGalleryResult: File
    lateinit var compressCameraResult: File
    lateinit var compressGalleryResult: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        context = this
        initialiseUI()
        getProfile()
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
        imageProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context, Manifest.permission.CAMERA)
                ) {
                    ActivityCompat.requestPermissions(
                        this@ProfileActivity,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        100)
                } else {
                    ActivityCompat.requestPermissions(
                        this@ProfileActivity,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        100)
                }
            } else {
                showCameraGalleryChoice()
            }
        }

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
        textMyCustomers.setOnClickListener {
            startActivity(Intent(
                this@ProfileActivity,
                CustomersActivity::class.java))
        }
    }

    private fun getProfile() {
        var profileMainResponse: ProfileMainResponseModel
        var profileResponse: Response
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
                    if (response.body() != null){
                        profileMainResponse = response.body()!!
                        profileResponse = profileMainResponse.response
                        if (profileResponse.status.equals("Success")){
                            profileData = profileResponse.data
                            name = profileData!!.name
                            owner = profileData!!.contact_person
                            district = profileData!!.district
                            city = profileData!!.city
                            state = profileData!!.state_name
                            pincode = profileData!!.pincode
                            phone = profileData!!.phone
                            url = profileData!!.image
                            mobile2 = profileData!!.phone2
                            email = profileData!!.email
                            editMobile2.setText(mobile2)
                            editEmail.setText(email)
                            editShop.setText(name)
                            editOwner.setText(owner)
                            editDistrict.setText(district)
                            editMobile.setText(phone)
                            editPlace.setText(city)
                            editState.setText(state)
                            editPin.setText(pincode)
                            editAddress.setText(profileData!!.address)
                            textCustomerID.text = "CUST_ID: - ${profileData!!.customer_id}"
                            Glide.with(context).load(url).placeholder(R.drawable.profile_image)
                                .into(imageProfile)

                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
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



    private fun showCameraGalleryChoice() {
        val dialogGetImageFrom = AlertDialog.Builder(context)
        dialogGetImageFrom.setTitle(resources.getString(R.string.select_item))
        val options = arrayOf<CharSequence>(
            context.resources.getString(R.string.take_picture),
            context.resources.getString(R.string.open_gallery)
        )
        dialogGetImageFrom.setItems(
            options
        ) { dialog, which ->
            when(which){
                0 -> {
                    try{
                        val imageFileName =
                            System.currentTimeMillis().toString() + ".jpg"
                        val storageDir: File = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                        filePath = storageDir.absolutePath + "/" + imageFileName
                        Log.e("PICTUREPATH : ", filePath)
                        val file = File(filePath)
                        outputFileUri = FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + "." + localClassName + ".provider",
                            file);
                        val cameraIntent =
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
                        startActivityForResult(cameraIntent, 0)
                    }catch (e: Exception){
                        Log.e("Error",e.toString())
                    }
                }
                1 -> {
                    val pickPhoto = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    startActivityForResult(Intent.createChooser(pickPhoto,resources.getString(
                        R.string.select_item)), 1)

                }
            }
        }
        dialogGetImageFrom.show()
    }

    private fun alertUpdateMobile(context: Activity) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_update)
        val textYes = dialog.findViewById<View>(R.id.textYes)
        val textNo = dialog.findViewById<View>(R.id.textNo)
        textYes.setOnClickListener {
            dialog.dismiss()
            updateMobile()
        }
        textNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateMobile() {
        var updatePhoneOTPMainResponse: UpdatePhoneOTPMainResponseModel
        var updatePhoneOTPResponse: com.vkc.bluetyga.activity.profile.model.phone_update_otp.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getPhoneUpdateResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context),
                PreferenceManager.getMobile(context)
            ).enqueue(object : Callback<UpdatePhoneOTPMainResponseModel> {
                override fun onResponse(
                    call: Call<UpdatePhoneOTPMainResponseModel>,
                    response: retrofit2.Response<UpdatePhoneOTPMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        updatePhoneOTPMainResponse = response.body()!!
                        updatePhoneOTPResponse = updatePhoneOTPMainResponse.response
                        if (updatePhoneOTPResponse.status.equals("Success")){
                            alertOTPDialog(context)
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<UpdatePhoneOTPMainResponseModel>, t: Throwable) {
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

    private fun alertOTPDialog(context: Activity) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_otp_alert_no_resend)
        val editOTP1 = dialog.findViewById<View>(R.id.editOtp1) as EditText
        val editOTP2 = dialog.findViewById<View>(R.id.editOtp2) as EditText
        val editOTP3 = dialog.findViewById<View>(R.id.editOtp3) as EditText
        val editOTP4 = dialog.findViewById<View>(R.id.editOtp4) as EditText
        val textCancel = dialog.findViewById<View>(R.id.textCancel) as TextView
        val textOTP = dialog.findViewById<View>(R.id.textOtp) as TextView
        val buttonCancel = dialog.findViewById<View>(R.id.buttonCancel) as Button
        val mob = PreferenceManager.getMobile(context).substring(6, 10)
        textOTP.text = "OTP has been sent to  XXXXXX$mob"
        editOTP1.isCursorVisible = false
        textCancel.setOnClickListener {
            dialog.dismiss()
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    if (editOTP1.text.isNotEmpty()
                        && editOTP2.text.isNotEmpty()
                        && editOTP3.text.isNotEmpty()
                        && editOTP4.text.isNotEmpty()
                    ) {
                        otpValue = editOTP1.text.toString().trim() + editOTP2.text.toString()
                            .trim() + editOTP3.text.toString().trim() + editOTP4.text.toString()
                            .trim()
                        verifyOTP(otpValue, editMobile.text.toString().trim())
                    }
                }
            }
        })
        dialog.show()
    }

    private fun verifyOTP(otpValue: String, phone: String) {
        var verifyOTPMainResponse: VerifyOTPMainResponseModel
        var verifyOTPResponse: com.vkc.bluetyga.activity.common.model.verify_otp.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getVerifyOTPResponse(
                otpValue,
                PreferenceManager.getUserType(context),
                PreferenceManager.getCustomerID(context),
                phone,
                "1"
            ).enqueue( object : Callback<VerifyOTPMainResponseModel>{
                override fun onResponse(
                    call: Call<VerifyOTPMainResponseModel>,
                    response: retrofit2.Response<VerifyOTPMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        verifyOTPMainResponse = response.body()!!
                        verifyOTPResponse = verifyOTPMainResponse.response
                        if (verifyOTPResponse.status.equals("Success")){
                            CustomToast.customToast(context)
                            CustomToast.show(55)
                            val intent = Intent(
                                this@ProfileActivity,
                                SignUpActivity::class.java
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }else{
                            /*** Do Nothing ***/
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

    private fun updateProfile() {
        var updateProfileMainResponse: UpdateProfileMainResponseModel
        var updateProfileResponse: com.vkc.bluetyga.activity.profile.model.update_profile.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            var requestFile: RequestBody? = null
            var profilePic: MultipartBody.Part? = null
            val file = File(filePath)
            if (file.length() > 0) {
                requestFile =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                profilePic = MultipartBody.Part.createFormData("image", file.name, requestFile)
            }
            val customerID: RequestBody = PreferenceManager.getCustomerID(context)
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val role: RequestBody = PreferenceManager.getUserType(context)
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val mobileNo: RequestBody = editMobile.text.toString().trim()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val ownerName: RequestBody = editOwner.text.toString().trim()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val place: RequestBody = editPlace.text.toString().trim()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val mobileNo2: RequestBody = editMobile2.text.toString().trim()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val email: RequestBody = editEmail.text.toString().trim()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (profilePic != null) {
                ApiClient.getApiService().getUpdateProfileResponse(
                    customerID,
                    role,
                    mobileNo,
                    ownerName,
                    place,
                    mobileNo2,
                    email,
                    profilePic
                ).enqueue(object : Callback<UpdateProfileMainResponseModel> {
                    override fun onResponse(
                        call: Call<UpdateProfileMainResponseModel>,
                        response: retrofit2.Response<UpdateProfileMainResponseModel>,
                    ) {
                        progressBarDialog.hide()

                        updateProfileMainResponse = response.body()!!
                        updateProfileResponse = updateProfileMainResponse.response
                        if (updateProfileResponse.status.equals("Success")){
                            CustomToast.customToast(context)
                            CustomToast.show(26)
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(27)
                        }
                    }

                    override fun onFailure(call: Call<UpdateProfileMainResponseModel>, t: Throwable) {
                        progressBarDialog.hide()
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                        Log.e("error43564", t.toString())
                    }

                })
            }else{
                ApiClient.getApiService().getUpdateProfileResponseNoImage(
                    customerID,
                    role,
                    mobileNo,
                    ownerName,
                    place,
                    mobileNo2,
                    email
                ).enqueue(object : Callback<UpdateProfileMainResponseModel> {
                    override fun onResponse(
                        call: Call<UpdateProfileMainResponseModel>,
                        response: retrofit2.Response<UpdateProfileMainResponseModel>,
                    ) {
                        progressBarDialog.hide()

                        updateProfileMainResponse = response.body()!!
                        updateProfileResponse = updateProfileMainResponse.response
                        if (updateProfileResponse.status.equals("Success")){
                            CustomToast.customToast(context)
                            CustomToast.show(26)
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(27)
                        }
                    }

                    override fun onFailure(call: Call<UpdateProfileMainResponseModel>, t: Throwable) {
                        progressBarDialog.hide()
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                        Log.e("error43564", t.toString())
                    }

                })
            }
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            fileCameraResult = File(filePath)
            if (fileCameraResult.exists()) {
                try {
                    fileCameraResult = FileUtil.from(context, outputFileUri)
                    compressCameraResult = Compressor.Builder(context)
                        .setMaxWidth(940f)
                        .setMaxHeight(800f)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(
                            Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES
                            ).absolutePath
                        )
                        .build()
                        .compressToFile(fileCameraResult)
                    Glide.with(context).load(outputFileUri).placeholder(R.drawable.profile_image)
                        .into(imageProfile)
                    filePath = compressCameraResult.path
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(context, "Cannot show image", Toast.LENGTH_SHORT).show()
                return
            }
            try {
                fileGalleryResult = FileUtil.from(context, data.data)
                compressGalleryResult = Compressor.Builder(context)
                    .setMaxWidth(940f)
                    .setMaxHeight(800f)
                    .setQuality(100)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        ).absolutePath
                    )
                    .build()
                    .compressToFile(fileGalleryResult)
                Glide.with(context).load(data.data).placeholder(R.drawable.profile_image)
                    .into(imageProfile)
                Log.e("path", fileGalleryResult.path)
                filePath = compressGalleryResult.path
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}