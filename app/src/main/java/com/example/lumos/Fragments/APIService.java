package com.example.lumos.Fragments;

import com.example.lumos.Notifications.MyResponse;
import com.example.lumos.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqG_u310:APA91bFsmgMotxx0I8xzWjSsvGcEkJsZdWtBULRy7kABZGgsDiEYEP9hgLSYkSewRdX1OkZ_MUE-wcMHtpHJfxjbN-_ZX22t4gNk38K2h6_REnTyhQ8nDSkoUlmncwXvnRYy0jRTqHHr"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
