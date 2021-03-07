package devices.configuration.reads;

import lombok.Value;

@Value
public class CrmAccountUpdate {
    String id;
    String partyKey;
    CrmAccount details;
}
