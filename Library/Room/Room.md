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
    var lastName: String)
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
    var picture: Bitmap)
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
    var picture: Bitmap)
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
    var picture: Bitmap)
~~~

## 인덱스와 고유성에 대해 주석달기
* 어떻게 data에 접근하냐에 따라, 쿼리의 속도를 높이기 위해 어떤 필드를 인덱스하고 싶을지도 모릅니다.
* @Entity 어노테이션의 속성으로 indices를 사용한다면 Entity를 인덱싱할 수 있습니다. column 이름의 목록을 적기만 하면 된다.
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
    var picture: Bitmap)
~~~

https://www.charlezz.com/?p=368