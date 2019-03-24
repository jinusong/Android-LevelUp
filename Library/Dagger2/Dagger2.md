# Dagger2

## Dagger2의 기본 개념
* Dagger는 5가지의 필수 개념이 있습니다.
1. Inject
2. Component
3. Subcomponent
4. Module
5. Scope

### Inject
* 의존성 주입을 요청합니다. Inject 어노테이션으로 주입을 요청하면 연결된 Component가 Module로부터 객체를 생성하여 넘겨줍니다.

### Component
* 연결된 Module을 이용하여 의존성 객체를 생성하고, Inject로 요청받은 인스턴스에 생성한 객체를 주입합니다.
* 의존성을 요청받고 주입하는 Dagger의 주된 역할을 합니다.

### Subcomponent
* Component는 계층관계를 만들 수 있습니다.
* Subcomponenet는 Inner Class 방식의 하위계층 Component입니다. Sub의 Sub도 가능합니다.
* Subcomponenet는 Dagger의 중요한 컨셉인 그래프를 형성합니다.
* Inject로 주입을 요청받으면 Subcomponent에서 먼저 의존성을 검색하고, 없으면 부모로 올라가면서 검색합니다.

### Module
* Componenet에 연결되어 의존성 객체를 생성합니다. 생성 후 Scope에 따라 관리도 합니다.

### Scope
* 생성된 객체의 Lifecycle 범위입니다. 안드로이드에서는 주로 PerActivity, PerFragment 등으로 화면의 생명주기와 맞추어 사용합니다.
* Module에서 Scope를 보고 객체를 관리합니다.

![DaggerFlow1](https://cdn-images-1.medium.com/max/1600/1*4HuI1KMicC5noqyBpkK-rw.png)
![DaggerFlow2](https://cdn-images-1.medium.com/max/1600/1*JK7yveoSwhlA8Lk_w8RrXQ.png)

## Dagger를 사용하기 전
### 햄버거 만들기

* 햄버거(Burger)를 만듭니다.
* Burger는 WheatBun과 BeefPatty로 이루어져 있습니다.
~~~kotlin
data class Burger(
    var bun: WheatBun,
    var patty: BeefPatty
) 
~~~

* WheatBun과 BeefPatty를 준비합니다.
* WheatBun
~~~kotlin
class WheatBun {
    fun getBun(): String = "밀빵"
}
~~~

* BeefPatty 
~~~kotlin
class BeefPatty {
    fun getPatty(): String = "소고기 패티"
}
~~~

* 이렇게 재료를 준비한 후 MainActivity에서 햄버거는 어떻게 만들까요?
~~~kotlin
class MainActivity: AppCompatActivity() {

    lateinit var burger: Burger

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bun = WheatBun()
        var patty = BeefPatty()

        burger = Burger(bun, patty)
    }
}
~~~

* 이렇게 WheatBun과 BeefPatty를 가져와서 햄버거를 만듭니다.
* 이렇게 MainActivity에서 인스턴스를 생성하는게 기존의 방식이었습니다. 
* 여기에 의존성 주입을 사용하면 new 오퍼레이터 사용없이 외부에서 자동으로 객체를 생성해 줍니다.

## Dagger2 적용하자
### 0.App 버전의 build.gradle에 라이브러리 추가하기

~~~gradle
implementation "com.google.dagger:dagger:$daggerVersion"
implementation "com.google.dagger:dagger-android:$daggerVersion"
implementation "com.google.dagger:dagger-android-support:$daggerVersion"

kapt "com.google.dagger:dagger-android-processor:$daggerVersion"
kapt "com.google.dagger:dagger-compiler:$daggerVersion"
~~~

### 1. Module과 Component를 만들어야 합니다.
* Module은 필요한 객체를 제공하는 역할을 합니다.
* 아까 MainActivity에서 이렇게 객체를 생성했습니다.
~~~kotlin
var bun = WheatBun()
var patty = BeefPatty()

burger = Burger(bun, patty)
~~~

* 이걸 Module이 처리해줍니다.
~~~kotlin
@Module
class BurgerModule {
    @Provides
    fun provideBurger(bun: WheatBun, patty: BeefPatty) = Burger(bun, patty)

    @Provides
    fun provideBun() = WheatBun()

    @Provides
    fun providePatty() = BeefPatty()
}
~~~
* 생성할 객체를 @Providers 어노테이션을 사용해서 생성해 줍니다.

* Component는 모듈에서 제공받은 객체를 조합하여 필요한 곳에 주입하는 역할을 합니다.

~~~kotlin
@Component(modules = BurgerModule.class)
interface BurgerComponent {
    fun inject(activity: MainActivity)
}
~~~
* @Component 어노테이션을 통해 BurgerModule.class를 가져왔습니다. 
* 이를 inject() 함수를 통해 MainActivity에 주입했습니다.

~~~kotlin
class MainActivity: AppCompatActivity() {
    @Inject
    lateinit var burger: Burger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val component: BurgerComponent = DaggerBurgerComponent.builder().burgerModule(BurgerModule()).buold()

        component.inject(this)

        Log.d("MyTag","burger bun : " + burger.bun.getBun() + ", patty : " + burger.patty.getPatty())
    }
}
~~~

* 위와 같은 코드로 burger 객체를 외부에서 생성하여 가져올 수 있습니다.

* 간단하게 이렇게 생각하면 됩니다.
    * Module: 공급하는 친구
    * Inject: 주입받는 친구
    * Component: 사이를 연결해주는 친구

![관계](https://black-jin0427.tistory.com/104)

## 모듈이 두개???
### 요리 주문을 받아 만들기
#### 1.요리를 만들기 위해 셰프와 주방이 필요합니다.
~~~kotlin
data class Chef (
    var firstName: String, 
    var lastName: String) {
        override fun toString(): String
            = "Chef [firstName= $firstName, lastName= $lastName]"
}
~~~

~~~kotlin
data class Kitchen (
    var chef: Check, 
    var order: String
) {
    fun isOrder(): Boolean {
        if(chef != null && order != null && order.length() > 0) {
            return true
        }
        return false
    }
}
~~~
* 주방에서는 주문을 받았는지 여부를 파악하여 Boolean 값을 리턴하는 함수인 isOrder()이 있습니다.

#### 2.모듈을 만듭니다.
~~~kotlin
@Module
class ChefModule {
    @Provides
    fun provideChef(): Chef = Chef("Black", "Jin")
}
~~~

~~~kotlin
@Module
class KitchenModule {

    @Provides
    fun provideIsOrder(chef: Chef, @Named("course1") order: String) = Kitchen(chef, order)

    @Provides
    @Named("course1")
    fun provideCourse1(): String = "한식"

    @Provides
    @Named("course2")
    fun provideCourse2() = "중식"
~~~

* 2개의 모듈을 만들었습니다.
* 특히 KitchenModule에서는 @Provides로 공급할 때 Named를 공급 객체를 구분할 수 있습니다. 코스요리1과 코스요리2로 2가지 Named를 설정하였습니다.

#### 3.컴포넌트
~~~kotlin
@Component(modules = {ChefModule.class, KitchenModule.class})
interface MyComponent {
    fun inject(activity: MainActivity)
}
~~~
* 2개의 모듈을 주입할 곳은 MainActivity입니다.


#### 4.MainActivity
~~~kotlin
class MainActivity(): AppCompatActivity() {
    @Inject
    lateinit var kitchen: Kitchen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myComponent = DaggerMyComponent.builder().build()
        myComponent.inject(this)

        isOrder()
    }

    private fun isOrder() {
        var isOrder = kitchen.isOrder()
        if (isOrder) {
            Log.d("MyTag", "order successful ")
        } else {
            Log.d("MyTag", "order failed")
        }
    }
}
~~~

* 1) 주입 받는 친구 Kitchen을 변수에 선언하였습니다.
* 2) Component를 build하게 되면 kitchen에 자동으로 객체가 주입됩니다.
* 3) KitchenModule에서 course1(한식)을 주입했으므로 BlackJin 셰프가 한식 요리를 하게 됩니다.

## SharedPref 사용해보자.
* 기존에는 ShardPref를 다음과 같이 사용했습니다.

~~~kotlin
class MainActivity(): AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putString("status", "sucess!").apply()

        Log.d("MyTag", "get : " + sharedPreferences.getString("status", null))
    }
}
~~~

* Dagger2를 사용해볼까요?

### 1.Application
~~~kotlin
@Module
class ApplicationModule(val app: Application) {
    @Provides
    @Singleton
    fun provieSharedPrefs(): SharedPreferences 
        = PreferenceManager.getDefaultSharedPreferences(app)
}
~~~
* 위와 같이 모듈을 만들어 줍니다. 
* SharedPreferences는 같은 객체 1개만 있으면 되기 때문에 @Singleton 어노테이션을 설정했습니다.

### 2.ApplicatinComponent
~~~kotlin
@Singleton
@Component(modules = ApplicationModule.class)
interface ApplicationComponent {
    fun inject(activity: MainActivity)
}
~~~
* MainActivity에 sharedPref를 주입할 예정입니다.

### 3.DemoApplication
~~~kotlin
class DemoApplication: Application() {

    private lateinit var mComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        mComponent = DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()
    }

    fun getComponent(): ApplicationComponent = mComponent
}
~~~
* Application 단계에서 component를 최초 초기화 해줍니다.
* 이렇게 해줌으로써 Module에서 제공하는 SharedPref를 최초 한번 초기화 해줄 수 있습니다.
* 그럼 이 초기화 한 SharedPref를 어떻게 공급할 수 있을까요?

### 4.MainActivity
~~~kotlin
class MainActivity(): AppCompatActivity() {

    @Inject
    lateinit var mSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (getApplication() as DemoApplication).getComponent().inject(this)

        mSharedPrefs.edit()
                .putString("status", "success!")
                .apply()

        Log.d("MyTag", "getString : " + mSharedPrefs.getString("status", "null"))
    }
}
~~~
* Application 단계에서 최초 초기화한 Component 객체에 MainActivity로 SharedPref를 주입하라는 로직을 작성합니다.
~~~kotlin
(getApplication() as DemoApplication)
            .getComponent()
            .inject(this)
~~~
* 그렇게 하면 변수로 선언한

~~~kotlin
@Inject
lateinit var mSharedPrefs: SharedPreferences
~~~
* mSharedPrefs에 자동으로 객체가 주입되어 사용할 수 있게 됩니다.
* 로그를 확인하면 success!를 확인할 수 있습니다.

## Named 어노테이션을 사용하여 SharedPref를 분리해서 사용하기
### 1.ApplicationModule
~~~kotlin
@Module
class ApplicationModule(val app: Application) {

    @Provides
    @Singleton
    @Named("default")
    fun provideDefaultSharedPrefs()
        = PreferenceManager.getDefaultSharedPreferences(app)
    
    @Provides
    @Singleton
    @Named("secret")
    fun provideSecretSharedPrefs()
        = app.getSharedPreferences("secret", Activity.MODE_PRIVATE)
}
~~~
* ApplicationModule 단계에서 default와 secret의 두가지 종류의 SharedPref를 초기화 해줍니다.

### 2.MainActivity
~~~kotlin
class MainActivity(): AppCompatActivity() {
    @Inject
    @Named("default")
    lateinit var mDefaultSharedPrefs: SharedPreferences

    @Inject
    @Named("secret")
    lateinit var mSecretSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (getApplication() as DemoApplication)
                .getComponent()
                .inject(this)
        
        mDefaultSharedPrefs.edit()
                .putString("status", "success!")
                .apply()

        mSecretSharedPrefs.edit()
                .putString("status", "another success!")
                .apply()

        Log.d("MyTag", "getString : " + mDefaultSharedPref.getString("status", "null"))
        Log.d("MyTag", "getString : " + mSecretSharedPrefs.getString("status", "null"))
    }
}
~~~

* Component를 불러와 MainActivity에서 선언한
~~~kotlin
@Inject
@Named("default")
lateinit var mDefaultSharedPrefs: SharedPreferences

@Inject
@Named("secret")
lateinit var mSecretSharedPrefs: SharedPreferences
~~~
* 위 두 변수에 객체를 주입해 줍니다. 이때 Named 를 통해 각기 다른 객체를 주입해 줄 수 있습니다.

## Subcomponent 친구는 안써요? 뭔가 좀 더 유기적인 구조를 보고 싶은데
* 이미 많이 Dagger2에 대해 알아봤지만 좀 더 제대로 알기 위해서, 그리고 정리 한다는 생각으로 보면 좋을 듯 합니다.
* Application Component부터 의존성을 요청하는 Fragment까지 순서대로 간단한 예제를 준비했습니다.
* 많은 클래스와 코드가 있지만 위 Flow를 유의하면서 차근차근 본다면 Dagger2의 유기적인 구조를 이해할 수 있습니다.
* 예제 출처: https://medium.com/@maryangmin/di-%EA%B8%B0%EB%B3%B8%EA%B0%9C%EB%85%90%EB%B6%80%ED%84%B0-%EC%82%AC%EC%9A%A9%EB%B2%95%EA%B9%8C%EC%A7%80-dagger2-%EC%8B%9C%EC%9E%91%ED%95%98%EA%B8%B0-3332bb93b4b9

### Application Component
~~~kotlin
@Singleton  // Scope
// 연결할 Module을 정의합니다.
@Component(modules = {AndroidSupportInjectionModule.class, ActivityBindingModule.class, ApplicationModule.class})
// Application과의 연결을 도울 AndroidInjector를 상속받고, 제네릭으로 BaseApplication 클래스를 정의합니다.
interface AppComponent: AndroidInjector<BaseApplication>() {

    // Application과의 연결을 도울 Builder를 정의합니다.
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): AppComponent.Builder
        fun build(): AppComponent
    }
}
~~~

### Application Module
~~~kotlin
@Module
class ApplicationModule {
    // Context 타입의 의존성 객체를 생성합니다.
    @Provides
    fun providesContext(application: Application) = application
}
~~~
* Provides 어노테이션으로 의존성 객체를 생성할 메소드를 정의합니다. 반환 타입을 따라 Component가 검색하여 활용합니다.

### BaseApplication에서 Component 연동하기
~~~kotlin
// DaggerApplication를 상속받고, ApplicationComponent에서 정의한 Builder를 활용하여 Component와 연결합니다.
class BaseApplication(): DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication>() = DaggerAppComponent.builder().application(this).build()
}
~~~

### ActivitySubcomponent 생성하기
~~~kotlin
// ActivityBindingModule은 위 ApplicationComponent에 연결되어 있습니다.
@Module
abstract class ActivityBindingModule() {
    // ContributesAndroidInjector 어노테이션을 달고 반환타입을 통해 해당 Activity의 Subcomponent를 생성합니다.
    // modules에 Subcomponent와 연결할 Module을 정의합니다. 이 Module들이 실제 의존성 객체를 생성합니다.
    @ActivityScoped // Scope
    @ContributesAndroidInjector(modules = TasksModule.class)
    abstract fun tasksActivity(): TasksActivity
}
~~~
* 원래 Subcomponent는 어노테이션을 활용하여 직접 만들었어야 했지만, 이제 ContributesAndroidInjector를 활용하여 Module에서 자동으로 생성할 수 있습니다.

### ActivitySubcomponent의 Module
~~~kotlin
@Module
abstract class TaskModule {
    // ContributesAndroidInjector로 FragmentSubcomponent를 생성합니다.
    @FragmentScoped // Scope
    @ContributesAndroidInjector
    abstract fun tasksFragment(): TasksFragment

    // TaskPresenter타입의 의존성 객체를 생성합니다.
    @ActivityScoped // Scope
    @Provides
    @JVMStatic
    fun taskPresenter(): TasksPresenter = TaskPresenter()
}
~~~

* ApplicationModule와 마찬가지로 Provides으로 의존성 객체를 생성할 메소드를 정의합니다. 
* 그리고 ContributesAndroidInjector로 하위 Fragment의 Subcomponent를 생성합니다.

### Activity에서 Component 연동하기
~~~kotlin
// DaggerAppCompatActivity를 상속받아 Component에 연결합니다.
class TasksActivity(): DaggerAppCompatActivity() {
    // TaskPresenter 타입의 의존성 주입을 요청합니다.
    @Inject
    lateinit var mTasksPresenter: TasksPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)
    }
}
~~~
* TasksPresenter 의존성을 요청하면 TasksActivity와 연결된 TasksModule에서 생성하고, Subcomponent에서 주입합니다. 
* TasksModule는 ActivityBindingModule에서 Subcomponent를 생성하면서 modules로 연결하였습니다.

### Fragment에서 Component 연동하기
~~~kotlin
// DaggerFragment를 상속받아 Component를 연결합니다.
@ActivityScoped // Fragment는 Activity에 속하므로 Activity Scope를 정의하였습니다.
class TaskFragment(): DaggerFragment() {
    // TasksContract.Presenter 타입의 의존성 주입을 요청합니다.
    @Inject
    fun mPresenter(): TasksContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
~~~

* 위 Activity와 비슷한 흐름입니다. 여기서 요청받은 TasksContract.Presenter 타입의 의존성은 FragmentModule이 없으므로 부모인 ActivitySubcomponent를 검색하여 ActivityModule에서 생성합니다. 
* 이렇게 Subcomponent를 활용하여 Dagger 그래프를 끊지 않고 하위 화면들을 연결할 수 있습니다.
* 코드들을 위 Flow와 매칭하면서 보신다면 이해에 큰 도움이 될것입니다. 이해가 힘드시다면 따로 그림을 그리면서 보기를 권장합니다.

## Dagger2의 키 컨셉은 뭔가요?

* Dagger는 ‘그래프’가 키 컨셉입니다. 
* 의존성을 요청받으면 Subcomponent, Component, Inject 생성자 순으로 검색하여 주입합니다. 
* 처음 Dagger 구조를 설계할 때 햇갈리는 부분이 많을텐데, 그래프 컨셉을 명심한다면 익숙해질 수 있습니다.
* DI 기본개념부터 간단한 사용법까지 시작하는 분들께 도움이 되도록 Dagger를 가볍게 훑었습니다. 
* Dagger는 학습비용이 높은만큼 얻는 것이 많은 라이브러리 입니다. 한번 빠져들면 없이 코딩하는 것을 상상하기 힘들만큼 편리합니다. 
* 포기하지 말고 천천히 적용하면서 Dagger와 함께 유연한 구조의 앱을 만드시기를 바랍니다.

