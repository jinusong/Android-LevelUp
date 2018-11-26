# ViewModel:Moving away from Android
*  ViewModel 을 안드로이드 프레임웍으로 부터 독립적으로 구성하는 과정에서 나오는 문제들을 알아보려고 합니다.
* Android 는 일부 케이스에서 직접적으로 View 나 Context 에 접근해야만 할 수 있는 동작들이 있습니다. 
* 이런 코드들은 지속적으로 ViewModel 이 Activity/Fragment 에 종속시키도록 합니다.
* 이를 해결해봅시다.
## 리소스 접근하기
* 화면을 갱신하는 과정에서 이미지나 문자열 리소스에 접근해야 하는 경우가 많습니다.
* 다음 코드는 문자열에 접근하기 위한 방법입니다.
~~~kotlin
class MainViewModel(private val resourcesProvider: ResourcesProvider) {
  
  val name = ObservableField()
  
  
  fun click() {
    name.set(resourceProvider.getString(R.string.name_title))
  }
}
~~~
~~~kotlin
interface ResourcesProvider {
  fun getString(resId:String) : CharSequence
}

class ResourcesProviderImpl(private val context:Context) : ResourcesProvider {
  fun getString(resId) = context.getString(resId)
}
~~~
* ViewModel 은 리소스에 접근하는 인터페이스를 컴포넌트로 하고 구현 클래스의 객체를 통해서 실제로 접근하도록 합니다.
* 테스트를 작성할 때는 Mocking 을 적절히 활용하면 의도된 테스트를 충분히 하실 수 있습니다.
~~~kotlin

class MainViewModelTest {
  private lateinit var vm : MainViewModel
  private lateinit var resourcesProvider : ResourcesProvider
  
  @Setup
  fun init() {
    resourcesProvider = mock(ResourcesProvider::class)
    vm = MainViewModelImpl(resourcesProvider)
  }
  
  @Test
  fun click() {
    when(resourcesProvider.getString(R.string.name_title)).thenReturn("name")
    vm.click()
    assertThat(vm.name.get()).equals("name")
  }
}
~~~
## Drawable Resource 처리하기
* 이미지뷰에 이미지를 넣는 과정에서 최대한 Drawable 이나 Bitmap 을 노출하지 않기 위해 Databinding을 활용합니다.
~~~xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    >

    <data>

        <variable
            name="vm"
            type="MainViewModel"
            />

    </data>
<TextView
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:drawableLeft="@{vm.textLeftImage}"
    />
</layout>
~~~
~~~kotlin
class MainViewModel {
  val textLeftImage = ObservableInt(R.drawable.search)
}
~~~
~~~kotlin
@BindingAdapter("android:drawableLeft")
fun bindingVectorDrawableLeft(textView: TextView, resourceId: Int) {
    val drawable = AppCompatResources.getDrawable(textView.context, resourceId)
    val drawables = textView.compoundDrawables
    textView.setCompoundDrawablesWithIntrinsicBounds(drawable,
            drawables[1], drawables[2], drawables[3])
}
~~~
* Vector Resource 를 처리할 때도 유용합니다.
* URI 형 이미지 로드는 대개 Glide 나 Picasso 와 같은 이미지 로더를 함께 씁니다. 
* 이럴 경우 이미지 로더와 URI 를 동시에 바인딩이 되도록 하기 위해 BindingAdapter 의 requireAll 을 이용해서 처리하도록 합니다.
~~~kotlin
@BindingAdapter({"bindUri", "bindImageLoader"}, requireAll = true)
fun loadImage(view: ImageView, uri : String, imageLoader : ImageLoader) {
  imageLoader.load(uri).into(view)
}
~~~
~~~xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    >
    <data>
        <variable name="vm" type="MainViewModel" />
    </data>
<ImageView
    android:layout_width="50dp"
    android:layout_height="50dp"
    app:bindImageLoader="@{vm.imageLoader}"
    app:bindUri="@{vm.imageUri}"
    />
</layout>
~~~
* 위와 같이 선언하게 되면 bindUri 와 bindImageLoader가 동시에 정의되어야 호출됩니다.
## View 상태 옵저빙 하기
* View 의 사이즈변경 과 같은 레이아웃 이벤트들을 모니터해야 하는 시점에 이르면 ViewModel의 코드와 Activity/Fragment코드 사이에서 굉장한 갈등이 시작됩니다. 
* View 의 정보를 알기 위해서는 Activity/Fragment로 부터 콜백을 받아야 하기 때문에 ViewModel 이 View 에 간섭하는 코드가 추가될 가능성이 큽니다.
* 이 경우에는 ViewUsecase 를 만들어 해당 뷰를 관찰하도록 합니다.
~~~kotlin

class MainViewModel(private val layoutCenterViewUsecase : LayoutCenterViewUsecase) {
  
  val centerY = ObservableInt()
  
  init {
    layoutCenterViewUsecase.observe()
      .subscribe { y : Int ->
        centerY.set(y)
      }
  }
  
}
~~~
~~~kotlin
class LayoutCenterViewUsecase(layouts : () -> View) {
  private val layout by lazy(layouts)
  
  fun observe() : Observable<Int> {
    return Observable.create { emitter -> 
        emitter.onNext(layout.bottom - layout.top)
                    
        val callback = object : View.OnLayoutChangeListener {
           override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
               emitter.onNext(bottom - top)
           }
        }
        layout.addOnLayoutChangeListener(callback)
                              
        emitter.setCancellable {
          layout.removeLayoutChangeListener(callback)
        }
     }.map { it / 2}
  }
}
~~~
~~~kotlin
class MainActivity : AppCompatActivity() {
  fun onCreated(saved : Bundle?) {
    val vm = MainViewModel(LayoutCenterViewUsecase { findViewById<ViewGroup>(R.id.layout) } )
  }
}
~~~
* MainViewModel 을 생성하는 과정에서 View에 관여하는 LayoutCenterViewUsecase 를 전달합니다. 
* 이제 MainViewModel 은 LayoutCenterViewUsecase 를 통해서 View 의 정보를 간접적으로 접근할 수 있습니다.
## 액티비티 실행하기
* 액티비티를 실행하는 것 또한 앞선 Usecase 예시를 적용하도록 합니다.
~~~kotlin
class SecondAppStarterUsecase(private val context: Context) {
  fun showSecond(index : Int) {
    context.startActivity(Intent().apply { putExtra("INDEX",index) })
  }
}
~~~
* 위의 Usecase 를 ViewModel 을 생성 할 때 전달하고 필요할 때 showSecond() 를 호출하도록 합니다.
## 정리
* 위의 모든 예제들은 ViewModel 이 직접적으로 Context 나 View 에 접근하는 것을 피하기 위함입니다. 
* 그렇게 함으로써 ViewModel 을 안드로이드로부터 독립적으로 유지되도록 합니다.