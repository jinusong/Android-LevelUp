# Understanding Fragment

## Fragment란?
* 프래그먼트는 안드로이드 3.0부터 도입된 컴포넌트입니다. 뷰를 가질 수 있어 UI를 가진 컴포넌트로서 통합할 수 있습니다.
* 액티비티와 마찬가지로 수명주기를 가지며, 개개의 수명주기에 따라 처리를 구현할 수 있다는 특징도 있습니다. 그러므로 액티비티와 유사한 형태로 이용할 수 있습니다.
* 이 문서는 UI를 가지지 않는 프래그먼트인 Headless나 중첩된 프래그먼트에 관해서도 가볍게 다룹니다.

## 프래그먼트를 이해하자
* 액티비티는 한 화면당 1개씩 있는 형태였지만 프래그먼트는 액티비티 1개당 여러 개 존재할 수 있습니다. 그러므로 액티비티 1개로 구현했던 것을 기능 단위로 프래그먼트로 나누어 구현할 수 있습니다.
* 프래그먼트는 안드로이드의 프레임워크에서 구현된 것과 지원라이브러리에서 구현된 것으로 2가지가 있습니다.
* 안드로이드 2.3 이상을 지원하는 경우 지원 라이브러리를 이용할 수 밖에 없지만 안드로이드 4.1 이상을 지원하는 경우라도 지원 라이브러리를 이용하는 편이 좋습니다.
* 지원라이브러리 쪽은 새로운 OS에서 구현된 기능이 수시로 백포트되기 때문입니다. 지원라이브러리를 이용하면 그만큼 앱크기가 늘어나지만 대부분 신경 쓰일 정도는 아닙니다.
* 예시로 버튼을 누르면 클릭 이벤트를 액티비티에 통보하고 액티비티에서 Toast를 표시하는 앱이 있습니다. 여기서 프래그먼트는 다음 3단계로 이용합니다.
    * 프래그먼트 클래스를 만든다.
    * 프래그먼트의 뷰 구축에 이용할 레이아웃 XML을 만든다.
    * 액티비티로부터 작성한 프래그먼트를 이용한다.

### 1. 프래그먼트 클래스를 만든다.
* MyFragment라는 프래그먼트 클래스를 만듭니다. 프래그먼트 클래스를 만드는 데는 두 가지 규칙이 있습니다. 첫번째는 프래그먼트 클래스를 상속하는 것이고, 두 번째는 인수가 없는 기본 생성자를 준비하는 것입니다.
~~~kotlin
import android.support.v4.app.Fragment
// 프래그먼트 클래스를 상속
class MyFragment: Fragment {
    // 빈 생성자는 Fragment를 이용하는 데 필요
    fun MyFragment() { }
}
~~~

### 2. 프래그먼트의 뷰 구축에 이용할 레이아웃 XML을 만든다.
* 레이아웃 XML 파일을 만듭니다.
~~~xml
<FrameLayout ~ 생략 ~>
    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="PUSH!"/>
</FrameLayout>
~~~
* 다음으로 MyFragment에서 이용하기 위해 레이아웃 XML을 전개하고 뷰를 생성합니다. 프래그먼트에서는 뷰를 생성하는 시점이 정해져 있어 onCreateView()에서 생성합니다. 뷰가 만들어지면 onViewCreated()가 콜백됩니다.
~~~kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.fragment_my, container, false)
}

override onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById(R.id.button).setOnClickListener{ v -> 
        if(mListener != null) {
            mListener.onFragmentInteraction()
        }
    }
}
~~~
* onClick() 안에 mListener.onFragmentInteraction()이 갑자기 등장했으므로 보충 설명을 하겠습니다. 이것은 버튼이 눌렸을 때의 처리를 하기 위해서 입니다.
* 이 리스너는 MyFragment에 정의합니다.
~~~kotlin
private var mListener: OnFragmentInteractionListener

/**
* 액티비티와 연계하기 위한 인터페이스
*/
interface OnFragmentInteractionListener {
    fun onFragmentInteraction()
}
~~~
* 그리고 이 리스너를 액티비티 쪽에서 구현합니다.
~~~kotlin
class MainActivity: AppCompatActivity(): MyFragment.OnFragmentInteractionListener {
    ~ 생략 ~

    override fun onFragmentInteraction() {
        Toast.makeText(getApplicationContext(), "버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show()
    }
}
~~~
* 액티비티에서 구현한 리스너와 프래그먼트의 연결은 액티비티와 프래그먼틍가 연결될 때 onAttach()에서 합니다. 또한 연결을 끊기 전에 onDetach()에서 리스너에 대한 참조를 해제합니다.
* onAttach()에서는 OnFragmentInteractionListener가 구현되지 않았으면 RuntimeException을 던져 처리를 계속할 수 없게 했습니다.
~~~kotlin
override fun onAttach(context: Context) {
    super.onAttach(context)
    // Activitiy 쪽에 필요한 인터페이스가 구현됐는지 확인
    if(context instanceof OnFragmentInteractionListener) {
        mListener = (OnFragmentInteractionListener) context
    } else {
        throw RuntimeException(context.toString() + " 
        OnFragmentInteractionListener를 구현해 주새요.")
    }
}
override fun onDetach() {
    super.onDetach()
    mListener = null
}
~~~
* 이제 액티비티에서 구현한 리스너를 프래그먼트에서 가질 수 있고, 버튼이 눌린 시점을 액티비티에 알려줄 수 있게 됐습니다. 
* 아울러 MainActivity에 대한 참조를 직접 가지지 않고 인터페이스로서 가지는 것은 특정 액티비티에 의존하지 않도록 결합을 느슨하게 만들기 위해서입니다.

### 3. 액티비티로부터 작성한 프래그먼트를 이용한다.
* 이번에는 정적으로 프래그먼트를 추가합니다. 정적 프래그먼트는 레이아웃 XML 파일에 직접 프래그먼트 태그를 기술해 실현할 수 있습니다.
~~~xml
<RelativeLayout ~ 생략 ~>
    <fragment
        android:id="@+id/myfragment"
        android:name="com.advanced_android.simplefragmentsample.MyFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</RelativeLayout>
~~~
* name 속성에는 Fragment 클래스 이름을 기술합니다. 이로써 activity_main.xml로 뷰를 생성할 때 프래그먼트도 생성됩니다.

## 프래그먼트 수명주기를 이해하자
* 프래그먼트는 액티비티와 마찬가지로 수명주기를 가집니다. 조금 전에 나온 onAttach()와 onDetach()도 수명주기의 콜백입니다. 
* 프래그먼트의 수명주기는 액티비티처럼 onCreate() 등의 콜백을 가지며, 프래그먼트 특유의 수명주기 콜백도 있습니다. 따라서 액티비티 때보다 상당히 복잡합니다.
![FragmentLifecycle 이미지](https://bricolsoftconsulting.com/wp-content/uploads/2015/06/fragment_recreation.jpg)

|메서드명|서점|실행하는 처리의 예|
|------|---|-------------|
|onAttach|프래그먼트와 액티비티가 연결될 때|이 시점에서 getActivity 메서드는 null을 반환합니다.|
|onCreate|생성 시|초기화 처리|
|onCreateView|생성 시|뷰 생성|
|onActivityCreated|생성 시|초기화 처리, 뷰 생성(setContentView의 호출) 등|
|onStart|비표시 상태|표시 전 시점|
|onResume|표시 시|필요한 애니메션 등 실행 화면 갱신 처리|
|onPause|일부 표시|애니메이션 등 화면 갱신 처리 정지, 일시정지 시에 불필요한 리소스 해제, 필요한 데이터 영속화|
|onStop|비표시 상태|비표시된 시점|
|onDestroyView|폐기 시|필요 없는 리소스 해제|
|onDestroy|폐기 시|필요 없는 리소스 해제|
|onDetach|폐기 시|필요 없는 리소스 해제|
* 액티비티에서 본 onSaveInstanceState()는 프래그먼트에도 있습니다. 이 메서드는 프래그먼트가 폐기되기 전에 호출되므로 필요한 정보는 이 때 저장할 수 있습니다.
* 액티비티와는 달리 onRestoreInstanceState()는 프래그먼트에는 존재하지 않지만 onCreate() ~ onActivityCreated()의 수명주기의 메서드의 인수에는 Bundle이 포함돼 있습니다.
* 이런 특징을 이용해 액티비티의 onRestroeInstanceState()와 똑같이 복귀하도록 구현할 수 있습니다.

### 프래그먼트를 동적으로 추가, 삭제하자.
* 프래그먼트를 레이아웃 XML 파일 안에 직접 기술했지만 이번에는 동적으로 프래그먼트를 추가하고 삭제해봅시다.
* 추가, 삭제 버튼을 누르면 각각 프래그먼트가 추가되거나 삭제됩니다.
* 각 프래그먼트는 TextView를 하나씩 가지고, 순서를 알 수 있도록 'X번째 프래그먼트'라는 문자열을 표시합니다.
* 프래그먼트를 추가하려면 프래그먼트를 추가할 컨테이너가 될 ViewGroup이 필요합니다. 여기서는 id가 fragment_container인 LinearLayout을 컨테이너로 준비했습니다.
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout android:id="@+id/fragment_container"
    android:orientation="vertical" ~ 생략 ~>
    <LinearLayout
        android:layout_width = "match_parent"
        android:layout_width = "wrap_content"
        android:orientation= "horizontal">
        <Button
            android:id="@+id/add_button"
            android:text="추가"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>
        <Button
            android:id="@+id/remove_button"
            android:text="삭제"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>
    </LinearLayout>
</LinearLayout>
~~~
* 추가 버튼이 눌리면 Fragment를 추가합니다.
    * 프래그먼트 추가와 삭제는 트랜잭션 단위로 한다.
    * 프래그먼트 추가는 ViewGroup에 한다. 이 예에서는 LinearLayout
    * 액티비티와 마찬가지로 백스택이 있다.
* 아울러 이번에는 뒤로가기 키를 눌러 백스택에서 꺼내지 않아도 상관없습니다. 삭제 버튼을 누르면 백스택으로부터 pop하고 한 단계 이전 상태로 돌아갑니다. 다시 말해, 추가된 프래그먼트가 삭제됩니다.
~~~kotlin
findViewById(R.id.add_button).setOnClickListener{
    var fragmentManager :FragmentManager = getSupportFragmentManager()
    fragmentManager.beginTransaction()
        .add(R.id.fragment_container, 
        MyFragment.getInstance(mNumber))
        // 프래그먼트 생성
        .addToBackStack(null)
        .commit()
}
findViewById(R.id.remove_button).setOnClickListener{
    if(mNumber == 0) {

    }
    var fragmentManager: FragmentManager = getSupportFragmentManager()
    fragmentManager.popBackStack()  // 백스택에서 꺼내서 직전 상태로 돌아간다.
}
~~~
* 앞에서는 프래그먼트를 MyFragment.getInstance(mNumber)와 같은 팩토리 메서드로 생성했습니다. 그런데 동적으로 추가한 프래그먼트는 화면 회전 등 액티비티가 재생성될 경우에는 어떻게 될까요?
* 사실, 액티비티와 마찬가지로 프래그먼트도 재생성됩니다. 그렇다면 프래그먼트를 재생성할 때 초깃값은 어떻게 하는지가 문제로 남습니다.
* 이 문제는 Fragment.setArguments(Bundle)로 초깃값을 설정함으로써 해결할 수 있습니다. 재생성 시에는 getArguments()를 호출해 설정한 값을 가져올 수 있습니다.
* 팩토리 메서드를 사용해 프래그먼트를 생성하는 방법은 모범 답안 중 하나이므로 관용구처럼 기억해 두면 좋습니다.