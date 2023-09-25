# 프로젝트 소개:  ``UsedBookMarket`` :books:

> 자신이 팔고 싶은 중고 도서를 올리고 판매할 수 있고, <br/>
> 원하는 책을 사용자로부터 구입할 수 있습니다.
> 거래 후 거래 만족도를 평가할 수 있습니다.


![](../header.png)

# 미리보기

> 인트로
<img src="https://github.com/HojinLim/UsedBookMarket/assets/69897998/5bd965f3-a965-41ff-ae18-2ecf1e51d455" width="900" height="550">

* * *

> 네이버 쇼핑 목록에 있는 책 정보를 가져와서 글 등록
<img src="https://github.com/HojinLim/UsedBookMarket/assets/69897998/c212801e-fc58-421f-b469-85aefe59ec2b" width="900" height="550">

* * *

> 거래 글 '좋아요' 누를 시 찜 목록 추가.
<img src="https://github.com/HojinLim/UsedBookMarket/assets/69897998/7f7beeb7-45ee-4b20-9fc8-04ce9a06ed9f" width="900" height="550">

* * *

> 메인 페이지가 Fragment로 화면 전환되게끔 구성
<img src="https://github.com/HojinLim/UsedBookMarket/assets/69897998/6f386ea0-4a2a-49f9-8a9a-8507d768a750" width="900" height="550">

* * *

> 글 쓰기 유효성 검사
<img src="https://github.com/HojinLim/UsedBookMarket/assets/69897998/5102211e-f73f-4cd9-866d-bb2c45f3d12f" width="900" height="550">

* * *

> 상대방과 채팅으로 거래 진행, 그 후에 평가
<img src="https://github.com/HojinLim/UsedBookMarket/assets/69897998/0acff949-78fd-49f9-b381-b7c50c22c483" width="900" height="550">

* * *

> 검색 기록 저장
<img src="https://github.com/HojinLim/UsedBookMarket/assets/69897998/3ad6daa1-4099-44cc-926e-db661788e28f" width="900" height="550">


## 주요 파일

* MainActivity.kt -> Fragment 변경 리스너, 로그인 후 알림 팝업
* SalesArticleFormActivity.kt -> 새로운 글 작성 코드
* ReviewActivity.kt -> 리뷰 작성 후 DB에 저장
* HistoryDao.kt, AppDatabase.kt, History.kt -> 검색 기록 저장 코드
* BookAPI-> 네이버 데이터 통신 코드
* HomeFragment.kt-> 현재 존재하는 글들을 리스트해서 보여줍니다.
* ChatFragment.kt-> 자신의 계정으로 채팅한 이력을 리스트해서 보여줍니다.
* AccountFragment-> 계정 정보, 찜 목록, 회원 등을 관리하는 화면입니다.


## 트러블 슈팅

_firebase 로그인 시 로그인이 안되고, 권한이 없다고 뜸_

- 검색을 해봐도 해결을 못하고 시간을 할애하였다.
- 결국엔 에뮬레이터와 sdk버전의 문제였다.
- 해결: 에뮬레이터의 버전을 변경 후 재 실행하니까 해결되었다.

## 아쉬운 점

1. FirebaseMessageService로 실시간 푸시 알림을 구현하려 했으나, <br/> 
구현 과정에서 오류는 뜨지 않았지만 원하는 결과가 나오지 않아서 <br />
많은 시간이 지체되어서 결국 구현을 하지 못한 점이 아쉬움이 남았다. <br />
2. 처리되지 않은 주석 코드와 반복되는 코드 (특히, ``recyclerview``)가 존재한다.




