# Retrofit2
* 다른 글은 적으면서 가장 기본인 Retrofit2는 안적어서 적습니다.
* Retrofit는 Restful API로 엄청 많이 사용하는 라이브러리입니다.
* 다른 라이브러리들도 있지만 성능도 좋고 구현 방법도 간단하여 이 라이브러리를 많이 사용합니다.
~~~gradle
implementation 'com.squareup.retrofit2:retrofit:2.3.0'
implementation 'com.google.code.gson:gson:2.8.0'
implementation 'com.squareup.retrofit2:converter-gson:2.1.0
~~~
* 보통은 Retrofit2 모듈이렇게 gradle에 추가하지만 별도의 OkHttp 설정이 필요하다면 다음과 같이 Retrofit2에서 OkHttp 종속성을 제외해야 합니다.
~~~gradle
implementation ('com.squareup.retrofit2:retrofit:2.3.0') {
    exclude module: 'okhttp'
}
implementation 'com.google.code.gson:gson:2.8.0'
implementation 'com.squareup.retrofit2:converter-gson:2.1.0'
implementation 'com.squareup.okhttp3:okhttp:3.9.1'
implementation 'com.squareup.okhttp3:logging-interceptor:3.9.1'
// logging-interceptor는 반환된 모든 응답에 대해 로그 문자열을 생성합니다.
~~~

## OkHttp Interceptors란?
* Interceptor는 Okhttp에 엤는 강력한 매커니즘으로 호출을 모니터, 재작성 및 재시도를 할 수 있습니다. Interceptor는 크게 두 가지 카테고리로 분류할 수 있습니다.
    * Application Interceptors: Application Interceptor를 등록하려면 OkHttpClient.Builder에서 addInterceptor()를 호출해야 합니다.
    * Network Interceptors: Network Interceptor를 등록하려면 addInterceptor() 대신 addNetworkInterceptor()를 추가해야 합니다.


## Retrofit Interface 설정
### APIClient.kt
~~~kotlin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient {

    companion object {

        fun getClient(): Retrofit {
         interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://example.in/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit
        }
    }
}
~~~
* getClient() 메서드는 Retrofit 인터페이스를 설정할 때마다 호출됩니다. 
* Retrofit은 **@GET, @POST, @PUT, @DELETE, @PATCH or @HEAD와 같은 annotation을 통해 HTTP method를 이용합니다.

### APIInterface.kt
~~~kotlin
import com.journaldev.retrofitintro.pojo.MultipleResource
import com.journaldev.retrofitintro.pojo.User
import com.journaldev.retrofitintro.pojo.UserList

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface APIInterface {

    @GET("api/unknown")
    fun doGetListResources(): Call<MultipleResource>

    @POST("api/users")
    fun createUser(@Body user: User): Call<User>

    @GET("api/users?")
    fun doGetUserList(@Query("page") page: String): Call<UserList>

    @FormUrlEncoded
    @POST("api/users?")
    fun doCreateUserWithField(@Field("name") name: String, @Field("job") job: String): Call<UserList>
}
~~~
* 위의 클래스에서 Annotation을 통해 테스트 HTTP request를 작성했습니다. 해당 API로 이곳을 통해 테스트 할 것입니다.
* @GET("api/unknown")은 doGetListResources()를 호출합니다. 
* doGetListResources()은 메서드 이름입니다. MultipleResource.java는 응답 객체의 Model POJO 클래스로서 Response parameter를 각각의 변수에 매핑하는 데 사용됩니다. 
* 이러한 POJO 클래스는 메소드 리턴 유형으로 동작합니다.

### MultipleResource.kt
~~~kotlin
import com.google.gson.annotations.SerializedName
import java.util.ArrayList
import java.util.List

class MultipleResource {

    @SerializedName("page")
    var page: Integer
    @SerializedName("per_page")
    var perPage: Integer
    @SerializedName("total")
    var total: Integer
    @SerializedName("total_pages")
    var totalPages: Integer
    @SerializedName("data")
    var data: List<Datum> = null

    inline class Datum {
        @SerializedName("id")
        var id: Integer
        @SerializedName("name")
        var name: String
        @SerializedName("year")
        var year: Integer
        @SerializedName("pantone_value")
        var pantoneValue: String
    }
}
~~~
* @SerializedName 어노테이션은 JSON 응답에서 각각의 필드를 구분하기 위해 사용합니다.
* POJO 클래스는 Retrofit Call 클래스로 래핑됩니다. (JSONArray는 POJO 클래스의 객체 목록으로 직렬화됩니다.)
* Method Parameters : 메서드 내에서 전달할 수 있는 다양한 매개 변수 옵션이 있습니다.
    * @Body - request body로 Java 객체를 전달합니다.
    * @Url - 동적인 URL이 필요할때 사용합니다.
    * @Query - 쿼리를 추가할 수 있으며, 쿼리를 URL 인코딩하려면 다음과 같이 작성합니다. @Query(value = "auth_token",encoded = true) String auth_token
    * @Field - POST에서만 동작하며 form-urlencoded로 데이터를 전송합니다. 이 메소드에는 @FormUrlEncoded 어노테이션이 추가되어야 합니다.

## 예제 프로젝트
### User.kt
~~~kotlin
import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("name")
    var name: String
    @SerializedName("job")
    var job: String
    @SerializedName("id")
    var id: String
    @SerializedName("createdAt")
    var createdAt: String

    fun User(name: String, job: String) {
        this.name = name
        this.job = job
    }
}
~~~
* 위 클래스는 createUser() 메서드에 대한 응답을 위해 사용합니다.

### UserList.kt
~~~kotlin
import com.google.gson.annotations.SerializedName

import java.util.ArrayList
import java.util.List

class UserList {

    @SerializedName("page")
    var page: Int = 0
    @SerializedName("per_page")
    var perPage: Int = 0
    @SerializedName("total")
    var total: Int = 0
    @SerializedName("total_pages")
    var totalPages: Int = 0
    @SerializedName("data")
    var data: List<Datum> = ArrayList()

    inline class Datum {

        @SerializedName("id")
        var id: Int = 0
        @SerializedName("first_name")
        var first_name: String? = null
        @SerializedName("last_name")
        var last_name: String? = null
        @SerializedName("avatar")
        var avatar: String? = null

    }
}
~~~

### CreateUserResponse.kt
~~~kotlin
import com.google.gson.annotations.SerializedName;

class CreateUserResponse {

    @SerializedName("name")
    var name: String? = null
    @SerializedName("job")
    var job: String? = null
    @SerializedName("id")
    var id: String? = null
    @SerializedName("createdAt")
    var createdAt: String? = null
}
~~~

### MainActivity.kt
* MainActivity.kt는 Interface 클래스에 정의된 각각의 API를 호출하고 그 결과를 Toast와 TextView를 통해 표시하고 있습니다.
~~~kotlin
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

import com.journaldev.retrofitintro.pojo.CreateUserResponse
import com.journaldev.retrofitintro.pojo.MultipleResource
import com.journaldev.retrofitintro.pojo.User
import com.journaldev.retrofitintro.pojo.UserList

import java.util.List

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity: AppCompatActivity {

    lateinit var responseText: TextView
    lateinit var apiInterface: APIInterface

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        responseText = findViewById(R.id.responseText)
        apiInterface = APIClient.getClient().create(APIInterface.class)

        var call: Call<MultipleResource> = apiInterface.doGetListResources()
        call.enqueue(Callback<MultipleResource>() {
            override fun onResponse(call: Call<MultipleResource> , response: Response<MultipleResource>) {

                var displayResponse: String = ""

                var resource: MultipleResource = response.body()
                var text: Int = resource.page
                var total: Int = resource.total
                var totalPages: Int = resource.totalPages
                var datumList: List<MultipleResource.Datum> = resource.data

                displayResponse += "$text Page\n $total Total\n $totalPages Total Pages\n"

                for (datum: MultipleResource.Datum in datumList) {
                    displayResponse += "$datum.id $datum.name $datum.pantoneValue $datum.year \n"
                }
                responseText.setText(displayResponse)
            }

            override fun onFailure(call: Call<MultipleResource>, t: Throwable) {
                call.cancel()
            }
        })

        var user = User("morpheus", "leader")
        val call1: Call<User> = apiInterface.createUser(user)
        call1.enqueue(Callback<User>() {
            override onResponse(call: Call<User>, response: Response<User>) {
                var user1: User = response.body()

                Toast.makeText(getApplicationContext(), "$user1.name $user1.job $user1.id $user1.createdAt", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(Call<User> call, Throwable t) {
                call.cancel()
            }
        })

        val call2: Call<UserList> = apiInterface.doGetUserList("2")
        call2.enqueue(Callback<UserList>() {
            override fun onResponse(call: Call<UserList>, response: Response<UserList>) {

                var userList: UserList = response.body()
                var text: Int = userList.page
                var total: Int = userList.total
                var totalPages: Int = userList.totalPages
                var datumList: List<UserList.Datum> = userList.data
                Toast.makeText(getApplicationContext(), "$text page\n $total total\n $totalPages totalPages\n", Toast.LENGTH_SHORT).show()

                for (datum: UserList.Datum in datumList) {
                    Toast.makeText(getApplicationContext(), "id : $datum.id name: $datum.first_name $datum.last_name avatar: $datum.avatar", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<UserList>, t: Throwable) {
                call.cancel()
            }
        })

        val call3: Call<UserList> = apiInterface.doCreateUserWithField("morpheus","leader")
        call3.enqueue(Callback<UserList>() {
            override fun onResponse(call: Call<UserList>, response: Response<UserList>) {
                var userList: UserList = response.body()
                var text: Int = userList.page
                var total: Int = userList.total
                var totalPages: Int = userList.totalPages
                var datumList: List<UserList.Datum> = userList.data
                Toast.makeText(getApplicationContext(), "$text page\n $total total\n $totalPages totalPages\n", Toast.LENGTH_SHORT).show()

                for (datum: UserList.Datum in datumList) {
                    Toast.makeText(getApplicationContext(), "id : $datum.id name: $datum.first_name $datum.last_name avatar: $datum.avatar", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<UserList>, t: Throwable) {
                call.cancel()
            }
        })
    }
}
~~~
* apiInterface = APIClient.getClient().create(APIInterface.class)는 APIClient를 인스턴스화 하기위해 사용됩니다. 
* API 응답에 Model 클래스를 매핑하기 위해서는 다음과 같이 사용합니다. val resource: MultipleResource = response.body()
* 이제 앱을 실행하면 각 API를 호출하고 이에 따라 토스트 메시지를 표시합니다.

## 참고 링크
* https://jongmin92.github.io/2018/01/29/Programming/android-retrofit2-okhttp3/