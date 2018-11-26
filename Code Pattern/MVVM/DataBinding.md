# DataBinding
## RecyclerView Adapter와 Data
* RecyclerView 나 ListView 처럼 View 아래에 또 다른 레이어 형태를 갖춘 UI 컴포넌트에 대해서 접근 하는 것이 쉽지 않습니다. 
* 그래서 이런 경우 Custom Attribute 를 정의해서 구현해야 합니다.
* ViewModel 에서 정의된 데이터 리스트를 RecyclerView 의 Custom attribute 와 연결시키고 
* ViewModel 의 데이터가 변경될때 Custom attribute 와 연결된 함수를 호출하도록 합니다.
~~~kotlin
@BindingAdapter("bind_items")
fun setBindiItems(view : RecyclerView, itesm : List<Item>) {
  val adapter = view.adapter as? MainAdapter ?: ItemAdapter().apply { view.adapter = this }
  adapter.items = items
  adapter.notifyDatasetChanged()
}

~~~
~~~kotlin
class ItemAdapter : RecyclerView.Adapter() {
  var items : List<Item> = emptyList()
}
~~~
~~~kotlin
class MainViewModel {
  val items = ObservableArrayList() 
  init {
    items.add(Item("John"))
    items.add(Item("Charles"))
  }
}
~~~
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<layout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <data>
        <variable name="vm" type="MainViewModel" />
    </data>

<android.support.v7.widget.RecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:bind_items="@{vm.items} />
</layout>
~~~
## 간단한 ScaleAnimation
~~~kotlin
class MainViewModel {
 
  val scaleAndAlpha = ObservableField<ScaleAlpha>()
  
  fun imageClick() {
    scaleAndAlpha.set(ScaleAlpha(1f,1f))
  }
}
~~~
~~~kotlin
data class ScaleAlpha(toScale : Float, toAlpha : Float , fromScale : Float = 0f, fromAlpha : Float = 0f)
~~~
~~~kotlin
@BindingAdapter("scaleAndAlpha")
fun animateScaleAndAlpha(view : View, scaleAndAlpha : ScaleAlpha) {
  view.scaleX = scaleAndAlpha.fromScale
  view.scaleY = scaleAndAlpha.fromScale
  view.alpha = scaleAndAlpha.fromAlpha
  view.animate()
    .scaleX(scaleAndAlpha.toScale)
    .scaleY(scaleAndAlpha.toScale)
    .alpha(scaleAndAlpha.toAlpha)
    .setDuration(300)
    .start()
}
~~~
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
<ImageView
    android:layout_width="50dp"
    android:layout_height="50dp"
    app:scaleAndAlpha="@{vm.scaleAndAlpha}"
    android:onClick="@{() -> vm.imageClick()}"
    />
</layout>
~~~
* 애니메이션을 지정하기 위한 클래스를 지정하고 이를 Observable 대상으로 지정합니다. 
* 이후 Custom Attribute 를 이용해서 값이 변하게 되면 바인딩 함수를 호출하도록 합니다.
## Custom View 에 Databinding 연결하기
* Setter 이용하기
* Databinding 은 View 에 setter 가 있을 경우 이를 attribute 에 사용하면 자동으로 Setter 함수와 연결해줍니다.
~~~kotlin
class RoundImageView : ImageView {
  
  private radious : Int
  
  fun setRadious(rad : Int) {
    this.radious = rad
  }
  // 이하 생략
}
~~~
~~~kotlin
class MainViewModel {
  val imageRadious = ObservableInt()
  
  init {
    imageRadious.set(10)
  }
}
~~~
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
<custom.RoundImageView
    android:layout_width="50dp"
    android:layout_height="50dp"
    app:radious="@{vm.imageRadious}"
    />
</layout>
~~~
* RoundImageView 에 지정된 setRadious() 에서 set 을 뺀 Radious 를 Custom Attribute 로 지정하면 
* 별도의 @BindingAdapter 함수 없이 View 의 setRadious() 에 직접 접근하여 갱신하도록 Databinding 이 제어합니다.
## Custom Attribute 2-Way binding 하기
* 간혹 View 에 지정한 정보를 ViewModel 에도 상호 갱신되도록 해야할 필요가 있습니다. 
* 보통은 ViewModel to View 로 화면을 갱신하는데 이를 1-Way Binding 이라고 하며 반대로 View to ViewModel 화면의 정보를 ViewModel 로 갱신하는 것을 2-Way Binding 이라고 합니다.
~~~xml
<EditText
  android:text="@={vm.inputText}" />
~~~
* 위와 같은 방식으로 간단하게 = 를 추가하면 되나 Custom Attribute 는 InverseBindingAdapter 라고 반대로 View 의 정보가 갱신되록 ViewModel 로 이벤트가 전달되도록 만들어줘야 합니다.
~~~kotlin
class MainViewModel {
  val visible = ObservableBoolean(false)
}
~~~
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
<ImageView
    android:layout_width="50dp"
    android:layout_height="50dp"
    app:viewVisible="@{vm.visible}"
    />
</layout>
~~~
~~~kotlin
// ViewModel -> View
@BindingAdapter("viewVisible")
fun viewVisible(v: View, visible: Boolean) {
    if (visible) {
        v.visibility = View.VISIBLE
    } else {
        v.visibility = View.GONE
    }
}

// Databinding -> ViewModel, viewVisibleAttrChanged.onChange() 가 호출되면 리턴 값이 ViewModel 로 바인딩됨
@InverseBindingAdapter(attribute = "viewVisible", event = "viewVisibleAttrChanged")
fun viewVisible(v: View): Boolean {
    return v.visibility == View.VISIBLE
}

// View -> Databinding, View 에 변화가 생기면 viewVisibleAttrChanged 이벤트로 등록된 InverseBindingAdapter 함수를 호출
@BindingAdapter("viewVisibleAttrChanged", requireAll = false)
fun viewVisibleChanged(v: View, newAttrChanged: InverseBindingListener) {
    newAttrChanged.onChange()
}
~~~
* View 에서 변화가 생기면 viewVisibleAttrChanged 함수를 호출하도록 등록하고 
* 이때 InverseBindingListener.onChange() 가 호출되면 viewVisibleAttrChanged 이 event 로 등록된 @InverseBindingAdapter 함수를 호출하도록 합니다.
* Custom Attribute 의 양방향 갱신은 위와 같은 방식으로 이루어질 수 있습니다.