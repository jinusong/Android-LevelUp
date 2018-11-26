# MVVM:Lifecycle
* Lifecycle 감지에 대해 알아보도록 하겠습니다.
* 예를 들면, 채팅화면에서 메세지를 쓰다가 전송하기 전에 화면을 닫은 경우 메세지를 특정 영역에 저장하였다가 다시 채팅화면으로 돌아온 경우 마지막 쓰다만 메세지를 보여줘야하는 경우가 필요합니다. 
* 이럴 경우 onDestroy 콜백을 받아서 처리해야 합니다.
위의 콜백을 받도록 하기 위해 
* Lifecycle 진입시 콜백을 호출하는 인터페이스를 만들어 ViewModel 이 Register, Observing 를 하도록 하는 방법 구현하도록 할 것입니다.
* 위의 콜백을 받도록 하기 위해 Lifecycle 진입시 콜백을 호출하는 인터페이스를 만들어 ViewModel 이 Register, Observing 를 하도록 하는 방법 구현하도록 할 것입니다.
## Lifecycle 진입시 콜백 호출하도록 하기
* Activity/Fragment 에서 ViewModel의 Lifecycle 과 관련된 함수를 직접 호출하는 방식이 있습니다.
~~~kotlin
class BaseLifecycleActivity : Activity {
  private val lifecycleOwner : LifecycleOwner = LifecycleOwner()
  
  fun onCreate() {
    super.onCreate()
    notifyEvent(LifecycleEvent.ON_CREATED)
  }
  
  fun onStart() {
    super.onStart()
    notifyEvent(LifecycleEvent.ON_STARTED)    
  }
  
  fun onResume() {
    super.onResume()
    notifyEvent(LifecycleEvent.ON_RESUMED)    
  }
  
  fun onPause() {
    notifyEvent(LifecycleEvent.ON_PAUSED)    
    super.onPause()
  }
  
  fun onStop() {
    notifyEvent(LifecycleEvent.ON_STOPPED)    
    super.onStop()
  }
  
  fun onDestroy() {
    notifyEvent(LifecycleEvent.ON_DESTROYED)    
    super.onDestroy()
  }
  
  fun register(callback : LifecycleCallback) {
    lifecycleOwner.register(callback)
  }
  
  fun unregister(callback : LifecycleCallback) {
    lifecycleOwner.unregister(callback)
  }
  
  private notifyEvent(event : LifecycleEvent) {
    lifecycleOwner.notifyEvent(event)
  }
}

class LifecycleOwner {
  private val callbacks = mutualListOf<LifecycleCallback>()
  private var lastEvent : LifecycleEvent = ON_CREATED
  fun register(callback : LifecycleCallback) {
    callback.apply(lastEvent)
    callbacks.add(callback)
  }
  
  fun unregister(callback : LifecycleCallback) {
    callbacks.remove(callback)    
  }
  
  fun notifyEvent(event : LifecycleEvent) {
    callbacks.forEach { it.apply(event) }
  }
}

interface LifecycleCallback {
  fun apply(event: LifecycleEvent)
}
~~~
~~~kotlin
class MainActivity : Activity {
  private lateinit var viewModel: MainViewModel
  
  fun onCreate() {
    super.onCreate()
    viewModel = MainViewModel()
    register(viewModel)
  }
}
~~~
~~~kotlin
class MainViewModel {
  
  private val repository by lazy { TemporaryMsgRepository() }
  private val msg = ObservableField<String>()
 
  override apply(event: LifecycleEvent) {
    switch (event) {
      ON_CREATED -> onInitialize()
      ON_DESTROYED -> onDeinitialize()
    }
  }

  
  private fun onInitialize() {
    val temporaryMsg = repository.getLastMessage()
    msg.set(temporaryMsg)
  }
   
  private fun onDeinitialize() {
    val temporaryMsg = msg.get()
    repository.saveLastMessage(temporaryMsg)
  }
}
~~~
* 위처럼 LifecycleOwner/Register/Notifier 를 정의하고 구현하는 방식입니다. 
* 이 방식은 AAC 의 LifecycleObserver 와 Provider 를 간단한 방식으로 구현한 것입니다.
* 위의 방식은 Event 를 받아 처리하는 코드가 많아질 때 가독성이 좋지 않기 때문에 RxJava 를 이용해서 바꿔보도록 하겠습니다.
## RxJava를 이용해서 Lifecycle 콜백 처리하기
* RxJava 로 구현한 경우 기초 코드는 위의 방법보다 다소 많습니다.
~~~kotlin
interface RxBinder {
  fun bindUntil(event: LifecycleEvent)
  fun apply(event: LifecycleEvent)
}

class RxBinderImpl {
  private val subject = BehaviorSubject.<LifecycleEvent>create()
  private val disposables = mapOf<LifecycleEvent, MutableList<() -> Disposable>>()
  
  override fun bindUntil(event: LifecycleEvent, disposable: () -> Disposable) {
    if (subject.value >= event) {
      (disposables[event] ?: run { disposables[event] = mutableList(); disposables[event]; })
        .let { it += disposable }
    } else {
      disposable.dispose()
    }
  }
  override fun apply(event: LifecycleEvent) {
    disposables[event]?.foreach { it.dispose() }
  }
  
}
~~~
~~~kotlin
class RxActivity {
  val rxBinder : RxBinder = RxBinderImpl()
  fun onCreate() {
    rxBinder.apply(LifecycleEvent.ON_CREATED)
  }
  fun onStart() {
    rxBinder.apply(LifecycleEvent.ON_STARTED)
  }
  fun onResume() {
    rxBinder.apply(LifecycleEvent.ON_RESUMED)
  }
  fun onPause() {
    rxBinder.apply(LifecycleEvent.ON_PAUSED)
  }
  fun onStop() {
    rxBinder.apply(LifecycleEvent.ON_STOPPED)
  }
  fun onDestroy() {
    rxBinder.apply(LifecycleEvent.ON_DESTROYED)
  }
  
}
~~~
~~~kotlin
class MainActivity : RxActivity {
  private lateinit var viewModel : MainViewModel
  
  fun onCreate() {
    viewModel = MainViewModel(rxBinder)
  }
}
~~~
~~~kotlin
class MainViewModel(private val rxBinder: RxBinder) {
  
  private val repository by lazy { TemporaryMsgRepository() }
  private val msg = ObservableField<String>()
  
  init {
    rxBinder.bindUntil(LifecycleEvent.ON_CREATED) {
      Disposables.fromAction { msg.set(respository.getLastMessage()) }
    }
    
    rxBinder.bindUntil(LifecycleEvent.ON_DESTROYED) {
      Disposables.fromAction { respository.saveLastMessage(msg) }
    }
  }
}
~~~
* ViewModel 에서는 이전의 구현 방식보다는 좀 더 자유도가 있는 구현을 할 수가 있습니다.
* 위의 구현은 기존의 AAC 의 LifecycleOwner, RxLifecycle 과 매우 유사한 형태의 구현이 됩니다. 
* 기존의 알려진 구현 방식들이 MVVM 에서 명시적인 Lifecycle 을 분리시킬 수도록 할 수 있고 
* 이는 Activity 와 ViewModel 간 독립성을 얻을 수 있습니다.