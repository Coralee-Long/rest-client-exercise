package spring.restclient.service;

import org.springframework.web.util.UriComponentsBuilder;
import spring.restclient.model.RmChar;
import spring.restclient.model.RmResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RmService {
    private final RestClient client;

    // Uses RestClient to make HTTP requests to API
    public RmService(RestClient.Builder restClientBuilder) {
        this.client = restClientBuilder.baseUrl("https://rickandmortyapi.com/api/character").build();
    }

    public List<RmChar> getAllCharacters() {
        return client
                .get() // REST method
                .retrieve() // Send request and wait for response
                .body(RmResponse.class) // Map response to specified object
                .results();
    }

    public RmChar getCharacterById(int id) {
        return client
                .get()
                .uri("/{id}", id) // Append the ID to the base URL
                .retrieve() // Send request and wait for response
                .body(RmChar.class); // Map response to a single character object
    }

    public List<RmChar> getAllCharactersByStatus(String status) {

        String url = UriComponentsBuilder.fromPath("")

                .queryParam("status", status)
                .build()
                .toUriString();

        RmResponse response = client
                .get()
                .uri(url)
                .retrieve()
                .body(RmResponse.class);

        return response.results();
    }

    // Fetch all characters by status and species and return count
    public long getCountOfCharactersBySpeciesAndStatus(String species, String status) {
        String url = UriComponentsBuilder.fromPath("")
                .queryParam("status", status)
                .build()
                .toUriString();

        RmResponse response = client
                .get()
                .uri(url)
                .retrieve()
                .body(RmResponse.class);

        // Filter results locally by species and count the matching characters
        return response.results()
                .stream()
                .filter(character -> character.species() != null)
                .filter(character -> character.species().equalsIgnoreCase(species))
                .count();
    }

    // Fetch all characters by status and species
    public List<RmChar> getAllCharactersBySpeciesAndStatus(String species, String status) {
        // Build the URL with both query parameters
        String url = UriComponentsBuilder.fromPath("")
            .queryParam("species", species)
            .queryParam("status", status)
            .build()
            .toUriString();

        // Call the external API and retrieve the results
        RmResponse response = client
            .get()
            .uri(url)
            .retrieve()
            .body(RmResponse.class);

        return response.results();
    }
}


