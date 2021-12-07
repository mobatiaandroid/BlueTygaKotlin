package com.vkc.bluetyga.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.TextView
import android.widget.Toast
import com.vkc.bluetyga.R

@SuppressLint("StaticFieldLeak")
object CustomToast {

    lateinit var activity: Activity
    lateinit var textView: TextView
    lateinit var toast: Toast

    fun customToast(mActivity : Activity) {
        CustomToast.activity = mActivity

        init()

    }

    fun init() {
        val inflater = activity.layoutInflater
        val layoutToast = inflater.inflate(R.layout.custom_toast, null)
        textView = layoutToast.findViewById(R.id.texttoast)
        toast = Toast(activity)
        toast.view = layoutToast
    }

    fun show(errorCode: Int) {
        if (errorCode == 0) {
            textView.text = activity.resources.getString(
                R.string.common_error
            )
        }
        if (errorCode == 1) {
            textView.text = "Successfully logged in."
        }
        if (errorCode == 2) {
            textView.text = "Login failed.Please try again later"
        }
        if (errorCode == 3) {
            textView.text = "Successfully submitted login request"
        }
        if (errorCode == 4) {
            textView.text = "Invalid user type"
        }
        if (errorCode == 5) {
            textView.text = "No results found"
        }
        if (errorCode == 6) {
            textView.text = "Successfully added to cart"
        }
        if (errorCode == 7) {
            textView.text = "Already redeemed this gift"
        }
        if (errorCode == 8) {
            textView.text = "OTP Verification Successful"
        }
        if (errorCode == 9) {
            textView.text = "Incorrect OTP"
        }
        if (errorCode == 10) {
            textView.text = "Please select dealers"
        }
        if (errorCode == 11) {
            textView.text = "Cannot assign more than 10 Dealers"
        }
        if (errorCode == 12) {
            textView.text = "Dealers added successfully"
        }
        if (errorCode == 13) {
            textView.text = "No record found"
        }
        if (errorCode == 14) {
            textView.text = "Please select a retailer"
        }
        if (errorCode == 15) {
            textView.text = "Please enter coupon value"
        }
        if (errorCode == 16) {
            textView.text = "Coupon value should not be greater than credit value"
        }
        if (errorCode == 17) {
            textView.text = "Please enter coupon value to issue"
        }
        if (errorCode == 18) {
            textView.text = "Coupon issued successfully"
        }
        if (errorCode == 19) {
            textView.text = "Image uploaded successfully"
        }
        if (errorCode == 20) {
            textView.text = "You have exceeded the image upload limit for this week"
        }
        if (errorCode == 21) {
            textView.text = "Please capture an image to upload"
        }
        if (errorCode == 22) {
            textView.text = "Insufficient coupon balance to redeem the gift"
        }
        if (errorCode == 23) {
            textView.text = "This feature is only available for retailers"
        }
        if (errorCode == 24) {
            textView.text = "Failed.Try again later"
        }
        if (errorCode == 25) {
            textView.text = "Please select a distributor"
        }
        if (errorCode == 26) {
            textView.text = "Profile updated successfully"
        }
        if (errorCode == 27) {
            textView.text = "Profile updation failed"
        }
        if (errorCode == 28) {
            textView.text =
                "Your registration with VKC Loyalty is on hold.Please login after verification"
        }
        if (errorCode == 29) {
            textView.text = "Cannot login using multiple devices. Please contact VKC"
        }
        if (errorCode == 30) {
            textView.text =
                "Mobile number updated successfully. Please login using new mobile number"
        }
        if (errorCode == 31) {
            textView.text = "This feature is currently not available."
        }
        if (errorCode == 32) {
            textView.text = "Please select state."
        }
        if (errorCode == 33) {
            textView.text = "Please select district."
        }
        if (errorCode == 34) {
            textView.text = "OTP sent successfully."
        }
        if (errorCode == 35) {
            textView.text = "Already registered with scheme"
        }
        if (errorCode == 36) {
            textView.text = "Please enter search key"
        }
        if (errorCode == 37) {
            textView.text = "Please agree terms and conditions to continue."
        }
        if (errorCode == 38) {
            textView.text = "Do not have enough coupons to add to cart"
        }
        if (errorCode == 39) {
            textView.text = "Add to cart failed"
        }
        if (errorCode == 40) {
            textView.text = "Please enter quantity value"
        }
        if (errorCode == 41) {
            textView.text = "Please select a voucher"
        }
        if (errorCode == 42) {
            textView.text = "Please enter the quantity value"
        }
        if (errorCode == 43) {
            textView.text = "No items in cart"
        }
        if (errorCode == 44) {
            textView.text = "No dealers found"
        }
        if (errorCode == 45) {
            textView.text = "Please select dealer"
        }
        if (errorCode == 46) {
            textView.text = "Not enough data to place order"
        }
        if (errorCode == 47) {
            textView.text = "Order Placed Successfully"
        }
        if (errorCode == 48) {
            textView.text = "Unable to place order. Try again"
        }
        if (errorCode == 49) {
            textView.text = "Unable to delete data. Try again"
        }
        if (errorCode == 50) {
            textView.text = "No messages found"
        }
        if (errorCode == 51) {
            textView.text = "No images uploaded this week"
        }
        if (errorCode == 52) {
            textView.text = "Image Deleted Successfully"
        }
        if (errorCode == 53) {
            textView.text = "Please enter mobile number"
        }
        if (errorCode == 54) {
            textView.text = "Invalid mobile number"
        }

        if (errorCode == 55) {
            textView.text = "Mobile number updated successfully. Please login again."
        }
        if (errorCode == 56) {
            textView.text = "Updation Failed."
        }
        if (errorCode == 57) {
            textView.text = "Please select user type"
        }
        if (errorCode == 58) {
            textView.text = "No internet connectivity"
        }
        if (errorCode == 59) {
            textView.text = "Please enter a valid quantity"
        }
        if (errorCode == 60) {
            textView.text = "Quantity cannot be 0"
        }
        if (errorCode == 61) {
            textView.text =
                "Unable to issue coupons,since there is no scheme running in your state"

        }
        if (errorCode == 62) {
            textView.text =
                "Unable to fetch cart count,since there is no scheme running in your state"

        }
        if (errorCode == 63) {
            textView.text = "Unable to update cart,since there is no scheme running in your state"

        }
        if (errorCode == 64) {
            textView.text = "Unable to add to cart,since there is no scheme running in your state"

        }
        if (errorCode == 65) {
            textView.text = "Please select retailer"

        }
        if (errorCode == 66) {
            textView.text = "No retailer redeem data found"

        }
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}