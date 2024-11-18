# Instructions to use Pet Registry

## Current Status
- distribution configuraton is implemented
- ownerMS uses `DiscoveryClient` to discover registryMS service, without any load balancing
- ownerMS uses a load balanced `RestTemplate` and `consul` to dynamically balance several pet instances


## Start PetRegistry

### 1. start consul
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
# server config
server:
  # setting port to 0 
  # will make consul choose 
  # random port number
  port: 0
spring:
  # db config
  datasource:
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
  # unique instance id configuration
  # need unique instance id for consul 
  # to register more than 1 services
  cloud:
    consul:
      discovery:
        instanceId: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
```

For ownerMS: `config/ownerMS/data`
```yml
# db config
spring:
  datasource:
    url: jdbc:mysql://localhost/owner
```

For petMS: `config/petMS/data`
```yml
# db config
spring:
  datasource:
    url: jdbc:mysql://localhost/pet
```

For registryMS: `config/registryMS/data`
```yml
# db config
spring:
  datasource:
    url: jdbc:mysql://localhost/registry
```


### 2. open & start microservices in spring suite
- open Spring Suite
- File 
    > Import Project 
    > Select Import Vizard: Maven / Existing Maven Project
    > Browse Select PetregistryMS folder
    > Select all subfolders and click Finish

- Update projects
    - right-click on porject > Maven > Update Project
    - select all projects to be udpated

To start several instance of a microservice in Sprint Suite:
- right click microservice
- duplicate configuration
- run all services

Verify MS running properly:
- running `localhost:<owner-port>/owners/1` verifies all
- ownerMS:
  - `localhost:<port>/owners`
  - should work without any other ms working
- registryMS:
  - `localhost:<port>/owners/<owner_id>/pets` 
  - should work without any other ms working
- petMS
  - `localhost:<port>/pets
  - should work without any other ms working