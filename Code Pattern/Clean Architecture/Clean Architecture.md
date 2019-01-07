# Clean Architecture
![Clean Architecture](https://github.com/bufferapp/clean-architecture-components-boilerplate/raw/master/art/architecture.png?raw=true)

## Clean Architecture을 왜 쓰는가?
* 변화에 잘 대응할 수 있는 코드를 작성하기 위해서 입니다.
* 즉, 변화가 일어나는 곳에서만 코드의 변화가 일어나서 변화에 따른 코드의 변경이 적다는 뜻입니다.
* 그리고 이를 위해서는 코드를 잘 분리시켜야 하고 코드가 본질에 맞게 설계돼 있어야 합니다.

## 본질
* 도메인의 본질: 획자나 실제 사용자가 받아들이는 형태 그 자체를 코드로 작성할 수 있어야 합니다.

* 개발 구조 상의 본질: 데이터라는 하나의 레벨로 추상화를 했는데, 실제로는 내부에 SharedPreferences, Realm, SQLite를 모두 포함하고 있다면, 구조 상으로 변화가 일어날 수 있는 지점에 대해 생각하지 않고 분리하지 않아 문제가 발생할 수 있습니다. 다른 코드에 영향을 미치게 되는 경우도 다음과 같습니다.

* 안드로이드 구조 상의 본질: 만약 화면에 보여주는 뷰의 생명주기를 액티비티에서 관리하므로 이런 부분에 맞게 코드 구조가 설계돼 있어야 합니다.

## Android Clean Architecture
![android Clean Architecture](https://cdn-images-1.medium.com/max/1600/1*dtL10Oo6gUKZU6GEt85Few.png)
*  양파 모양의 레이어 가장 바깥 쪽이 사용자와의 접접에 있는 Presentation 이고 가장 안쪽의 Entities가 사용자가 실제로 생각하는 개념 단위입니다.
* UI를 독립시키고 Database를 분리시키고, 외부적인 설정에 독립적인 구조를 적용하면 프레임워크에 의존적이지 않은 코드를 짤 수 있고, 테스트가 가능한 코드를 짤 수 있는 원리가 될 수 있습니다.

## 4 Layers
* Presentation 레이어: 사용자에게 보여지는 로직과 관련된 레이어 
* Data 레이어: 네트워크를 포함한 데이터를 가져오는 레이어
* Domain 레이어: 사용자의 유스케이스로 분리되는 레이어 
* Entity 레이어: 사용자의 개념을 정의하는 레이어

### 규칙
* 이 네 레이어 간의 의존성은 안쪽으로만 발생해야 합니다. 즉, 가장 하단부의 레이어일 수록 가장 의존성이 낮아야 합니다. 
* 가량 프리젠테이션 레이어는 데이터 레이어를 알지만 데이터는 프리젠테이션을 몰라야 하며, 이 덕분에 맨 아래의 엔티티는 순수한 Java 내지는 Kotlin 모듈이 될 수 있습니다.

### 고민 방향
* 레이어의 분리 덕분에 본질을 정의할 때 어떤 데이터베이스에 저장될지, 어떤 뷰에서 보일지 고민하지 않고 Entity를 작성할 수 있고, 이에 대한 유스 케이스로 Domain 레이어를 작성할 수 있습니다.
* 트랜잭션을 가져오는 것을 Data에서, 어떻게 보여줄 것인지를 Presentation에서 고민하면 됩니다.

## 출처
* https://academy.realm.io/kr/posts/clean-architecture-in-android/