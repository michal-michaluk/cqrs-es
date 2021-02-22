package devices.configuration.features.catalogue.location;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class MapsAutocompleteRestController {

    private final GoogleAutocompleteClient autocompletion;

    @GetMapping("/locations/autocomplete")
    @ResponseBody
    public String autocompleteLocation(@RequestParam(value = "input") String input) {
        return autocompletion.locationAutoCompleted(input);
    }
}
