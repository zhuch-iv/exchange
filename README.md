### Exchange app

[Open api spec](https://github.com/zhuch/exchange/blob/master/src/main/resources/openapi.yaml)

#### How to run project

Clone repo:
`git clone git@github.com:zhuch/exchange.git`

Enter to project directory:
`cd ./exchange`

Build project:
`./gradlew clean build`

Run :
`java -jar ./build/libs/exchange-0.1.0.jar`

Or run in **debug** mode :
`java -jar -Dspring.profiles.active="debug" ./build/libs/exchange-0.1.0.jar`

Build docker container:
`DOCKER_BUILDKIT=1 docker build -t exchange:0.1.0 .`

Run docker container:
`docker run --publish 8080:8080 exchange:0.1.0`


#### Set env options

Http client type:
- `export FETCH_STRATEGY=webflux` *(Default)*
- `export FETCH_STRATEGY=feign`
  
Your [giphy](https://developers.giphy.com/docs/api#quick-start-guide) api key from [here](https://developers.giphy.com/dashboard/)

`export GIPHY_API_KEY=<your api key>`

Your [open exchange rates](https://docs.openexchangerates.org/) app id from [here](https://openexchangerates.org/account):

`export OER_API_KEY=<your api key>`


#### Тестовое задание на собеседование

> Создать сервис, который обращается к сервису курсов валют, и отдает gif в ответ:
>
> если курс по отношению к рублю за сегодня стал выше вчерашнего, то отдаем рандомную отсюда https://giphy.com/search/rich   
> если ниже - отсюда https://giphy.com/search/broke   
> Ссылки
> REST API курсов валют - https://docs.openexchangerates.org/   
> REST API гифок - https://developers.giphy.com/docs/api#quick-start-guide   
> Must Have
> Сервис на Spring Boot 2 + Java / Kotlin
> Запросы приходят на HTTP endpoint, туда передается"
