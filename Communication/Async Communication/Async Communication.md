# Async Communication
~~~kotlin
class Async: AsyncTask<FirstType, SecondType, ThirdType>{
    override fun onPreExecute() {
        suprt.onPreExecute()
    }

    override fun doInBackground(params: FirstType): ThirdType {
        return result
    }

    override fun onPostExecute(result: ThirdType){
        super.onPostExecute(s)
    }

    override fun onCancelled(result: String){
        super.onCancelled(result)
    }

    override fun onProgressUpdate(values: SecondType) {
        super.onProgressUpdate(values)
    }
}
~~~
## 부모클래스의 제네릭
* 제네릭 FirstType은 doInBackground의 매개변수의 자료형이다.
* 제네릭 SecondType은 onProgressUpdate의 매개변수의 자료형이다.
* 제네릭 ThirdType은 doInBackground의 반환형이면서 onPostExecute의 매개변수의 자료형이다.
## 함수 역할
### onPreExecute()
* 작업이 실행되기 전에 UI 스레드에서 호출된다. 이 단계는 일반적으로 사용자 인터페이스에 진행률 표시줄을 표시하여 작업을 설정하는 데 사용된다.
### doInBackground(params: FirstType)
* onPreExecute()가 실행을 마친 직후 백그라운드 스레드에서 호출된다. 이 단계는 가장 핵심적인 작업을 수행하는 데 사용된다.
### onPostExecute(params: String)
* 백그라운드 작업(doInBackground())이 끝난 후에 UI 스레드에서 호출된 doInBackground()의 리턴 값을 매개변수로 받아 처리한다.
### onCancelled()
* doInBackground() 수행 중이나 메인스레드에서 cancel(true)를 호출하면 doInBackground() 수행 완료 후 onPostExecute()가 호출되는 대신 이 메서드가 호출된다.
### onProgressUpdate(values: SecondType)
* doInBackground() 수행 중 publishProgress()를 호출하면 호출되며, 주로 작업의 진행 상태을 프로그레스 바로 표시하기 위해 사용한다.
