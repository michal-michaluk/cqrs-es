package devices.configuration.features.catalogue;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.util.List;

@Slf4j
class CsvWriter {
    private static final String DELIMITER = ";";

    static void writeStations(PrintWriter writer, List<Station> stations) {
        StringBuilder columns = new StringBuilder("Station unique ID,Station name,Station Physical reference like a QR/sticker/could be the same as station name (Physical_reference)," +
                "Station number of outlets,Station maximum number of outlets by hardware capability,Station Vendor/produced by,Station Model/Type/name of product," +
                "Station Model/Type/name of product,Station Color,station comment 1,station comment 2,station Capability/ocpp,Station capability/payment terminal DEBIT," +
                "Station capability/station can be reservered,Station capability/payment terminal CREDIT,Station capability/RFID,Station capability/remote start,Station Capability/SCN/DLB," +
                "station Capability/token group capable,station Capability/smart charging dso (profiles),station Capability/unlock,station capability AC,station capability DC," +
                "Connector 1 physical reference/visual id/qr/sticker/code,Connector 1  EVSE_ID,Connector 1 plug type (ConnectorType),Connector 1 format (connectorFormat)," +
                "Connector 1 Voltage,Connector 1 Phases,Connector 1 Amps,Connector 1 capability AC,Connector 1 capability DC,Connector 1 picture,Connector 2 name/uID/unique ID," +
                "Connector 2 physical reference/visual id/qr/sticker/code,Connector 2  EVSE_ID,Connector 2 plug type (ConnectorType),Connector 2 format (connectorFormat)," +
                "Connector 2 Voltage,Connector 2 Phases,Connector 2 Amps,Connector 2 capability AC,Connector 2 capability DC");

        int maxNumberOfConnectorsInExport = 0;
        for (Station station : stations) {
            maxNumberOfConnectorsInExport = Math.max(maxNumberOfConnectorsInExport, station.getNumberOfOutlets());
        }
        for (int i = 1; i <= maxNumberOfConnectorsInExport; i++) {
            columns.append(",Connector ").append(i).append(" name");
            columns.append(",Connector ").append(i).append(" physical reference/visual id/qr/sticker/code");
            columns.append(",Connector ").append(i).append(" EVSE_ID");
            columns.append(",Connector ").append(i).append(" plug type (ConnectorType)");
            columns.append(",Connector ").append(i).append(" format (connectorFormat)");
            columns.append(",Connector ").append(i).append(" Voltage");
            columns.append(",Connector ").append(i).append(" Phases");
            columns.append(",Connector ").append(i).append(" Amps");
            columns.append(",Connector ").append(i).append(" capability AC");
            columns.append(",Connector ").append(i).append(" capability DC");
        }
        writer.println(columns.toString().replaceAll(",", DELIMITER));

        for (Station station : stations) {
            writer.println(station.toStringCsv(DELIMITER));
        }
        writer.close();
    }
}
