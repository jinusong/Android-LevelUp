# MVVM
* MVVM은 Model-View-ViewModel을 이야기하고 MVC가 모델, 뷰, 컨트롤러로 이루어져있다면 MVVM은 모델, 뷰, 뷰모델로 이루어진 패턴입니다. 
* MVC에서 컨트롤러가 뷰모델로 교체된 형테이고 뷰모델은 UI레이어 아래에 위치합니다. 뷰모델은 뷰가 필요로 하든 데이터와 커맨드 객체를 노출해 주기 때문에 뷰가 필요로하는 데이터와 액션은 담고 있는 컨테이너 객체로 볼 수도 있습니다.

## MVVM의 다른 점
* MVVM이 MVC와 다른 점은 뷰모델은 뷰를 지원하고 뷰가 필요한 데이터와 커맨드를 제공하기 위해서 만들어졌다는 것입니다. 
* 이름 그대로 뷰모델은 뷰를 위한 모델이며 뷰모델을 뷰에 바인딩할 때 가장 강력합니다. 
* 여러가지 뷰를 제공하는 일반적인 객체가 아닌 각 뷰에 맞춰서 만들어진 것입니다. 때문에 뷰는 뷰모델에 대해서만 알고 있으면 되고 그외의 아키텍처에 대해서는 신경쓰지 않아도 됩니다. 
* 그래서 MVC와 가장 다른 점은 커맨드와 데이터바인딩이라고 할 수 있습니다. 
* 이 2가지 요소로 인하여 뷰와 컨트롤러(MVVM에서는 뷰모델)의 관계를 끊을 수 있습니다. 
* 커맨드를 사용함으로써 비헤이비어를 뷰모델에서 정의한 특정한 뷰액션과 연결할 수 있습니다. 
* 데이터바인딩 특정한 뷰 속성과 뷰모델의 속성을 연결할 수 있도록 하고 뷰모델에서 속성이 변경되었을 때 뷰에 반영이 됩니다.

## Model
* MVC와 동일하며 변화가 없습니다.

## View
* View 는 화면에 표현되는 레이아웃에 대해 관여합니다. 기본적으로 비지니스 로직을 배제하지만 UI 와 관련된 로직을 수행할 수 있습니다.


## ViewModel
* View 에 연결 할 데이터와 명령으로 구성되어있으며 변경 알림을 통해서 View 에게 상태 변화를 전달합니다. 
* 전달받은 상태변화를 화면에 반영할지는 View 가 선택하도록 합니다. 명령은 UI 통해서 동작하도록 합니다.

![MVVM](https://cdn-images-1.medium.com/max/1600/0*Zr6NnNram5jtbhD7.png)
*  ViewModel 은 Model 을 알지만 View 를 알지 못합니다. View 는 Model 을 알지 못하나 ViewModel 을 알 수 있습니다. 
* View 는 ViewModel 을 옵저빙 하고 있다가 상태 변화가 전달되면 화면을 갱신해야 합니다.

## View와 ViewModel을 연결합니다.
~~~kotlin
class MainActivity : Activity() {
  fun onCreate() {
    val tv : TextView = findViewById(R.id.tv)
    // ViewModel 정의
    val viewModel = MainViewModel()
    // ViewModel 옵저빙 후 갱신
    viewModel.name.observe {
      tv.text = it
    }
    
    tv.setOnClickListener {
      viewModel.nameClick()
    }
  }
}

class MainViewModel {
  val name = ObservableField("")
  
  init {
    // ViewModel 용 데이터 변경
    name.set(initName())
  }
  
  private fun initName() : String = "John"
  
  fun nameClick() = name.set("Click!!")
}
~~~
* 하지만 이런 코드는 서로간의 의존적 형태를 지속 시키기 때문에 유지보수성을 높이는데는 한계를 가져올 수 있습니다.

## DataBinding : View 와 ViewModel 독립시킵니다.

![DataBinding](https://cdn-images-1.medium.com/max/1600/1*HpBpwd9E6IyWmlO0jth0Mg.png)

* 닷넷유저들은 MVVM 을 구현하는데 있어서 Databinding 을 필수 기술로 간주하고 있습니다. 
* Databinding 은 View 와 ViewModel 간의 데이터와 명령을 연결해주는 매개체가 되어 서로의 존재를 명확히 알지 않더라도 다양한 인터랙션을 할 수 있도록 도와줍니다.
* Model 에서 데이터 변경되면 ViewModel 을 거쳐서 View 로 전달되도록 하는데 Android 에서는 LiveData 나 RxJava 등을 통해 구현할 수 있습니다.
* Databinding 을 통해서 ViewModel 의 notify 와 View 가 ViewModel 로 명령을 전달하는 과정들을 UI 코드로 정의하기에 View 와 ViewModel 은 서로의 독립성을 더 높일 수 있습니다.

~~~xml
<?xml version="1.0" encoding="utf-8"?>
<layout
    xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- 데이터바인딩 정의 -->
    <data>
        <variable
            name="vm"
            type="MainViewModel" />
    </data>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <TextView
            android:id="@+id/et_lottie_list"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" 
            android:text="@{vm.name}"
            android:onClick="@{(() -> vm.nameClick()}" />
    </FrameLayout>
</layout>
~~~
~~~kotlin
class MainActivity : Activity() {
  fun onCreate() {
    val tv : TextView = findViewById(R.id.tv)
    // ViewModel 정의
    val viewModel = MainViewModel()
    // View-ViewModel 바인딩
    dataBinding.setVariable(BR.vm, viewModel)
    
  }
}

class MainViewModel {
  // databinding 을 통해 view 와 연결
  val name = ObservableField("")
  
  init {
    name.set(initName())
  }
  
  private fun initName() : String = "John"

  // databinding 을 통해 view 와 연결  
  fun nameClick() = name.set("Click!!")
}
~~~
* 기존의 코드와 크게 달라진 점은 없으나 observe 와 클릭 이벤트를 전달하는 코드는 UI 코드에 데이터 바인딩으로 처리되어 있습니다.
* 동일한 MVVM 의 코드이지만 Databinding 을 통해 구현된 코드는 더욱 의존성이 낮아졌다는 것을 알 수 있습니다.
## 종합
* 위에서 말했듯이 MVVM 은 MVP 에서 파생된 모델이지만 MVP 가 기존에 가지는 문제점을 개선하고자 나온 모델입니다. 
* 하지만 MVVM 을 아무런 도움 없이 구현한다면 기존의 문제점을 개선하는 과정에서 새로운 문제를 일으킬 수 있습니다. 
* 때문에 Databinding 이라는 툴을 이용함으로써 서로간의 의존성을 낮춤으로써 유닛테스트를 더욱 쉽게 작성할 수 있고 
* UI 코드는 네이티브 코드에 관여하지 않아도 되게 하였습니다.
* Android 에서 MVVM 을 구현함에 있어서 Databinding 을 제외하고 구현하시고 있다면 이미 문제를 잠재적으로 내재한 코드들이 쓰여지고 있다는 것입니다. 
* 문제를 최소화 하기 위해 Databinding 을 함께 써야만 그 진가가 발휘됨을 꼭 명심해야 합니다.

## 참고 링크
*  https://medium.com/@jsuch2362/android-에서-mvvm-으로-긴-여정을-82494151f312