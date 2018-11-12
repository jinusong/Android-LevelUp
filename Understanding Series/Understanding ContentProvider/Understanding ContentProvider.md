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
    switch(sUriMatcher.match(uri)) {
        case ROW_DIR:
            Log.d(TAG, "query(dir) uri=" + uri.toString)
    }
}
~~~