package devices.configuration.features.catalogue;

import devices.configuration.features.catalogue.exceptions.IllegalCsvFormatException;
import devices.configuration.util.CsvReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@RequiredArgsConstructor
@Component
public class StationFactory {

    private static final int COLUMNS_PER_OUTLET_DEFINITION = 9;
    private static final int OUTLETS_FIRST_COLUMN = 23;

    private final StationImageProperties stationImageProperties;

    public Station createStation(CsvReader.Line line) {
        requireCorrectNumberOfColumns(line);

        Station station = new Station();
        int numberOfOutlets = getNumberOfOutlets(line);
        station.setImageId(stationImageProperties.getId());
        station.setId(getStationId(line));
        station.setName(getStationName(line));
        station.setPhysicalReference(line.getItem(2));
        station.setNumberOfOutlets(numberOfOutlets);
        station.setMaxNumberOfOutlets(Integer.parseInt(line.getItem(4)));
        station.setVendor(line.getItem(5));
        station.setProduct(line.getItem(6));
        station.setProductDetails(line.getItem(7));
        station.setColor(line.getItem(8));
        station.setComment1(line.getItem(9));
        station.setComment2(line.getItem(10));

        StationCapabilities stationCapabilities = new StationCapabilities();
        stationCapabilities.setAvailableOcppVersions(line.getItem(11));
        stationCapabilities.setDebitPayment(getLineItemAsBoolean(line, 12));
        stationCapabilities.setReservation(getLineItemAsBoolean(line, 13));
        stationCapabilities.setCreditPayment(getLineItemAsBoolean(line, 14));
        stationCapabilities.setRfidReader(getLineItemAsBoolean(line, 15));
        stationCapabilities.setRemoteStart(getLineItemAsBoolean(line, 16));
        stationCapabilities.setScnDlb(getLineItemAsBoolean(line, 17));
        stationCapabilities.setTokenGrouping(getLineItemAsBoolean(line, 18));
        stationCapabilities.setSmartCharging(getLineItemAsBoolean(line, 19));
        stationCapabilities.setUnlock(getLineItemAsBoolean(line, 20));
        stationCapabilities.setAc(getLineItemAsBoolean(line, 21));
        stationCapabilities.setDc(getLineItemAsBoolean(line, 22));
        station.setCapabilities(stationCapabilities);

        List<Connector> connectors = new ArrayList<>(numberOfOutlets);
        for (int i = 0; i < numberOfOutlets; i++) {
            int currentOutletFirstColumn = OUTLETS_FIRST_COLUMN + i * COLUMNS_PER_OUTLET_DEFINITION;

            Connector connector = new Connector();
            connector.setName(generateConnectorName(station, i));
            connector.setPhysicalReference(line.getItem(currentOutletFirstColumn));
            connector.setOcppConnectorId(i + 1);
            connector.setEvseId(generateEvseId(line, i));
            String columnValue = line.getItem(currentOutletFirstColumn + 2);
            connector.setType(getConnectorType(columnValue));
            connector.setFormat(line.getItem(currentOutletFirstColumn + 3) != null ? Format.valueOf(line.getItem(currentOutletFirstColumn + 3)) : null);
            connector.setVoltage(line.getItem(currentOutletFirstColumn + 4));
            connector.setPhases(line.getItem(currentOutletFirstColumn + 5));
            connector.setAmps(line.getItem(currentOutletFirstColumn + 6));
            connector.setAc(getLineItemAsBoolean(line, currentOutletFirstColumn + 7));
            connector.setDc(getLineItemAsBoolean(line, currentOutletFirstColumn + 8));
            connectors.add(connector);
        }
        station.setConnectors(connectors);
        station.setSettings(Settings.defaultSettings());

        return station;
    }

    private ConnectorType getConnectorType(String columnValue) {
        return stream(ConnectorType.values())
                .filter(value -> value.toString().equals(columnValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown connector type: " + columnValue + ". Supported values: " + asList(ConnectorType.values())));
    }

    private UUID getStationId(CsvReader.Line line) {
        final String item = line.getItem(0);
        return isEmpty(item) ? null : UUID.fromString(item);
    }

    private void requireCorrectNumberOfColumns(CsvReader.Line line) {
        int numberOfOutlets = getNumberOfOutlets(line);

        int expectedColumns = OUTLETS_FIRST_COLUMN + numberOfOutlets * COLUMNS_PER_OUTLET_DEFINITION;
        int existingColumns = line.getNumberOfColumns();
        if (existingColumns != expectedColumns) {
            throw new IllegalCsvFormatException("Number of columns are not matching. expected: " + expectedColumns + " having: " + existingColumns);
        }
    }

    private String getStationName(CsvReader.Line line) {
        return line.getItem(1);
    }

    private int getNumberOfOutlets(CsvReader.Line line) {
        return Integer.parseInt(line.getItem(3));
    }

    private boolean getLineItemAsBoolean(CsvReader.Line line, int columnNumber) {
        String value = line.getItem(columnNumber);
        return (value != null && value.toLowerCase().equals("yes"));
    }

    private String generateEvseId(CsvReader.Line line, int indexOfOutlet) {
        //TODO generate based on some input data
        return line.getItem(OUTLETS_FIRST_COLUMN + indexOfOutlet * COLUMNS_PER_OUTLET_DEFINITION) + 2;
    }

    private String generateConnectorName(Station station, int indexOfOutlet) {
        return station.getName() + "_" + (indexOfOutlet + 1);
    }

    public Station newStation(String stationName) {
        return new Station()
                .setName(stationName)
                .setImageId(stationImageProperties.getId())
                .setSettings(Settings.defaultSettings());
    }
}
