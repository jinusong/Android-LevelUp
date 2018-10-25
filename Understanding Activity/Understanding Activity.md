# Understanding Activity
## Activity란?
* 액티비티라는 단어의 의미인 '활동'이 나타내는 것처럼 액티비티는 전화를 걸고, 메일을 작성하고, 사진을 찍는 등 사용자가 어떤 활동을 할 때 실행되는 애플리케이션의 컴포넌트를 가리킵니다.
* 액티비티에는 윈도우가 있고, 그 윈도우에 텍스트나 이미지를 표시해 사용자 조작에 반응할 수 있습니다. UI가 없는 액티비티도 있지만 기본적으로 한 액티비티가 한 화면을 표시합니다.
~~~kotlin
(MainActivity)
class MainActivity: AppCompatActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
~~~
* 액티비티를 만들려면 우선 액티비티를 상속한 클래스를 만들어야 합니다.
* 안드로이드 스튜디오의 마법사로 액티비티를 만들면 android.support.v7.app.App.AppCompatActivity를 상속한 위와 같은 클래스가 생성됩니다.
* AppCompatActivity는 액티비티를 상속하며, 액티비티를 상속함으로써 머티리얼 디자인(Material Design)의 가이드라인에 따른 AppCompat 라이브러리를 제대로 활용할 수 있습니다.
* AppCompatActivity를 상속할 수 없을 때는 다음과 같이 android.support.v7.app.AppCompatDelegate를 이용합니다.
~~~kotlin
AppCompatDelegate를 이용하는 경우
class MainActivity: Activity {
    lateinit var mDelegate: AppCompatDelegate
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate = AppCompatDelegate(this)
        mDelegate.setContentView(R.layout.activity_main)
    } 
}
~~~
## Activity Lifecycle을 이해하자
* 액티비티에는 몇 가지 상태가 있고, 상태를 변경할 때 onCreate 등의 콜백이 안드로이드 프레임워크에서 호출됩니다.

![ActivityLifecycle 이미지](https://cdn-images-1.medium.com/max/583/1*-XLXJAKxtSWf0OwGUlnitg.png)
* 다음은 수명주기의 종류와 흐름을 비롯해 상황에 따라 각각 무엇을 구현해야 하는지 살펴보겠습니다.

|메서드 명|시점|처리 예|
|-------|-----|-----|
|onCreate|생성 시|초기화 처리와 뷰 생성(setContentView 호출) 등|
|onStart|비표시 시|통신이나 센서 처리를 시작|
|onRestart|표시 시(재시작만)|보통은 아무것도 하지 않아도 된다.|
|onPause|최전면 표시|필요한 애니메이션 실행 등의 화면 갱신 처리(*)|
|onStop|비표시(정지) 상태|통신이나 센서 처리를 정지|
|onDestroy|폐기 시|필요 없는 리소스를 해제. 액티비티 참조는 모두 정리한다.|

* 시스템 메모리가 모자랄 경우 시스템은 onStop, onDestroy를 콜백하지 않고 액티비티를 강제로 종료시켜 메모리를 확보할 때가 있습니다.
* 이러한 경우 데이터를 영속적으로 보존하려면 액티비티가 일시정지 상태로 전환되는 onPause에서 이를 처리할 필요가 있습니다.
* onCreate와 onDestroy, onStart와 onStop, onResume과 onPause를 쌍으로 해서 준비와 뒷정리, 혹은 시작과 종료하는 조합을 생각하면 어떤 시점에 어떤 작없을 처리할지 쉬워집니다.
* onCreate에서 뷰를 만들면 onDestroy에서 해제합니다. 뷰는 액티비티가 폐기된 다음, 가비지 콜렉션(GC)에 의해 자동으로 메모리에서 해제됩니다.
* onStart에서 위치 정보 취득을 시작했다면 onStop에서(만약 정보 취득을 완료하지 않았다면) 취득을 정지하는 식입니다. onRestart는 거의 사용하지 않으니 호출되는 타이밍만 알아두면 됩니다.
* onDestroy에서 액티비티가 폐기되면 GC가 메모리 영역에서 해제합니다. 단, 액티비티의 인스턴스가 다른 클래스에서 참조되고 있을 때는 폐기된 후에도 메모리에 남아 결국 메모리 누수가 발생합니다.

## 디바이스 설정의 갱신 탐지

* 액티비티는 디바이스 설정에 변경이 발생하면 기본적으로 시스템에서 현재 액티비티를 폐기하고 새로 생성합니다. 예를 들어, 화면을 세로에서 가로로 돌리거나 언어 설정 변경, 단말기 SIM 교체에 따른 전화번호 병경 등 디바이스 설정이 변경된 경우입니다.
* 더 구체적으로 만약 언어 설정이 한국어에서 영어로 바뀐다면 액티비티에 표시되는 문자열도 영어로 변경하고 싶을 것입니다. 안드로이드에서는 액티비티가 시작될 때 단말기 상태에 맞게 리소스를 선택하는 기능이 있습니다.
* 영어 버전용 문자열 리소스 파일을 만들어 두면 언어 설정이 영어로 바뀌었을 때 영어 리소스 파일을 읽어옵니다.
* 또, 액티비티를 재생성할 때는 현재 상태를 일시적으로 저장해서 이용하고 싶은 경우가 있습니다. 액티비티에는 onSaveInstanceState/onRestoreInstanceState라는 콜백 메서드가 있어서 일시적으로 데이터를 저장하고 복귀 시 저장한 데이터를 가져올 수 있습니다.
~~~kotlin

~~~