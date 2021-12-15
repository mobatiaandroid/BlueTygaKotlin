package com.vkc.bluetyga.api

import com.vkc.bluetyga.activity.cart.model.dealer_sub_dealer.DealerSubDealerMainResponseModel
import com.vkc.bluetyga.activity.cart.model.delete_cart.DeleteCartMainResponseModel
import com.vkc.bluetyga.activity.cart.model.edit_cart.EditCartMainResponseModel
import com.vkc.bluetyga.activity.common.model.district.DistrictResponseModel
import com.vkc.bluetyga.activity.common.model.new_register.NewRegisterMainResponseModel
import com.vkc.bluetyga.activity.common.model.register.RegisterMainResponseModel
import com.vkc.bluetyga.activity.common.model.resend_otp.ResendOTPMainResponse
import com.vkc.bluetyga.activity.common.model.state.StateResponseModel
import com.vkc.bluetyga.activity.common.model.user_details.UserDetailsMainResponseModel
import com.vkc.bluetyga.activity.common.model.verify_otp.VerifyOTPMainResponseModel
import com.vkc.bluetyga.activity.customers.model.get_customers.CustomersMainResponseModel
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer.RedeemedGiftsMainResponseModel
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.RedeemReportMainResponseModel
import com.vkc.bluetyga.activity.dealers.model.assign_dealers.AssignDealersMainResponseModel
import com.vkc.bluetyga.activity.dealers.model.get_dealers.DealersMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.add_to_cart.AddToCartMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.cart_count.CartCountResponseModel
import com.vkc.bluetyga.activity.gifts.model.get_cart.GetCartMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.gift.GiftsMainResponseModel
import com.vkc.bluetyga.activity.home.model.app_version.AppVersionMainResponseModel
import com.vkc.bluetyga.activity.home.model.loyalty_points.LoyaltyPointsMainResponseModel
import com.vkc.bluetyga.activity.home.model.register_device.RegisterDeviceMainResponseModel
import com.vkc.bluetyga.activity.inbox.model.inbox.NotificationMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.fetch_user_data.FetchUserDataMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.get_retailers.RetailersMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.get_users.GetUsersMainResponseModel
import com.vkc.bluetyga.activity.issue_point.model.submit_points_response.SubmitPointsResponse
import com.vkc.bluetyga.activity.point_history.model.transaction.TransactionMainResponseModel
import com.vkc.bluetyga.activity.profile.model.phone_update_otp.UpdatePhoneOTPMainResponseModel
import com.vkc.bluetyga.activity.profile.model.profile.ProfileMainResponseModel
import com.vkc.bluetyga.activity.profile.model.update_profile.UpdateProfileMainResponseModel
import com.vkc.bluetyga.activity.shop_image.model.delete_image.DeleteImageResponse
import com.vkc.bluetyga.activity.shop_image.model.get_image.GetImageMainResponseModel
import com.vkc.bluetyga.activity.shop_image.model.image.ImageModel
import com.vkc.bluetyga.activity.shop_image.model.upload_image.UploadImageMainResponse
import com.vkc.bluetyga.activity.sub_dealer_redeem.model.redeem_history.SubDealerRedeemHistoryMainResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @POST("getstate")
    fun getStateResponse(): Call<StateResponseModel>

    @FormUrlEncoded
    @POST("getdistrict")
    fun getDistrictResponse(
        @Field("state") stateId: String
    ): Call<DistrictResponseModel>

    @FormUrlEncoded
    @POST("getuserdetailswithMobile")
    fun getUserDetailsResponse(
        @Field("mobile") mobileNo: String,
        @Field("customer_id") customerID: String,
        @Field("imei_no") imeiNo: String
    ): Call<UserDetailsMainResponseModel>

    @FormUrlEncoded
    @POST("registration")
    fun getRegisterResponse(
        @Field("phone") phone: String,
        @Field("role") role: String,
        @Field("cust_id") customerID: String,
        @Field("contact_person") owner: String,
        @Field("city") city:String
    ): Call<RegisterMainResponseModel>

    @FormUrlEncoded
    @POST("OTP_verification")
    fun getVerifyOTPResponse(
        @Field("otp") otp: String,
        @Field("role") role: String,
        @Field("cust_id") customerID: String,
        @Field("phone") mobileNo: String,
        @Field("isnewMobile") isNewMobile: String
    ): Call<VerifyOTPMainResponseModel>

    @FormUrlEncoded
    @POST("newRegRequest")
    fun getNewRegisterResponse(
        @Field("customer_id") customerID: String,
        @Field("shop_name") shopName: String,
        @Field("state_name") stateName: String,
        @Field("district") district: String,
        @Field("city") city: String,
        @Field("pincode") pincode: String,
        @Field("contact_person") contactPerson: String,
        @Field("phone") phone: String,
        @Field("door_no") doorNo: String,
        @Field("address_line1") addressLine1: String,
        @Field("landmark") landmark: String,
        @Field("user_type") userType: String
    ): Call<NewRegisterMainResponseModel>

    @FormUrlEncoded
    @POST("resend_otp")
    fun getResendOTPResponse(
        @Field("role") role: String,
        @Field("cust_id") customerID: String
    ): Call<ResendOTPMainResponse>


    @FormUrlEncoded
    @POST("getLoyalityPoints")
    fun getLoyaltyPointsResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String
    ): Call<LoyaltyPointsMainResponseModel>

    @POST("loyalty_appversion")
    fun getAppVersionResponse(): Call<AppVersionMainResponseModel>

    @FormUrlEncoded
    @POST("device_registration")
    fun getDeviceRegistrationResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String,
        @Field("device_id") deviceID: String
    ): Call<RegisterDeviceMainResponseModel>


    @FormUrlEncoded
    @POST("getProfile")
    fun getProfileResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String
    ): Call<ProfileMainResponseModel>

    @FormUrlEncoded
    @POST("getDealerswithState")
    fun getDealersResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String,
        @Field("search_key") searchKey: String
    ): Call<DealersMainResponseModel>

    @FormUrlEncoded
    @POST("assignDealers")
    fun getSubmitDealersResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String,
        @Field("dealer_id") dealerID: String
    ): Call<AssignDealersMainResponseModel>
//
    @FormUrlEncoded
    @POST("phoneUpdateOTP")
    fun getPhoneUpdateResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String,
        @Field("phone") phone: String
    ): Call<UpdatePhoneOTPMainResponseModel>

    @FormUrlEncoded
    @POST("getMyCustomers")
    fun getCustomersResponse(
        @Field("cust_id") customerID: String
    ): Call<CustomersMainResponseModel>

    @FormUrlEncoded
    @POST("NotificationsList")
    fun getNotificationResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String
    ): Call<NotificationMainResponseModel>

    @FormUrlEncoded
    @POST("fetchUserData")
    fun getUserResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String
    ): Call<FetchUserDataMainResponseModel>

    @FormUrlEncoded
    @POST("getRetailerswithState")
    fun getRetailersResponse(
        @Field("cust_id") customerID: String
    ): Call<RetailersMainResponseModel>

    @FormUrlEncoded
    @POST("issueLoyalityPoints")
    fun getSubmitPointsResponse(
        @Field("userid") customerID: String,
        @Field("to_user_id") selectedID: String,
        @Field("to_role") toRole: String,
        @Field("points") points: String,
        @Field("role") role: String
    ): Call<SubmitPointsResponse>

    @FormUrlEncoded
    @POST("transaction_history")
    fun getTransactionHistoryResponse(
        @Field("userid") customerID: String,
        @Field("role") role: String,
        @Field("type") type: String
    ): Call<TransactionMainResponseModel>

    @Multipart
    @POST("profile_updation")
    fun getUpdateProfileResponse(
    @Part("cust_id") customerID: RequestBody,
    @Part("role") role: RequestBody,
    @Part("phone") mobileNo: RequestBody,
    @Part("contact_person") contactPerson: RequestBody,
    @Part("city") city: RequestBody,
    @Part("phone2") mobileNo2: RequestBody,
    @Part("email") email: RequestBody,
    @Part image: MultipartBody.Part
    ): Call<UpdateProfileMainResponseModel>

    @Multipart
    @POST("profile_updation")
    fun getUpdateProfileResponseNoImage(
        @Part("cust_id") customerID: RequestBody,
        @Part("role") role: RequestBody,
        @Part("phone") mobileNo: RequestBody,
        @Part("contact_person") contactPerson: RequestBody,
        @Part("city") city: RequestBody,
        @Part("phone2") mobileNo2: RequestBody,
        @Part("email") email: RequestBody
    ): Call<UpdateProfileMainResponseModel>

    @FormUrlEncoded
    @POST("getUsers")
    fun getUsersListResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String
    ): Call<GetUsersMainResponseModel>

    @FormUrlEncoded
    @POST("last_uploaded_image")
    fun getImageResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String
    ): Call<GetImageMainResponseModel>

    @FormUrlEncoded
    @POST("uploaded_images_history")
    fun getImageHistoryResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String
    ): Call<ImageModel>

    @Multipart
    @POST("upload_shop_images")
    fun uploadShopImage(
        @Part("cust_id") customerID: RequestBody,
        @Part("role") role: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<UploadImageMainResponse>

    @FormUrlEncoded
    @POST("delete_uploaded_images")
    fun deleteShopImage(
        @Field("id") id: String
    ): Call<DeleteImageResponse>

    @FormUrlEncoded
    @POST("redeem_historyForSubdealer")
    fun getRedeemHistoryForSubDealer(
        @Field("cust_id") customerID: String
    ): Call<SubDealerRedeemHistoryMainResponse>

    @FormUrlEncoded
    @POST("Redeemed_gifts")
    fun getRedeemHistoryForDealer(
        @Field("cust_id") customerID: String
    ): Call<RedeemedGiftsMainResponseModel>

    @FormUrlEncoded
    @POST("RedeemReportForApp")
    fun getRedeemReportResponse(
        @Field("dealerId") dealerID: String
    ): Call<RedeemReportMainResponseModel>

    @FormUrlEncoded
    @POST("getGifts")
    fun getGiftsResponse(
        @Field("cust_id") customerID: String
    ): Call<GiftsMainResponseModel>

    @FormUrlEncoded
    @POST("addGiftsCartItem")
    fun getAddToCartResponse(
        @Field("cust_id") customerID: String,
        @Field("gift_id") giftId: String,
        @Field("quantity") quantity: String,
        @Field("gift_type") giftType: String
    ): Call<AddToCartMainResponseModel>

    @FormUrlEncoded
    @POST("GiftcartList")
    fun getCartResponse(
        @Field("cust_id") customerID: String
    ): Call<GetCartMainResponseModel>

    @FormUrlEncoded
    @POST("giftCartCount")
    fun getCartCountResponse(
        @Field("cust_id") customerID: String
    ): Call<CartCountResponseModel>

    @FormUrlEncoded
    @POST("updateGiftCart")
    fun getEditCartResponse(
        @Field("cust_id") customerID: String,
        @Field("id") giftID: String,
        @Field("quantity") quantity: String
    ): Call<EditCartMainResponseModel>

    @FormUrlEncoded
    @POST("deleteGiftCart")
    fun getDeleteCartResponse(
        @Field("cust_id") customerID: String,
        @Field("ids") deleteIDs: String
    ): Call<DeleteCartMainResponseModel>

    @FormUrlEncoded
    @POST("myDealers_Subdealers")
    fun getDealerAndSubDealersResponse(
        @Field("cust_id") customerID: String,
        @Field("role") role: String
    ): Call<DealerSubDealerMainResponseModel>
}
