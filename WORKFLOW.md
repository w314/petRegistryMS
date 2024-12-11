# Monolith -> Microservices Step by Step

## STEP-1: Separate Services

- Created 3 services
    - petService: to list pets
    - ownerService: to list owners
    - registryService: to list owner-pet relations
- Set up separate databases for each service
    - removed any foreign keys
- used `RestTemplate` to call one service from the other


### Setting up Service Connections

In ownerService an owner profile should give us a list of its pets with all the pet details.

#### 1. set up service url information
In `application.properties` added URL information of services needed to provide owner information.
```bash
registryUri=http://localhost:8400
petUri=http://localhost:8100
```

#### 2. call microservices to get information needed

`OwnerController.java`
```java
@RestController
@RequestMapping
public class OwnerController {

    @Autowired
    OwnerService ownerService;

    // get uri-s of other microservices needed
    @Value("${registryUri}")
    String registryUri;
    @Value("${petUri}")
    String petUri;

    @GetMapping("/owners")
    public ResponseEntity<List<OwnerDTO>> getOwners() {
        List<OwnerDTO> ownerDTOS = this.ownerService.getOwners();
        return new ResponseEntity<>(ownerDTOS, HttpStatus.OK);
    }

    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<OwnerDTO> getOwner(@PathVariable int ownerId) {
    	// get owner from service
    	// this will include an empty list of petDTOs
        OwnerDTO ownerDTO = this.ownerService.getOwnerById(ownerId);
        // define registry microservice url needed to get list of pet ids
        String registryUrl = registryUri + "/owners/" + ownerDTO.getId() + "/pets";
        // get the ids of pets belonging to the owner
        List<Integer> petIds = new RestTemplate().getForEntity(registryUrl, List.class).getBody();
        // create empty petDTO array to store pet information
        List<PetDTO> petDTOS = new ArrayList<>();
        // get details for each pet that belongs to the owner
        for(Integer petId: petIds) {
        	// define pet microservice url for getting pet details
        	String petUrl = petUri + "/pets/" + petId;
            PetDTO petDTO = new RestTemplate().getForObject(petUrl,PetDTO.class);
            petDTOS.add(petDTO);
        }
        // use the information received from the pet microservice 
        // to set the pets property of the owner
        ownerDTO.setPets(petDTOS);
        return new ResponseEntity<>(ownerDTO, HttpStatus.OK);
    }
}
```

## STEP-2: Setup Distributed Configuration

### Install `consul`
- install from: [consul](https://developer.hashicorp.com/consul)
- add to Path
- restart gitbash

Start consul with:
```bash
consul agent -server -bootstrap-expect=1 -data-dir=consul-data2 -ui -bind=192.168.254.79
```
- `-server` starts it as a server (not a client)
- `-bootstrap-expect-1` set it up to create one instance (not a cluster)
- `-data-dir` specifies the folder consul stores the config data
  - it will be created in the same folder where consul is
  - if does not exists consul will create it
  - if exists consul will use it the config data from here to start the consul server
- `-ui` sets consul up to run in a web ui mode
- `-bind` specifies the IP address of the host where the consule server should start (find out ip address with `ipconfig`)

To see consul running open browser to: `localhost:8500/ui`.

### Store Configuration Details in consul

#### store common configuration
- go to consul ui (`localhost:8500/ui`)
- select `Key/Value` menu item (on left side panel)
- click `Create` button in top right corner
- enter file name in `Key or Folder` input area
- `config/application/data`
  - by default configuration data is stored in the `config` folder
  - `application` folder will store all common properties
  - `data` will be the name of the file we store the configuration data 

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

#### store microservice specific configuration
Create new `Key/Value` a file.
- `config/ownerMS/data`
- create new folder in the `config` folder
- new folder's name has to match the application name

`config/ownerMS/data`
```yml
# server config
server:
  port: 8200
# db config
spring:
  datasource:
    url: jdbc:mysql://localhost/owner

# config to store url of other microservices
registryUri: http://localhost:8400
petUri: http://localhost:8100
```

`config/petMS/data`
```yml
# server config
server:
  port: 8100
# db config
spring:
  datasource:
    url: jdbc:mysql://localhost/pet
```

`config/registryMS/data`
```yml
# server config
server:
  port: 8400
# db config
spring:
  datasource:
    url: jdbc:mysql://localhost/registry
```

### Configure Microservices to work with Consul Server

Do the following steps in each microservice:

#### 1. add dependencies

`pom.xml`
```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-config</artifactId>
        </dependency>
```

#### 2. import configuration data

In `application.properties` add:
```bash
spring.config.import=optional:consul:
```
- will import configuration from the consul server
- removing the  `optional` prefix  will make consul config to fail if it cannot connect to the consul server
- will connect to default `Consul Agent` at `http://localhost:8500`
- to set host and port use syntax: `spring.config.import=optional:consul:myhost:8500` 

Remove properties moved to `Consul Server` from the  `application.properties` file.

####  3. configure consul server

Create an `src/main/resources/application.yml` file:
```yml
spring:
  cloud:
    # configuring Cloud Config
    consul:
      config:
        # true enables Cloud Config
        enabled: true
        # sets the base folder for configuration
        # config is the default name
        prefixes: config
        # sets the folder name used by all applications
        defaultContext: application
        # set the value of the separator
        # used to separate the profile name
        # in property sources with profiles
        profileSeparator: '::'
        # name of file config for specific microservice
        # is stored in the microsercice's name space
        # default is `data`
        data-key: data
        # configures consul server to resend config data
        # to the microservice at every 100ms
        watch:
          delay: 100
        # IMPORTANT will not work without setting format
        format: YAML
```

## STEP-3: Setup Service Discovery

### 1. Add Dependency 

`pom.xml`
```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>	
```
- add to all microservices
- will register microservice with the Consul Server

### 2. Modify Microservice Configuration

Remove urls of other microservices from the configuration file of the ownerMS microservice

### 3. Modify Microservice Controller

- use `DiscoveryClient`
- to get microservice urls from consul server

In `OwnerController
.java`:
```java
// import DiscoveryClient
import org.springframework.cloud.client.discovery.DiscoveryClient;
// other imports

@RestController
@RequestMapping
public class OwnerController {

    @Autowired
    OwnerService ownerService;
    
    // autowire DiscoveryClient
    @Autowired
    private DiscoveryClient client;

    // define variables to store microservice urls
    private String registryUri;
    private String petUri ;

    @GetMapping("/owners")
    public ResponseEntity<List<OwnerDTO>> getOwners() {
        List<OwnerDTO> ownerDTOS = this.ownerService.getOwners();
        return new ResponseEntity<>(ownerDTOS, HttpStatus.OK);
    }

    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<OwnerDTO> getOwner(@PathVariable int ownerId) {
    	
    	// get owner from service
    	// this will include an empty list of petDTOs
        OwnerDTO ownerDTO = this.ownerService.getOwnerById(ownerId);
        System.out.println(ownerDTO);
        
        // use service discovery to get microservice urls
        // get registryMS microservice urls from service discovery
        // use microservice name
        // returns list of serviceIntance (can be several instances of the MS running)
        List<ServiceInstance> listOfRegistryInstances = client.getInstances("registryMS");
        // check if there is at least one instance returned
        if(listOfRegistryInstances != null && !listOfRegistryInstances.isEmpty()) {
        	// get microservice uri from the first instance
        	// will contain host and port number
        	registryUri = listOfRegistryInstances.get(0).getUri().toString();
        	System.out.println("REGISTRY URL: " + registryUri);
        }
        
        
        // define registry microservice url needed to get list of pet ids
        String registryUrl = registryUri + "/owners/" + ownerDTO.getId() + "/pets";
        // get the ids of pets belonging to the owner
        List<Integer> petIds = new RestTemplate().getForEntity(registryUrl, List.class).getBody();
        
        // get url for petMS the same way
    }
}
```


## STEP-4: Implement Dynamic Load Balancing

### Update Configuration in Consul

UPDATE APPLICATION CONFIGURATION

`config/application/data`
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
  # need unique instance id for consul to register 2 services
  cloud:
    consul:
      discovery:
        instanceId: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
```

UPDATE MICROSERVICE CONFIGURATION

Remove port config from the configuration in the microservice, example:

`config/petMS/data`
```yml
# db config
spring:
  datasource:
    url: jdbc:mysql://localhost/pet
```

### Update ownerMS

#### Add dependency

`pom.xml`
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

#### Create Load Balanced RestTemplate

`src/main/java/controller/OwnerConfig.java`
```java
// needs the @Configuration annotation
@Configuration
public class OwnerConfig {
	
	@Bean
	// @LoadBalanced will return a load balanced rest template to use
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
```

#### Update Controller Class

`src/main/java/controller/OwnerMS.java`
```java
public class CustomerController {

    // autowire load balanced RestTemplate
    @Autowire
    RestTemplate template;

    // remove variable to store petUri
    // private String petUri ;


    // remove getting uli-s from service registry
    /*
        // get petMS microservice urls from service registry
       List<ServiceInstance> listOfPetInstances = client.getInstances("petMS");
       if(listOfPetInstances != null && !listOfPetInstances.isEmpty()) {
       	petUri = listOfPetInstances.get(0).getUri().toString();
      }
    */

    // WHEN CONTACTING THE PET MICROSERCIE
    // remove
    // define pet microservice url for getting pet details
    // String petUrl = petUri + "/pets/" + petId;
    // instead of using
    // PetDTO petDTO = new RestTemplate().getForObject(petUrl,PetDTO.class);
    
    // use load balanced template when making API calls to other microservices
    // add MS name and form url as needed
    PetDTO petDTO = template.getForObject("http://petMS/pets/"+petId,PetDTO.class);
```

## STEP-5 Implement Resiliency

Resiliency will stop our MS to continously trying to connect an other MS that is down.


### 5-1 Add dependencies

`pom.xml` in OwnerMS
```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```




