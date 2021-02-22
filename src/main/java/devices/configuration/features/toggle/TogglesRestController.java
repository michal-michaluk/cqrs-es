package devices.configuration.features.toggle;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
class TogglesRestController {

    private final TogglesService togglesService;

    @GetMapping(value = "/toggles", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Toggle> getAllToggles() {
        return togglesService.findAll();
    }
}
