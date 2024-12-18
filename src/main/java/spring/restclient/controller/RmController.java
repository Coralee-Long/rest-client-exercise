package spring.restclient.controller;

import org.springframework.web.bind.annotation.*;
import spring.restclient.model.RmChar;
import org.springframework.beans.factory.annotation.Autowired;
import spring.restclient.service.RmService;

import java.util.List;

/**
 * REST Controller to manage Rick and Morty character data.
 * Provides endpoints to fetch all characters, filter by status, species,
 * retrieve a character by ID, and get character statistics.
 */
@RestController
@RequestMapping("/api/characters")
public class RmController {

    private final RmService rmService;

    /**
     * Constructor to inject the RmService.
     *
     * @param rmService The service layer for interacting with the Rick and Morty API.
     */
    @Autowired
    public RmController(RmService rmService) {
        this.rmService = rmService;
    }

    /**
     * Retrieve all characters or filter characters by their status (e.g., alive, dead, unknown).
     *
     * @param status Optional query parameter to filter characters by status.
     * @return A list of {@link RmChar} objects matching the criteria.
     */
    @GetMapping
    public List<RmChar> getCharacters(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return rmService.getAllCharactersByStatus(status);
        }
        return rmService.getAllCharacters();
    }

    /**
     * Retrieve a single character by their unique ID.
     *
     * @param id The unique identifier of the character.
     * @return The {@link RmChar} object representing the character.
     */
    @GetMapping("/{id}")
    public RmChar getCharacterById(@PathVariable int id) {
        return rmService.getCharacterById(id);
    }

    /**
     * Count the number of characters of a specified species and status.
     *
     * @param species The species to filter by (e.g., Human, Alien).
     * @param status  The status to filter by (default is "alive").
     * @return The number of characters matching the criteria.
     */
    @GetMapping("/species-statistic")
    public long getSpeciesStatistic(@RequestParam String species, @RequestParam(defaultValue = "alive") String status) {
        return rmService.getCountOfCharactersBySpeciesAndStatus(species, status);
    }

    /**
     * Retrieve all characters filtered by species and status.
     *
     * This method provides a flexible way to fetch characters based on multiple criteria.
     *
     * @param species Optional query parameter to filter characters by species.
     * @param status  Optional query parameter to filter characters by status.
     * @return A list of {@link RmChar} objects matching the provided criteria.
     */
    @GetMapping
    public List<RmChar> getCharacters(
        @RequestParam(required = false) String species,
        @RequestParam(required = false) String status) {
        if (species != null && status != null) {
            // Fetch characters filtered by both species and status
            return rmService.getAllCharactersBySpeciesAndStatus(species, status);
        } else if (status != null) {
            // Fetch characters filtered only by status
            return rmService.getAllCharactersByStatus(status);
        }
        // Fetch all characters if no filter is provided
        return rmService.getAllCharacters();
    }
}
