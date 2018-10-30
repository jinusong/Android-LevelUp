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