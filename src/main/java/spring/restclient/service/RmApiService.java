package spring.restclient.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import spring.restclient.model.RmChar;
import spring.restclient.model.RmResponse;

@Service
public class RmApiService {

	 private static final String BASE_URL = "https://rickandmortyapi.com/api";
	 private final RestClient client;

	 public RmApiService(RestClient.Builder builder) {
			this.client = builder.baseUrl(BASE_URL).build();
	 }

	 public RmResponse fetchAllCharacters() {
			return client
					.get()
					.uri("/character")
					.retrieve()
					.body(RmResponse.class);
	 }

	 public RmResponse fetchAllCharactersByStatus(String status) {
			return client
					.get()
					.uri(uriBuilder -> uriBuilder
							.path("/character")
							.queryParam("status", status)
							.build())
					.retrieve()
					.body(RmResponse.class);
	 }

	 public RmResponse fetchAllCharactersBySpecies(String species) {
			return client
					.get()
					.uri(uriBuilder -> uriBuilder
							.path("/character")
							.queryParam("species", species) // Add species to query
							.build())
					.retrieve()
					.body(RmResponse.class);
	 }

	 public RmChar fetchCharacterById(int id) {
			return client
					.get()
					.uri("/character/" + id)
					.retrieve()
					.body(RmChar.class);
	 }

	 public RmResponse fetchAllCharactersBySpeciesAndStatus(String species, String status) {
			return client
					.get()
					.uri(uriBuilder -> uriBuilder
							.path("/character")
							.queryParam("species", species)
							.queryParam("status", status)
							.build())
					.retrieve()
					.body(RmResponse.class);
	 }
}
