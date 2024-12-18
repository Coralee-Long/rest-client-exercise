package spring.restclient.service;

import org.springframework.stereotype.Service;
import spring.restclient.model.RmChar;
import spring.restclient.model.RmResponse;

import java.util.List;

@Service
public class RmCharService {

	 private final RmApiService rmApiService;

	 public RmCharService(RmApiService rmApiService) {
			this.rmApiService = rmApiService;
	 }

	 public List<RmChar> getAllCharacters() {
			return rmApiService.fetchAllCharacters().results();
	 }

	 public RmChar getCharacterById(int id) {
			return rmApiService.fetchCharacterById(id);
	 }

	 public List<RmChar> getAllCharactersByStatus(String status) {
			return rmApiService.fetchAllCharactersByStatus(status).results();
	 }

	 public List<RmChar> getAllCharactersBySpecies(String species) {
			return rmApiService.fetchAllCharactersBySpecies(species).results();
	 }

	 public List<RmChar> getAllCharactersBySpeciesAndStatus(String species, String status) {
			return rmApiService.fetchAllCharactersBySpeciesAndStatus(species, status).results();
	 }

	 public long getCountOfCharactersBySpeciesAndStatus(String species, String status) {
			return getAllCharactersBySpeciesAndStatus(species, status).size();
	 }
}
