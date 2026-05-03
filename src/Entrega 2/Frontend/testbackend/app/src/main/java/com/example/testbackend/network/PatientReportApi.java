package com.example.testbackend.network;

import com.example.testbackend.models.PatientReport;
import com.example.testbackend.models.ReportAttachment;
import com.example.testbackend.models.ReportAttachmentList;
import com.example.testbackend.models.ReportCreate;
import com.example.testbackend.models.ReportStatistics;
import com.example.testbackend.models.ReportUpdate;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface PatientReportApi {
    @GET("reports/professional/{professionalId}")
    Call<List<PatientReport>> getProfessionalReports(@Path("professionalId") int professionalId);

    @POST("reports/")
    Call<PatientReport> createReport(@Body ReportCreate report);

    @GET("reports/{reportId}")
    Call<PatientReport> getReport(@Path("reportId") int reportId);

    @PUT("reports/{reportId}")
    Call<PatientReport> updateReport(@Path("reportId") int reportId, @Body ReportUpdate report);

    @DELETE("reports/{reportId}")
    Call<Void> deleteReport(@Path("reportId") int reportId);

    @GET("reports/patient/{patientId}")
    Call<List<PatientReport>> getPatientReports(@Path("patientId") int patientId);

    @GET("reports/statistics")
    Call<ReportStatistics> getStatistics(@Query("professional_id") int professionalId);

    @GET("reports/search/patient")
    Call<List<PatientReport>> searchReports(@Query("name") String name);

    // # ENDPOINTS DE ANEXOS
    @Multipart
    @POST("reports/{reportId}/attachments")
    Call<List<ReportAttachment>> uploadAttachments(
        @Path("reportId") int reportId,
        @Part List<MultipartBody.Part> files,
        @Part("description") RequestBody description
    );
    
    @GET("reports/{reportId}/attachments")
    Call<ReportAttachmentList> getReportAttachments(@Path("reportId") int reportId);
    
    @Streaming
    @GET("reports/{reportId}/attachments/{attachmentId}/download")
    Call<ResponseBody> downloadAttachment(
        @Path("reportId") int reportId,
        @Path("attachmentId") int attachmentId
    );
    
    @DELETE("reports/{reportId}/attachments/{attachmentId}")
    Call<Void> deleteAttachment(
        @Path("reportId") int reportId,
        @Path("attachmentId") int attachmentId
    );
    
    @GET("reports/{reportId}/with-attachments")
    Call<PatientReport> getReportWithAttachments(@Path("reportId") int reportId);
}
