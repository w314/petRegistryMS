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

#### 1. Set up service information
In `application.properties` added URL information of services needed to provide owner information.
```bash
registryUri=http://localhost:8400
petUri=http://localhost:8100
```


