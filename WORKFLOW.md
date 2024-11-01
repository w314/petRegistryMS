# Monolith -> Microservices

## Separate Services

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
