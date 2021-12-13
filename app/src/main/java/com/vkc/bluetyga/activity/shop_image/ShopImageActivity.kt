package com.vkc.bluetyga.activity.shop_image

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.vkc.bluetyga.BuildConfig
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.shop_image.model.delete_image.DeleteImageResponse
import com.vkc.bluetyga.activity.shop_image.model.get_image.Data
import com.vkc.bluetyga.activity.shop_image.model.get_image.GetImageMainResponseModel
import com.vkc.bluetyga.activity.shop_image.model.get_image.Response
import com.vkc.bluetyga.activity.shop_image.model.image.ImageModel
import com.vkc.bluetyga.activity.shop_image.model.upload_image.UploadImageMainResponse
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
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

class ShopImageActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var imageShop: ImageView
    lateinit var imageBack: ImageView
    lateinit var buttonCapture: Button
    lateinit var buttonUpload: Button
    lateinit var headerManager: HeaderManager
    lateinit var imageOne: ImageView
    lateinit var imageTwo: ImageView
    lateinit var imageOneDelete: ImageView
    lateinit var imageTwoDelete: ImageView
    lateinit var viewOne: ConstraintLayout
    lateinit var viewTwo: ConstraintLayout
    lateinit var progressBarDialog: ProgressBarDialog
    var filePath = ""
    lateinit var imageList: ArrayList<com.vkc.bluetyga.activity.shop_image.model.image.Data>
    private val destination: File? = null
    var outputFileUri: Uri? = null
    lateinit var fileCameraResult: File
    lateinit var fileGalleryResult: File
    lateinit var compressCameraResult: File
    lateinit var compressGalleryResult: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_image)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        imageShop = findViewById(R.id.imageShop)
        headerManager =
            HeaderManager(this@ShopImageActivity, resources.getString(R.string.shop_image))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        imageOne = findViewById(R.id.imageOne)
        imageTwo = findViewById(R.id.imageTwo)
        imageOneDelete = findViewById(R.id.deleteImage1)
        imageTwoDelete = findViewById(R.id.deleteImage2)
        viewOne = findViewById(R.id.constraint1)
        viewTwo = findViewById(R.id.constraint2)
        buttonCapture = findViewById(R.id.buttonCapture)
        buttonUpload = findViewById(R.id.buttonUpload)
        progressBarDialog = ProgressBarDialog(context)

        imageOneDelete.visibility = View.GONE
        imageTwoDelete.visibility = View.GONE
        imageList = ArrayList()
        getImage()
        imageBack.setOnClickListener { finish() }
        buttonCapture.setOnClickListener { showCamera() }
        buttonUpload.setOnClickListener {
            if (filePath == ""){
                CustomToast.customToast(context)
                CustomToast.show(21)
            }else{
                uploadImage()
            }
        }
        imageOne.setOnClickListener {  }
        imageTwo.setOnClickListener {  }
        imageOneDelete.setOnClickListener { showDeleteImageAlert(context, imageList[0].id) }
        imageTwoDelete.setOnClickListener { showDeleteImageAlert(context, imageList[1].id) }
    }

    private fun showDeleteImageAlert(context: Activity, id: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete)
        val textYes = dialog.findViewById<View>(R.id.textYes)
        val textNo = dialog.findViewById<View>(R.id.textNo)
        textYes.setOnClickListener {
            dialog.dismiss()
            deleteImage(id)
            dialog.dismiss()
        }
        textNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteImage(id: String) {
        var deleteImageResponse: DeleteImageResponse
        if(UtilityMethods.checkInternet(context)){
            ApiClient.getApiService().deleteShopImage(id)
                .enqueue(object : Callback<DeleteImageResponse> {
                    override fun onResponse(
                        call: Call<DeleteImageResponse>,
                        response: retrofit2.Response<DeleteImageResponse>
                    ) {
                        progressBarDialog.hide()
                        deleteImageResponse = response.body()!!
                        if (deleteImageResponse.status == "Success"){
                            CustomToast.customToast(context)
                            CustomToast.show(52)
                            getImageHistory()
                        }else if (deleteImageResponse.status == "Error"){
                            getImageHistory()
                        }
                    }

                    override fun onFailure(call: Call<DeleteImageResponse>, t: Throwable) {
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

    private fun uploadImage() {
        var imageUploadMainResponse: UploadImageMainResponse
        var imageUploadResponse: com.vkc.bluetyga.activity.shop_image.model.upload_image.Response
        val customerID: RequestBody = PreferenceManager.getCustomerID(context)
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val role: RequestBody = PreferenceManager.getUserType(context)
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        var requestFile: RequestBody? = null
        var uploadImageFile: MultipartBody.Part? = null
        val file = File(filePath)
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            if (file.length() > 0) {
                requestFile =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                uploadImageFile = MultipartBody.Part.createFormData("image", file.name, requestFile)
            }
            if (uploadImageFile != null) {
                ApiClient.getApiService().uploadShopImage(
                    customerID,role,uploadImageFile
                ).enqueue(object : Callback<UploadImageMainResponse>{
                    override fun onResponse(
                        call: Call<UploadImageMainResponse>,
                        response: retrofit2.Response<UploadImageMainResponse>
                    ) {
                        progressBarDialog.hide()
                        imageUploadMainResponse = response.body()!!
                        imageUploadResponse = imageUploadMainResponse.response
                        if (imageUploadResponse.status == "Success"){
                            CustomToast.customToast(context)
                            CustomToast.show(19)
                            getImageHistory()
                        }else if (imageUploadResponse.status == "Exceeded"){
                            CustomToast.customToast(context)
                            CustomToast.show(20)
                            getImageHistory()
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }

                    override fun onFailure(call: Call<UploadImageMainResponse>, t: Throwable) {
                        progressBarDialog.hide()
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                })
            }
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }

    private fun showCamera() {
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
                        .into(imageShop)
                    filePath = compressCameraResult.path
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun getImage() {
        var getImageMainResponse: GetImageMainResponseModel
        var getImageResponse: Response
        var getImageData: ArrayList<Data>
        if(UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getImageResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context)
            ).enqueue(object : Callback<GetImageMainResponseModel> {
                override fun onResponse(
                    call: Call<GetImageMainResponseModel>,
                    response: retrofit2.Response<GetImageMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        getImageMainResponse = response.body()!!
                        getImageResponse = getImageMainResponse.response
                        if (getImageResponse.status == "Success"){
                            getImageData = getImageResponse.data
                            val imageUrl: String = getImageData[0].image
                            Glide.with(context).load(imageUrl).placeholder(R.drawable.shop_image)
                                .into(imageShop)
                            getImageHistory()
                        }else{
                            /***Do Nothing***/
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }

                }

                override fun onFailure(call: Call<GetImageMainResponseModel>, t: Throwable) {
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

    private fun getImageHistory() {
        var getImageMainResponse: ImageModel
        var getImageResponse: com.vkc.bluetyga.activity.shop_image.model.image.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getImageHistoryResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context)
            ).enqueue(object : Callback<ImageModel> {
                override fun onResponse(
                    call: Call<ImageModel>,
                    response: retrofit2.Response<ImageModel>
                ) {
                    progressBarDialog.hide()
                    if (response != null){
                        getImageMainResponse = response.body()!!
                        getImageResponse = getImageMainResponse.response
                        if (getImageResponse.status == "Success"){
                            imageList = getImageResponse.data
                            if (imageList.size > 0){
                                if (imageList.size > 1){
                                    if (imageList[0].image != "") {
                                        viewOne.visibility = View.VISIBLE
                                        imageOneDelete.visibility = View.VISIBLE
                                        Glide.with(context).load(imageList[0].image)
                                            .centerInside().into(imageOne)
                                    } else {
                                        viewOne.visibility = View.GONE
                                        imageOneDelete.visibility = View.GONE
                                    }

                                    if (imageList[1].image != "") {
                                        viewTwo.visibility = View.VISIBLE
                                        imageTwoDelete.visibility = View.VISIBLE
                                        Glide.with(context).load(imageList[1].image)
                                            .centerInside().into(imageTwo)
                                    } else {
                                        viewTwo.visibility = View.GONE
                                        imageTwoDelete.visibility = View.GONE
                                    }
                                }else{
                                    viewTwo.visibility = View.GONE
                                    imageTwoDelete.visibility = View.GONE
                                    if (imageList[0].image != "") {
                                        viewOne.visibility = View.VISIBLE
                                        imageOneDelete.visibility = View.VISIBLE
                                        Glide.with(context).load(imageList[0].image)
                                            .centerInside().into(imageOne)
                                    } else {
                                        viewOne.visibility = View.GONE
                                        imageOneDelete.visibility = View.GONE
                                    }
                                }
                            }else{
                                viewOne.visibility = View.GONE
                                imageOneDelete.visibility = View.GONE
                                viewTwo.visibility = View.GONE
                                imageTwoDelete.visibility = View.GONE
                                CustomToast.customToast(context)
                                CustomToast.show(51)
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

                override fun onFailure(call: Call<ImageModel>, t: Throwable) {
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