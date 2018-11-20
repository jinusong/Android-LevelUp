# MVPExample
* 사용자가 입력한 문자를 저장하고 삭제할 수 있는 하나의 액티비티로 구성된 간단한 노트 예제이다.
![good](https://t1.daumcdn.net/cfile/tistory/99DA303359D5CAB935)

## 앱의 흐름
1. View에서 사용자가 저장할 텍스트를 입력하고 추가 버튼을 누른다.
2. Presenter에서 사용자가 입력한 텍스트를 바탕으로 새로운 Note객체를 생성하고 
Model에 Database로 저장하도록 요청한다. 
3. Model에서 Database로 저장을 완료하면 Presenter에 Note의 리스트가 변경되었음을 알린다.
4. Presenter에서 View로 리스트를 갱신하고 새로생긴 Note 아이템을 보여주도록 요청한다.

![flow](https://t1.daumcdn.net/cfile/tistory/99E63D3359D5D2B722)
* 다양한 객체를 느슨하게 결합(loosely coupled)하기 위해서 
인터페이스를 사용하고, MVP 레이어간 통신이 이루어진다.

1. ProvidedPresenterOps
    *View와 통신을 위해서 Presenter에서 제공된 작업이다.
(Presenter에서 실제 동작이 구현되어 있다.)

    * ex) 사용자가 텍스트를 입력하고 버튼을 눌러서 새로운 노트를 등록하려고 한다.
    * View에서 새로운 노트를 생성하는 동작을 호출하고 Presenter에서 동작한다.

2. ProvidedModelOps
    * Presenter와 통신을 위해서 Model에서 제공되는 작업이다.
(Model에서 실제 동작이 구현되어 있다.)

    * ex) View를 통해서 새로운 노트를 생성하라는 명령을 받으면 Presenter는
Database에 등록하기 위한 동작을 호출하고 Model에서 동작한다.

3. RequiredPresenterOps
    * Model에 필요한 Presenter에서 제공되는 작업이다.
(Presenter에서 실제 동작이 구현되어 있다.)

    * ex) Model에서 Database에 데이터를 저장하기 위해서 Context가 필요한데
Presenter를 통해서 제공받는다.

4. RequiredViewOps
    * Presenter에 필요한 View에서 제공되는 작업이다.
(View에서 실제 동작이 구현되어 있다.)

    * ex) Presenter에서 Database에 저장된 리스트가 변경되었음을 
View에 알리기 위해 동작을 호출하고 View에서 동작한다.

* loosely coupled :
컴퓨터 시스템에서 각 구성 요소가 다른 개별 구성 요소의 정의를 거의 또는 전혀 모르는 경우
