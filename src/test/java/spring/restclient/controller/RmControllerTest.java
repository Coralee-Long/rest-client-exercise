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

    @Test
    void getCharacters() throws Exception {
        // Mock response from the external API
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

        // Perform GET request to the "/api/characters" endpoint
        mockMvc.perform(get("/api/characters")) // Path should start with "/"
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

        // Verify that the mock server was used
        mockServer.verify();
    }

    @Test
    void getCharacterById() throws Exception {
			 // Mock response from the external API
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

			 // Perform GET request to the "/api/characters/1" endpoint
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
			 // Verify that the mock server was used
			 mockServer.verify();
    }

	 @Test
	 void getSpeciesStatistic() throws Exception {
			// Given: Mock response from the external API for status "alive"
			mockServer.expect(requestTo("https://rickandmortyapi.com/api/character?status=alive"))
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
															},
															{
																	"id": 3,
																	"name": "Birdperson",
																	"status": "Alive",
																	"species": "Birdperson"
															}
													]
											}
											""", MediaType.APPLICATION_JSON
																 ));

			// When: Performing GET request to "/api/characters/species-statistic"
			mockMvc.perform(get("/api/characters/species-statistic")
													.param("species", "Human")
													.param("status", "alive"))
					.andExpect(status().isOk())
					.andExpect(content().string("2")); // Expecting count of 2 Humans

			// Then: Verify the mock server interaction
			mockServer.verify();
	 }

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

}
