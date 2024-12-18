package spring.restclient.controller;

import org.springframework.web.bind.annotation.*;
import spring.restclient.model.RmChar;
import spring.restclient.service.RmCharService;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
public class RmController {

    private final RmCharService rmCharService;

    public RmController(RmCharService rmCharService) {
        this.rmCharService = rmCharService;
    }

    @GetMapping
    public List<RmChar> getCharacters(
        @RequestParam(required = false) String species,
        @RequestParam(required = false) String status) {
        if (species != null && status != null) {
            return rmCharService.getAllCharactersBySpeciesAndStatus(species, status);
        } else if (species != null) {
            return rmCharService.getAllCharactersBySpecies(species); // Correctly handle species only
        } else if (status != null) {
            return rmCharService.getAllCharactersByStatus(status);
        }
        return rmCharService.getAllCharacters();
    }


    @GetMapping("/{id}")
    public RmChar getCharacterById(@PathVariable int id) {
        return rmCharService.getCharacterById(id);
    }

    @GetMapping("/species-statistic")
    public long getSpeciesStatistic(
        @RequestParam String species,
        @RequestParam(defaultValue = "alive") String status) {
        return rmCharService.getCountOfCharactersBySpeciesAndStatus(species, status);
    }
}
