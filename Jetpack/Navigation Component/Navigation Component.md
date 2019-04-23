# Navigation Component
## 왜 생겼지?
* 기존 네비게이션 방법에는 문제가 많았습니다.
    1. Fragment Transaction (Lifecycle Dancing)
    2. 상황마다 목적지가 다른 Up and Back Action
    3. Passing Arguments
    4. Deep Links
    5. 위 문제들로 인한 Error-prone Boilerplate Code
* 이런 문제들로 구글이 만들었습니다.

## Navigation Components
* 네비게이션 컴포넌트는 크게 명세, 실행 2가지로 이루어져 있습니다.
* 명세는 Navigation Graph이고 XML로 작성됩니다.
* 실행은 NavController 클래스의 navigate() 메소드로 명세를 동작합니다.

## Navigation Graph
* Destination은 작성해놓은 Activity, Fragment 등의 화면들입니다.
* Action은 Destination 간 이동작업을 정의한 것입니다.
* 이렇게 Destination과 Action으로 이루어져 있고 GUI로 보고 편집할 수 있습니다.

~~~xml
// Navigation Graph는 XML로 작성할 수 있습니다.
<navigation
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    app:startDestination="@+id/title_screen">
    // Destination
    <fragment
        android:id="@+id/title_screen"
        android:name=".navigationsample.TitleScreen"
        android:label="fragment_title_screen"
        tools:layout="@layout/fragment_title_screen">
    // Action
    <action
            android:id="@+id/action_title_screen_to_register"
            app:destination="@id/register"
            app:enterAnim="@anim/slide_in_right"
            app:exitAnim="@anim/slide_out_left"
            app:popEnterAnim="@anim/slide_in_left"
            app:popExitAnim="@anim/slide_out_right" />
    </fragment>
</navigation>
~~~

## Roll of Activity
* 네비게이션 컴포넌트는 Activity의 역할을 기존과 다르게 바라볼 것을 요구합니다. 
* 원래 Activity는 화면의 Entry Point 이면서도 Content와 Navigation Method를 들고있는 Owner 였습니다. 
* 하지만 네비게이션 컴포넌트를 활용하기 위해서는 Entry Point로서의 역할만 보아야 합니다. 
* Content와 Navigation Method는 모두 NavHost 라는 Fragment에게 위임합니다. 
* 그리고 이는 대부분의 화면을 Single Activity로 설계해야 함을 의미합니다.

~~~xml
// Activity에 NavHostFragment를 두고, Navigation Graph를 연결합니다.
<fragment
    android:id="@+id/nav_host"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:defaultNavHost="true"
    app:layout_constraintBottom_toTopOf="@id/nav_bottom"
    app:navGraph="@navigation/nav_graph_main" />
~~~
~~~kotlin
// Activity는 NavHostFragment를 ActionBar와 연결할 뿐입니다.
// 모든 Contents와 Navigation은 NavHostFragment에서 이루어집니다.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val navController = findNavController(R.id.nav_host)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host).navigateUp()
}
~~~

## Navigating
* 네비게이션 그래프를 정의하고 Activity 구조도 잘 설계하였으면 실행해야 합니다. 
* 실행은 모두 NavController.navigate() 하나의 메소드로 이루어집니다. 
* findNavController() 메소드로 NavController를 찾고, navigate()는 Destination, Action 아이디를 파라미터로 받습니다.

~~~kotlin
// findNavController()로 NavController는 받아온다.
// Destination Id를 받는다.
btnGame.setOnClickListener { view ->
    view.findNavController().navigate(R.id.gameFragment)
}
// Action Id를 받는다.
btnGame.setOnClickListener { view -> 
    view.findNavController().navigate(R.id.action_to_gameFragment)
}
// Navigation Class에서 Static Shortcut Method를 제공한다.
btnGame.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_to_gameFragment))
~~~

## SafeArgs
* 화면을 옮길 때 Arguments를 넘기는 경우가 많습니다. 
* Arguments를 넘기기 위해서는 Map 형태로 Key, Value를 지정해야 합니다. 
* String 타입의 Key값은 넘기는 쪽과 받는 쪽이 같아야 합니다. 받는 타입 또한 통일해줘야 합니다.

~~~kotlin
// 보내는 쪽. 받는 쪽과 userName 키값을 통일해야 한다.
view.findNavController().navigate(R.id.action_to_gameFragment, bundleOf("userName" to "maryang"))
// 받는 쪽. userName 키값의 Value가 String임을 인지하고 getString() 메소드를 사용해야 한다.
arguments?.getString("userName")
~~~

* SafeArgs 기능은 Navigation Graph의 Arguments 정의에 따라 자동으로 Boilerplate Code를 생성하여 위 문제를 해결합니다. 
* 보내는 쪽에서는 Directions Class가 생성되고, 받는 쪽에서는 Args Class가 생성됩니다. 
* 그리고 각 클래스는 보내고 받아야 할 Arguments를 인스턴스 변수로 가지고 있습니다.

~~~xml
// Navigation Graph에서 Arguments를 정의
<fragment
    android:id="@+id/gameFragment"
    android:name="com.rfrost.navigationsample.GameFragment"
    android:label="GameFragment"
    tools:layout="@layout/game_fragment">
    <argument
        android:name="screenName"
        app:argType="string" />
    <argument
        android:name="userName"
        android:defaultValue="name"
        app:argType="string" />
</fragment>
~~~
~~~kotlin
// 보내는 쪽에서는 Directions 클래스를 활용
// 보낼 Arguments를 Constructor 및 Setter로 안전하게 설정
val directions = MainFragmentDirections.action("GameFragment")
directions.setUserName("rfrost")
view.findNavController().navigate(directions)
// 받는 쪽에서는 Args 클래스를 활용
// Bundle로부터 arguments를 받아오고, Instance 변수로 안전하게 접근
arguments?.let {
    val arguments = GameFragmentArgs.fromBundle(it)
    txt_screen_name.text = arguments.screenName
    txt_name.text = arguments.userName
}
~~~
## DeepLinking
* Explicit는 Notification 등 앱 내부에서 발생하는 딥링크 입니다. 
* Implicit는 Web URL 등 앱 외부에서 발생하는 딥링크 입니다.

### Explicit DeepLinking
* 안드로이드에서는 Explicit Deeplink를 PendingIntent 형태로 많이 활용합니다. 
* 네비게이션 컴포넌트는 NavDeepLinkBuilder를 제공하여 네비게이션 그래프를 활용한 PendingIntent를 만들 수 있도록 돕습니다.

~~~kotlin
val deepLinkBuilder = NavDeepLinkBuilder(context!!)
        // NavGraph,Destination,Arguments 설정
        .setGraph(R.navigation.nav_graph_main)
        .setDestination(R.id.gameFragment)
        .setArguments(MainFragmentDirections.actionToGameFragment(
            "GameFragment").arguments)
// PendingIntent 생성 및 활용
val pendingIntent = deepLinkBuilder.createPendingIntent()
notificationBuilder.setContentIntent(pendingIntent)
~~~

### Implicit DeepLinking
* Implicit DeepLink는 Navigation Graph에서 <deeplink> 태그를 활용해 정의합니다.

~~~xml
<fragment
    android:id="@+id/resultFragment"
    android:name="com.rfrost.navigationsample.ResultFragment"
    android:label="ResultFragment"
    tools:layout="@layout/result_fragment">
    // deeplink 태그를 활용한다.
    <deepLink
        android:autoVerify="true"
        app:uri="http://www.google.com" />
    // 대괄호를 활용하여 Arguments를 그대로 Deeplink path로 받아올 수 있다.
    <deepLink app:uri="navsample://result/userName/{userName}" />
    // Deeplink의 대괄호와 같은 키값을 활용하면 연동이 된다.
    <argument
        android:name="userName"
        android:defaultValue="name"
        app:argType="string" />
</fragment>
~~~

* 정의한 deeplink는 Manifest에서 <nav-graph> 태그를 활용하여 사용합니다.
~~~xml
// Deeplink를 정의한 NavGraph를 받아줄 Activity에 연동합니다.
<activity android:name=".MainActivity">
    <nav-graph android:value="@navigation/nav_graph_main" />
</activity>
~~~

## 출처
* 진짜 제가 뭔가 좀더 간결하고 요점 중심으로 정리하고 싶었지만 블로그의 글이 너무 잘 설명되어 있어서 거의 내용이 동일하네요...
* 이승민님의 블로그
* https://medium.com/@maryangmin/navigation-components-in-android-jetpack-1-introduction-e38442f70f