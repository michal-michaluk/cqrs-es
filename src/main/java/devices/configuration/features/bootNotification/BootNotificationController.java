package devices.configuration.features.bootNotification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
class BootNotificationController {

    private final BootNotificationService bootNotificationService;

    @PutMapping("/stations/{stationName}/bootNotification")
    void handleBootNotification(@PathVariable String stationName,
                                       @RequestBody BootNotificationFields bootNotificationFields) {
        log.info("Got handle boot notification request for station: {}, and fields: {}", stationName, bootNotificationFields);
        bootNotificationService.handleBootNotificationFromEggplant(stationName, bootNotificationFields);
        log.info("Handle boot notification request for station: {} is done", stationName);
    }

    @PutMapping("/stations/{stationName}/connections")
    void handleStationConnectedFromCsc(@PathVariable String stationName, @RequestBody StationProtocolFromCsc stationProtocolDTOFromCsc) {
        log.info("Got handle station connected from csc request for station: {}, and station protocol: {}", stationName, stationProtocolDTOFromCsc);
        bootNotificationService.handleStationConnectedFromCsc(stationName, stationProtocolDTOFromCsc);
        log.info("Handle station connected from csc request for station: {} is done", stationName);
    }

    @GetMapping(value = "/stations/{stationName}", produces = "application/vnd.vattenfall.v1.bootNotification+json")
    BootNotificationFields findBootNotificationFields(@PathVariable String stationName) {
        log.info("Got find boot notification fields for station: {}", stationName);
        final BootNotificationFields bootNotificationFields = bootNotificationService.getBootNotificationFieldsForStation(stationName);
        log.info("Found boot notification fields for station {}: , {}", stationName, bootNotificationFields);
        return bootNotificationFields;
    }

    @GetMapping(value = "/stations/{stationName}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<StationProtocolDTO> getStationDetails(@PathVariable String stationName) {
        log.info("Got get station details request for station: {}", stationName);
        StationProtocolDTO stationDetailsDTO = bootNotificationService.getStationDetails(stationName);
        log.info("Found station details: {}", stationDetailsDTO);
        return new ResponseEntity<>(stationDetailsDTO, HttpStatus.OK);
    }
}
