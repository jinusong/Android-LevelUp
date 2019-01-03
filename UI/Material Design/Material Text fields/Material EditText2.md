# Material EditText2
## Material EditText2 란?
![EditText2](https://storage.googleapis.com/spec-host-backup/mio-design%2Fassets%2F1DxDXQRkLemKRP1vsos0kJC-ET1ibXY2I%2Ftextfields-outline-states-enabled.png)
* 이런 Material Design 입니다. Outline 박스로 둘러 쌓여있는 EditText입니다.
* 저는 DMS V3 프로젝트를 진행하다가 발견했습니다.

## Setting
~~~gradle
implementation 'com.android.support:design:28.0.0'
~~~
* Design을 추가합니다.

## 사용하는 법
~~~xml
<android.support.design.widget.TextInputLayout
    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <android.support.design.widget.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="내용"/>
</android.support.design.widget.TextInputLayout>
~~~
* 이렇게 TextInputEditText를 TextInputLayout으로 감싸서 사용합니다.
* 그리고 style은 @style/Widget.MaterialComponents.TextInputLayout.OutlinedBox로 설정해서 간단하게 사용할 수 있습니다.
* 처음의 이미지에서의 Label은 EditText를 포커싱할 경우 hint가 올라가서 Label이 됩니다. ( hint == Label )

## 추가 정보
### 문제 해결
* 위의 사용하는 법을 사용하면 되긴 하는데 그 뒤의 뷰들이 사라지는 버그? 같은 것이 존재했습니다.
~~~xml
<LinearLayout
    android:layout_width="wrap_content"
    android:layout_height="match_parent">
    <android.support.design.widget.TextInputLayout
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <android.support.design.widget.TextInputEditText
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="내용"/>
    </android.support.design.widget.TextInputLayout>
</LinearLayout>
~~~
* 다음과 같이 LinearLayout으로 감싸서 사용하면 앞의 에러 사항을 해결할 수 있습니다.

### EditText Error 띄우기
* Material EditText2 에서는 자신이 원하는 정보가 담기지 않았을 경우 Error Message를 띄울 수 있습니다.
~~~xml
<android.support.design.widget.TextInputLayout
    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:errorEnabled="true"
    <android.support.design.widget.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="내용"/>
</android.support.design.widget.TextInputLayout>
~~~
* app:errorEnabled="true" 를 xml코드에 추가합니다.

~~~kotlin
fun flagError{
    TextInputEditText.error = "내용을 입력하세요."
}
~~~
* 그리고 TextInputEditText뒤에 error를 적고 Error Message를 Set합니다.

## 링크
* https://material.io/design/components/text-fields.html#spec
* 여기에 다양한 Textfield 들이 있습니다.