# Async Communication

## 메인스레드(UI Thread)와 비동기처리
* 안드로이드 어플리케이션에서 UI는 UI스레드라고 불리는 메인스레드가 관여하고 처리합니다.
* 하지만 '777APP'이라는 어플리케이션에 '다운로드'와 '페이지 닫기'라는 두 개의 버튼이 구성되어 있습니다. 그리고 다운로드 버튼을 클릭해 해당 파일을 다운로드 하려하는 동작은 어떻게 처리할까요?
* 이 때 메인스레드가 수행하도록 코드를 구성했다면 버튼에 대한 처리는 메인스레드가 할 것이고, 다운로드하는 처리도 메인스레드가 할 것입니다. 
* 하지만 이런다면 다운로드 받는 동안 타 UI와의 교류가 비활성화 될 것입니다. 즉, '다운로드 버튼'과 '페이지 닫기' 버튼은 먹통이 될 것입니다.
* 이 때 우리는 메인스레드와 별개로 작업을 수행하고 그 결과를 UI에 나타낼 수 있도록 처리해야합니다. '비동기 처리'라고 하며, 이를 AsyncTask로 해결할 수 있습니다.

## AsyncTask란?
* AsyncTask가 메인스레드에서 수행될 작업을 수행해주는 비동기적 처리 방법 중 하나라고 했습니다.
* AsyncTsk는 메인스레드를 작업을 좀 더 효율적이게 해줄 수 있는 백그라운드 처리 기법입니다.
* AsyncTask는 작업 수행 시간이 수 초간 진행될 때 유용하며, 오랜 시간 작업을 해야하는 경우에는 AsyncTask가 아닌 다른 방법을 권장합니다.

## AsyncTask Code
~~~kotlin
class Async: AsyncTask<FirstType, SecondType, ThirdType>{
    override fun onPreExecute() {
        suprt.onPreExecute()
    }

    override fun doInBackground(params: FirstType): ThirdType {
        return result
    }

    override fun onPostExecute(result: ThirdType){
        super.onPostExecute(s)
    }

    override fun onCancelled(result: ThirdType){
        super.onCancelled(result)
    }

    override fun onProgressUpdate(values: SecondType) {
        super.onProgressUpdate(values)
    }
}
~~~
## 부모클래스의 제네릭
* 제네릭 FirstType은 doInBackground의 매개변수의 자료형입니다.
* 제네릭 SecondType은 onProgressUpdate의 매개변수의 자료형입니다.
* 제네릭 ThirdType은 doInBackground의 반환형이면서 onPostExecute의 매개변수의 자료형입니다.
## 함수 역할
### onPreExecute()
* 작업이 실행되기 전에 UI 스레드에서 호출된다. 이 단계는 일반적으로 사용자 인터페이스에 진행률 표시줄을 표시하여 작업을 설정하는 데 사용됩니다.
### doInBackground(params: FirstType)
* onPreExecute()가 실행을 마친 직후 백그라운드 스레드에서 호출됩니다. 이 단계는 가장 핵심적인 작업을 수행하는 데 사용됩니다.
### onPostExecute(params: ThirdType)
* 백그라운드 작업(doInBackground())이 끝난 후에 UI 스레드에서 호출된 doInBackground()의 리턴 값을 매개변수로 받아 처리합니다.
### onCancelled()
* doInBackground() 수행 중이나 메인스레드에서 cancel(true)를 호출하면 doInBackground() 수행 완료 후 onPostExecute()가 호출되는 대신 이 메서드가 호출됩니다.
### onProgressUpdate(values: SecondType)
* doInBackground() 수행 중 publishProgress()를 호출하면 호출되며, 주로 작업의 진행 상태을 프로그레스 바로 표시하기 위해 사용합니다.

## AsyncTask 사용 방법
~~~kotlin
override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    var async: Async = Async

    var arr: IntArray = IntArray(3)
    async.execute(arr)
    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arr)
}
~~~
* execute()를 통해 작업을 수행하며 인자의 자료형은 doInBackground()의 매개변수의 자료형과 같습니다. 또한 이 자료형의 변수는 가변인자이며 단일 변수나 배열과 같은 형태의 변수를 사용할 수 있습니다.
* doInBackground()에서 가변 형태인 매개변수 사용 시에는 변수명[0]과 같이 배열의 형태로 사용합니다.
* AsyncTask 작업을 수행하기 위해서는 객체를 생성해 execute() 메서드와 executeOnExecutof() 메서드를 호출하는 방법이 있습니다. 
* * execute() 메서드 호출을 통한 작업 수행
일반적인 사용하는 수행 방법이며 여러 AsyncTask 객체를 만들어 다수의 작업을 수행할 때 execute()가 호출된 순서대로 처리합니다.
* * executeOnExecutor() 메서드 호출을 통한 작업 수행
병렬처리를 위한 수행 방법이며 여러 AsyncTask 객체를 만들어 다수의 작업을 수행할 때 executeOnExector()가 호출된 순서에 상관없이 동시처리합니다.

## AsyncTask 주의점
* AsyncTask 수행을 위해 생성된 객체는 execute()를 통해 단 한번만 실행 가능하며, 재실행시 예외 상황이 발생합니다. 또한 AsyncTask는 메인스레드에서만 실행되고 호출되어야 합니다.
* AsyncTask는 백그라운드에서 수행되며, 그 결과를 메인스레드에 전달해 사용자에게 제공합니다. 그렇다면 AsyncTask에 백그라운드 작업을 요청한 메인스레드, 즉 AsyncTask를 호출한 Activity가 destory된다면 어떻게 될까요?
* 여기서부터 문제가 발생합니다. 일반적으로 특별한 처리를 해두지 않았다면 AsyncTask이 참조하고 있던 UI가 사라져도 AsyncTask는 백그라운드에서 작업을 수행합니다.
* 그리고 개발자의 코드에 의해 그 결과가 사라진 메인스레드에 넘겨주려 할 것이고, 이 과정에서 사라진 UI를 참조하게 됩니다. 하지만 자신이 참조하는 UI는 이미 destroy되었으며 예외 상황이 발생하게 됩니다.
* 또한 갈비지컬렉터는 AsyncTask가 참조하고 있는 이전 Activity를 선택할 수 없어 메모리릭이 발생할 수 있습니다. 또한 화면 회전에 의해 Activity가 destroy되고 새 인스턴스로 Activity를 생성할 때도 이와 같은 상황이 발생할 수 있습니다.
* 이를 위한 대비를 해야하며, cancel()을 통해 doInBackground() 실행 완료후, onPostExcute() 호출을 막고 onCancelled를 호출하도록 해야합니다.
* 마지막으로 AsyncTask를 여러 개 실행하면 이는 순차적으로 수행이 이뤄집니다. ATask.execute()와 BTask.execute()를 순서대로 호출하면 ATask에 대한 작업이 끝나야 BTask에 대한 작업이 수행된다는 뜻입니다.
* 그렇다면 동시에 처리하려면 어떻게 해야 할까요? 이를 위해 execute() 메서드 대신 executeOnExecutor()라는 메서드가 제공되며 이를 사용해 병렬처리가 가능합니다.

## 비동기 처리를 간단하게 retrofit2에 응용해보기
### 모델 클래스
~~~kotlin
class Contributor {
    lateinit var login: String
    lateinit var html_url: String
    
    var contributions: Int

    override fun toString(): String{
        return login + " (" + contributions + ")"
    }
}
~~~
### 인터페이스
~~~kotlin
interface GitHubService {
    @GET("repos/(owner)/(repo)/contributors")
    Call<List<Contributor>> repoContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String)
}
~~~
### HTTP 호출(동기)
~~~kotlin
var gitHubService: GitHubService = GitHubService.retrofit.create(GitHubService::class.java)
var call: Call<List<Contributor>> = gitHubService.repoContributors("square", "retrofit")
var result: List<Contributor> = call.execute().body() 
~~~
### 레이아웃
~~~xml
<Button 
android:layout_width="wrap_content" android:layout_height="wrap_content" 
android:text="Fetch" 
android:id="@+id/button" 
android:layout_alignParentBottom="true" android:layout_centerHorizontal="true" android:layout_marginBottom="151dp" /> 

<TextView 
android:layout_width="wrap_content" android:layout_height="wrap_content" 
android:textAppearance="?android:attr/textAppearanceLarge" android:text="" 
android:id="@+id/textView" 
android:layout_above="@+id/button" android:layout_alignParentEnd="true" android:layout_alignParentStart="true" android:layout_alignParentTop="true" android:textIsSelectable="false" />
~~~
### 버튼을 이용해 HTTP동기호출
~~~kotlin
var button: Button = findViewbyId(R.id.button)
button.setOnClickListener{ v -> 
    var gitHubService: GitHubService = GitHubService.retrofit.create(GitHubService::class.java)
    var call: Call<List<Contributor>> = gitHubService.repoContributors("square", "retrofit")
    var result: String = call.execute().body().toString()
    var textView: TextView = findByViewId(R.id.textView)
    textView.setText(result)
}
~~~
* 아쉽지만 위의 소스는 작동하지 않습니다. 안드로이드는 UI Thread에서 네트워크 호출을 허용하지 않습니다.
~~~kotlin
class MainActivity: AppCompatActivity {
    override fun onCreate(savedInstanceState: Bundle){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var button: Button = findViewById(R.id.button)
        button.setOnClickListener{ v -> 
            var gitHubService: GitHubService = GitHubService.retrofit.create(GitHubService::class.java)
            val call: Call<List<Contributor>> = gitHubService.repoContributors("square", "retrofit")
            Network().execute(call)
        }
    }

    inner class NetworkCall: AsyncTask<Call, Unit, String> {
        override fun doInBackground(params: Array<Call>): String{
            try {
                var call: Call<List<Contributor>> = params[0]
                var response: Response<List<Contributor>> = call.execute()
                return response.body().toString()
            } catch (e: IOException){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String){
            val textView: TextView = findViewById(R.id.textView)
            textView.setText(result)
        }
    }
}
~~~
* 이렇게 바꿔줍니다.
### 비동기 방식
~~~kotlin
var gitHubService: GitHubService = GitHubService.retrofit.create(GitHubService::class.java)
var call: Call<List<Contributor>> = gitHubService.repoContributors("square", "retrofit")
call.enqueue(Callback<List<Contributor>>(){
    override fun onResponse(call: Call<List<Contributor>>, response: Response<List<Contributor>>) {
        var textView: TextView  =  findViewById(R.id.textView)
        textView.setText(response.body().toString())
    }

    override fun  onFailure(call: Call<List<Contributor>>, t: Throwable){

    }
})
~~~
* 비동기 방식은 Button이랑 클릭 리스너 생성할 필요없습니다. 그냥 선언과 동시에 백그라운드 처리합니다.
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceLarge"
        android:text=""
        android:id="@+id/textView"
        android:layout_above="@+id/button"
        android:layout_alignParentEnd="true"
        android:layout_alignParentStart="true"
        android:layout_alignParentTop="true"
        android:textIsSelectable="false" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Fetch"
        android:id="@+id/button"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="151dp" />
</LinearLayout>
~~~
### Post방식
* Retrofit은 Restful api와 잘맞습니다. 인터페이스에서 GET,POST,DELETE 방식으로 선언이 쉽습니다.
* Restful API 테스트 서버를 열 수도 있지만 시간이 많아 소요될 것 같아서 간단하게 테스트하였습니다.
* 인터페이스 부분에서
~~~kotlin
@FormUriEncoded
@POST("token")
Call<Test> createUser(@Field("token") token: String)
~~~
* 밑 @POST부분을 추가하여 특정 값을 서버쪽으로 넘겨주는 메소드입니다.
~~~kotlin
var gitHubService: GitHubService = GitHubService.retrofit.create(GitHubService::class.java)
var call: Call<Test> = gitHubService.createUser("token")
call.enqueue(Callback<Test>(){
    override fun onResponse(call: Call<Test>, response: Response<Test>) {
        // 성공
    }

    override fun  onFailure(call: Call<Test>, t: Throwable){
        // 실패
    }
})
~~~
* 비동기 부분의 소스입니다. .createUser("넘기고싶은 값"); 선언하여 비동기식으로 넘겨주면 http://특정 서버 URL/../../token에게 입력한 값을 Key값 token으로 Value값 "넘기고싶은 값" 으로 전달해줍니다. @Query , @BODY 등등 있으나 생략하겠습니다.

### 동기 비동기 방식 차이
* 동기(synchronous): 요청과 동시에 결과가 나타남. 특정함수를 선언하여 결과값을 즉시 return 받는 방식
* 비동기(Aysnchronous): 요청과 동시에 결과가 나타나지 않음. 사용자는 요청을  하고 제어권을 다시 가짐. 간단하게 설명하면 요구 프로세스를 백그라운드에서 처리한다라고 이해하면 됩니다.