

## To work with project in Spring Suite
- Close project
- open Spring Suite
- File 
    > Import Project 
    > Select Import Vizard: Maven / Existing Maven Project
    > Browse Select PetregistryMS folder
    > Select all subfolders and click Finish

- Update projects
    - right-click on porject > Maven > Update Project
    - select all projects to be udpated



## Current State

- PetMS should start and `localhost:8100/pets` should list all pets
- 


## Configuration

```xml
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-consul-config</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-consul-discovery</artifactId>
		</dependency>
```