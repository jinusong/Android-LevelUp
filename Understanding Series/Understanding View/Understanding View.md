# Understanding View

## 뷰를 이해하자
* 뷰란 UI를 구성하는 바탕이 되는 컴포넌트로서 네모난 그리기 영역을 가집니다. TextView, Button, EditText, ImageView, CheckBox 등 다양한 종류가 있습니다.
* 뷰를 생성할 때는 XML로 기술하는 방법과 코드로 기술하는 방법이 있습니다. XML로 기술하는 것이 코드 양도 적고 읽기에도 편해 유지 및 관리에 유리합니다.

### 크기
* 뷰의 크기는 3가지 방법으로 지정할 수 있습니다. 최종적인 크기는 여백의 마진도 포함해 해당 뷰가 속한 부모 레이아웃에 따라 결정됩니다. 
* 여기서 지정한 내용을 바탕으로 부모 레이아웃의 크기를 결정합니다. 화면 밀도에 따른 dp와 px의 비율도 아래 표에 정리했습니다.

|크기 지정|내용|
|-------|---|
|wrap_content|뷰를 표시하기 위한 크기|
|match_parent|부모 뷰와 같은 크기|
|수치 지정|지정한 수치와 같은 크기|

|밀도|dp|px|
|---|--|--|
|ldpi|1dp|0.75px|
|mdpi|1dp|1px|
|hdpi|1dp|1.5px|
|xhdpi|1dp|2px|
|xxhdpi|1dp|3px|
|xxxhdpi|1dp|4px|

### 패딩과 마진
* 패딩으로 지정한 간격은 배경색으로 칠해지고, 마진으로 지정된 간격은 공백이 됩니다. 패딩은 뷰크기에 포함되지만 마진은 포함되지 않습니다.
* 마진은 뷰 요소 사이의 거리를 나타낸다고 생각하면 이해하기 쉬울 것입니다.

## 레이아웃을 이해하자
* 레이아웃은 뷰를 어떤 위치에 어떤 크기로 표시할지 결정하는 것입니다. 여기서는 대표적인 LinearLayout을 소개합니다.

### LinearLayout
* LinearLayout은 사용하기 쉬운 레이아웃입니다. LinearLayout에서 자식 뷰의 크기를 지정하는 방법을 예로 들어보겠습니다.
* 여기서는 LinearLayout의 orientation이 horizontal(수평 방향)으로 돼 있음을 전제로 설명합니다. vertical인 경우에는 세로 방향이 되므로 layout_width 대신 layout_height로 바꿔 읽어줘야합니다. 
* LinearLayout의 자식 뷰는 layout_width와 layout_weight 양쪽을 이용해 폭을 결정합니다. 
    * layout_width를 wrap_content로 지정
    * layout_width를 wrap_content, layout_weight를 각각 1:1로 지정
    * layout_weight를 1:1이 되도록 지정(layout_width는 0dp로 지정)
* layout_weight를 사용하면 여백 문제를 해결할 수 있습니다.

## 커스텀 뷰를 만들자
* 3개의 별 아이콘을 전환할 수 있는 커스텀 뷰를 예로 듭니다. 선택된 별에 노란색이 칠해져 표시됩니다.
* 기존 뷰를 조합한 커스텀 뷰 만들기는 아래 4단계로 진행됩니다.
    * 커스텀 뷰의 레이아웃을 결정한다.
    * 레이아웃 XML로 설정할 수 있는 항목을 attrs.xml에 기재한다.
    * 커스텀 뷰 클래스를 만든다.
    * 메인 앱의 레이아웃에 삽입해서 확인한다.

### 1. 커스텀 뷰의 레이아웃을 결정한다.
* 가장 먼저 레이아웃부터 만들어 봅시다. 이번에는 별을 3개 표시하고 싶으니 ImageView를 3개 이용합니다. 또한 ImageView를 옆으로 나열할 것이므로 LinearLayout으로 에워 쌉니다.
* 옆으로 나열하므로 orientation을 horizontal로 지정했습니다. 각 ImageView에서 지정한 drawable에서 star.png는 색이 칠해진 별 이미지고, star_empty.png는 테두리만 있는 별 이미지입니다.
* 기본으로 처음 star1의 id를 가진 ImageView가 색이 칠해진 별 이미지로 표시되게 했습니다.
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<merge xmls:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal">
    <ImageView
        android:id="@+id/star1"
        android:src="@drawable/star"
        android:layout_margin="4dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <ImageView
        android:id="@+id/star2"
        android:src="@drawable/star_empty"
        android:layout_margin="4dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
    
    <ImageView
        android:id="@+id/star3"
        android:src="@drawable/star_empty"
        android:layout_margin="4dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
</merge>
~~~
* 여기서 한 가지 주의할 점이 있습니다. 맨 처음 루트 태그가 LinearLayout이 아니라 merge 태그라는 점입니다. 이렇게 한 이유는 커스텀 뷰가 LinearLayout을 상속한 클래스이므로 LinearLayout의 불필요한 중첩을 피하기 위해서입니다. 

### 2. 레이아웃 XML로 설정할 수 있는 항목을 attrs.xml에 기재합니다.
* 커스텀 뷰의 XML로 설정을 변경할 수 있게 준비합니다. XML로 몇 번째 별이 선택됐는지 설정할 수 있게 selected 속성을 추가했습니다. format은 형 정보로, 이번에는 수치인 integer가 됩니다.
~~~xml
(attrs.xml)
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="MyCustomView">
        <attr name="se;ected" format="integer"/>
    </declare-styleable>
</resources>
~~~

### 3. 커스텀 뷰 클래스를 만든다.
* 커스텀 뷰를 만들 때는 View를 상속할 필요가 있습니다. 조금 전에 설명한 대로, 이번 예제에서는 LinearLayout을 상속합니다.
* 염두에 둘 것은 3가지입니다. 1. 레이아웃 XML 2. 스타일 반영 3. 외부 클래스로서, 예를 들어 액티비티로 조작할 수 있게 공개 메서드를 구현합니다.
* 레이아웃을 전개하는 데는 LayoutInflate.inflate()를 아용합니다.
~~~kotlin
LayoutInflate.inflate(resource: Int, root: ViewGroup)
~~~
* 두 번째 인수인 root에서는 부모가 될 ViewGroup을 지정합니다. 이번에는 자기 자신(this)을 부모로 설정합니다. 
* inflate 시점에서 조금 전 레이아웃의 XML에서 기술한 내용이 이 LinearLayout에 추가됩니다. 선두에 merge 태그를 이용했으므로 자신의 LinearLayout에 합쳐집니다.
~~~kotlin
class MyCustomView: LinearLayout {
    private lateinit var mStar1: ImageView
    private lateinit var mStar2: ImageView
    private lateinit var mStar3: ImageView
    private var mSelected: Int = 0

    ~ 생략 ~

    fun MyCustomView(context: Context, attrs: AttributeSet) {
        super.(context, attrs)
        initializeViews(context, attrs)
    }

    /**
    * 레이아웃 초기화
    */
    private fun initializeViews(context: Context, attrs: AttributeSet) {
        var inflater: LayoutInflater = context.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // 레이아웃 전개
        inflater.inflate(R.layout.three_stars_indicator, this)
        if(attrs != null) {
            // attrs.xml에 정의한 스타일을 가져온다.
            var a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCustomView)
            mSelected = a.getInteger(0, 0)
            a.recycle() // 이용이 끝났으면 recycle()을 호출합니다.
        }
    }

    /**
    * inflate가 완료되는 시점에서 콜백된다.
    */
    override fun onFinishInflate(){
        super.onFinishInflate()
        mStar1 = findViewById(R.id.star1) as ImageView
        mStar2 = findViewById(R.id.star2) as ImageView
        mStar3 = findViewById(R.id.star3) as ImageView
        // 처음에만 XML의 지정을 반영하고자 2번째 인수인 force를 true로 한다.
        setSelected(mSelected, true)
    }

    /**
    * 지정된 번호로 선택한다.(내부용)
    *
    * @param select: 지정할 번호(0이 가장 왼쪽)
    * @param force: 지정을 강제로 반영한다.
    */

    private fun setSelected(select: Int, force: Boolean) {
        ~ 생략 ~
    }

    // 외부 클래스에서 이용할 수 있는 공개 메서드 구현
    /**
    * 지정된 번호로 선택한다
    * 
    * @param select: 지정할 번호(0이 가장 왼쪽)
    */
    fun setSelected(select: Int) {
        ~ 생략 ~
    }
}
~~~

### 4. 메인 앱의 레이아웃에 삽입해서 확인한다.
* 작성한 커스텀 뷰를 삽입하려면 <패키지명.클래스명> 태그를 삽입합니다. 이번에는 attrs.xml에서 정의한 몇 번째 별을 선택하는지 설정할 수 있게 selected 속성도 사용하겠습니다.
* app:selected가 되고 이름공간이 부여돼 있는데, attrs.xml에서 지정한 정의를 이용하기 위해 필요합니다. app이라는 이름공간은 xmls:app="http://schemas.android.com/apk/res-auto"가 됩니다.
* 이 이름공간을 이용하면 자동으로 attrs.xml에서 정의한 내용을 연결할 수 있습니다.

~~~xml
<RelativeLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"
    ~ 생략 ~ >
    <com.advanced_android.compositecustomviewsample.MyCustomView
        android:id="@+id/indicator" app:selected="1"
        android:layout_width="wrap_content" android:layout_height="wrap_content" />
    ~ 생략 ~
</RelativeLayout>
~~~

* 다음으로 버튼을 눌렀을 때 선택한 별을 바꾸는 부분을 살펴봅니다. 처음에 findViewById()를 호출해 커스텀 뷰의 인스턴스를 가져옵니다.
* 인스턴스를 가져올 수 있으면 나머지는 커스텀 뷰에서 정의된 public 메서드를 호출할 수 있습니다. 버튼의 onClick() 안에서 MyCustomView.setSelected()로 선택된 별을 변경합니다.

~~~kotlin
val indicator: MyCustomView = findViewById(R.id.indicator) as MyCustomView
findViewById(R.id.button).setOnClickListener{ v -> 
    var selected: Int = indicator.getSelected()
    if(selected == 2) {
        selected = 0
    } else {
        selected++
    }
    indicator.setSelected(selected)
}
~~~