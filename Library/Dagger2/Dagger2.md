# Dagger2

## Dagger2의 기본 개념
* Dagger는 5가지의 필수 개념이 있습니다.
1. Inject
2. Component
3. Subcomponent
4. Module
5. Scope

### Inject
* 의존성 주입을 요청합니다. Inject 어노테이션으로 주입을 요청하면 연결된 Component가 Module로부터 객체를 생성하여 넘겨줍니다.

### Component
* 연결된 Module을 이용하여 의존성 객체를 생성하고, Inject로 요청받은 인스턴스에 생성한 객체를 주입합니다.
* 의존성을 요청받고 주입하는 Dagger의 주된 역할을 합니다.

### Subcomponent
* Component는 계층관계를 만들 수 있습니다.
* Subcomponenet는 Inner Class 방식의 하위계층 Component입니다. Sub의 Sub도 가능합니다.
* Subcomponenet는 Dagger의 중요한 컨셉인 그래프를 형성합니다.
* Inject로 주입을 요청받으면 Subcomponent에서 먼저 의존성을 검색하고, 없으면 부모로 올라가면서 검색합니다.

### Module
* Componenet에 연결되어 의존성 객체를 생성합니다. 생성 후 Scope에 따라 관리도 합니다.

### Scope
* 생성된 객체의 Lifecycle 범위입니다. 안드로이드에서는 주로 PerActivity, PerFragment 등으로 화면의 생명주기와 맞추어 사용합니다.
* Module에서 Scope를 보고 객체를 관리합니다.

![DaggerFlow1](https://cdn-images-1.medium.com/max/1600/1*4HuI1KMicC5noqyBpkK-rw.png)
![DaggerFlow2](https://cdn-images-1.medium.com/max/1600/1*JK7yveoSwhlA8Lk_w8RrXQ.png)

## Dagger를 사용하기 전
### 햄버거 만들기

* 햄버거(Burger)를 만듭니다.
* Burger는 WheatBun과 BeefPatty로 이루어져 있습니다.
~~~kotlin
data class Burger(
    var bun: WheatBun,
    var patty: BeefPatty
) 
~~~

* WheatBun과 BeefPatty를 준비합니다.
* WheatBun
~~~kotlin
class WheatBun {
    fun getBun(): String = "밀빵"
}
~~~

* BeefPatty 
~~~kotlin
class BeefPatty {
    fun getPatty(): String = "소고기 패티"
}
~~~

* 이렇게 재료를 준비한 후 MainActivity에서 햄버거는 어떻게 만들까요?
~~~kotlin
class MainActivity: AppCompatActivity() {

    lateinit burger: Burger

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bun = WheatBun()
        var patty = BeefPatty()

        burger = Burger(bun, patty)
    }
}
~~~

* 이렇게 WheatBun과 BeefPatty를 가져와서 햄버거를 만듭니다.
* 이렇게 MainActivity에서 인스턴스를 생성하는게 기존의 방식이었습니다. 
* 여기에 의존성 주입을 사용하면 new 오퍼레이터 사용없이 외부에서 자동으로 객체를 생성해 줍니다.

## Dagger2 적용하자
### 0.App 버전의 build.gradle에 라이브러리 추가하기

~~~gradle
implementation "com.google.dagger:dagger:$daggerVersion"
implementation "com.google.dagger:dagger-android:$daggerVersion"
implementation "com.google.dagger:dagger-android-support:$daggerVersion"

kapt "com.google.dagger:dagger-android-processor:$daggerVersion"
kapt "com.google.dagger:dagger-compiler:$daggerVersion"
~~~

### 1. Module과 Component를 만들어야 합니다.
* Module은 필요한 객체를 제공하는 역할을 합니다.
* 아까 MainActivity에서 이렇게 객체를 생성했습니다.
~~~kotlin
var bun = WheatBun()
var patty = BeefPatty()

burger = Burger(bun, patty)
~~~

* 이걸 Module이 처리해줍니다.
~~~kotlin
@Module
class BurgerModule {
    @Provides
    fun provideBurger(bun: WheatBun, patty: BeefPatty) = Burger(bun, patty)

    @Provides
    fun provideBun() = WheatBun()

    @Provides
    fun providePatty() = BeefPatty()
}
~~~
* 생성할 객체를 @Providers 어노테이션을 사용해서 생성해 줍니다.

* Component는 모듈에서 제공받은 객체를 조합하여 필요한 곳에 주입하는 역할을 합니다.

~~~kotlin
@Component(modules = BurgerModule.class)
interface BurgerComponent {
    fun inject(activity: MainActivity)
}
~~~
* @Component 어노테이션을 통해 BurgerModule.class를 가져왔습니다. 
* 이를 inject() 함수를 통해 MainActivity에 주입했습니다.

~~~kotlin
class MainActivity: AppCompatActivity() {
    @Inject
    lateinit burger: Burger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val component: BurgerComponent = DaggerBurgerComponent.builder().burgerModule(BurgerModule()).buold()

        component.inject(this)

        Log.d("MyTag","burger bun : " + burger.bun.getBun() + ", patty : " + burger.patty.getPatty())
    }
}
~~~

* 위와 같은 코드로 burger 객체를 외부에서 생성하여 가져올 수 있습니다.

* 간단하게 이렇게 생각하면 됩니다.
    * Module: 공급하는 친구
    * Inject: 주입받는 친구
    * Component: 사이를 연결해주는 친구

![관계](https://black-jin0427.tistory.com/104)

## 모듈이 두개???
### 요리 주문을 받아 만들기
#### 1.요리를 만들기 위해 셰프와 주방이 필요합니다.
~~~kotlin
data class Chef (
    var firstName: String, 
    var lastName: String) {
        override fun toString(): String
            = "Chef [firstName= $firstName, lastName= $lastName]"
}
~~~

~~~kotlin
data class Kitchen (
    var chef: Check, 
    var order: String
) {
    fun isOrder(): Boolean {
        if(chef != null && order != null && order.length() > 0) {
            return true
        }
        return false
    }
}
~~~
* 주방에서는 주문을 받았는지 여부를 파악하여 Boolean 값을 리턴하는 함수인 isOrder()이 있습니다.

#### 모듈을 만듭니다.
~~~kotlin
@Module
class ChefModule {
    @Provides
    fun provideChef(): Chef {
        return Chef("Black", "Jin")
    }
}
~~~
