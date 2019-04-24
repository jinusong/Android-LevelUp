# Paging Library
* Paging Library는 앱이 데이터 소스로부터 필요한 정보를 점진적으로 읽어오는 작업을 쉽게 만들어주는 라이브러리입니다.

## 왜 생겼지?
* 페이징을 하지 않으면 불필요한 네트워크 트래픽의 발생과, 디바이스의 성능저하를 초래합니다. 
* 데이터가 원격저장소에 저장되거나 동기화되면, 이 또한 앱이 느려지는 원인이 되고, 사용자의 데이터요금을 낭비하게 됩니다.
* Room과 같은 기존의 아키텍쳐 컴포넌트 라이브러리들과도 원활하게 잘 동작하도록 구성되어 있습니다.

## 기능
### 데이터를 어떻게 가져오는지 정의하기
* DataSource 클래스를 이용하여 페이징된 데이터를 얻어올 데이터 소스를 정의합니다. 
* 데이터 접근 방법에 따라, 이 클래스를 상속받아 서브클래스로 구성할 수도 있습니다.
    * 로드할 데이터가 다음 / 이전 키를 포함하고 있다면, PageKeyedDataSource 클래스를 사용합니다.
    * 항목 N의 데이터를 이용해 N+1의 데이터를 가져와야 한다면, ItemKeyedDataSource 클래스를 사용합니다.
    * 데이터 저장소에서 선택한 위치로부터 데이터의 페이지를 가져와야한다면, PositionalDataSource 클래스를 사용합니다.
* 만약 Room 라이브러리를 사용하여 데이터를 관리하고 있다면, 이 Room 라이브러리는 자동적으로 PositionalDataSource 인스턴스를 생성하기 위해 DataSource.Factory를 발생시킬 수 있습니다.

~~~kotlin
@Query("select * from users WHERE age > :age order by name DESC, id ASC")
fun usersOlderThan(int age): DataSource.Factory<Integer, User>
~~~

### 매모리로부터 데이터 로드
* PagedList클래스는 DataSource로부터 데이터를 불러옵니다. 
* 개발자는 데이터가 한번에 얼마나 로드가 되어야 하고, 미리 패치되어야 하는지 설정하여, 사용자가 데이터로드가 완료될 때까지 기다려야 하는 시간을 최소화할 수 있습니다.

### UI에 데이터 나타내기
* PagedListAdapter 클래스는 PagedList로부터 데이터를 UI에 나타내기 위한 RecyclerView.Adapter의 구현체입니다.
* 데이터가 갱신되었음을 RecyclerView에게 알려준다.
* 백그라운드 스레드를 사용하여 한 PagedList에서 다른 PagedList 로의 변경 사항을 계산하고 필요에 따라 목록 내용을 업데이트하기 위해 notifyItem ... () 메소드를 호출합니다. 
* 그리고나서 RecyclerView는 필요한 변화를 수행합니다.

### 데이터 갱신 관찰하기
* LivePageListBuilder : 개발자가 제공한 DataSource.Factory를 통해 LiveData<PagedList>를 생성합니다. 
* 만약 데이터베이스를 관리하기 위해 Room 라이브러리를 사용한다면, DAO는 PositionalDataSource를 사용하여 DataSource.Factory를 생성할 수 있습니다.

~~~kotlin
val pagedItems: LiveData<PagedList<Item>> =
        LivePagedListBuilder(myDataSource, /* page size */ 50)
                .setFetchExecutor(myNetworkExecutor)
                .build()
~~~

* RxpagedListBuilder : LivepagedListBuilder와 유사하게, RxJava2 기반의 기능을 제공합니다. 
* 아키텍쳐 라이브러리의 RxJava2기반으로 제공됩니다. 
* PagedList를 구현할 때 Flowable과 Observable을 생성할 수 있습니다.

~~~kotlin
val pagedItems: Flowable<PagedList<Item>> =
        RxPagedListBuilder(myDataSource, /* page size */ 50)
                .setFetchScheduler(myNetworkScheduler)
                .buildFlowable(BackpressureStrategy.LATEST)
~~~

### 데이터 흐름 생성하기
* 페이징 라이브러리의 컴포넌트는 백그라운드 스레드의 생산자로부터 UI스레드의 화면 출력까지의 데이터 흐름을 구성합니다.
* DataSource는 갱신될 것이고, LiveData<PagedList> / Flowable<PagedList> 는 백그라운드 스레드에서 새로운 PagedList를 생산합니다.

* 새로 생성된 PagedList는 UI스레드에서 PagedListAdapter로 보내집니다. 
* PagedListAdapter는 백그라운드 스레드에서 DiffUtil을 이용하여, 기존의 리스트와 새로운 리스트 사이의 차이를 계산합니다. 
* 비교가 끝났으면, PagedListAdapter는 리스트의 차이점을 이용하여 RecyclerView.Adapter.notifyItemInserted() 를 호출하여 UI에 항목들을 삽입합니다.

## 데이터베이스 예제
### LiveData를 이용하여 페이징된 데이터 관찰하기
~~~kotlin
@Dao
interface UserDao {
    // The Integer type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT * FROM user ORDER BY lastName ASC")
    abstract fun usersByLastName(): DataSource.Factory<Integer, User>
}

class MyViewModel(userDao: UserDao): ViewModel() {
    val usersList: LiveData<PagedList<User>>
    init {
        usersList = LivePagedListBuilder<>(
                userDao.usersByLastName(), /* page size */ 20).build()
    }
}

class MyActivity: AppCompatActivity() {
    private lateinit var mAdapter: UserAdapter<User>

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState)
        val viewModel: MyViewModel = ViewModelProviders.of(this).get(MyViewModel.class)
        val recyclerView: RecyclerView = findViewById(R.id.user_list)
        mAdapter = UserAdapter()
        viewModel.usersList.observe(this, pagedList ->
                mAdapter.submitList(pagedList))
        recyclerView.setAdapter(mAdapter)
    }
}

class UserAdapter: PagedListAdapter<User, UserViewHolder>(DIFF_CALLBACK) {
    
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User = getItem(position)
        if (user != null) {
            holder.bindTo(user)
        } else {
            // Null defines a placeholder item - PagedListAdapter will automatically invalidate
            // this row when the actual object is loaded from the database
            holder.clear()
        }
    }
    val DIFF_CALLBACK: DiffUtil.ItemCallback<User> = DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldUser: User, newUser: User): Boolean  {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldUser.getId() == newUser.getId()
        }
        override fun areContentsTheSame(oldUser: User, newUser: User): Boolean {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldUser.equals(newUser)
        }
    }
}
~~~

### RxJava2를 이용하여 페이징된 데이터 관찰하기
* LiveData 대신 RxJava2를 사용하려고 한다면, Observable이나 Flowable 객체를 생성합니다.

~~~kotlin
class MyViewModel(userDao: UserDao): ViewModel() {

    val usersList: Flowable<PagedList<User>>

    init {
        usersList = RxPagedListBuilder<>(userDao.usersByLastName(),
                /* page size */ 50).buildFlowable(BackpressureStrategy.LATEST)
    }
}
~~~
* 그리고, 다음의 코드와 같이 해당 데이터의 관찰을 시작 / 중지할 수 있습니다.
~~~kotlin
class MyActivity: AppCompatActivity() {
    private lateinit var mAdapter: UserAdapter<User>
    private val mDisposable = CompositeDisposable()

    override fun onCreate(savedState: Bundle) {
        super.onCreate(savedState)
        val viewModel: MyViewModel = ViewModelProviders.of(this).get(MyViewModel.class)
        val recyclerView: RecyclerView = findViewById(R.id.user_list)
        mAdapter = UserAdapter()
        recyclerView.setAdapter(mAdapter)
    }

    override fun onStart() {
        super.onStart()
        myDisposable.add(mViewModel.usersList.subscribe(flowableList ->
                mAdapter.submitList(flowableList)))
    }

    override fun onStop() {
        super.onStop()
        mDisposable.clear()
    }
}
~~~

* UserDao, UserAdapter 클래스의 코드는 LiveData의 예제와 같으므로 생략합니다.

## 데이터 로딩 구조 선택하기
* 페이징 라이브러리를 이용해 데이터를 페이징하는 두가지의 기본적인 방법이 있습니다.

### 네트워크 or 데이터베이스
* 첫번째로, 하나의 소스로부터 페이징을 할 수 있습니다. 이 경우 다음 샘플과 같이 LiveData를 이용하여 로드된 데이터를 UI에 전달합니다.
* 데이터의 소스를 지정하려면 LivePagedListBuilder에 DataSource.Factory를 전달합니다.
* 데이터베이스를 관찰할 때, 컨텐츠의 변화가 발생하면, 데이터베이스는 새로운 PagedList에 푸쉬합니다. 
* 네트워크 페이징인 경우에, 스와이프 리프레시 같은 시그널은 현재 데이터소스를 무효화함으로써 새로운 PagedList를 받아올 수 있습니다. 
* 이는 모든 데이터를 비동기적으로 새로고침합니다.

### 네트워크 and 데이터베이스
* 두번째로, 로컬저장소로부터 데이터를 페이지할 수 있고, 로컬저장소는 네트워크로부터 추가 데이터를 페이징합니다. 
* 이는 종종 네트워크 연결을 최소화하고, 백엔드의 캐시로 데이터베이스를 사용하여 더 나은 연결지양 경험을 제공합니다.
* 이후, 이 콜백을 네트워크 요청에 연결하면 데이터가 데이터베이스에 바로 저장됩니다. 
* UI는 데이터베이스의 갱신을 구독하고 있으므로, 새로운 데이터의 변경은 자동적으로 UI로 전달됩니다.