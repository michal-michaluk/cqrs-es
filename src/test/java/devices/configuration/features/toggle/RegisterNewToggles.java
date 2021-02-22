package devices.configuration.features.toggle;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class RegisterNewToggles {

    @Autowired
    List<NewToggle> toggleList;

    @Autowired
    TogglesRepository togglesRepository;

    @PostConstruct
    void registerToggles() {
        toggleList.forEach(newToggle -> {
            Toggle toggle = new Toggle();
            toggle.setName(newToggle.getName());
            toggle.setValue(newToggle.getInitialValue());
            togglesRepository.save(toggle);
        });
    }
}
