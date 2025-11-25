package smu.ai.teampj_schedule.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 기본 주소 (역 목록 가져올 때 쓰는 곳)
    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // JSON을 엄격하게 검사하지 않도록 설정 (Lenient)
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Gson 변환기 장착
                    .build();
        }
        return retrofit;
    }
}