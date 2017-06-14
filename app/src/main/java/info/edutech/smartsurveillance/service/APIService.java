package info.edutech.smartsurveillance.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import info.edutech.smartsurveillance.MyApplication;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.CaptureService;
import info.edutech.smartsurveillance.model.UserService;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.model.ValidationAlarm;
import info.edutech.smartsurveillance.model.ValidationLock;
import info.edutech.smartsurveillance.model.ValidationSMS;
import info.edutech.smartsurveillance.model.ValidationUser;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Baso on 11/12/2016.
 */
public interface APIService {

    @FormUrlEncoded
    @POST("login.php")
    Call<ValidationUser> login(@Field("phone_number") String phone_number, @Field("password") String password);

    @FormUrlEncoded
    @POST("register.php")
    Call<Validation> register(@Field("name") String name, @Field("phone_number") String phoneNumber, @Field("password") String password);

    @FormUrlEncoded
    @POST("updateuser.php")
    Call<Validation> updateUser(@Field("private_key") String private_key,@Field("id") int id,@Field("password") String password,@Field("name") String name, @Field("phone_number") String phoneNumber, @Field("new_password") String newPassword);

    @FormUrlEncoded
    @POST("config/getcameraconfig.php")
    Call<Validation> getCameraConfig(@Field("private_key") String private_key);

    @FormUrlEncoded
    @POST("config/getsmsconfig.php")
    Call<ValidationSMS> getSMSConfig(@Field("private_key") String private_key);

    @FormUrlEncoded
    @POST("config/getalarmconfig.php")
    Call<ValidationAlarm> getAlarmConfig(@Field("private_key") String private_key);

    @FormUrlEncoded
    @POST("config/getlockconfig.php")
    Call<ValidationLock> getLockConfig(@Field("private_key") String private_key);

    @FormUrlEncoded
    @POST("getuser.php")
    Call<UserService> getUsers(@Field("private_key") String private_key);

    @FormUrlEncoded
    @POST("getcapture.php")
    Call<CaptureService> getCaptures(@Field("private_key") String private_key);

    @FormUrlEncoded
    @POST("config/config_camera.php")
    Call<Validation> setCameraConfig(@Field("private_key") String private_key,@Field("quality") int quality);

    @FormUrlEncoded
    @POST("config/config_sms.php")
    Call<Validation> setSMSConfig(@Field("private_key") String private_key,@Field("send_when_fire") Boolean sendWhenFire,@Field("send_when_motion") Boolean sendWhenMotion, @Field("sms_fire") String smsFire,@Field("sms_motion") String smsMotion);

    @FormUrlEncoded
    @POST("config/config_alarm.php")
    Call<Validation> setAlarmConfig(@Field("private_key") String private_key,@Field("alarm_enable") Boolean alarmEnable,@Field("beep_motion") Boolean beepMotion,@Field("beep_fire") Boolean beepFire);

    @FormUrlEncoded
    @POST("config/config_lock.php")
    Call<Validation> setLockConfig(@Field("private_key") String private_key, @Field("duration") int duration,@Field("auto_lock") boolean autoLock);

    @FormUrlEncoded
    @POST("send_sms.php")
    Call<Validation> sendSMS(@Field("private_key") String private_key, @Field("numbers") String numbers, @Field("content") String content);

    @FormUrlEncoded
    @POST("adduser.php")
    Call<Validation> addUser(@Field("private_key") String private_key, @Field("name") String name, @Field("phone_number") String phoneNumber);

    @FormUrlEncoded
    @POST("edituser.php")
    Call<Validation> editUser(@Field("private_key") String private_key, @Field("id") int id,@Field("name") String name, @Field("phone_number") String phoneNumber);

    @FormUrlEncoded
    @POST("deleteuser.php")
    Call<Validation> deleteUser(@Field("private_key") String private_key, @Field("id") int id);

    @FormUrlEncoded
    @POST("alarm.php")
    Call<Validation> setAlarm(@Field("private_key") String private_key, @Field("alarm_number") String alarmNumber, @Field("action") String action);

    @FormUrlEncoded
    @POST("lock.php")
    Call<Validation> setLock(@Field("private_key") String private_key, @Field("action") String action);


    @GET("ultrasonic.php")
    Call<Validation> askImage(@Query("private_key") String private_key,@Query("type") String type);


    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getBaseUrl(MyApplication.getAppContext()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    APIService service = retrofit.create(APIService.class);

}
