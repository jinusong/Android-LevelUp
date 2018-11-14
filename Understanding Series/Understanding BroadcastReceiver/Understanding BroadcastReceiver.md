# Understanding BroadcastReceiver
## BroadcastReceiver의 기본을 이해하자
* BroadcastReceiver는 브로드캐스트 Intent를 받았을 때의 처리를 onReceive에서 구현합니다. 어느 브로드캐스트 Intent를 받을지는 IntentFilter로 정의합니다.
~~~kotlin
abstract fun OnReceive(context: Context, intent: Intent)
~~~
* 두 번째 인수로 전달되는 Intent는 Context.sendBroadcast() 등에서 보내진 브로드캐스트 Intent의 인스턴스입니다. 
* 이제부터 Intent.getAction()을 호출해 액션의 이름을 가져오거나 주어진 데이터가 있을 때는 Intent.getExtras()를 호출해 Bundle을 가져오고 거기서 데이터를 추출합니다. 
* 또한 onReceive()의 처리는 메인 스레드에서 수행되므로 처리에 시간이 걸려서는 안 됩니다. onReceive() 처리에 10초 이상 걸리는 경우 ANR이 발생해 프로세스가 강제로 종료됩니다.
