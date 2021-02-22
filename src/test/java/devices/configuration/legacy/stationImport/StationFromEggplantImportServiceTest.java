package devices.configuration.legacy.stationImport;

import devices.configuration.features.eggplant.EmobilityOldPlatformClient;
import devices.configuration.features.toggle.TogglesService;
import devices.configuration.legacy.stationImport.StationFromEggplantImportService.ImportMode;
import devices.configuration.legacy.stationImport.StationsImporter.UpdateScope;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class StationFromEggplantImportServiceTest {
    private static final String FIRST_PAGE_STATION = "stationFromFirstPage";
    private static final String SECOND_PAGE_STATION = "stationFromSecondPage";

    @Captor
    private ArgumentCaptor<List<StationView.ChargingStation>> listCaptor;

    @Mock
    private EmobilityOldPlatformClient eggplantClient;

    @Mock
    private TogglesService togglesService;

    @Mock
    private StationsImporter stationsImporter;

    @InjectMocks
    private StationFromEggplantImportService importService;

    @BeforeEach
    void setup() {
        initMocks(this);

        // given
        toggleIsEnabled();
    }

    @Test
    void Should_not_import_locations_when_toggle_disabled() {
        // given
        toggleIsDisabled();

        eggplantReturnsSinglePageWithStation(stationView(FIRST_PAGE_STATION));

        // when
        importService.importStations(ImportMode.NORMAL, Set.of(UpdateScope.values()));

        // then
        verify(eggplantClient, never()).getPageOfStations(anyInt(), anyInt());
        verify(stationsImporter, never()).importStations(any(), anyList(), any());
    }

    @Test
    void Should_process_second_page_when_its_returned_from_Eggplant() {
        // given
        eggplantReturnsTwoPages(
                stationView(FIRST_PAGE_STATION),
                stationView(FIRST_PAGE_STATION + 2),
                stationView(SECOND_PAGE_STATION));

        // when
        importService.importStations(ImportMode.NORMAL, Set.of(UpdateScope.values()));

        // then
        verifyRequestedPage(0);
        verifyRequestedPage(1);
        verifyNeverRequestedPage(2);

        verify(stationsImporter, times(2)).importStations(any(), listCaptor.capture(), any());
        var capturedCalls = listCaptor.getAllValues();

        assertThat(capturedCalls).hasSize(2);
        Assertions.assertThat(capturedCalls.get(0)).contains(stationView(FIRST_PAGE_STATION));
        Assertions.assertThat(capturedCalls.get(0)).contains(stationView(FIRST_PAGE_STATION + 2));
        Assertions.assertThat(capturedCalls.get(1)).contains(stationView(SECOND_PAGE_STATION));
    }

    @Test
    void Should_not_process_second_page_when_its_returned_from_Eggplant_but_mode_is_TEST() {
        // given
        eggplantReturnsTwoPages(
                stationView(FIRST_PAGE_STATION),
                stationView(FIRST_PAGE_STATION + 2),
                stationView(SECOND_PAGE_STATION));

        // when
        importService.importStations(ImportMode.TEST, Set.of(UpdateScope.values()));

        // then
        verifyRequestedPage(0);
        verifyNeverRequestedPage(1);

        verify(stationsImporter).importStations(any(), listCaptor.capture(), any());
        var stations = listCaptor.getValue();

        Assertions.assertThat(stations).hasSize(2);
        Assertions.assertThat(stations).contains(stationView(FIRST_PAGE_STATION));
        Assertions.assertThat(stations).contains(stationView(FIRST_PAGE_STATION + 2));
    }

    @Test
    void Should_process_second_page_when_exception_is_thrown_for_first() {
        // given
        var shouldBeSaved = stationView(SECOND_PAGE_STATION);
        eggplantReturnsTwoPages(
                stationView(FIRST_PAGE_STATION),
                stationView(FIRST_PAGE_STATION + 2),
                shouldBeSaved);

        doThrow(new HttpServerErrorException(INTERNAL_SERVER_ERROR))
                .when(eggplantClient).getPageOfStations(eq(0), anyInt());

        // when
        importService.importStations(ImportMode.NORMAL, Set.of(UpdateScope.values()));

        // then
        verifyRequestedPage(0);
        verifyRequestedPage(1);
        verifyNeverRequestedPage(2);

        verify(stationsImporter, times(2)).importStations(any(), listCaptor.capture(), any());
        var capturedCalls = listCaptor.getAllValues();

        assertThat(capturedCalls).hasSize(2);
        Assertions.assertThat(capturedCalls.get(0)).isEmpty();
        Assertions.assertThat(capturedCalls.get(1)).contains(stationView(SECOND_PAGE_STATION));
    }

    private StationView.ChargingStation stationView(String stationName) {
        return StationViewFixture.stationViewWithLocation(stationName);
    }

    private void eggplantReturnsSinglePageWithStation(StationView.ChargingStation stationView) {
        doReturnPage(0, false, singletonList(stationView));
    }

    private void eggplantReturnsTwoPages(StationView.ChargingStation stationView, StationView.ChargingStation stationView2, StationView.ChargingStation stationView3) {
        doReturnPage(0, true, asList(stationView, stationView2));
        doReturnPage(1, false, singletonList(stationView3));
    }

    private void doReturnPage(int pageNumber, boolean hasNext, List<StationView.ChargingStation> stationViews) {
        var page = mock(RestPage.class);
        doReturn(pageNumber).when(page).getNumber();
        doReturn(hasNext).when(page).hasNext();
        doReturn(stationViews).when(page).getContent();

        doReturn(page).when(eggplantClient).getPageOfStations(eq(pageNumber), anyInt());
    }

    private void verifyNeverRequestedPage(int pageNumber) {
        verify(eggplantClient, never()).getPageOfStations(eq(pageNumber), anyInt());
    }

    private void verifyRequestedPage(int pageNumber) {
        verify(eggplantClient).getPageOfStations(eq(pageNumber), anyInt());
    }

    private void toggleIsDisabled() {
        doReturn(false).when(togglesService).isEnabled(any(), anyBoolean());
    }

    private void toggleIsEnabled() {
        doReturn(true).when(togglesService).isEnabled(any(), anyBoolean());
    }
}
