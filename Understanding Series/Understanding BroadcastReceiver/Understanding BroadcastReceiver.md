# Understanding BroadcastReceiver
## BroadcastReceiver의 기본을 이해하자
* BroadcastReceiver는 브로드캐스트 Intent를 받았을 때의 처리를 onReceive에서 구현합니다. 어느 브로드캐스트 Intent를 받을지는 IntentFilter로 정의합니다.
~~~kotlin
abstract fun OnReceive(context: Context, intent: Intent)
~~~
* 두 번째 인수로 전달되는 Intent는 Context.sendBroadcast() 등에서 보내진 브로드캐스트 Intent의 인스턴스입니다. 
* 이제부터 Intent.getAction()을 호출해 액션의 이름을 가져오거나 주어진 데이터가 있을 때는 Intent.getExtras()를 호출해 Bundle을 가져오고 거기서 데이터를 추출합니다. 
* 또한 onReceive()의 처리는 메인 스레드에서 수행되므로 처리에 시간이 걸려서는 안 됩니다. onReceive() 처리에 10초 이상 걸리는 경우 ANR이 발생해 프로세스가 강제로 종료됩니다.
### BroadcastReceiver 등록
* BroadcastReceiver를 등록하는 방법은 2가지가 있습니다. 우선 첫 번째는 AndroidManifest.xml에 receiver 태그를 이용해 등록하는 방법입니다.
~~~xml
<!--  Start the Service if application on boot -->
<receiver android:name=".BootReceiver">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED"/>
    </intent-filter>
</receiver>
~~~
* 아울러 BOOT-COMPLETED의 브로드캐스트를 받으려면 AndroidManifest.xml에 RECEIVE_BOOT_COMPLETED 퍼미션을 이용한다고 선언해야 합니다.
~~~xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
~~~
* 두 번째는 Context.registerReceiver를 이용해 실행 시 등록하는 방법입니다. 실행 시에 등록했을 때는 해제도 직접해줄 필요가 있습니다. 액티비티의 onResume에서 등록했다면 onPause에서 unregisterReceiver()를 호출해서 해제합니다.
~~~kotlin
val VOLUME_CHANGED_ACTION: String = "android.media.VOLUME_CHANGED_ACTION" 
var mReceiver = BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent){
        var action: String = intent.getAction()
        intent.getExtras()
        if(TextUtils.equals(action, VOLUME_CHANGED_ACTION)) {
            Toast.makeText(MainActivity.this, "음량이 변화했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume(){
        super.onResume()
        var filterOn = IntentFilter(VOLUME_CHANGED_ACTION)
        registerReceiver(mReceiver, filterOn)
    }

    override fun onPause(){
        super.onPause()
        unregisterReceiver(mReceiver)
    }
}
~~~
## LocalBroadcastReceiver를 이해하자
* 브로드캐스트는 다른 앱에 송신하는 것이 가능하지만 경우에 따라서는 다른 앱에 알릴 필요없이 앱 내에서 완결시키고 싶을 때도 있습니다. 그럴 때는 로컬 브로드캐스트로 다른 앱에 알리지 않고 끝낼 수 있습니다. 로컬 브로드캐스트를 수신하려면 LocalBroadcastReceiver를 이용합니다.
* LocalBroadcastReceiver의 장점으로는 다른 앱에 통지하지 않아 보안이 향상되고, 프로세스 간 통신을 하지 않아 성능이 향상되는 것을 들 수 있습니다.
* 로컬 브로드캐스트는 Service와 조합해 이용하는 경우가 많습니다. Service쪽에서 처리를 하고, 처리가 끝나면 액티비티나 프래그먼트에 로컬 브로드캐스트해 처리 완료를 알리는 식으로 사용합니다.
## 브로드캐스트를 수신해 처리할 때 주의할 점을 알아보자
* 안드로이드는 전력 소비를 줄이고자 사용자가 화면을 끄면 슬립 상태로 들어갑니다. 브로드캐스트를 수신해서 뭔가 시간이 오래 걸리는 처리를 한창하는 중에 슬립되는 경우가 있습니다.
### 단말기의 상태
* 단말기는 절전을 위해 항상 화면을 켜고 있지 않습니다. 또한 화면이 꺼졌을 때도 CPU가 동작하는 상태와 동작하지 않는 상태가 있습니다.
* 일반적으로는 화면이 꺼지고 CPU가 동작하지 않는 상태를 슬립 상태라고 합니다. 이런 슬립 상태에서도 브로드캐스트를 받을 수 있지만 받은 후에 곧 바로 슬립하므로 시간이 걸리는 처리는 취소되어 계속할 수 있습니다.
* 그러므로 처리를 계속하려면 CPU를 깨울 필요가 있습니다. 이런 동작을 안드로이드 시스템 세계에서는 WakeLock을 얻는다고 합니다. Wake는 '깨운다', Lock은 '열쇠로 잠근다'는 의미인데, 잠들 수 없는 방에 CPU를 넣고 열쇠로 잠가두는 것을 상상하면 기억하기 쉽습니다.
* 빈대로 처리가 끝나면 WakeLock을 해제합니다. 열쇠로 잠가둔 것을 열어준다는 의미입니다. 이제 CPU는 잠을 잘 수 없습니다. WakeLock의 종류와 닽말기 상태 변화를 표로 정리했습니다.

|상태|대응하는 WakeLock|
|---|---------------|
|화면 ON(밝다), CPU ON|FULL_WAKE_LOCK|
|화면 ON(약간 어둡다), CPU ON|SCREEN_DIM_WAKE_LOCK|
|화면 OFF, CPU ON|PARTIAL_WAKE_LOCK|
|화면 OFF, CPU OFF|없음|

* 아울러 FULL_WAKE_LOCK, SCREEN_DIM_WAKE_LOCK은 현재는 폐기 예정(Deprecated)상태라서 WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON을 대신 이용하길 권장합니다.
* 한가지 주의할 점은 WakeLock 해제를 잊으면 CPU가 슬립할 수 없어 계속 동작하고 전력을 낭비하게 된다는 것입니다. 이런 실수는 앱의 신뢰성과도 연관되니 충분히 주의해야만 합니다.
* 다만 다행히 지원 라이브러리에 WakefulBroadcastReceiver라는 클래스가 있어서 이 클래스를 이용하면 처리 중에만 WakeLock을 얻고, 처리가 끝나면 해제하는 일련의 흐름을 실행해 줍니다.
* 덧붙여, WakefulBroadcastReceiver로 얻은 WakeLock은 60초로 타임아웃이 설정돼 있습니다. 따라서 처리 시간이 60초 이상 걸리면 슬립 상태로 전환됩니다. 이 점에도 주의가 필요합니다.
~~~xml
<uses-permission android:name="android.permission.WAKE_LOCK"/>
~~~
* WakefulBroadcastReceiver.onReceive()에서 startWakefulService()를 호출해 WakeLock을 얻은 상태에서 IntentService를 시작하고 그 안에서 처리합니다.
~~~kotlin
class MyReceiver: WakefulBroadcastReceiver{
    init{
    }

    override fun onReceive(context: Context, intent: Intent){
        var serviceIntent = Intent(context, MyIntentService::class.java)
        startWakefulService(context, serviceIntent)
    }
}
~~~
* IntentService 쪽에서 처리가 끝나면 WakefulBroadcastReceiver.completeWake fulIntent()를 호출해 WakeLock을 해제합니다. 덧붙여, IntentService는 처리를 백그라운드 스레드에서 실행해주는 Service로서 처리가 끝나면 자기 자신을 완료하는 편리한 클래스입니다.
~~~kotlin
override fun onHandleIntent(intent: Intent){
    try {
        // 이곳에 실행하고 싶은 처리 내용을 기술한다.
        ~ 생략 ~
    } finally {
        WakefulBroadcastReceiver.completeWakefulIntent(intent)
    }
}
~~~
* WakeLock의 상태는 adb를 통해 확인할 수 있습니다. 실제로 WakeLock을 얻었는지 또는 제대로 해제했는지 조사할 수 있습니다.
~~~
adb shell dumpsys power
~ 생략 ~
Wake Locks: size=1
PARTIAL_WAKE_LOCK 'wake:com.advanced_android.wakefulbroadcastreceiversample/.MyIntentService'
~ 생략 ~
~~~
* WakeLock이 해제됐는지 adb shell dumpsys power 명령의 출력 결과에 포함되는 PARTIAL_WAKE_LOCK에 WakeLock을 얻은 패키지명이 없는 것으로 확인할 수 있습니다.
~~~
adb shell dumpsys power
~ 생략 ~
Wake Locks: size=0
~ 생략~
~~~
* adb logcat으로 로그를 출력하고 있으면 CPU가 슬립할 수 없으므로 슬립 상태가 동작하는지 확인하려면 일단 USB 연결을 해제하고 단말기가 슬립 상태로 전환되기를 잠시 기다려야합니다. 그동안은 adb logcat으로 로그를 확인할 수 없습니다. 나중에 어떻게 됐는지 확인하기 위해 로그를 준비해 두면 좋겠죠?