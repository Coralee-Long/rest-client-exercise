<!-- TOC -->
# Table of Contents
  * [1. Project Structure Overview](#1-project-structure-overview)
  * [2. Dependencies (pom.xml)](#2-dependencies-pomxml)
  * [3. Model Classes](#3-model-classes)
  * [4. API Service](#4-api-service)
  * [5. Service Layer](#5-service-layer)
  * [6. Controller Layer](#6-controller-layer)
  * [7. Integration Testing (Controller Tests)](#7-integration-testing-controller-tests)
  * [8. Unit Testing (Service Layer)](#8-unit-testing-service-layer)
  * [9. Best Practices (for REST and Testing)](#9-best-practices-for-rest-and-testing)
<!-- TOC -->

## 1. Project Structure Overview

```
src/
├── main/
│   ├── java/
│   │   ├── spring/
│   │   │   ├── restclient/
│   │   │   │   ├── controller/       # REST Controllers
│   │   │   │   ├── service/          # Service and API Service
│   │   │   │   ├── model/            # Data Models
│   │   │   │   └── RestClientApplication.java
│   │   │
│   └── resources/
│       └── application.properties    # Spring Boot Configurations
│
├── test/                             # Tests
│   ├── java/
│   │   ├── spring/
│   │   │   ├── restclient/
│   │   │   │   ├── controller/       # Controller Tests
│   │   │   │   └── service/          # Service Unit Tests
│
└── pom.xml  
```
---
## 2. Dependencies (pom.xml)

**1. Spring Boot Starter Web**: For building REST APIs.
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ```
**2. Spring Boot Starter Test**: For testing.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```
**3. Mockito:** For mocking dependencies in unit tests.
**4. RestClient:** For making HTTP requests.
---
## 3. Model Classes

**1. RmChar**: Represents a Rick & Morty character.
   ```java
   public record RmChar(
       int id,
       String name,
       String species,
       String status
   ) {}
   ```
**2. RmResponse**: Represents the API response structure.
```java
public record RmResponse(List<RmChar> results) {}
```
---
## 4. API Service

**Purpose:**
- Handles communication with the external Rick & Morty API.
- All API calls are centralized here.

**Example Code:**
```java
@Service
public class RmApiService {
    private static final String BASE_URL = "https://rickandmortyapi.com/api";
    private final RestClient client;

    public RmApiService(RestClient.Builder builder) {
        this.client = builder.baseUrl(BASE_URL).build();
    }

    public RmResponse fetchAllCharacters() {
        return client.get()
                .uri("/character")
                .retrieve()
                .body(RmResponse.class);
    }

    public RmChar fetchCharacterById(int id) {
        return client.get()
                .uri("/character/" + id)
                .retrieve()
                .body(RmChar.class);
    }

    public RmResponse fetchAllCharactersByStatus(String status) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/character").queryParam("status", status).build())
                .retrieve()
                .body(RmResponse.class);
    }
}
```
---
## 5. Service Layer
**Purpose:**
- Contains business logic.
- Uses `RmApiService` to fetch data and apply further logic.

**Example Code:**
```java
@Service
public class RmCharService {
    private final RmApiService rmApiService;

    public RmCharService(RmApiService rmApiService) {
        this.rmApiService = rmApiService;
    }

    public List<RmChar> getAllCharacters() {
        return rmApiService.fetchAllCharacters().results();
    }

    public long getCountOfCharactersBySpeciesAndStatus(String species, String status) {
        return rmApiService.fetchAllCharactersBySpeciesAndStatus(species, status)
                .results()
                .stream()
                .filter(character -> species.equalsIgnoreCase(character.species()))
                .count();
    }
}
```
---
## 6. Controller Layer

**Purpose:**
- Exposes REST endpoints for the client.

**Example Code:**
```java
@RestController
@RequestMapping("/api/characters")
public class RmController {
    private final RmCharService rmCharService;

    public RmController(RmCharService rmCharService) {
        this.rmCharService = rmCharService;
    }

    @GetMapping
    public List<RmChar> getCharacters(@RequestParam(required = false) String status) {
        return (status != null) ? rmCharService.getAllCharactersByStatus(status)
                                : rmCharService.getAllCharacters();
    }

    @GetMapping("/species-statistic")
    public long getSpeciesStatistic(@RequestParam String species, @RequestParam String status) {
        return rmCharService.getCountOfCharactersBySpeciesAndStatus(species, status);
    }
}
```
---
## 7. Integration Testing (Controller Tests)

**Tools:**
- **MockMvc**: Simulate HTTP requests.
- **MockRestServiceServer**: Mock external API calls.

**Example Code:**
```java
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class RmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void getCharacters_ShouldReturnListOfCharacters() throws Exception {
        mockServer.expect(requestTo("https://rickandmortyapi.com/api/character"))
                .andRespond(withSuccess("""
                    {
                        "results": [
                            {"id": 1, "name": "Rick Sanchez", "species": "Human", "status": "Alive"}
                        ]
                    }
                """, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/characters"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        {"id": 1, "name": "Rick Sanchez", "species": "Human", "status": "Alive"}
                    ]
                """));
    }
}
```
---
## 8. Unit Testing (Service Layer)

**Tools:**
- **Mockito**: Mock API service responses.
- **Assertions**: Verify outputs.

**Example Code:**
```java
@ExtendWith(MockitoExtension.class)
class RmCharServiceTest {

    @Mock
    private RmApiService rmApiService;

    @InjectMocks
    private RmCharService rmCharService;

    @Test
    void getAllCharactersByStatus_ShouldReturnFilteredCharacters() {
        List<RmChar> mockCharacters = List.of(
            new RmChar(1, "Rick Sanchez", "Human", "Alive"),
            new RmChar(2, "Morty Smith", "Human", "Alive")
        );
        when(rmApiService.fetchAllCharactersByStatus("alive"))
                .thenReturn(new RmResponse(mockCharacters));

        List<RmChar> result = rmCharService.getAllCharactersByStatus("alive");

        assertEquals(2, result.size());
        assertEquals("Rick Sanchez", result.get(0).name());
    }
}
```
---
## 9. Best Practices (for REST and Testing)

**1. Layered Architecture**:
    - Separate Controller, Service, and API logic.

**2. Testing**:
    - Controller → Integration Tests.
    - Service → Unit Tests.

**3. Mocking**:
    - Mock external APIs to avoid real HTTP calls.

**4. Readable Code**:
    - Use descriptive method names and clean structure.

**5. Configurable URLs**:
    - Use properties for base URLs instead of hardcoding.
---