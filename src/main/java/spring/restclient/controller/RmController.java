package spring.restclient.controller;

import org.springframework.web.bind.annotation.*;
import spring.restclient.model.RmChar;
import org.springframework.beans.factory.annotation.Autowired;
import spring.restclient.service.RmService;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
public class RmController {

    private final RmService rmService;

    @Autowired
    public RmController(RmService rmService) {
        this.rmService = rmService;
    }

    // Get all characters or filter by status
    @GetMapping
    public List<RmChar> getCharacters(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return rmService.getAllCharactersByStatus(status);
        }
        return rmService.getAllCharacters();
    }

    // Get a character by ID
    @GetMapping("/{id}")
    public RmChar getCharacterById(@PathVariable int id) {
        return rmService.getCharacterById(id);
    }

    // Get species Statistics
    @GetMapping("/species-statistic")
    public long getSpeciesStatistic(@RequestParam String species, @RequestParam(defaultValue = "alive") String status) {
       return rmService.getCountOfCharactersBySpeciesAndStatus(species, status);
    }
}
