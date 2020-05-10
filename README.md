# ServerDataBase
The first step is build application.
`mvn clean install`
After build application you can run docker container and application.
`docker-compose up`

Если нужно грохнуть полностью, то в docker-compose берём путь volume и грохаем физически папку путь к которой указывает часть до :
