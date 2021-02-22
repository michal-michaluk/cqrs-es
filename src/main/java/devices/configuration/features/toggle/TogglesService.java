package devices.configuration.features.toggle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TogglesService {

    private final TogglesRepository togglesRepository;

    List<Toggle> findAll() {
        return togglesRepository.findAll();
    }

    public boolean isEnabled(String name, boolean defaultValue) {
        Toggle toggle = togglesRepository.findByName(name);
        if (toggle == null) {
            return defaultValue;
        }
        return toggle.isEnabled();
    }

    public boolean isDisabled(String name, boolean defaultValue) {
        return !isEnabled(name, defaultValue);
    }

    public void enableToggle(String name) {
        Toggle toggle = togglesRepository.findByName(name);
        toggle.setValue(true);
        togglesRepository.save(toggle);
    }

    public void disableToggle(String name) {
        Toggle toggle = togglesRepository.findByName(name);
        toggle.setValue(false);
        togglesRepository.save(toggle);
    }
}
