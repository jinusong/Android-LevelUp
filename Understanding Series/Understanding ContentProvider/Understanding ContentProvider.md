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
