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

## Dagger 사용하기
* Application Component
~~~kotlin

~~~