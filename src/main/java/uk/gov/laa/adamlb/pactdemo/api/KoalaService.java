package uk.gov.laa.adamlb.pactdemo.api;

import org.springframework.stereotype.Component;

@Component
final class KoalaService {

    public String getLocation(String day) {
        if (day == null || day.isBlank()) {
            throw new IllegalArgumentException("Day must not be blank");
        }

        return switch (day.strip().toLowerCase()) {
            case "monday" -> "Merthyr Tydfil";
            case "tuesday" -> "Glasgow";
            case "wednesday" -> "Leeds";
            case "thursday" -> "London";
            case "friday" -> "Belfast";
            case "saturday", "sunday" -> "Eucalyptus Tree";
            default -> throw new IllegalArgumentException("Invalid day");
        };
    }
}
