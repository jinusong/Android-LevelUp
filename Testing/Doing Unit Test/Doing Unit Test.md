# Doing Unit Test

* 이 문서는 MVP패턴에서 UnitTest를 하는 법을 다룹니다.

## 테스트 과정
### 테스트 환경
~~~gradle
    // JUnit 4 framework
    testCompile 'junit:junit:4.12'
    // Mockito framework
    testCompile 'org.mockito:mockito-core:1.10.19'
~~~
* JUnit: Unit Testing Framework
* Mockito : Mock Object 구현을 도와주는 라이브러리
* Hamcrest : Test Class에서 사용하는 assert()와 같은 Matcher를 확장한 Library

### Test Code 디렉토리 확인
* Local Unit Test의 디렉토리는 src/test/java 입니다.
* 해당 패키지가 없다면 생성해줍니다.

### TestClass 생성
* "module-name/src/test/java/." 디렉터리에 Test kotlin class를 생성합니다.
* Model-Presenter-View에서 UI test가 아닌 Presenter의 Unit test만 하기 위해서 "PresenterTest.kt"을 생성합니다.

### @Before
* 테스트를 하기 전에 필요한 객체를 초기화하거나 기타 설정들을 위해 이용합니다.
* @Before 메서드는 모든 테스트 전에 실행되기 때문에 만약 4개의 테스트가 있는 경우 4번 실행됩니다.
* 즉, 이전 @Before 메서드의 영향을 받지 않습니다.

~~~kotlin
var mockLoginModel: LoginActivityContract.Model
var mockView: LoginActivityContract.View
var presenter: LoginActivityPresenter

    @Before
    fun setup() {
        mockLoginModel = Mockito.mock(LoginActivityContract.Model::class)
        mockView = Mockito.mock(LoginActivityContract.View::class)
    }
~~~

### @Test
* @Test가 선언되면 이 메서드는 테스트 대상임을 의미합니다. Presenter의 핵심 로직만 테스트하는데 집중하면 됩니다.
![MVP Logic](https://t1.daumcdn.net/thumb/R1280x0/?fname=http://t1.daumcdn.net/brunch/service/user/1OLd/image/_7RhdpzDXVYERmwQBUqZGNZnxgY.png)
* 특성 동작에 대해 Presenter가 view와 model과 상호작용을 잘하는지를 아래와 같이 테스트할 수 있습니다.
~~~kotlin
@Test
fun loadtheUserFromTheRepository_whenValidUserIsPresent() {
    Mockito.when(mockLoginModel.getUser()).thenReturn(user)

    presenter.getCurrentUser()

    // verify model interactions
    Mockito.verify(mockLoginModel, Mockito.times(1)).getUser()

    // verify view interactions
    Mockito.verify(mockView, Mockito.times(1)).setFirstName("jin")
    Mockito.verify(mockView, Mockito.times(1)).setLastName("woo")
    Mockito.verify(mockView, Mockito.never()).showUserNotAvailable()
}
~~~

### Test 실행하는 법
1. 안드로이드 스튜디오의 "Run > Edit Configurations"를 선택합니다.
2. "Run/Debug Configurations" 대화상자에서 JUnit을 추가합니다.
3. 모듈, 이름, 테스트 종류 등을 선택하고 "Apply" 버튼을 선택합니다.
4. 실행합니다.

### Test 결과
* 모든 테스트 성공 시 프로그래스바가 녹색으로 표시되고, 실패 시 빨간색으로 표시됩니다.
* 테스트 결과를 안드로이드 스튜디오를 통해 다른 형태로 추출(Export)할 수도 있습니다.

## 코드 커버리지 (JaCoCo)
* 코드 커버리지는 소프트웨어의 테스트를 논할 때 얼마나 테스가 충분한가를 나타내는 지표 중 하나입니다. 
* 말 그대로 코드가 얼마나 커버되었는가, 소프트웨어 테스트를 진행했을 때 코드 자체가 얼마나 실행되었느냐 입니다.
* Android Studio에서는 코드 커버리지 JaCoCo를 사용할 수 있습니다.

### JaCoCo 설정 및 실행
1. Run > Edit Configuration > Code Coverage 탭을 선택합니다.
2. Code Coverage 탭에서 JaCoCo를 선택합니다.
3. 코드 커버리지를 실행합니다. 마우스 우클릭 > Run 'CalculatorTest' With Coverage 선택
4. 커버리지에 대한 결과를 확인해보면 테스트할 add, sub 메서드를 모두 테스트했으므로 100%의 결과가 나옵니다. add, sub 메서드 중 하나만 테스트하면 이 수치는 달라집니다.
5. testSub() 안에 있는 sub 메서드에 대한 테스트를 주석 처리 후 다시 코드 커버리지를 실행하면,
6. Calculator 부분의 Method 퍼센테이지에 50%라고 출력되는 결과를 볼 수 있습니다. 또한 Calculator.java 파일을 보면 테스트 한 메서드는 초록색, 테스트를 하지 않은 메서드는 빨간색으로 표시됩니다.