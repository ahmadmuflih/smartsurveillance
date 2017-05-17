package info.edutech.smartsurveillance.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import info.edutech.smartsurveillance.MyApplication;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.CaptureService;
import info.edutech.smartsurveillance.model.UserService;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.model.ValidationAlarm;
import info.edutech.smartsurveillance.model.ValidationSMS;
import info.edutech.smartsurveillance.model.ValidationServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Baso on 11/12/2016.
 */
public interface ServerService {


    @GET("token.php")
    Call<ValidationServer> verify(@Query("token") String token);


    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getMainServer())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    ServerService service = retrofit.create(ServerService.class);

}
