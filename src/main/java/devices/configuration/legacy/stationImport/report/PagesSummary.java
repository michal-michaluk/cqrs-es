package devices.configuration.legacy.stationImport.report;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
class PagesSummary {
    private int all = 0;
    private int failed = 0;
    private int succeeded = 0;
    private final List<FailedPage> failedPages = new ArrayList<>();

    static PagesSummary blank() {
        return new PagesSummary();
    }

    void addSuccess() {
        succeeded++;
        all++;
    }

    void addFail(int pageNumber, String message) {
        failedPages.add(new FailedPage(pageNumber, message));
        failed++;
        all++;
    }

    @AllArgsConstructor
    @Getter
    private static class FailedPage {
        private int pageNumber;
        private String message;
    }
}