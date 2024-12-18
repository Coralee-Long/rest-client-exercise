package spring.restclient.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spring.restclient.model.RmChar;
import spring.restclient.model.RmResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RmCharServiceTest {

	 @Mock
	 private RmApiService rmApiService;

	 @InjectMocks
	 private RmCharService rmCharService;

	 @BeforeEach
	 void setUp() {
			MockitoAnnotations.openMocks(this); // Initialize mocks
	 }

	 @Test
	 void getAllCharacters_ShouldReturnListOfCharacters() {
			// Given
			List<RmChar> mockCharacters = List.of(
					new RmChar(1, "Rick Sanchez", "Human", "Alive"),
					new RmChar(2, "Morty Smith", "Human", "Alive")
																					 );
			RmResponse mockResponse = new RmResponse(mockCharacters);

			when(rmApiService.fetchAllCharacters()).thenReturn(mockResponse);

			// When
			List<RmChar> result = rmCharService.getAllCharacters();

			// Then
			assertEquals(2, result.size());
			assertEquals("Rick Sanchez", result.get(0).name());
	 }

	 @Test
	 void getCharacterById_ShouldReturnCharacter() {
			// Given
			RmChar mockCharacter = new RmChar(1, "Rick Sanchez", "Human", "Alive");

			when(rmApiService.fetchCharacterById(1)).thenReturn(mockCharacter);

			// When
			RmChar result = rmCharService.getCharacterById(1);

			// Then
			assertEquals("Rick Sanchez", result.name());
			assertEquals("Human", result.species());
	 }

	 @Test
	 void getAllCharactersByStatus_ShouldReturnFilteredCharacters() {
			// Given
			List<RmChar> mockCharacters = List.of(
					new RmChar(1, "Rick Sanchez", "Human", "Alive"),
					new RmChar(2, "Morty Smith", "Human", "Alive")
																					 );
			RmResponse mockResponse = new RmResponse(mockCharacters);

			when(rmApiService.fetchAllCharactersByStatus("alive")).thenReturn(mockResponse);

			// When
			List<RmChar> result = rmCharService.getAllCharactersByStatus("alive");

			// Then
			assertEquals(2, result.size());
			assertEquals("Morty Smith", result.get(1).name());
	 }

	 @Test
	 void getCountOfCharactersBySpeciesAndStatus_ShouldReturnCorrectCount() {
			// Given: Mock response with exactly 2 valid characters
			List<RmChar> mockCharacters = List.of(
					new RmChar(1, "Rick Sanchez", "Human", "Alive"),
					new RmChar(2, "Morty Smith", "Human", "Alive")
																					 );
			RmResponse mockResponse = new RmResponse(mockCharacters);

			// Mock the API response (already filtered)
			when(rmApiService.fetchAllCharactersBySpeciesAndStatus("Human", "alive")).thenReturn(mockResponse);

			// When: Call the service method
			long count = rmCharService.getCountOfCharactersBySpeciesAndStatus("Human", "alive");

			// Then: Verify the count matches only "Human" characters with status "Alive"
			assertEquals(2, count);
	 }



	 @Test
	 void getAllCharactersBySpeciesAndStatus_ShouldReturnFilteredCharacters() {
			// Given
			List<RmChar> mockCharacters = List.of(
					new RmChar(4, "Alien Rick", "Alien", "Alive"),
					new RmChar(5, "Alien Morty", "Alien", "Alive")
																					 );
			RmResponse mockResponse = new RmResponse(mockCharacters);

			when(rmApiService.fetchAllCharactersBySpeciesAndStatus("Alien", "alive")).thenReturn(mockResponse);

			// When
			List<RmChar> result = rmCharService.getAllCharactersBySpeciesAndStatus("Alien", "alive");

			// Then
			assertEquals(2, result.size());
			assertEquals("Alien Rick", result.get(0).name());
	 }
}
