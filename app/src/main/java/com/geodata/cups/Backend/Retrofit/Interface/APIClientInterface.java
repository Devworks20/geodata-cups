package com.geodata.cups.Backend.Retrofit.Interface;

import com.geodata.cups.Backend.Retrofit.Model.OnlineHistory.MainReportHistoryModel;
import com.geodata.cups.Backend.Retrofit.Model.Other.CUPS_FMModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.DesignationModel;
import com.geodata.cups.Backend.Retrofit.Model.Other.LoginModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.PoliceDistrictModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.PolicePrecintModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.PoliceStationModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.RankModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.SuffixModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.RegistrationResponseModel;
import com.geodata.cups.Backend.Retrofit.Model.Other.TokenModel;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIClientInterface
{
    //@Body = Raw
    //@Field = x-www-form-urlencoded
    //@Path  = {url}
    //@HeaderMap = Header Authorization
    //@Query = /?url
    //@Url = Customize URL


    //Optimized
    @FormUrlEncoded
    @POST("token")
    Call<TokenModel>ValidateToken(@Field("grant_type") String grant_type,
                                  @Field("username") String username,
                                  @Field("password") String password);


    //Optimized
    @GET("api/Login/{Username}/{Password}/{SystemType}")
    Call<LoginModel> LoginUser(@HeaderMap Map<String,String> headers,
                               @Path("Username") String Username,
                               @Path("Password") String Password,
                               @Path("SystemType") String SystemType);


    //Optimized
    @POST("api/Login/PostChangePassword/{AccountID}/{NewPassword}")
    Call<String> PostChangePassword(@HeaderMap Map<String,String> headers,
                                    @Path("AccountID") String AccountID,
                                    @Path("NewPassword") String NewPassword);

    //Optimized
    @GET("api/Login/CheckAccountPassword/{AccountID}")
    Call<String> CheckAccountPassword(@HeaderMap Map<String,String> headers,
                                      @Path("AccountID") String AccountID);


    //Optimized
    @GET("api/CUPSReportHistory/{AccountID}/{DateReport}")
    Call<List<MainReportHistoryModel>> ReportHistory(@HeaderMap Map<String,String> headers,
                                                     @Path("AccountID") String AccountID,
                                                     @Path("DateReport") String DateReport);


    //Optimized
    @FormUrlEncoded
    @POST("api/Login/RequestForgotPassword")
    Call<String> RequestForgotPassword(@Field("Email") String Email);

    //Optimized
    @GET("api/Rank_List_CUPS")
    Call<List<RankModel>>GetRankList();

    //Optimized
    @GET("api/Designation_List_CUPS")
    Call<List<DesignationModel>>GetDesignationList();

    //Optimized
    @GET("api/Suffix_List")
    Call<List<SuffixModel>>GetSuffixList();

    //Optimized
    @GET("api/PoliceDistrict_List")
    Call<List<PoliceDistrictModel>>GetPoliceDistrictList();

    //Optimized
    @GET("api/PoliceStation_List/{PoliceDistrictID}")
    Call<List<PoliceStationModel>>GetPoliceStationList(@Path("PoliceDistrictID") String PoliceDistrictID);

    //Optimized
    @GET("api/PolicePrecint_List/{PoliceStationID}")
    Call<List<PolicePrecintModel>>GetPolicePrecintList(@Path("PoliceStationID") String PoliceStationID);

    //Optimized - Need to test
    @GET("api/CUPS_FM/{DesignationID}")
    Call<List<CUPS_FMModel>> GetCupsFMList(@HeaderMap Map<String,String> headers,
                                           @Path("DesignationID") String DesignationID);

    //Optimized
    @GET("api/UsernameChecker/{Username}")
    Call<String> GetUsernameChecker(@Path("Username") String Username);

    //Optimized
    @FormUrlEncoded
    @POST("api/CUPS_Registration/PostRegistration")
    Call<RegistrationResponseModel>PostRegistration(@Field("Username") String Username,
                                                    @Field("Password") String Password,
                                                    @Field("Firstname") String Firstname,
                                                    @Field("Middlename") String Middlename,
                                                    @Field("Lastname") String Lastname,
                                                    @Field("Email") String Email,
                                                    @Field("Mobileno") String Mobileno,
                                                    @Field("RankID") Integer RankID,
                                                    @Field("DesignationID") Integer DesignationID,
                                                    @Field("SuffixID") String SuffixID,
                                                    @Field("PoliceDistrictID") Integer PoliceDistrictID,
                                                    @Field("PoliceStationID") Integer PoliceStationID,
                                                    @Field("PolicePrecintID") Integer PolicePrecintID);


    //Optimized
    @Multipart
    @POST("api/CUPS_Registration/PostRegistrationAttachment")
    Call<ResponseBody> PostRegistrationAttachment(@Part MultipartBody.Part photo,
                                                  @Part("AccountID") RequestBody AccountID);

    //Optimized
    @FormUrlEncoded
    @POST("api/CUPS_Report/PostMain")
    Call<Integer>PostMain(@Header("Authorization") String auth,
                          @Field("AccountID") String accountid);

    //Optimized
    @FormUrlEncoded
    @POST("api/CUPS_Report/PostTask")
    Call<Integer>PostTask(@Header("Authorization") String auth,
                          @Field("ReportID") Integer reportid,
                          @Field("ProgramID") Integer programid,
                          @Field("Remarks") String remarks);


    //Optimized
    @Multipart
    @POST("api/CUPS_Report/PostAttachment")
    Call<ResponseBody> PostAttachment(@Part MultipartBody.Part photo,
                                      @Part("ReportTaskID") RequestBody ReportTaskID,
                                      @Part("FileType") RequestBody FileType);
}
