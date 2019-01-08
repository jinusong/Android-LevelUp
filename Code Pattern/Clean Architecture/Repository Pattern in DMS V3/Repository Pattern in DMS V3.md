# Repository Pattern in DMS V3
![Repository Pattern](https://cdn-images-1.medium.com/max/1600/1*5kNXJ7aFSGJvuh4r4egpTg.png)

## Repository Pattern이란?
* Repository (레파지토리) 패턴은 아키텍처라기보다는 디자인 패턴중에 하나인데 데이터가 있는 어떤 저장소이든 간에 데이터를 사용하는 로직에서 분리시키는 것을 목적으로 합니다.

## 왜 Repository Pattern을 쓰는가?
* 응용 프로그램을 데이터 소스에서 분리합니다.
* 클라이언트가 문제를 염려하지 않고도 여러 소스 (DB, API)의 데이터를 제공합니다.
* 데이터 층 분리할 수 있고, 중앙 집중방식, 일관된 데이터 액세스를 할 수 있습니다.
* 단위 테스트를 통해 검증 가능한 비즈니스 로직이 가능해집니다.
* 새로운 코드를 쉽게 추가 할 수 있습니다.

## Repository Pattern을 이해하자
![Mapping](https://t1.daumcdn.net/cfile/tistory/274A9A4258E1186631)
* 도메인과 데이터 사이를 중재하는 매핑 레이어로 도메인 객체에 접근하기 위한 콜렉션과 같은 인터페이스를 사용합니다.
* 도메인은 필요한 데이터를 레파지토리에 요청하고, 레파지토리는 도메인이 요청한 정보를 데이터를 가공해서 보내줍니다. 
* 이 패턴을 사용하는 장점은 도메인이 실제로 어떤 데이터를 사용하는지는 알지 못하기 때문에 환경에 맞는 데이터를 사용하기 편리하다는 것입니다.

![Domain and Repository](https://t1.daumcdn.net/cfile/tistory/260DD34258E118661D)
* 위의 그림을 보면 테스트 환경에서는 In-Memory 데이터를 사용하고 프로덕션 환경에서는 원격 서버에 있는 DB의 데이터를 사용하지만 도메인은 두 경우 모두 변함이 없습니다. 
* 바뀌는 부분은 레파지토리의 실제 구현인데 추상화된 Repository 클래스로 구현된 클래스들이 싸여져 있어 도메인은 언제나 같은 인터페이스로 요청을 할 수 있습니다. 

### 복잡한 도메인 모델일 경우에는?
* 복잡한 도메인 모델을 가지는 시스템은 데이터 매핑하는 레이어를 두어 레이어를 장점으로 이용할 수 있습니. 
* 그렇게 하면 도메인 객체를 상세한 데이터베이스 접근 코드에서 분리시킬 수 있게 됩니다. 
* 데이터베이스 쿼리를 구현하는 코드가 만들어지는 매핑레이어로 또다른 추상화 레이어를 만드는 것입니다. 
* 이 패턴은 수많은 도메인 클래스들 또는 복잡한 쿼리를 가질 때 더 중요합니다. 
* 특히 이런 경우 이 레이어를 더하는 것은 쿼리 로직의 중복을 최소화하는데 도움이 됩니다.

#### 작동은?
* Repository는 도메인과 데이터 매핑 레이어들 사이를 중재해서 in-memory 도메인 객체 콜렉션 같은 역할을 합니다. 
* 클라이언트 객체는 쿼리 명세를 선언적으로 만들고 만족하는 Repository에 보냅니다. 객체는 Repository에 추가되거나 제거될 수 있습니다. 그리고 Repository에서 캡슐화되는 매핑 코드는 안보이는 곳에서 적절한 작동을 합니다. 
* 개념적으로 Repository는 데이터 저장소에 있는 객체 집합을 캡슐화하고 더욱 더 객체 기반적인 뷰를 제공합니다. 
* 그리고 Repository는 깔끔한 분리를 추구하고 도메인과 데이터 매핑 레이어간 단방향 의존성을 지원합니다. 

### 그러면 실제 패턴에서는?
![MVP](https://t1.daumcdn.net/cfile/tistory/2771924858E1186731)

* Repository 패턴은 MVP 아키텍처에서도 사용됩니다. 
* Android MVP 아키텍처를 예를 들면 Activity, Fragment 등의 View가 Presenter에게 어떤 기능을 요청하면 Presenter는 Model에서 데이터를 받아와 가공한 뒤 View에게 콜백합니다. 
* 이 과정에서 Presenter는 Model에게 어떻게 요청을 보내고 데이터를 받아올 수 있을까요? 직접 DB 쿼리를 할 수도 있겠지만 그러면 Presenter와 Model이 서로 의존하게 됩니다.

![Repository in MVP](https://t1.daumcdn.net/cfile/tistory/2620424958E1192225)

* 이때 Presenter와 Model사이 Repository가 중재 역할을 할 수 있습니다. 
* Presenter는 Repository의 인터페이스를 통해 필요한 데이터를 요청하고 Repository는 Presenter가 직접적으로 어떤 데이터를 사용하는지는 알 수없게 하며 데이터를 어딘가에서 가져와 Presenter에게 넘겨줍니다. 
* 이 때 Presenter는 Repository 패턴의 Domain 역할을 한다고 볼 수 있습니다. 실제로 Presenter의 역할은 비즈니스 로직 즉, 기능 구현이기 때문에 일맥상통합니다.

![Model and Repository](https://t1.daumcdn.net/cfile/tistory/224F9D4E58E1192D30)

* Repository는 MVP 아키텍처의 옵션입니다. 만약 Model이 복잡하지 않고 환경이 변하지 않는다면 굳이 사용하지 않아도 됩니다. 
* 여기서 Repository는 데이터는 아니지만 필요한 데이터를 쿼리하는 로직이기 때문에 비즈니스 로직 혹은 유즈케이스를 구현하는 Presenter보다는 Model에 속하는 것이 맞다고 생각하는데 안드로이드 패키지를 구조화할 때 domain 패키지 안에 repository를 넣는 경우도 있습니다. 
* 어디에 속하든 중요한 것은 모델과 비즈니스 로직을 분리하는 것이다. 

![Repository in Model](https://t1.daumcdn.net/cfile/tistory/265FD34D58E119391F)
* Repository 패턴을 사용하는 MVP 패턴은 다음과 같은 다이어그램이으로 결론지을 수 있을 것 같습니다.

## DMS V3 개발을 위한 예시
![구조](https://cdn-images-1.medium.com/max/2000/1*wfqhaIhyBVcfDwYe4P3sHg.png)

* local 저장은 어케하는게 좋을까요?
    * SQLite만 사용(상용구가 너무 많아요 ㅠㅠ)
    * Realm (사용 사례들이 너무 복잡해서 대부분 기능이 필요하지 않아요)
    * Room (GreenDao 대신 새로 도입된 ORM입니다. RxJava2를 지원합니다.)

* 그래서 이번 DMS V3에서는 Google의 새로운 라이브러리인 Room을 사용합니다.

### Room은 어떻게 쓰지?
~~~kotlin
data class Trail(
    var uid: Int,
    var firstName: String?,
    var lastName: String?
)
~~~
* 보통 다음과 같은 일반적인 POJO가 있다면
* Room에서는 조금 다른 POJO를 씁니다.
~~~kotlin
@Entity
data class Trail( 
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "first_name") var firstName: String?,
    @ColumnInfo(name = "last_name") var lastName: String?
)
~~~
* 위 코드 Room POJO이고 @Entity를 붙여 테이터베이스 내의 테이블을 나타냅니다.
* 또, @PrimaryKey로 Key값을 설정하고 @ColumnInfo로 정보를 나타냅니다.

~~~Kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
           "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}
~~~
* 위의 코드는 DAO라고 하며, 데이터베이스에 액세스하는 데 사용되는 메소드를 포함합니다.

~~~kotlin
@Database(entities = arrayOf(User::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
~~~
* 위의 파일을 만든 후 다음 코드를 사용하여 만든 데이터베이스의 인스턴스를 가져옵니다.
~~~kotlin
val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
~~~

### 예제 코드로 가보자
#### 예제 소개
* Retrofit으로 Github 정보를 가져 와서 응용 프로그램 저장소 Room에 즉시 저장하는 예시입니다.
* 그리고 다음 화면이 다시 시작될 때 우리는 동일한 정보를 Room에서 먼저 가져 오고 필요한 경우 Github API에서 새로 고칩니다.
* 이 예제는 LiveData, Room, Repository Pattern 등을 사용하는 예제이기 때문에 DMS V3의 아키텍쳐 구조를 구현하기 전 익혀두면 많은 도움이 될 것 입니다.
* 또, 이 예제에서는 DMS V2에서 발생했던 화면 재시작시 앱이 튕겨지는 이슈에 대한 해결책도 함께 다루고 있어서 이 예제를 선택했습니다.

#### 주의
* 예제를 볼 때 다른 기술에 신경쓰지말고 오직 아키텍쳐 구조에만 신경써서 봐주셨으면 좋겠습니다.
* 이 예제는 분명 좋은 예제입니다. 하지만 원문이 영어라서 저의 해석이 부족할 수도 있습니다.
* 그리고 다음 예제는 Kotlin으로 되어 있지만 제가 원문의 Java코드를 Kotlin으로 번역한 것입니다.
* 또, 억지로 번역한 코드들이다 보니까 코드 스타일도 Java언어에 맞게 작성되었고 버터나이프 같은 Kotlin에는 필요없는 것들을 사용하기도 허였습니다.
* 그래서 제가 쓴 글만 보지 마시고 아래의 링크에서 코드를 한번씩 봐주셨으면 좋겠습니다.
* https://proandroiddev.com/the-missing-google-sample-of-android-architecture-components-guide-c7d6e7306b8f

~~~kotlin
@Entity
class User {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: String

    @SerializedName("login")
    @Expose
    var login: String? = null

    @SerializedName("avatar_url")
    @Expose
    var avatar_url: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("company")
    @Expose
    var company: String? = null

    @SerializedName("blog")
    @Expose
    var blog: String? = null

    var lastRefresh: Date? = null
}
~~~
#### LiveData를 활용해보자!
* LiveData는 관찰 가능한 데이터입니다. 이 것은 의존 경로를 따로 만들어주지 않아도 변경 사항에 대한 개체를 관찰할 수 있다는 뜻입니다.
* 또한 LiveData는 Activity, Fragment, Service의 수명주기 상태를 존중하며 앱이 더 많은 메모리를 소비하지 않도록 누출을 방지하는 올바른 방법을 제공합니다.
~~~kotlin
@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    fun save(user: User)

    @Query("SELECT * FROM user WHERE login = :userLogin")
    fun load(userLogin: String): LiveData<User>

    @Query("SELECT * FROM user WHERE login = :userLogin AND lastRefresh > :lastRefreshMax LIMIT 1")
    fun hasUser(userLogin: String, lastRefreshMax: Date): User
}
~~~

* Room은 Date 객체를 유지할 수 없으므로 TypeConverter 를 만들어 줍니다.

~~~kotlin
object DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return (if (date == null) null else date!!.getTime()).toLong()
    }
}
~~~
* 마지막으로 데이터베이스 객체를 만들어줍니다.
~~~kotlin
@Database(entities = { User.class}, version = 1)
@TypeConverters(DateConverter::class)
abstract class MyDatabase : RoomDatabase() {
    // --- DAO ---
    abstract fun userDao(): UserDao

    companion object {
        // --- SINGLETON ---
        private val INSTANCE: MyDatabase? = null
    }
}
~~~

### Retrofit 구성
* User 객체는 Retrofit과 Room 모두에서 사용됩니다. 이제는 Retrofit 인터페이스만 작성하면됩니다.
~~~kotlin
interface UserWebservice {
    @GET("/users/{user}")
    fun getUser(@Path("user") userId: String): Call<User>
~~~

### Repository 구성
* 드디어 가장 중요한 Repository 입니다. 
* 내부에 미리 정의된 흐름에 따라 사용자의 데이터를 가져오는데 사용할 데이터를 하나 선택합니다.
    * 처음으로 사용자가 앱을 실행 하면 Retrofit를 사용합니다.
    * 사용자 데이터의 API에서 마지막으로 가져 오는 것이 3분 이상 지난 경우 데이터베이스 대신 Retrofit을 이용합니다.
    * 그렇지 않으면, 데이터베이스를 사용합니다.

* Repository는 데이터 조작을 처리합니다.
* 데이터를 가져올 위치와 데이터를 업데이트할 때 수행할 API 호출을 알고 있습니다. 
* 서로 다른 데이터(모델 객체, Retrofit, 캐시 등) 사이의 중개자로 간주할 수 있습니다.

~~~kotlin
class UserRepository(private val webservice: UserWebservice, private val userDao: UserDao, private val executor: Executor) {
    companion object {
        private val FRESH_TIMEOUT_IN_MINUTES = 3
    }
    
    // ---

    fun getUser(userLogin: String): LiveData<User> {
        refreshUser(userLogin) // try to refresh data if possible from Github Api
        return userDao.load(userLogin) // return a LiveData directly from the database.
    }

    // ---

    private fun refreshUser(userLogin: String) {
        executor.execute({
            // Check if user was fetched recently
            val userExists = userDao.hasUser(userLogin, getMaxRefreshTime(Date())) != null
            // If user have to be updated
            if (!userExists) {
                webservice.getUser(userLogin).enqueue(object : Callback<User>() {
                    fun onResponse(call: Call<User>, response: Response<User>) {
                        Toast.makeText(App.context, "Data refreshed from network !", Toast.LENGTH_LONG).show()
                        executor.execute({
                            val user = response.body()
                            user.setLastRefresh(Date())
                            userDao.save(user)
                        })
                    }

                    fun onFailure(call: Call<User>, t: Throwable) {}
                })
            }
        })
    }

    // ---

    private fun getMaxRefreshTime(currentDate: Date): Date {
        val cal = Calendar.getInstance()
        cal.setTime(currentDate)
        cal.add(Calendar.MINUTE, -FRESH_TIMEOUT_IN_MINUTES)
        return cal.getTime()
    }
}
~~~
### ViewModel 구성
* ViewModel Factory를 만들어서 의존성을 핸들링해서 좀 더 깔끔하게 해봅시다.
~~~Kotlin
class FactoryViewModel(private val creators: Map<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
        try {
            return creator!!.get()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
~~~
* 위 코드는 ViewModel Factory 입니다.
~~~kotlin

~~~
* 뷰 모델은 이러한 Fragment 또는 Activity와 같은 특정 UI 요소에 대한 데이터를 제공하고, 
* 이러한 데이터를 로드하기 위해 다른 구성 요소를 호출 또는 사용자 수정을 전송 등의 데이터 처리의 비즈니스 부분과의 통신을 처리합니다.
* 그리고 ViewModel은 View에 대해 알지 못하고 회전으로 인해 Activity 재작성과 같은 변경에 영향을 받지 않습니다.

~~~kotlin
class UserProfileViewModel(private val userRepo: UserRepository) : ViewModel() {

    var user: LiveData<User>? = null

    // ----

    fun init(userId: String) {
        if (this.user != null) {
            return
        }
        user = userRepo.getUser(userId)
    }
}
~~~
* 이렇게 ViewModel은 간결해집니다.

### Dagger2로 DI를 해보자
* 사실 DMS V3 개발을 할 때 DI가 필요하다면 Dagger2 보다는 Koin을 사용할 것 같습니다.
* 저부터도 Dagger2를 잘 모르기도 하고요 ㅎㅎ 일단 DMS V3 팀원 분들은 Dagger2를 참고용으로 보시면 될 것 같습니다.
~~~kotlin
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
~~~
~~~Kotlin
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel::class)
    internal abstract fun bindUserProfileViewModel(repoViewModel: UserProfileViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: FactoryViewModel): ViewModelProvider.Factory
}
~~~
~~~Kotlin
@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    internal abstract fun contributeUserProfileFragment(): UserProfileFragment
}
~~~
~~~kotlin
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = FragmentModule::class)
    internal abstract fun contributeMainActivity(): MainActivity
}
~~~
~~~kotlin
@Module(includes = ViewModelModule::class)
class AppModule {

    companion object {
        private val BASE_URL = "https://api.github.com/"
    }

    // --- DATABASE INJECTION ---

    @Provides
    internal fun provideDatabase(application: Application): MyDatabase {
        return Room.databaseBuilder(
            application,
            MyDatabase::class.java, "MyDatabase.db"
        )
            .build()
    }

    @Provides
    internal fun provideUserDao(database: MyDatabase): UserDao {
        return database.userDao()
    }

    // --- REPOSITORY INJECTION ---

    @Provides
    internal fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    internal fun provideUserRepository(
        webservice: UserWebservice,
        userDao: UserDao,
        executor: Executor
    ): UserRepository {
        return UserRepository(webservice, userDao, executor)
    }

    // --- NETWORK INJECTION ---

    @Provides
    internal fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    internal fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build()
    }

    @Provide
    internal fun provideApiWebservice(restAdapter: Retrofit): UserWebservice {
        return restAdapter.create<UserWebservice>(UserWebservice::class.java!!)
    }
}
~~~
~~~kotlin
@Component(modules = { ActivityModule.class, FragmentModule.class, AppModule.class })
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}
~~~
~~~kotlin
class App : Application(), HasActivityInjector {

    companion object {
        var context: Context
    }
    
    internal var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>? = null

    fun onCreate() {
        super.onCreate()
        this.initDagger()
        context = getApplicationContext()
    }

    fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    // ---

    private fun initDagger() {
        DaggerAppComponent.builder().application(this).build().inject(this)
    }
}
~~~
### Fragment와 Activity에서 사용
~~~kotlin
class UserProfileFragment : Fragment() {

    companion object {
        // FOR DATA
        val UID_KEY = "uid"
    }
    
    internal var viewModelFactory: ViewModelProvider.Factory? = null
    private var viewModel: UserProfileViewModel? = null

    // FOR DESIGN
    @BindView(R.id.fragment_user_profile_image)
    internal var imageView: ImageView? = null
    @BindView(R.id.fragment_user_profile_username)
    internal var username: TextView? = null
    @BindView(R.id.fragment_user_profile_company)
    internal var company: TextView? = null
    @BindView(R.id.fragment_user_profile_website)
    internal var website: TextView? = null

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    fun onActivityCreated(@Nullable savedInstanceState: Bundle) {
        super.onActivityCreated(savedInstanceState)
        this.configureDagger()
        this.configureViewModel()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    private fun configureDagger() {
        AndroidSupportInjection.inject(this)
    }

    private fun configureViewModel() {
        val userLogin = getArguments().getString(UID_KEY)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserProfileViewModel::class.java!!)
        viewModel!!.init(userLogin)
        viewModel!!.getUser().observe(this, { user -> updateUI(user) })
    }

    // -----------------
    // UPDATE UI
    // -----------------

    private fun updateUI(user: User?) {
        if (user != null) {
            Glide.with(this).load(user!!.getAvatar_url()).apply(RequestOptions.circleCropTransform()).into(imageView)
            this.username!!.setText(user!!.getName())
            this.company!!.setText(user!!.getCompany())
            this.website!!.setText(user!!.getBlog())
        }
    }
}
~~~
~~~kotlin
class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    companion object {
        private val USER_LOGIN = "JakeWharton"
    }
    
    internal var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.configureDagger()
        this.showFragment(savedInstanceState)
    }

    fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment>? {
        return dispatchingAndroidInjector
    }

    // ---

    private fun showFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {

            val fragment = UserProfileFragment()

            val bundle = Bundle()
            bundle.putString(UserProfileFragment.UID_KEY, USER_LOGIN)
            fragment.setArguments(bundle)

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, null)
                .commit()
        }
    }

    private fun configureDagger() {
        AndroidInjection.inject(this)
    }
}
~~~
![구조](https://cdn-images-1.medium.com/max/960/1*KnYBBZIDDeg4zVDDEcLw2A.png)
* Dagger 때문에 결국 엄청 많은 클래스로 나뉘게 되었지만 위 그림의 컴포넌트의 분리를 완벽하게 지켜낸 코드입니다.

## 출처
* http://imcreator.tistory.com/105
* https://medium.com/corebuild-software/android-repository-pattern-using-rx-room-bac6c65d7385
* https://proandroiddev.com/the-missing-google-sample-of-android-architecture-components-guide-c7d6e7306b8f
* https://developer.android.com/training/data-storage/room/