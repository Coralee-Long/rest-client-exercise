package spring.restclient.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class RmControllerTest {

	 @Autowired
	 private MockMvc mockMvc;

	 @Autowired
	 private MockRestServiceServer mockServer;

	 /**
		* Test: Fetch all characters.
		* Verifies that the `/api/characters` endpoint returns all characters as expected.
		*/
	 @Test
	 void getCharacters() throws Exception {
			// Mock API response
			mockServer.expect(requestTo("https://rickandmortyapi.com/api/character"))
					.andRespond(withSuccess(
							"""
											{
													"results": [
															{
																	"id": 1,
																	"name": "Rick Sanchez",
																	"status": "Alive",
																	"species": "Human"
															},
															{
																	"id": 2,
																	"name": "Morty Smith",
																	"status": "Alive",
																	"species": "Human"
															}
													]
											}
											""", MediaType.APPLICATION_JSON
																 ));

			// Perform GET request to the endpoint
			mockMvc.perform(get("/api/characters"))
					.andExpect(status().isOk())
					.andExpect(content().json(
							"""
											[
													{
															"id": 1,
															"name": "Rick Sanchez",
															"status": "Alive",
															"species": "Human"
													},
													{
															"id": 2,
															"name": "Morty Smith",
															"status": "Alive",
															"species": "Human"
													}
											]
											"""
																	 ));

			// Verify interaction with mock server
			mockServer.verify();
	 }

	 /**
		* Test: Fetch a single character by ID.
		* Verifies that the `/api/characters/{id}` endpoint returns the correct character.
		*/
	 @Test
	 void getCharacterById() throws Exception {
			// Mock API response
			mockServer.expect(requestTo("https://rickandmortyapi.com/api/character/1"))
					.andRespond(withSuccess(
							"""
											{
													"id": 1,
													"name": "Rick Sanchez",
													"species": "Human",
													"status": "Alive"
											}
											""", MediaType.APPLICATION_JSON
																 ));

			// Perform GET request to the endpoint
			mockMvc.perform(get("/api/characters/1"))
					.andExpect(status().isOk())
					.andExpect(content().json(
							"""
											{
													"id": 1,
													"name": "Rick Sanchez",
													"species": "Human",
													"status": "Alive"
											}
											"""
																	 ));

			// Verify interaction with mock server
			mockServer.verify();
	 }

	 /**
		* Test: Get species statistics.
		* Verifies that the `/api/characters/species-statistic` endpoint returns the correct count.
		*/
	 @Test
	 void getSpeciesStatisticCount() throws Exception {
			// Mock response for species "Human" and status "alive"
			mockServer.expect(requestTo("https://rickandmortyapi.com/api/character?species=Human&status=alive"))
					.andRespond(withSuccess(
							"""
											{
													"results": [
															{
																	"id": 1,
																	"name": "Rick Sanchez",
																	"status": "Alive",
																	"species": "Human"
															},
															{
																	"id": 2,
																	"name": "Morty Smith",
																	"status": "Alive",
																	"species": "Human"
															}
													]
											}
											""", MediaType.APPLICATION_JSON
																 ));

			// Perform GET request with query parameters
			mockMvc.perform(get("/api/characters/species-statistic")
													.param("species", "Human")
													.param("status", "alive"))
					.andExpect(status().isOk())
					.andExpect(content().string("2")); // Expecting count of 2 Humans

			// Verify interaction
			mockServer.verify();
	 }



	 /**
		* Test: Fetch characters by species and status.
		* Verifies that the `/api/characters` endpoint filters characters correctly.
		*/
	 @Test
	 void getCharactersBySpeciesAndStatus() throws Exception {
			// Given: Mock response from the external API for species "alien" and status "alive"
			mockServer.expect(requestTo("https://rickandmortyapi.com/api/character?species=alien&status=alive"))
					.andRespond(withSuccess(
							"""
											{
													"results": [
															{
																	"id": 4,
																	"name": "Alien Morty",
																	"status": "Alive",
																	"species": "Alien"
															},
															{
																	"id": 5,
																	"name": "Alien Rick",
																	"status": "Alive",
																	"species": "Alien"
															}
													]
											}
											""", MediaType.APPLICATION_JSON
																 ));

			// When: Perform GET request to "/api/characters" with query parameters
			mockMvc.perform(get("/api/characters")
													.param("species", "alien")
													.param("status", "alive"))
					.andExpect(status().isOk())
					.andExpect(content().json(
							"""
											[
													{
															"id": 4,
															"name": "Alien Morty",
															"status": "Alive",
															"species": "Alien"
													},
													{
															"id": 5,
															"name": "Alien Rick",
															"status": "Alive",
															"species": "Alien"
													}
											]
											"""
																	 ));

			// Then: Verify the mock server interaction
			mockServer.verify();
	 }

	 @Test
	 void getCharactersByStatus() throws Exception {
			// Mock response for status "dead"
			mockServer.expect(requestTo("https://rickandmortyapi.com/api/character?status=dead"))
					.andRespond(withSuccess(
							"""
											{
													"results": [
															{
																	"id": 8,
																	"name": "Adjudicator Rick",
																	"species": "Human",
																	"status": "Dead"
															},
															{
																	"id": 16,
																	"name": "Amish Cyborg",
																	"species": "Alien",
																	"status": "Dead"
															}
													]
											}
											""", MediaType.APPLICATION_JSON
																 ));

			// Perform GET request with status
			mockMvc.perform(get("/api/characters")
													.param("status", "dead"))
					.andExpect(status().isOk())
					.andExpect(content().json(
							"""
											[
													{
															"id": 8,
															"name": "Adjudicator Rick",
															"species": "Human",
															"status": "Dead"
													},
													{
															"id": 16,
															"name": "Amish Cyborg",
															"species": "Alien",
															"status": "Dead"
													}
											]
											"""
																	 ));

			mockServer.verify();
	 }

	 @Test
	 void getCharactersBySpecies() throws Exception {
			// Mock response for species "alien"
			mockServer.expect(requestTo("https://rickandmortyapi.com/api/character?species=alien"))
					.andRespond(withSuccess(
							"""
											{
													"results": [
															{
																	"id": 6,
																	"name": "Abadango Cluster Princess",
																	"species": "Alien",
																	"status": "Alive"
															}
													]
											}
											""", MediaType.APPLICATION_JSON
																 ));

			// Perform GET request with species
			mockMvc.perform(get("/api/characters")
													.param("species", "alien"))
					.andExpect(status().isOk())
					.andExpect(content().json(
							"""
											[
													{
															"id": 6,
															"name": "Abadango Cluster Princess",
															"species": "Alien",
															"status": "Alive"
													}
											]
											"""
																	 ));

			mockServer.verify();
	 }
}

