package uk.gov.laa.adamlb.pactdemo.api;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/koala")
public class KoalaController {

    private final KoalaService koalaService;

    @GetMapping("/location")
    public String getLocation(@RequestParam String day) {
        return koalaService.getLocation(day);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleInvalidDay() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
