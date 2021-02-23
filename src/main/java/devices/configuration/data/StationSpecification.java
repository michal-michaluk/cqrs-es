package devices.configuration.data;

import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class StationSpecification {

    private String name;

    public Specification<Device> withNameContaining(String name) {
        if (name == null) {
            return null;
        }

        String nameExpression = "%" + name + "%";

        return Specification.where((root, query, cb) -> cb
                .like(cb.lower(root.get("name")), nameExpression.toLowerCase()));
    }
}
