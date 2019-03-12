# Room
* Room은 SQLite를 추상계층으로 감싸고 있고, 쉽게 데이터베이스에 접근할 수 있습니다.
* Room을 사용한다면 만만치 않은 양의 구조화된 데이터를 영구적으로 저장하고 처리하는 어플리케이션에서 이득을 볼 수 있다고 합니다.
* 구글이 강추하는 이유는 기기가 온라인으로 다시 접속하면 모든 컨텐츠의 변경사항이 동기화되기 때문이라고 합니다.

## Room의 구성요소
* Database: 데이터베이스 홀더를 포함하고, 관계형 데이터 베이스에 접근할 수 있는 액세스 포인트를 제공합니다. @Database라는 어노테이션을 클래스에 달아야 하고 다음 조건을 만족해야합니다.
    * 1. RoomDatabase를 상속한 abstract class여야 합니다.
    * 2. 데이터베이스와 관현된 엔티티들을 어노테이션의 인자값으로 포함해야합니다.
    * 3. abstract method 포함해야하는데, 이 메소드에는 인자가 0개이고 return 되는 클래스가 @Dao 어노테이션을 달고 있어야합니다.

* 런타임일 때에는 Room.databaseBuilder() 또는 Room.inMemoryDatabaseBuilder()를 통해 Database의 객체를 얻어낼 수 있습니다.

* Entity: 데이터베이스의 테이블을 표현합니다.
* DAO: 데이터베이스에 접속하기 위한 메소드를 포함합니다.

## 데이터베이스 환경설정 샘플 코드
* User.kt
~~~kotlin
@Entity
data class User (
    @PrimaryKey
    var uid: int,
    @ColumInfo(name = "first_name")
    var firstName: String,
    @ColumnInfo(name = "last_name")
    var lastName: String
)
~~~

* UserDao.kt
~~~kotlin
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: Array<Int>): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(users: User...)
    @Delete
    fun delete(user: User)
}
~~~

* AppDatabase.kt
~~~kotlin
@Database(entities = {User.class}, version = 1)
abstract class AppDatabase: RoomDataBase {
    abstract fun userDao(): UserDao
}
~~~

* 위와 같은 클래스 파일을 만들고 난뒤에 아래의 코드를 통해 데이터 베이스를 생성할 수 있습니다.

~~~kotlin
val db: AppDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build()
~~~

* RoomDatabase객체를 인스턴스화 하는 비용은 매우 크므로 개발자는 이 AppDatabase 객체를 얻는 작업을 싱글톤패턴으로 만들기를 권장합니다.

## Room Entity를 사용하여 데이터 구조 정의하는 법
* Room 사용시 Entity를 사전에 정의해야하는데, 각각의 엔티티 별로 데이터베이스에서는 테이블이 생성되어 아이템들을 보관할 수 있게 됩니다.
* 기본적으로 Room은 Entity에 정의된 필드에 맞춰 컬럼을 구성하게 됩니다. 
* 만약 Entity를 위해 작성된 Data 클래스에서 어떠한 변수를 선언했고 이것이 데이터베이스에서 컬럼 샘플 코드는 다음과 같습니다.

~~~kotlin
@Entity
data class User (
    @PrimaryKey
    var id: Int,

    var firstName: String,
    var lastName: String,

    @Ignore
    var picture: Bitmap
)
~~~
* Entity들은 빈 생성자(해당 DAO 클래스가 각 필드에 액세스 할 수 있는경우) 또는 매개변수가 엔티티의 필드와 유형 및 이름이 일치하는 생성자를 포함 할 수 있습니다. 
* Room은 또한 전체 또는 몇몇 필드만을 매개변수로 받는 생성자를 사용할 수 있습니다.

## Primary Key 사용하기
* 각 Entity는 반드시 한개의 필드를 기본키로 정의해야합니다.
* 필드가 한개밖에 없더라도 반드시 @PrimaryKey 어노테이션을 붙여서 기본키로 정의를 해야 합니다.
* 만약에 ID와 같은 기본키 값을 자동으로 지정하고 싶다면, @PrimayKey의 속성값으로 autoGenerate를 true로 지정해주면 됩니다.

~~~kotlin
@Entity(PrimaryKeys = {"firstName", "lastName"})
data class User (
    var firstName: String,
    var lastName: String,
    @Ignore
    var picture: Bitmap
)
~~~
* 기본적으로 Room은 클래스이름을 데이터베이스의 테이블명으로 사용합니다. 근데 만약에 다른 이름을 쓰고 싶다면 tableName 속성을 지정하면 됩니다.
~~~kotlin
@Entity(tableName = "users")
data class User(
    ...
)
~~~
* tableName과 비슷하게, Room은 필드명 또한 다르게 지정할 수 있습니다. @ColumnInfo 어노테이션을 이용합니다.
~~~kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "first_name")
    var firstName: String,
    @ColmnInfo(name = "last_name")
    var lastName: String,
    @Ignore
    var picture: Bitmap
)
~~~

## 인덱스와 고유성에 대해 주석달기
* 어떻게 data에 접근하냐에 따라, 쿼리의 속도를 높이기 위해 어떤 필드를 인덱스하고 싶을지도 모릅니다.
* @Entity 어노테이션의 속성으로 indices를 사용한다면 Entity를 인덱싱할 수 있습니다. column 이름의 목록을 적기만 하면 됩니다.
~~~kotlin
@Entity(indices = {@Index("firstName"),
        @Index(value = {"last_name", "address"})})
data class User(
    @PrimaryKey
    var id: Int,
    var firstName: String,
    var address: String,
    @ColumnInfo(name = "last_name")
    var lastName: String,
    @Ignore
    var picture: Bitmap
)
~~~

* 때로는 어떠니 필드나 필드의 그룹을 고유하게 만들어야 할 때가 있습니다. 강제로 고유성을 부여할 수도 있는데 바로 unique 속성입니다.
* 인덱스 어노테이션과 같이 쓰이며 true로 지정하기만 하면 됩니다.

~~~kotlin
@Entity(indices = {@Index(value = {"first_name", "last_name"}, 
        unique = true)})
data class User (
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "first_name")
    var firstName: String,
    @ColumnInfo(name = "last_name")
    var lastName: String,
    @Ignore
    var picture: Bitmap
)
~~~

## 객체간의 관계 정의하기
* SQLite는 관계형 데이터베이스이기 때문에 객체간의 관계도 지정가능합니다.
* 비록 대부분의 관계형 오브젝트 매핑 라이브러리들은 Entity가 다른 것들을 참조하도록 하지만 Room은 분명하게 이 것을 금지하고 있습니다.
* 비록 직접적으로 관계를 맺을 수는 없지만, Room은 여전히 Entity간에 외부키를 정의하는 것을 허용하고 있습니다.
* 예를 들면 Book이라는 Entity가 있고 User라는 Entity와 관계를 맺고 싶다면 @ForeignKey 어노테이션을 통해 정의할 수 있습니다.

~~~kotlin
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))
data class Book (
    @PrimaryKey
    var bookId: Int,
    var title: String,
    @ColumnInfo(name = "user_id")
    var userId: Int
)
~~~

* 외부키는 매우 강력합니다. 참조한 Entity가 업데이트될 때 어떤 일이 발생했는지 명시하는 것을 허용하고 있습니다.
* 예를 들면 @ForeignKey(onDelete=CASCADE) 어노테이션을 가지고 있는 어떤 User객체가 삭제된다면 모든 해당 유저의 모든 책을 삭제해라 라는 명령을 SQLite에게 줄 수도 있습니다.
* SQLite는 @Insert(onConflict = REPLACE)를 하나의 UPDATE 연산자 대신에 REMOVE 와 REPLACE 연산자들의 묶음으로 다룹니다.

## 또 다른 Entity를 내포하는 오브젝트 만들기
* 때로는 여러 필드를 포함하고 이쓴ㄴ 어떠한 객체를 POJO나 Entity를 통해 표현하고 시을 때가 있습니다.
* 이런 경우에는 @Embedded 어노테이션을 사용하여 테이블 내의 하위 필드로 분해 할 객체를 나타낼 수 있습니다.
* 그런 다음 다른 각각의 컬럼과 마찬가지로 포함된 필드를 쿼리할 수 있습니다.

~~~kotlin
data class Address (
    var street: String,
    var state: String,
    var city: String,
    @ColumnInfo(name = "post_code")
    var postCode: Int
)
@Entity
data class User (
    @PrimaryKey
    var id: Int,
    var firstName: String,
    @Embedded
    var address: Address
)
~~~

* 결과적으로 User 테이블은 id, firstName, street, state, city, post_codee를 다 포함해서 표현합니다.
* 만약 Entitty가 같은 자료형의 여러 임베디드 필드를 갖는다면, prefix 속성을 통해 각 컬럼이 고유하도록 할 수 있습니다. 그런 다음 제공된 값을 포함된 객체의 각 컬럼 이름 시작 부분에 추가 합니다.

## Room의 DAO들을 이용하여 data에 접근하기
* Room을 통해 database에 있는 데이터를 사용하기 위해서는 DAO가 필요합니다. DAO는 데이터베이스에 추상적인 접근을 제공하는 메소드들을 포함합니다.
* 질의를 만드는 빌더나 직접적인 쿼리를 작성하는 것 대신, DAO클래스를 사용하여 데이터베이스에 접근하는 것은 데이터베이스 구조의 구성요소를 분리합니다.
* 더 나아가 DAO는 쉽게 데이터베이스를 모킹(Mock)하여 애플리케이션을 테스트하기 쉽게 합니다.

* 하나의 DAO는 interface나 abstract class가 되야 합니다. 만약 abstract class로 만들었다면 선택적으로 RoomDatabase객체를 생성자의 매개변수로 가질 수 있으며, Room은 각 DAO의 구현을 컴파일 시간에 생성해냅니다.
* Room은 메인스레드에서의 데이터베이스 접근을 허용하지 않습니다. 허용하고 싶다면 데이터베이스를 생성하는 빌더에서 allowMainThreadQueries()를 호출해야합니다.
* 그 이유는 데이터를 받아오는 작업이 길어질 경우 UI가 장시간 멈춰버릴 수 있기 때문입니다. 그래서 보통 비동기 쿼리를 하게 되는데 반환값으로는 LiveData 또는 RxJava의 Flowable이 될 수 있습니다.

## 편의성을 위한 메소드 정의하기
### Insert
* DAO메소드를 만들때 @Insert를 달아줄 수 있습니다. 
* Room은 이와 관련된 코드를 생성해내고 모든 파라미터를 하나의 트랙잭션 내에서 삽입(insert)하게 됩니다.

~~~kotlin
@Dao
interface MyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: User..)
    @Insert
    fun insertBothUsers(user1: User, user2: User)
    @Insert
    fun insertUsersAndFriends(user: User, friends: List<User>)
}
~~~

* 만약 @Insert 메소드가 하나의 파라미터만 받는다면, 삽입한 데이터에 대한 long형 rowld를 리턴받을 수 있습니다.
* 만약 파라미터가 여러개라면 List<Long>으로 대신 리턴 받을 수 있습니다.