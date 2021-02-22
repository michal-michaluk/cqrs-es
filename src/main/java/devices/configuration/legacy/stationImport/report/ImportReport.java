package devices.configuration.legacy.stationImport.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@ToString
@EqualsAndHashCode
public class ImportReport {
    private int all = 0;
    private int created = 0;
    private int unchanged = 0;
    private int rejectedLocation = 0;
    private int rejectedConnectors = 0;
    private int updated = 0;
    private int fixed = 0;
    private final List<Message> rejectedStations = new ArrayList<>();
    private final List<Message> fixedStations = new ArrayList<>();
    @JsonInclude(NON_NULL)
    @Setter
    @Accessors(chain = true)
    private String message;

    private final PagesSummary pagesSummary = PagesSummary.blank();

    public static ImportReport blank() {
        return new ImportReport();
    }

    public ImportReport addCreated() {
        created++;
        all++;
        return this;
    }

    public ImportReport addUnchanged() {
        unchanged++;
        all++;
        return this;
    }

    public ImportReport addUpdated() {
        updated++;
        all++;
        return this;
    }

    public ImportReport addRejectedLocation(String name, String message) {
        //rejectedStations.add(new Message(name, message));
        rejectedLocation++;
        all++;
        return this;
    }

    public ImportReport addRejectedConnectors(String name, String message) {
        rejectedStations.add(new Message(name, message));
        rejectedConnectors++;
        all++;
        return this;
    }

    public ImportReport addFixed(String name, String message) {
        fixedStations.add(new Message(name, message));
        fixed++;
        all++;
        return this;
    }

    public void addSucceededPage() {
        pagesSummary.addSuccess();
    }

    public void addFailedPage(int pageNumber, String message) {
        pagesSummary.addFail(pageNumber, message);
    }

    public int getRejected() {
        return rejectedLocation + rejectedConnectors;
    }

    @Value
    private static class Message {
        String name;
        String message;
    }
}


