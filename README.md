# Start PetRegistry

1. start consul
```bash
consul agent -server -bootstrap-expect=1 -data-dir=consul-data2 -ui -bind=192.168.254.79
```

if getting error
```bash
 startup error: error="refusing to rejoin cluster because server has been offline for more than the configured server_rejoin_age_max (168h0m0s) - consider wiping your data dir"
```
Delete data directory `consul-data2`, under `c:Users/piros`

To recreate key-value pairs create:

For Common Config:
`config/applciation/data`
```yml
spring:
  datasource:
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

For ownerMS:
`config/ownerMS/data`
```yml
# db config
spring:
  datasource:
    url: jdbc:mysql://localhost/owner
# server config
server:
  port: 8200
# config to store url of other microservices
registryUri: http://localhost:8400
petUri: http://localhost:8100
```

2. open & start microservices in spring suite
- open Spring Suite
- File 
    > Import Project 
    > Select Import Vizard: Maven / Existing Maven Project
    > Browse Select PetregistryMS folder
    > Select all subfolders and click Finish

- Update projects
    - right-click on porject > Maven > Update Project
    - select all projects to be udpated
