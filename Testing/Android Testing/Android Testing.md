# Android Testing
* 안드로이드 앱 개발에서 테스트는 매우 중요합니다.
* 이 문서에서는 테스트에 대한 간단한 개요가 담겨있습니다.

## 테스트의 필요성
* 테스트는 개발할 때 생각의 전환점이 되기도 하고 암묵적으로는 코드를 더 깔끔하게 생산할 수 있게 합니다.
* 테스트를 거치면 개발자는 코드에 자신감을 갖습니다.
* 자동화 테스트에서 버그를 먼저 잡아내기에 리그레이션 테스트를 쉽게 할 수 있습니다. 코드를 리팩토링하고 다시 테스트틀 패스할 수 있는 것입니다.

## 안드로이드 테스트의 종류

### Unit 테스트
* 일반적으로 코드의 유닛 단위(메소드, 클래스, 컴포넌트)의 기능을 실행하는 방식
    * 관련툴: JUnit, Mockito,PowerMock
### UI 테스트
* 사용자 인터랙션(버튼 클릭, 테스트 입력 등)을 평가
    * 관련 툴:  Espresso, UIAutomator, Robotium, Appium, Calabash, Robolectric 

### 자동화 테스트(automated testing)을 하기 위해 필요한 것
* 특정 패턴의 아키텍쳐를 따라함
* 예를 들면 뷰를 위해 MVP패턴을, 네트워킹과 데이터 접근을 위해 Repository패턴을 구현해 테스트할 수 있는 구조를 갖추는 것입니다.
* 테스트 가능한 방식으로 앱 구조 갖추기 아래 그림고 ㅏ같은 구조를 기반으로 이후 시리즈에서 테스트을 설명합니다.
![아키텍쳐 모장](images.ctfassets.net/s72atsk5w5jo/2B1bUGZJk0848IuqQ4aIAU/e5281d78a1f2caeaf47af2341456dd26/android_weekly212.png?fm=jpg&fl=progressive')