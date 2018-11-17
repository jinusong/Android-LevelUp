# MVP
![MVP](https://thdev.tech/images/posts/2016/10/Android-MVP-Intro/MVP.png)
* Model, View, Presenter 간의 상호 의존성을 떨어트리기 위한 용도임과 동시에 Test Code 작성을 위한 구조 중 하나입니다.
* 기존 안드로이드 코드 작성 시에는 View에 모든 코드가 포함되어 있다거나, 중복 코드를 Util 형태로 사용하는 경우도 많았습니다.
* 그에 비해 MVP는 각각의 독립된 코드의 구현이 가능한 형태입니다.

## MVP란?
* 일단 MVP에 대해서 알기 위해서는 MVP의 각 단어의 역할과 목적이 중요합니다. - 이건 MVC도 마찬가지입니다.
* 그래서 다음과 같이 View, Presenter, Model의 약자를 먼저 정리해보면
    * model : Data와 관련된 처리를 담당
        * Data의 전반적인 부분을 model에서 담당하고, 네트워크, 로컬 데이터 등을 포함
    * View : 사용자의 실질적인 이벤트가 발생하고, 이를 처리 담당자인 Presenter로 전달
        * 완전한 View의 형태를 가지도록 설계합니다. 계산을 하거나, 데이터를 가져오는 등의 행위는 Presenter에서 처리하도록
    * Presenter : View에서 전달받은 이벤트를 처리하고, 이를 다시 View에 전달
        * View와는 무관한 Data등을 가지고, 이를 가공하고, View에 다시 전달하는 역할

## MVP의 기본패턴
![VP 흐름](https://thdev.tech/images/posts/2016/10/Android-MVP-Intro/mvp-default.png)

1. View : View에서 터치 이벤트 발생
2. View -> Presenter : Presenter로 이벤트 전달
3. Presenter : View에서 요청한 이벤트 처리
4. Presenter -> View : 처리한 결과를 View로 전달
5. View : 처리된 결과를 바탕으로 UI를 갱신

## MVP에 모델을 더한다.
![MVP흐름](https://thdev.tech/images/posts/2016/10/Android-MVP-Intro/mvp-model.png)
1. View : View에서 터치 이벤트 발생
2. View -> Presenter : Presenter에 이벤트 전달
3. Presenter : 이벤트의 형태에 따라 캐시 데이터를 가져오거나 Model에 요청
4. Presenter -> Model : Presenter에서 데이터를 요청받음
5. Model : 데이터를 로컬 또는 서버에서 가져온다
6. Model -> Presenter : Model로부터 데이터를 통보받는다
7. Presenter : 전달받은 데이터를 가공
8. Presenter -> View : 가공한 데이터를 View에 전달
9. View -> Presenter로 전달받은 데이터를 View에 갱신
# 먼저 MVC를 알아보자
![MVC](https://thdev.tech/images/posts/2016/10/Android-MVC-Architecture/MVC.png)
* MVC는 MVP 이전의 구조 중 하나입니다.
* Model, View, Control의 약자로 웹에서 주로 사용되는 구조입니다.
* 그래서 Android에 적용된 구조는 조금 다른 형태로 표현됩니다.
## MVC란?
* Model, View, Control의 약자입니다.
* MVC는 주로 웹에서 사용하고, 가장 널리 사용되는 구조 중 하나입니다.
* MVC 구조에서의 입력은 모두 Control에서 발생하게 되는 구조입니다.
* 이벤트가 발생한 Control에 의해 모듈의 정의와 View의 용도가 결정됩니다.
    * Model : 데이터를 가집니다.
    * View : 사용자에게 보일 화면을 표현합니다.
    * Control : 사용자로부터 입력을 받고, 이를 모델에 의해 View 정의를 하게 됩니다.
![MVC흐름](https://thdev.tech/images/posts/2016/10/Android-MVC-Architecture/default-mvc.png)
1. Control : 사용자 이벤트 발생
2. Control : 사용자 이벤트가 발생하였는데 갱신이 필요한지 Model에 확인
3. Model : 데이터 갱신이 필요하다는 이벤트 발생
4. View : Model 또는 Control로부터 갱신 필요 여부 이벤트를 받는다.
5. View : Model에서 실제 필요한 데이터를 받아와 View를 갱신
## 하지만 안드로이드에서는?
* Android에서는 View와 Control이 Activity/Fragment같은 View들이 모두 가지고 있습니다.
~~~java
public class MainActivity extends AppCompactActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // ...

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 데이터 갱신 요청
            // Model에 접근해서 최신 데이터를 요청
              // ex) ArrayList<String> getItems()
            // 전달받은 ArrayList를 통해 View를 갱신
        }
    });
  }
}
~~~
* 하나의 화면 안에서 Control인 setOnClickListener이 발생하고, 이를 View에서 모두 처리하는 형태입니다. Web에서 적용된 MVC 패턴은 View와 Control이 모두 분리된 상태를 말합니다.

하지만 안드로이드는 위와 같습니다.
![MVC in Android](https://thdev.tech/images/posts/2016/10/Android-MVC-Architecture/android-mvc.png)
1. Activity에서 사용자 이벤트 발생
2. Model로부터 데이터 갱신이 필요한지 확인
3. Model로부터 전달받은 데이터를 통해 View 갱신 여부 판단
4. View에서 UI 갱신 처리 
## 한 화면에서 모든 데이터를 처리함으로써 얻는 장점
* Android에서 흔하게 사용되는 MVC는 class 하나로 처리가 가능한 구조가 만들어지게 됩니다.
* 그렇기에 정리만 잘하면 한눈에 코드 파악이 가능할 수 있지만, 어느 정도 범주를 벗어나면 코드 파악이 어렵습니다. 함수 분리가 안되어 있다면 더욱 그렇고요. 그래서 함수 분리 또는 class 분리를 적절하게 해야 복잡도가 낮아질 수 있습니다.
~~~java
boolean isLast() {
  return itemList.size() >= 100;
}
~~~
* 그 외에도 공통으로 분리될 수 있어 보이는 setVisibility()도 여러 번 Visible/Gone이 발생한다면 당연히 함수로 분리합니다.
* 그렇기에 코드 분리만 잘해도 사실 MVC 패턴으로 코드 작성은 문제는 없습니다.
## 장점은?
* 개발 기간이 짧을 수 있음
    * 생각보다 개발 기간이 짧을 수 있습니다. 생각해야 할 부분도 많지 않고, 그냥 Android Activity에서 모든 걸 다 동작하게 처리만 해주면 되므로, 개발 기간이 짧을 수 있습니다.
* 코드만 읽을 수 있다면 누구나 쉽게 파악 가능
    * 그리고 처음 보는 사람도 별도의 패턴을 구분하지 않고, 쉽게 파악이 가능합니다.
## 단점은?
* 코드 양의 증가
    * 하나의 class에서 모든 걸 할 수 있습니다.
    * 그로 인해 하나의 class에서 수백 ~ 수천 줄이 넘는 코드를 발견하실 수 있습니다.
* 스파게티 코드 가능성
    * 복사 붙여넣기가 많아지고, 코드 분리가 안되어 있다면 스파게티 코드처럼 빙빙 꼬여있는 모습을 볼 수 있을 수 있습니다.
    * 그래서 복잡도는 증가합니다.
    * 결국 처음 설계부터 중요하고... 분리도 잘해야겠죠
* 유지 보수의 어려움
    * 개발 기간이 짧다는 말은 그만큼 코드를 막 작성할 수 있다는 말이고, 코드의 정체성이 혼란이 생길 수 있습니다.
    * 꾸준하게 이런 일이 발생하게 되면 쓰레기 코드의 양이 증가도 동시에 가져오게 됩니다.
    * 이러한 이유들로 유지 보수 역시 어려워지게 됩니다.
* View와 Model의 결합도가 높다
    * MVC는 View와 Model간의 결합도가 높습니다. 대부분의 코드를 View에서 Model을 직접 호출하여 사용하게 됩니다.
    * View와 Model 간의 결합도가 높아지게 되고, 테스트 코드 작성에도 어려움이 발생합니다.
* 테스트 코드 작성이 어렵다
    * MVC는 대부분 UI에서 모든 걸 할 수 있기 때문에 테스트 코드 작성이 어려워지게 됩니다.
    * 작성을 한다고 하더라도 UI위주의 테스트 코드 작성이 가능합니다.
    * 하지만 UI는 변화가 자주 있는 곳 중에 하나입니다. UI가 아닌 모델까지 변경이 된다면…
## MVC 작성한 샘플 코드
* Activity
    * 여기에서 apply는 ImageAdapter 내부 로직을 간단하게 불러올 수 있는 block이라고 생각하시면 되겠습니다.
    * 연속적으로 위와 같이 처리해야 하는 경우에 유용하게 사용이 가능합니다.
~~~kotlin
// Adapter를 생성
imageAdapter = ImageAdapter(this);
// Adapter에 itemList를 data를 통해서 불러와서 저장
imageAdapter.setImageItems(SampleImageData.getInstance().getImages(this, 10));
// RecyclerView에 adapter를 세팅
recyclerView.setAdapter(imageAdapter);
~~~
~~~kotlin
// 사용자가 reload 버튼 클릭
if (id == R.id.action_reload) {
    // Image adapter가 null이 아니라면 다음 apply 블락 안의 내용을 수행
    imageAdapter?.apply {
        // clear
        imageList?.clear()
        // 새로운 데이터를 불러와서 imageList를 교체
        imageList = ImageData.getSampleList(baseContext, 10)
        // Adapter를 notifyDataSetChanged 호출
        notifyDataSetChanged()
    }
    return true
}
~~~
* Adapter
    * Adapter에서 ViewHolder 부분은 아래와 같습니다.
    * ImageAsync를 직접 구현하여 사용하였습니다. 아직 외부 다른 라이브러리를 추가하지 않고, 로컬로 처리하였습니다.
    * setOnClickListener은 람다식이 적용된 모습입니다.
~~~kotlin
override fun onBindViewHolder(holder: ImageViewHolder?, position: Int) {
    val item = imageList?.get(position)

    ImageAsync(holder?.imageView).execute(item?.resource)
    holder?.textView?.text = item?.title

    holder?.itemView?.setOnClickListener {
        Toast.makeText(context, "Show ${item?.title}", Toast.LENGTH_SHORT).show()
    }
}
~~~
* Model
~~~kotlin
object ImageData {

    fun getSampleList(context: Context, size: Int) : ArrayList<ImageItem> {
        val list = ArrayList<ImageItem>()
        for (index in 0..size) {
            // Random을 통해 이미지를 불러온다.
            val name = String.format("sample_%02d", (Math.random() * 15).toInt())
            // getIdentifier을 통해서 resource을 찾습니다.
            val resource = context.resources.getIdentifier(name, "drawable", context.packageName)
            list.add(ImageItem(resource, name))
        }
        return list
    }
}
~~~
# Presenter 정의는?
* View : Presenter에서 전달받은 View의 이벤트입니다.
* Presenter : View에서 전달된 이벤트에 대한 처리를 한다(View와 무관한 처리만 한다)
![Presenter](https://thdev.tech/images/posts/2016/11/Android-MVP-One/mvp-default.png)
## Presenter을 구분하는 방법들
* 3가지 정도의 방법이 있는데 여기서는 크게 2가지 정도만 다룹니다.
* 먼저 Google Architecture를 따를 것입니다.

* View에 대한 interface만 정의하는 방법
    * interface View : View에 대한 interface만 정의
    * Presenter : interface 정의 없이 함수를 생성하여 사용
    * View : interface View을 상속받아서 정의
* Google architecture를 따른다.
    * Contract : View와 Presenter에 대한 interface을 작성
    * Presenter : Contract.Presenter을 상속받아서 구현
    * View : Contract.View을 상속받아서 구현
* PresenterImpl을 구현
    * Presenter : Presenter와 View에 대한 interface을 구현
    * PresenterImpl : Presenter을 상속받아서 구현
    * View : Presenter.View을 상속받아서 구현

* 구현하는 방법은 크게 3가지 정도로 나뉠 수 있습니다.
* 정답은 없고, 편한 방법과 다른 사람과 공유했을 때의 장/단점을 모두 따져서 작성하시면 좋겠습니다.
* interface 정의시의 장법은 처음 보는 사람이 파악이 쉽다이고, 단점은 역시나 interface 정의가 너무 많다가 될 수 있습니다.
* 결국 View와 Presenter간의 통신을 위한 리스너 역할의 interface View에 대한 정의는 처리가 되어야 합니다.

# Google Architecture을 따르면…

* MVP 따라 하기를 진행하기 전 조금은 어려운 내용을 먼저 정리해보았습니다.
* 우선 예제에서는 Google Architecture에서 정의하는 View/Presenter을 100% 따르지는 않고, interface 정의(Contract 정의)하는 방법만을 따르고 있습니다.
* 구글에서 정의하는 Presenter의 생성 방법은 다음과 같습니다.

![Google Architecture](https://thdev.tech/images/posts/2016/11/Android-MVP-One/Google_Architecture.png)

* Presenter의 생성은 View가 아닌 실제 View가 만들어지는 시점의 Activity/Fragment/View등에서 생성을 하고 해당 Presenter에 setView을 실행한다.
* setView가 호출되는 시점에 자기 자신(this)을 setPresenter함수를 통해서 실제 Presenter가 사용되어야 할 View에 전달한다.
* View에서는 setPresenter을 통해서 전달받은 Presenter을 가지고 이후 loadItem, OnClickListener 등의 처리를 합니다.

## 여기서 의문점
* Activity만 있는 경우는?
    * Activity만 있는 경우인데 별도의 View를 생성해야 하느냐?
    * 그냥 자기 자신이 받아도 되는가?
## 의문에 대한 답은?
* 구글이 제안하는 방법대로라면 
    * 별도의 View가 있다고 생각하시는게 좋겠죠? 
    * 코드의 통일성을 위해서 자기 자신이 받는 것은 없다고 보시면 됩니다.
## 의문을 해결할 수 있는 방법
* Activity만 있는 경우에도 별도의 view가 아닌 그냥 자기 자신이 가지도록 하면 됩니다.
    * 예제 중에서는 별도의 View을 생성하는 경우도 있습니다.
* Activity 저는 자기 자신이 받아도 된다고 생각합니다.

**다시 한번 정리하면**
* 결국 자기 자신이 new Presenter을 처리할 수 있어야 합니다.
    * 그럼 setPresenter라는 메소드가 필요하지 않게 됩니다.
* Activity/Fragment/View등에서 필요한 경우 Presenter을 생성하고, 자기 자신이 사용할 수 있어야 합니다.
![방법](https://thdev.tech/images/posts/2016/11/Android-MVP-One/My_Architecture.png)
* Activity/Fragment/View에서 필요한 Presenter을 직접 생성
* setView을 전달한다
* loadItem을 직접 호출
* View을 통해서 처리 결과에 따른 View을 갱신한다
## Contract 구현하기
* 구글의 Contract 정의를 따른 예정이므로 Contract을 구현합니다.
* Contract을 사용하는 이유는 간단합니다. View와 Presenter을 각각 정의하기 위함이며, 이해를 돕기 위한 이유도 있습니다.
* 하나의 interface에 View/Presenter을 정의하고, 이를 각각의 View와 Presenter에서 정의하는 방식입니다.
* 먼저 View와 Presenter을 각각 다음과 같이 구현할 수 있습니다.
~~~kotlin
interface SampleContract {
	interface View {
		 // View method
	}

	interface Presenter {
		// Presenter method
		fun setView(view: View);
	}
}
~~~
## Presenter 상속 정의
* SampleContract.Presenter을 상속받아서 구현하며, SampleContract.View을 가지게 됩니다.
~~~kotlin
class SamplePresenter : SampleContract.Presenter {
	// SampleContract.Presenter에서 정의한 내용을 구현

	private var view: SampleContract.View? = null

	override fun setView(view: SampleContract.View) {
		this.view = view
	}
}
~~~
* View에서 데이터가 필요하다면 다음의 과정을 거치게 됩니다.
    * View : loadItem을 호출합니다.
    * Presenter : loadItem이 발생하면 새로운 데이터를 호출합니다.
    * Presenter -> View : loadItem이 성공적으로 완료되면 updateView을 호출합니다.
    * 이때 SampleContract.View의 정의되어 있는 updateView을 호출합니다.
    * View : 전달된 updateView에 따라서 실제 View를 갱신합니다.
## View 상속 정의
* SampleContract.View를 상속받으면 다음과 같이 정의해주시면 되겠습니다.
~~~kotlin
class SampleActivity : AppCompatActivity : SampleContract.View {

	private var presenter: SampleContract.Presenter

	override fun onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

		presenter = SamplePresenter()
		presenter.setView(this)

		presenter.loadItem()
	}

	override fun updateView(items: List<Item>) {
		// UI 갱신
	}
}
~~~
# Presenter/PresenterImpl
## Presenter interface를 구현하기전에
* Presenter interface을 구현하는 방법입니다.
* 아까는 구글에서 추천하는 방법인 Contract을 통해서 interface을 구현하는 방법을 살펴보았고, 구글의 MVP 패턴 적용하는 방법을 살펴보았습니다.
* 그리고 유용한 패턴을 설명드렸습니다.
* 이번에는 많이 사용되는 2 번째 방법을 소개합니다.

* Presenter : interface로 구현하며, View를 포함
* PresenterImpl : Presenter interface을 상속받아서 구현
    * 기존과 같은 방식으로 접근하지만 interface 생성하는 방법을 달리하였습니다.

![create interface](https://thdev.tech/images/posts/2016/11/Android-MVP-One/My_Architecture.png)
## Presenter interface 구현
* 구글은 Contract을 통해서 View/Presenter의 interface 2개를 작성하였지만, 다음의 방법은 Presenter에 View interface만을 작성하였습니다.
~~~kotlin
interface SamplePresenter {
	// Presenter 구현

	fun loadItem()

	interface View {
		// 해당 Presenter에서 사용할 View 구현
		fun updateView(items: List<Items>);
	}
}
~~~
## PresenterImpl 구현
* 다음과 같이 implements를 통해서 상속받고, 이를 아래와 같이 구현합니다.
* 다만 이름은 implements의 구현체의 이름으로 Impl을 사용합니다.
~~~kotlin
class SamplePresenterImpl(val view: SamplePresenter.View) : SamplePresenter {

	// 상속 받은 interface 구현
	override fun loadItem() {
		// ...
		val list = ArrayList<String>()
		view.updateView(list)
	}
}
~~~
## View 구현
* SamplePresenter인 SamplePresenterImpl을 초기화하여 사용합니다.
* 사용법은 Contract을 통한 생성과 동일합니다.
~~~kotlin
class SampleActivity : AppCompatActivity : SampleContract.View {

	private var presenter: SamplePresenter

	override fun onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

		presenter = SamplePresenterImpl(this)

		presenter.loadItem()
	}

	override fun updateView(items: List<Items>) {
		// UI 갱신
	}
}
~~~
## Presenter interface가 없는 경우
* 추가로 Presenter interface을 사용하지 않는 경우입니다.
* Presenter에 대한 interface을 사용하지 않기 때문에 View에 대한 interface만을 정의합니다.
~~~kotlin
interface SampleView {
	fun updateView(items: List<Items>);
}
~~~
* 그리고 다음과 같이 Presenter를 구현합니다.
~~~kotlin
class SamplePresenter(val view: SampleView) {

	fun loadItem() {
		// ...
		val list = ArrayList<String>()
		view.updateView(list)
	}
}
~~~
* 그리고 다음과 같이 View의 사용이 가능하게 됩니다.
~~~kotlin
class SampleActivity: AppCompatActivity: SampleContract.View {

	private var presenter: SamplePresenter

	override fun onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

		presenter = new SamplePresenter(this)

		presenter.loadItem()
	}

	override fun updateView(items: List<Items>) {
		// UI 갱신
	}
}
~~~
* MVP를 작성하는 방법 3 가지를 정리해보았습니다.
* interface 정의가 있는 경우와 없는 경우의 장/단점이 있으므로 적절하게 사용하시면 좋을 것 같습니다.
* 그 외에는 메소드 형태로 attachView을 적용하는지, 아니면 초기화 시에 바로 View를 넘겨주는지 등에 따른 방법이 서로 다를 수 있습니다.
* attachView를 통해서 넘겨주는 방식을 사용하기도 합니다.
* 이유는 View에 대한 attachView가 명확하다고 할 수 있겠네요. 다만 null 체크 등은 들어가야 합니다.
~~~kotlin
view?.updateView()
~~~

# Presenter 분리하기
* 앞의 MVC를 Presenter로 분리하려고 합니다.
* MainActivity의 코드 중 setImageItems의 모델 사용하는 부분을 Presenter로 분리합니다.
~~~kotlin
// Adapter를 생성
imageAdapter = ImageAdapter(this);
// Adapter에 itemList를 data를 통해서 불러와서 저장
imageAdapter.setImageItems(SampleImageData.getInstance().getImages(this, 10));
// RecyclerView에 adapter를 세팅
recyclerView.setAdapter(imageAdapter);
~~~
* MainActivity의 오른쪽 상단 버튼인 reload 발생할 경우의 코드에 대해서 Presenter로 분리
~~~kotlin
// reload 액션이 발생
if (id == R.id.action_reload) {
    // 기존 itemList clear
    imageAdapter.clear();
    // 새로운 ImageList 불러와서 교체
    imageAdapter.setImageItems(SampleImageData.getInstance().getImages(this, 10));
    // UI Change
    imageAdapter.notifyDataSetChanged();
    return true;
}
~~~
## MainContract.kt 정의
* kotlin에서 사용가능한 interface 정의를 다음과 같이 합니다.
* var view와 var ImageData를 정의합니다.
~~~kotlin
interface MainContract {
    interface View {

        fun updateItems(items: ArrayList<ImageItem>, isClear: Boolean)

        fun notifyAdapter()
    }

    interface Presenter {
        var view: View
        var imageData: ImageData

        fun loadItems(context: Context, isClear: Boolean)
    }
}
~~~
## MainPresenter.kt 정의
* 그리고 lateinit을 통해서 변수를 선언합니다.
* java에서는 이때 setView/getView가 자동으로 생성됩니다.
~~~kotlin
lateinit override var view: MainContract.View
lateinit override var imageData: ImageData
~~~
* 그리고 loadItems을 아래와 같이 생성합니다.
* updateItems과 notifyAdapter을 각각 호출해주어 UI를 갱신합니다.
~~~kotlin
override fun loadItems(context: Context, isClear: Boolean) {
    imageData.getSampleList(context, 10).let {
        view.updateItems(it, isClear)
        view.notifyAdapter()
    }
}
~~~
## MainActivity.kt 정의
~~~kotlin
private lateinit var presenter: MainPresenter

override fun onCreate(savedInstanceState: Bundle?) {
    // 생략

    presenter = MainPresenter().apply {
        view = this@MainActivity
        imageData = ImageData
    }

    // 생략

    presenter.loadItems(this, false)
}
~~~
* 그리고 presenter로 부터 View에 대한 콜백을 다음과 같이 처리합니다.
~~~kotlin
override fun updateItems(items: ArrayList<ImageItem>, isClear: Boolean) {
    imageAdapter?.apply {
        if (isClear) {
            imageList?.clear()
        }
        imageList = items
    }
}

override fun notifyAdapter() {
    imageAdapter?.notifyDataSetChanged()
}
~~~
# Adapter Contract 정의하기
* Adapter에 대한 Contract 정의를 하고, 이를 상속받아 사용하는 방법을 정리합니다.
## Adapter Contract 정의란?
* 아까는 Activity/Fragment에서 사용할 Contract을 정의하고, 분리해보았습니다.
![Adapter 처리](https://thdev.tech/images/posts/2016/12/Android-MVP-Four/mvp_adapter.png)
* View : Presenter에게 데이터를 요청
* Presenter : Model에게 데이터 요청
* Model : 데이터를 Presenter로 전달
* Presenter : model로부터 전달받은 데이터를 View에게 전달
* View : View에서 가지고 있던 Adapter에게 데이터를 추가하고, notify를 처리
## Presenter에서 직접 data를 전달하지 않으면?
* MVP 방식으로 분리만 하면 위에서 설명한 그림과 같이 동작합니다.
* 단점은 뭘까요?
## 단점
* Presenter에서 Adapter의 데이터를 저장하고, 불러오는 모든 부분에 항상 View가 함께 해야 합니다.
* Presenter에서 Adapter에 데이터를 셋팅하는 경우
    * View에서 이를 전달받아 Adapter에 저장
* 사용자의 onClick 이벤트를 받아 Presenter에서 처리한다면?
    * View에서 onClick 시에 필요한 데이터를 Adapter로부터 전달받아야 하므로, 필요한 함수(getItem)가 만들어져야 한다.
* 위와 같은 상황이 아니더라도 모두 View에서 이를 대신 받아서 전달해야 합니다. 그럼 복잡도가 높아지고, 귀찮아집니다.
## 그래서 더 좋은 방법은?
* 여기에 저는 Contract를 추가하였습니다. 그래서 다음과 같이 Adapter에 대한 Contract을 다시 생성하였습니다.
~~~kotlin
interface AdapterContract {
  interface View {}

  interface Model {}
}
~~~
## Contract을 Adapter에 상속받아 구현
* View/Model을 구분해서 Contract을 생성하였으니, Adapter에서 이를 상속받아서 구현해줍니다.
~~~kotlin
class SampleAdapter : RecyclerView.Adapter(), AdapterContract.View, AdapterContract.Model {
  // 생략
}
~~~
* 그리고 기존 View에서 정의하였던, addItem, notify에 대해서 AdapterContract에서 정의하도록 하고, 이를 Adapter에서 구현 만 합니다.
## Presenter에서는?
* Presenter에서는 AdapterContract.View와 AdapterContract.Model을 구현합니다.
~~~kotlin
class SamplePresenter implement SampleContract.Presenter {
  private AdapterContract.View adapterView;
  private AdapterContract.Model adapterModel;

  public void setAdapterView(AdapterContract.View adapterView) {
    this.adapterView = adapterView;
  }

  public void setAdapterModel(AdapterContract.Model adapterModel) {
    this.adapterModel = adapterModel;
  }
}
~~~
* 이렇게 구현해주면, 이후에는 adapterView/adapterModel을 통해서 직접 Adapter를 접근하게 됩니다.
* 기존에서와 같이 View를 통해서 접근할 필요성이 없어집니다.
* 그래서 다시 그리면 아래와 같습니다.
![AdapterModel](https://thdev.tech/images/posts/2016/12/Android-MVP-Four/mvp_adapter_contract.png)
