# Предупреждение
Перед началом хочу отметить некоторые важные нюансы:
+ Приложение создавалось мною в качестве дипломного проекта для площадки Skillbox
 + Приложение было законченно ещё в середине 2022 года. Так что не исключаю возможности встретить ошибки новичка, странный или плохой код, а так же некоторые недочёты в интерфейсе, eстественно это не oзначает что я допустил бы те же ошибки создавая подобное приложение сейчас

___

# Описание
<img src="screenshots/1.png" width="220" height="483"><img src="screenshots/2.png" width="220" height="483"><img src="screenshots/3.png" width="220" height="483">

Приложение основанно на API площадки Reddit. Присутсвует продвинутое использование таких компонентов как:
+ RecyclerView с пагинацией
+ exoplayer с Gilde
+ MVVM с LiveData
+ Retrofit + OKHTTP
+ протокол OAuth 2.0
+ JSON parsing
+ Room DB
+ Android Navigation components

Приложение поддерживает русский и английский языки, темную тему, а так же имеет при себе ряд полезных функций таких как: просмотр фото и видео на весь экран, поиск интересующих постов и т.д.
___
# RecyclerView & Paging3

Связка recycler view и библиотеки Paging 3 даёт возможность бесконечно листать ленту реддита, делиться и сохранять посты, оставлять и лайкать комментарии.
___

# Exoplayer & Glide

С помощью таких библиотек как exoplayer и glide пользователь может потреблять контент в высоком качестве

___

# Архитектура MVVM

С архитектурным подходом MVVM и использованием ViewModel вместе с LiveData, приложение сохраняет состояние экранов. Данный подход рекомендуется использовать в современной разработке под Android

___ 

# Reteofit + OTHTTP

Так как приложение полностью основанно на API реддита, тут важно грамотно описать взаимодействие с сервером. В данном случае я использую Retrofit вместе с OKHTTP. В коде демонстрируется парсинг приходящего JSON ответа как вручную, так и с помощью автоматического парсинга через  data классы.

___

# Room Database

В приложении так же присутствует взаимодействие с локальной базой данных. Можно сохранять посты и комментарии чтобы при отсутствии интернета была возможность их просмотреть

___

# OAuth 2.0
Для входа в приложение через аккаунт Reddit используется ключ авторизации получаемый через защищённый протокол OAuth 2.0. Этот протокол обеспечивает безопасное получение ключа авторизации через WebView

___

# Navigation Components
Вся навигация и передача аргументов в приложении происходит через данную библиотеку. Так же эта библиотека даёт возможность передавать Parcelable классы (что очень удобно) и я этим активно пользуюсь.


