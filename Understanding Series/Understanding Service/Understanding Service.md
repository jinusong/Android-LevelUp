# Understanding Service
## Service의 종류와 수명주기를 이해하자
### Service의 종류
* Service는 크게 세 종류로 나눌 수 있습니다. 첫 번째는 Context.startService()를 호출해 시작되는 서비스입니다. 두 번째는 Context.bindService()를 호출해서 Service에 바인드하는 종류의 서비스입니다. 세 번째는 AIDL(Android Interface Definition Language; 안드로이드 인터페이스 정의 언어)을 이용하는 서비스입니다.
    * 백그라운드에서 동작하는 Service
    * Binder를 통해 바인드하는 Service
    * AIDL로 앱을 연계할 수 있는 Service
* AIDL로 앱끼리 연계할 때는 각각 Service로 실행되는 스레드가 다르다는 점에 주의할 필요가 있습니다. 이 경우 구현 난도가 매우 높아집니다. 단순히 앱을 연계하고 싶을 때는 startActivityForResult()를 이용하는 액티비티 연계로 대신할 수 없는지 검토해 보는 것이 좋습니다.
### 수명주기와 콜백
* Service에도 수명주기가 있지만 UI가 없어서 단순합니다.
![Service 수명주기](https://t1.daumcdn.net/cfile/tistory/216788345273DAF02E)
* 수명주기에 관한 메서드는 다음과 같습니다.

|메서드명|내용|
|------|---|
|onCreate|Service가 생성된 뒤에 콜백된다.|
|onStartCommand|Service가 시작된 뒤에 콜백된다.|
|onBind|Context.bindService()를 통해 이 Service가 바인드되는 경우 호출된다. 또한 바인드 후, 서비스에 접속할 때는 ServiceConnection.onServiceConnected가 콜백된다.|
|onRebind|이 Service가 언바인드된 다음, 다시 접속했을 때 콜백된다.|
|onUnbind|이 Service가 언바인드될 때 콜백된다.|
|onDestroy|Service가 폐기되기 직전에 콜백된다.|

* Service가 폐기되는 타이밍을 알아보겠습니다. 바인드된 경우는 바인드한 모든 클라이언트로부터 언바인드됐을 때 폐기됩니다. Service가 바인드되지 않은 채 startService로 시작된 경우에는 명시적으로 Service.stopSelf()로 Service 자신이 스스로 종료하거나 다른 컴포넌트에서 Context.stopService()를 호출해 Service를 종료했을 때 폐기됩니다.
## 상주 서비스를 만들자
* 상주하는 서비스의 예로 지금부터 '간이 음악 플레이어'를 만들겠습니다. 이번에 재생할 샘플음원은 구현을 간단히하고자 앱 내에 저장하기로 합니다. 
* 음악 재생은 MediaPlayer 클래스로 합니다. 이번 예제 앱에서는 음악의 재생과 정지라는 두 가지 기능만 구현합니다.
### 음악의 재생과 정지
* MediaPlayer 인스턴스를 만들고 start()를 호출하면 음악이 재생됩니다. MediaPlayer.create()의 2번째 인수로 전달하는 R.raw.bensound_clearday는 음악 파일(mp3)dlqslek.
~~~kotlin
/**
* 음악을 재생한다.
*/
fun play(){
    mPlayer = MediaPlayer.create(this, R.raw.bensound_clearday)
    mPlayer.setLooping(true)
    mPlayer.setVolume(100, 100)
    mPlayer.start()
}
~~~
* 음악을 정지하고 MediaPlayer가 필요없어지면 relerase()를 호출해서 MediaPlayer를 해제합니다.
~~~kotlin
/*
* 정지 버튼이 눌리면 호출된다. MediaPlayer를 정지하고 해제한다.
*/
fun stop() {
    if(mPlayer.isPlaying()){
        mPlayer.stop()
        mPlayer.release()
        mPlayer = null
    }
}
~~~
### 서비스 만들기 
* 액티비티가 최상위가 아니어도 백그라운드에서 재생되는 음악을 듣고 싶습니다. 이번에는 백그라운드  재생기능을 상주 서비스로 구현해 보겠습니다.
* 플레이어는 아래와 같은 사양으로 구현합니다. 백그라운드 서비스와 메인 액티비티의 관계는 다음과 같습니다.
    * 앱이 시작되면 서비스에 바인드하고, 현재 음악이 재생 중인지 문의한다.
    * 음악 정지 상태에서 액티비티가 정지한 경우는 서비스도 정지합니다.
    * 음악 재생 상태에서 액티비티가 정지해도 서비스는 상주한 채로 두고 계속 음악을 재생한다.
* 앱이 시작될 때 서비스에 바인드하고, 현재 음악이 재생 중인지 서비스에 문의하는 처리를 합니다.
~~~kotlin
override fun onResume() {
    Log.d(TAG, "onResume")
    super.onResume()
    if(mServiceBinder ==  null){
        doBindService()
    }
    // 백그라운드에서도 음악 플레이어가 실행되도록
    // startService로 서비스를 시작한다.
    startService(Intent(getApplicationContext(), BackgroundMusicService::class.java))
}
~~~
* 실제로 바인드하면 ServiceConnection으로 구현한 콜백이 호출됩니다. 서비스에 연결하면 onServiceConnected()가 콜백됩니다. 두 번째 인수로 전달되는 IBinder(바인더 인터페이스)로부터 서비스의 인터페이스를 가져오고, 다른 메서드로부터도 참조할 수 있게 필드에 보관합니다.
* 서비스가 정지하거나 unbindService()를 호출해 언바인드하면 서비스에서 벗어납니다. 서비스가 비정상적으로 종료해서 서비스에서 벗어난 경우에는 onServiceDisconnected()가 호출됩니다.
~~~kotlin
// 서비스와의 연결 콜백
var myConnection: ServiceConnection = ServiceConnection(){
    fun onServiceConnected(className: ComponentName, binder: IBinder){
        mServiceBinder = (binder as BackgroundMusicService).getService()
        // [정지] 버튼, [재생] 버튼을 재생 상태에 맞게 활성화한다.
        undateButtonEnabled()
    }

    fun onServiceDisconnected(className: ComponentName){
        Log.d("ServiceConnection", "disconnected")
        mServiceBinder = null
    }
}

fun doBindService(){
    var intent: Intent = null
    intent = Intent(this, BackgroundMusicService::class.java)
    bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
}
~~~
* 음악 정지 상태에서 액티비티가 정지한 경우에는 서비스도 정지합니다. 또한 음악 재생 상태에서는 액티비티가 정지해도 서비스는 상주한 채로 두고 음악은 계속 재생되어 들을 수 있게 합니다.
~~~kotlin
override fun onPause() {
    Log.d(TAG, "onPause")
    super.onPause()
    if(mServiceBinder != null){
        mIsPlaying = mServiceBinder.isPlaying()
        // 정지 중인 경우는
        // 서비스를 계속 실행할 필요가 없으므로 정지한다.
        // 재생 중인 경우는 그대로 실행한 채로 둔다
        if(!mIsPlaying){
            mServiceBinder.stopSelf()
        }
        // 액티비티가 비표시일 때는
        // 이번에는 액티비티에서만 조작하므로
        // 언바인드한다
        unbindService(myConnection)
        mServiceBinder = null
    }
}
~~~
* 상주 서비스는 설정 화면에서 확인할 수 있습니다. 음악 재생 중에 [설정]->[앱]에서 MusicPlayerSample을 선택해 주세요.
## IntentService를 활용하자
* 액티비티와 프래그먼트 수명주기에 의존하지 않고 백그라운드에서 처리하고 싶은 경우 일반적으로 IntentService가 최적의 선택이 됩니다. 여기서는 백그라운드로 벤치마크에서도 자주 이용되는 피보나치 수열을 계산하고, 그 결과를 LocalBroadcastReceiver를 통해 액티비티에 전달합니다.
* IntentService를 이용하는 방법은 간단합니다.
* IntentService를 상속한 클래스를 만들고, AndroidManifest.xml에 등록하기만 하면 됩니다. 안드로이드 스튜디오에는 IntentService 생성용 메뉴가 있으니 그 메뉴를 이용해도  됩니다. 메뉴에서 생성하면 AndroidManifest.xml 등록은 자동으로 이뤄집니다. 메뉴에서 [File]->[New]->[Service]->[Service(IntentService)]를 선택합니다.
* 피보나치 수열을 계산하는 IntentService는 다음과 같습니다.
* IntentService.onHandleIntent(Intent)는 워커 스레드로 실행되므로 이 안에서 계산을 처리합니다.
~~~kotlin
class FibService: IntentService("FibService"){
    // 서비스 액션
    val ACTION_CALC = "ACTION_CALC"
    // 브로드캐스트 액션
    val ACTION_CALC_DONE = "ACTION_CALC_DONE"
    // 브로드캐스트로 계산 결과를 주고받기 위한 키
    val KEY_CALC_RESULT = "KEY_CALC_RESULT"
    // 브로드캐스트로 계산에 걸린 시간(초)을 주고받기 위한 키
    val KEY_CALC_MILLISECONDS = "KEY_CALC_MILLISECONDS"

    // 피보나치 수열 계산
    val N: Int = 40

    override fun onHandleIntent(intent: Intent){
        if(intent != null){
            val action: String = intent.getAction()
            if(ACTION_CALC == action) {
                var start: Long = System.nanoTime()
                var result: Int = fin(N)
                var end: Long = System.nanoTime
                var resultIntent: Intent = Intent(ACTION_CALC_DONE)
                // 결과를 Intent에 부여
                resultIntent.putExtra(KEY_CALC_RESULT, result)
                resultIntent.putExtra(KEY_CALC_MILLISECONDS, (end - start) / 1000 / 1000)
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(resultIntent)
            }
        }
    }

    /** 피보나치 수열 계산 */
    fun fib(n: Int){
        return n <= 1 ? n : fib(n - 1) + fib(n - 2)
    }
}
~~~
* IntentService에서 계산한 결과를 BroadcastReceiver로 받을 수 있게 LocalBroadcastManager에 등록합니다.
~~~kotlin
lateinit var mLocalBroadcastManager: LocalBroadcastManager
override fun onResume(){
    super.onResume()
    // Local BroadcastReceiver를 받도록 등록
    mLocalBroadcastManager.registerReceiver(mReceiver, mIntentFilter)
}

override fun onPause() {
    super.onPause()
    // 등록한 Local BroadcastReceiver 해제
    mLocalBroadcastManager.unregisterReceiver(mReceiver)
}
~~~
* BroadcastReceiver는 인수로 받은 Intent로부터 결과를 가져와 TextView에 설정해 표시합니다.
~~~kotlin
var mReceiver = BroadcastReceiver(){
    override fun onReceiver(context: Context, intent: Intent){
        var action: String = intent.getAction()
        if(FibService.ACTION_CALC_DONE == action){
            var result: Int = intent.getIntExtra(FibService.KEY_CALC_RESULT, -1)
            var msec: Long = intent.getLongExtra(FibService.KEY_CALC_MILLIECONDS, -2)
            // 결과 표사ㅣ
            mTextView.setText("fib(" + FibService.N + ") = " + result + "(" + msec + ") 밀리초")
        }
    }
}
~~~
* 또한 IntentService는 HandlerThread, Looper, Handler를 조합해 실용적으로 구현돼 있고, 또한 코드 양도 적으므로 구현 방식을 읽어둘 가치가 있습니다.