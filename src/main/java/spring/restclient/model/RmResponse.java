package spring.restclient.model;

import java.util.List;

// Represents the response structure from the Rick & Morty API.
public record RmResponse(List<RmChar> results) {
}
