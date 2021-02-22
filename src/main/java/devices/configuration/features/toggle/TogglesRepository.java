package devices.configuration.features.toggle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TogglesRepository extends JpaRepository<Toggle, Long> {

    Toggle findByName(String name);
}
