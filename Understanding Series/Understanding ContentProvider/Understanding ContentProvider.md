# Understanding ContentProvider
* ContentProvider는 앱사이에서 각종 데이터를 공유할 수 있게 해주는 컴포넌트입니다. 
* 안드로이드 표준 시스템에서는 연락처인 Contacts나 이미지나 동영상 등의 데이터를 보관하는 MediaStore 등이 ContentProvier로 공개돼있습니다.
* 데이터를 검색, 추가, 갱식, 삭제할 수 있으며, 주로 SQLite 드으이 관계형 데이터베이스 이용을 염두에 두고 설계됐습니다.
* 그러므로 관계형 데이터베이스를 다룬 경험이 있으면 비교적 이해하기 쉽습니다.
## ContentProvider로부터 데이터를 읽어오자
* ContentProvider로부터 데이터를 읽어오려면 해당 ContentProvider가 어디에 있는지 알아야 합니다. 
* 경로는 'content://스키마'를 가진 URI(Universal Resource Ideentifier)로 지정되고, 일반적으로 접근할 앱에서 정의됩니다.
* 또한 이 URI는 authority로 불리며, ContentProvider를 직접 만들 때는 AndroidManifest.xml에 기술해야 합니다.
### ContentResolver를 통해 데이터를 읽는다
* ContentProvider가 제공하는 데이터에는 ContentResolver를 통해 접근하도록 설계돼 있고, ContentProvier 자신에 대한 참조는 필요 없습니다. ContentResolver의 인스턴스는 getContentResolver() 메서드로 가져옵니다.
~~~kotlin
var cr: ContentResolver = getContentResolver()
~~~
* ContentResolver에 URI를 전달함으로써 ContentProvider의 데이터에 접근할 수 있습니다. 데이터는 ContentResolver.query()를 이용해 가져옵니다.
~~~kotlin
fun query(uri: Uri, projection: String[], selection: String, selectionArgs: String[], sortOrder: String): Cursor
~~~
* query 메서드의 인수는 조금 복잡하므로 다음 표로 정리했습니다.

|인수|내용|
|---|----|
|uri|ContentProvider가 관리하는 uri|
|projection|가져오고 싶은 칼럼명(select에 해당)|
|selection|필터링할 칼럼명을 지정(where)|
|selectionArgs|selection으로 지정한 칼럼명의 조건을 설정(프리페어드 스테이트먼트에 해당)|
|sortOrder|정렬하고 싶은 칼럼명을 지정(order by에 해당|
* query()의 반환값인 Cursor란 어떤 것일까요? Cursor란 데이터에 접근하는 포인터입니다. 2차원 표를 떠올리면 이해하기 쉽습니다.

### MediaStore에서 이미지를 가져온다
* 이번에는 '갤러리'앱에서 이용되는 이미지를 저장하는 ContentProvider에서 이미지를 가져와 봅시다. 
* 우선 MediaStore에서 이미지를 가져오는 가져오는 부분을 살펴봅니다. ContentProvider에 접근할 때 기본적으로 다른 앱이 이용할 수 있도록 필요한 상수는 정의돼 있습니다.
* Authority를 나타내는 Uri는 보통 CONTENT_URI, EXTERNAL_CONTENT_URI같은 상수명으로 공개됩니다.
* 또한 가져오고 싶은 칼럼명도 마찬가지로 정의돼 있으므로 이를 이용합니다. 우선 필요한 projection 등을 기술하고, ContentResolver.query()를 호출해 Cursor를 가져옵니다.
~~~kotlin
private fun getImage(): Cursor {
    var contentResolver: ContentResolver = getContentResolver()
    var queryUri: Uri = Media.EXTERNAL_CONTENT_URI

    var projection = arrayof{ ImageColums._ID, ImageColums.TITLE, ImageColums.DATE_TAKEN,}

    var sortOrder: String = ImageColumns.DATE_TAKEN + " DESC"
    queryUri = queryUri.buildUpon().appendQueryParameter("limit", "1").build()
    return contentResolver.query(queryUri,  projection, null, null, sortOrder)
}
~~~
* MediaStore는 공개된 인터페이스가 중첩돼 있어 코드가 길어지므로 여기서는 static 임포트를 이용합니다. 중첩된 내부 클래스와 인터페이스에 접근할 때 깔끔하게 만들 수 있습니다.
~~~kotlin
import static android.provider.MediaStore.Images.ImageColumns
import static android.provider.MediaStore.Images.Media
~~~
### Cursor로부터 데이터를 가져오는 방법
* 우선 가져온 Cursor를 통해 데이터에 접근합니다.
~~~kotlin
var cursor: Cursor = getImage()
if(cursor.moveToFirst()) {
    // 1. 각 칼럼의 열 인덱스 취득
    var idColNum: Int = cursor.getColumnIndexOrThrow(ImageColumns._ID)
    var titleColNum: Int = cursor.getColumnIndexOrThrow(ImageColumns.TITLE)
    var dateTakenColNum: Int = cursor.getColumnIndexOrThrow(ImageColumns.DATE_TAKEN)


    // 2. 인데스를 바탕으로 데이터를 Cursor로부터 취득
    var id: Long = cursor.getLong(idColNum)
    var title: String = cursor.getString(titleColNum)
    var dateTaken: Long = cursor.getLong(dataTakenColNum)
    var imageUri: Uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id)

    // 3. 데이터를 View로 설정
    var textView: TextView = findViewById(R.id.textView)
    var imageView: ImageView = findViewById(R.id.imageView)
    var calendar: Calendar =  Calendar.getInstance()
    calendar.setTimeInMillis(dateTaken)
    var text: String = DateFormat.format("yyyy/MM/dd(E) kk:mm:ss", calendar).toString()
    textView.setText("촬영일시: " + text)]
    imageView.setImageURI(imageUri)
}
cursor.close()
~~~
* 처음에 Cursor.moveToFirst()를 호출해 커서를 맨 앞으로 이동합니다. true가 반환된 경우에만 Cursor에서 데이터를 가져옵니다. 
* false가 반환된 경우는 데이터가 비었으므로 그 이후의 처리는 필요 없습니다.
* Cursor로부터 데이터를 가져오라면 두 단계가 필요합니다. 우선 처음에 가져오고 싶은 칼럼의 인덱스를 얻습니다.
* 그리고 다음으로 Cursor.getString(Int)를 호출해 데이터를 가져옵니다. 문자열인 경우는 getString(), 숫자인 경우 getInt() 등 자료형에 따른 메서드가 준비돼 있습니다.
* 아울러 인수에는 가져오고 싶은 칼럼의 인덱스를 전달합니다.
* Cursor의 사용이 끝나면 close()를 호출합니다. Cursor는 ContentProvider로부터 가져온 데이터에 대한 참조를 가지고 있으므로 닫아서 참조하는 데이터를 해제할 필요가 있습니다.

### ContentProvider에서 데이터를 가져오는 흐름
1. ContentProvider 구하기
2. Cursor 구하기
3. Cursor에서 데이터 가져오기
4. Cursor 해제

## ContentProvider를 만들자
### 마법사로 ContentProvider 생성
* 안드로이드 스튜디오에는 ContentProvider의 템플릿이 준비돼 있으므로 템플릿을 바탕으로 생성하는 것이 간단합니다.
* 클래스명을 WordOfTodayProvider, authorities를 com.advanced_android.wordoftoday2로 지정합니다. authhorities가 복수형이듯 여러 개 관리할 수 있지만 보통은 1개의 Provider에 1개 생성합니다.
* Authority 이름은 패키지명처럼 해두면 일관성을 확보할 수 있고 이해하기도 쉽습니다. 마법사로 생성하면 AndroidManifest.xml 파일에 선언이 추가되고 코드 템플릿이 준비됩니다.

~~~xml
<provider
    android:name=".WordsOfTodayProvider"
    android:authorities="com.advanced_android.wordoftoday2"
    android:enabled="true"
    android:exported="true">
</provider>
~~~
* android:exported="true"로 지정해서 다른 앱에 대해서도 공개합니다.
### 구현해야 하는 추상 메서드 목록
* ContentProvider를 작성할 때 구현해야 하는 추상(abstract)메서드는 6개입니다.

|메서드|용도|
|-----|--|
|onCreate()|초기화 처리|
|getType()|인수로 전달된 URI에 대응하는 MIME 타입을 반환|
|insert()|레코드 추가|
|query()|레코드 검색, 취득|
|update()|레코드 갱신|
|delete()|레코드 삭제|

* 마법사로 작성된 코드는 이처럼 6개의 추상메서드를 구현해야 합니다. 미리 준비된 것은 UnsupportedOperationException을 던지는 것뿐이니 개발자가 직접 내용을 구현해야합니다.

~~~kotlin
class ToDoContentProvider: ContentProvider {
    fun ToDoContentProvider() {
    }

    override fun delete(uri: Uri, selection: String, selectionArgs: String[]): Int {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun getType(uri: Uri): String{
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValue): Uri{
        throw UnsupportedOperationException("Not yet implemented")
    }

    override onCreate():Boolean{
        return False
    }

    override query(uri: Uri, values: ContentValues, selection: String, selectionArgs: String[]): Cursor{
        throw UnsupportedOperationException("Not yet implemented")
    }
}
~~~

### WordsOfToday의 데이터 구조
* 다음은 '오늘의 한마디' 앱의 데이터 구조입니다. 데이터를 저장할 때는 SQLite라는 데이터베이스를 이용합니다.

|칼럼명|자료형|내용|
|----|-----|---|
|_id|Integer(Long)|고유ID|
|name|Text|작성자|
|words|Text|작성 내용|
|date|Text|작성일|
* 이 구조로 스키마를 정의합니다. 이번에는 오늘의 한마디를 저장할 테이블을 만듭니다.
~~~
CREATE TABLE WordsOfToday(
    _id INTEGER PRIMRARY KEY AUTOINCREMENT, 
    name TEXT,
    word  TEXT,
    data TEXT)
~~~
~~~kotlin
class WordsOfToday: Parcelable {
    var _id: Long
    var name: String
    var words: String
    var date: Int
}
~~~
* 또한 초깃값으로서 동작을 확인하기 위해 미리 7개의 데이터를 삽입해 둡니다.
~~~kotlin
import static com.advanced_android.wordoftoday2.WordsOfTodayContract.TABLE_NAME
import static com.advanced_android.wordoftoday2.WordsOfTodayContract.DATE
import static com.advanced_android.wordoftoday2.WordsOfTodayContract.NAME
import static com.advanced_android.wordoftoday2.WordsOfTodayContract.WORDS
~ 생략 ~
val SQL_INSERT_INITIAL_DATA = {
    String.format("INSERT INTO %s (%s, %s, %s)" +  "VALUES('Taiki', '날씨 참 좋다', '20151001')", TABLE_NAME, NAME, WORDS, DATE),
    String.format("INSERT INTO %s (%s, %s, %s)"+"VALUES('Taiki', '아침 4시 30분에 일어났다', '20151004')",TABLE_NAME, NAME, WORDS, DATE),
}
~~~

### WordsOfToday의 공개용 정보를 정의
* 다른 앱이 필요로 하는 것은 공개용 상수가 정의된 WordsOfTodayContract라는 인터페이스로 공개합니다. '연락처' 앱에서 이용하는 ContactContract 등 기본 앱도 Contract라는 이름을 이용하므로 그에 따라 이름을 붙였습니다.

1. ContentProvider에 접근하기 위한 URI(CONTENT_URI로 정의)
2. ContentProvider에서 제공할 데이터 구조(WordsOfTodayColumns로 정의)
3. ContentProvider에서 제공할 데이터의 MIME 타입

~~~kotlin
interface WordsOfTodayContract {
    val AUTHORITY: String = "com.advanced_android.wordoftoday2"
    val TABLE_NAME: String = "WordsOfToday"
    val CONTENT_URI: Uri = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + TABLE_NAME)
    val MIME_TYPE_DIR: String = "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_NAME
    val MIME_TYPE_ITEM: String = "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_NAME

    interface WordsOfTodayColumns: BaseColumns {
        val NAME: String = "name"
        val WORDS: String = "words"
        val DATE: String = "date"
    }
}
~~~
* SQLiteOpenHelper.getWritableDatabase()를 호출해 가져올 수 있는 SQLiteDatabase 인스턴스에 query()나 insert() 메서드가 있습니다.
~~~kotlin
companion object {
    val sUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    sUriMatcher.addURI(AUTHORITY, TABLE_NAME, ROW_DIR)
    sUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", ROW_ITEM)
}
~~~
* query() 메서드의 첫 번째 인수로 전달되는 uri 인스턴스를 UriMatcher로 비교합니다. 비교 결과로 ID를 지정한 1건의 매칭(ROW_ITEM)과 그 밖의 여러 건의 매칭(ROW_DIR)으로 나눕니다.
~~~kotlin
override fun query: Cursor(uri: Uri, projection: Array<String>, selection: String, selectionArgs: Array<String>, sortOrder: String){
    var cursor: Cursor? = null
    whensUriMatcher.match(uri)) {
        ROW_DIR ->
            Log.d(TAG, "query(dir) uri=" + uri.toString)
            synchronized(mDbHelper){
                var id: Long = ContentUris.parseId(uri)
                var db: SQLiteDatabase = mDbHelper.getWritableDatabase()
                cursor = db.query(TABLE_NAME, projection, _ID, Arrayof(Long.toString(id)), null, null, null)
            }
        else ->
        throw IllegalArgumentException("인수의 URI가 틀렸습니다.")
    }
    return cursor
}
~~~

### insert() 메서드 구현
* query() 메서드와 마찬가지로 SQLiteDatabase를 가져와 SQLiteDatabase.insert()를 호출합니다. 또한 미리 UriMatcher로 나눕니다.
* 두 번째 인수인 ContentValues는 키-값 형식의 데이터 구조입니다. 삽입 후, 데이터에 발생한 변경을 알리기 위해 ContentResolver.notifyChange()를 호출합니다.
~~~kotlin
override fun insert(uri: Uri, values: ContentValues): Uri{
    resultUri = null;
    when(sUriMatcher.match(uri)) {
        ROW_DIR  -> 
        synchronized(mDbHelper) {
            var db: SQLiteDatabase = mDbHelper.getWritableabase()
            var lastId: Long = db.insert(TABLE_NAME, null, values)
            resultUri = ContentUris.withAppendedId(uri, lastId)
            Log.d(TAG, "WordsOfTodayProvider insert " + values)
            getContext().getContentResolver().notifyChange(resultUri, null)
        }
        else -> 
            throw IllegalArgumentException("인수의 URI가 틀렸습니다.")
    }
    return resultUri
}
~~~
* ContentValues에 관해 보충하겠습니다. ContentValues는 ContentProvider에 데이터를 추가하거나 갱신 등을 할 때 다루는 데이터 구조입니다.
* 키-값 쌍으로 돼 있고 내부적으로 HashMap<String, Object>를 래핑한 것입니다. 덧붙여, ContentProvider는 프로세스 간 통신에서 데이터를 주고 받을 수 있으므로 ContentValues는 Parcelable을 구현하고 있어 값으로 설정할 수 있는 것도 Parcelable일 필요가 있습니다.
* 다음처럼 인수로 넘어온 ContentValues로 데이터를 삽입합니다. 아울러 위 코드에는 포함되지 않았지만 ContentValues로부터 값을 가져올 때는 getAsInteger(), getAsString() 등 값의 자료형에 맞게 메서드를 호출합니다.
* 이러한 메서드는 호출한 메서드와 실제로 가져오는 값의 자료형이 맞지 않는 경우(내부적으로 ClassCastException이 발생) 단순히 null을 반환합니다.
### getType() 메서드 구현
* WordsOfToday라는 데이터 구조의 한 종류이므로 Uri와 일치하면 WordsOfToday Contract에서 정의한 MIME 타입을 반환합니다. 
* ROW_DIR인 경우는 MIME_TYPE_DIR(vnd.android.cursor.dir/)을 반환하고, ROW_ITEM인 경우 MIME_TYPE_ITEM(vnd.android.cursor.item/)을 반환합니다.
~~~kotlin
override fun getType(uri: Uri): String{
    when(sUriMatcher.match(uri)){
        ROW_DIR ->
            return MIME_TYPE_DIR
        ROW_ITEM ->
            return MIME_TYPE_ITEM
        else -> 
            return null
    }
}
~~~
* update()와 delete()도 구현이 가능합니다.(여기서 설명하지는 않습니다.)

### ContentProvider의 데이터 변화를 알려주는 ContentObserver
* ContentProvider의 데이터가 변경됐을 때 어떻게 그 변경을 탐지하면 좋을까요? 
* 정기적으로 폴링해서 매번 데이터가 변경됐는지 확인하는 비효율적인 방법도 있지만 그보다는 ContentProvider 프레임워크가 제공하는 메커니즘을 이용하면 편리합니다.
* 코드를 보면 notifyChange(..)를 호출하는 곳이 있습니다. 이곳이 ContentObserver에 변경을 통지하는 부분입니다. ContentProvider를 직접 만들 때는 변경을 적절하게 통지하는 구현도 함께 합니다.

~~~kotlin
getContext().getContentResolver().notifyChange(uri, null)
~~~
* ContentObserver는 추상 클래스라서 반드시 onChange()라는 추상 메서드를 구현해야 합니다. 생성자에서 넘겨주는 Handler가 실제로 onChange()를 호출합니다.
* 앱의 사양에 따라 어떻게 대응할지 달라지지만 변경된 데이터에 맞게 사용자에게 적절한 알림을 표시하거나 표시하는 UI를 갱신하고 또한 데이터를 다시 가져오는 등의 구현이 필요해집니다.
* Loader에 관한 설명을 생략했지만 CursorLoader를 사용하는 경우는 내부적으로 ContentObserver를 이용하므로 데이터를 가져오는 ContentProvider의 데이터가 갱신되면 다시 데이터를 가져오게 돼 있습니다.
~~~kotlin
mObserver = ContentObserver(Handler()){
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        // 데이터 변경이 있었음
    }
}
~~~
* ContentObserver의 등록 및 해제는 액티비티의 수명주기에 맞게 onStart()에서 등록하고 onStop()에서 해제합니다.
~~~kotlin
fun onStart() {
    super.onStart()
    // 두 번째 인수가 false인 경우 첫 번째 인수의 URI와 일치할 때만
    // ContentObserver.onChange()를 호출
    // true인 경우는 URI와 부분 일치
    mContentResolver.registerContentObserver(WordsOfTodayProvider.CONTENT_URI, true, mObserver)
}

fun onStop() {
    super.onStop()
    mContentResolver.unregisterContentObserver(mObserver)
}
~~~
### adb로 간단히 ContentProvider에 접속
* ContentProvider를 간단히 확인하고 싶은 경우에는 터미널에서 adb shell의 content 커맨드를 사용합니다. 조금 전에 만든 '오늘의 한마디'의 ContentProvider에 접근합니다. 다음은 커맨드를 실행하는 예입니다.
~~~
// 전체 가져오기
adb shell content query --uri content:://com.advanced_android.wordoftoday2/wordoftoday
--projection _id:words:name
~~~
~~~
// 하나만 가져오기(id지정)
adb shell content query --uri content:://com.android_android.wordoftoday2/wordoftoday/0
--projection _id:words:name
~~~
~~~
// 목록 가져오기(필터링)
adb shell content query --uri content:://com.advanced.android.wordoftoday2/wordoftoday/0
--projection _id:words:name
~~~
~~~
// 삽입
adb shell content insert --uri content:://com.advanced_android.wordoftoday2/wordoftoday/
--bind name:s:Shunsuke --bind date:i:20181113 --bind words:s:'오늘은 멋진날'
~~~
* 또한 데이터베이스를 확인할 때는 adb content 커맨드를 사용해도 좋지만 페이스북에서 개발한 Stetho를 이용하면 더욱 간단히 확인할 수 있습니다.
~~~kotlin
// Stetho 활성화
if(BuildConfig.DEBUG) {
    var context: Context = getApplicationContext()
    Stetho.initializeWithDefaults(this)
}
~~~ 
~~~gradle
// Stetho 도입
dependencies{
    ~ 생략 ~
    debugCompile 'com.facebook.stetho:stetho:1.4.1'
}
~~~
* Stetho를 도입했습니다. Stetho로 데이터베이스를 살펴봅니다. Stetho에서는 SQL을 실행할 수 있으므로 앱 내에서 구현하기 전에 우선 여기서 시험해보고 구현하면 더 효율적으로 개발할 수 있습니다. 
* 앱을 표시한 상태에서 단말기와 컴퓨터를 USB 케이블로 연결하고 크롬 브라우저 주소창에서 chrome://inspect/#device를 열어줍니다.
* 그리고 [inspect]를 누르면 'Developer Tools'가 시작됩니다. 시작된 후에는 앱 내의 데이터베이스에 접근할 수 있습니다.