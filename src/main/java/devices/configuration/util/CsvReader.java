package devices.configuration.util;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.function.Consumer;

public class CsvReader {

    @RequiredArgsConstructor
    public static class Line {
        private final String[] items;

        public String getItem(int column) {
            return items[column];
        }

        public int getNumberOfColumns() {
            return items.length;
        }

        @Override
        public String toString() {
            return Arrays.toString(items);
        }
    }

    public static void readLines(InputStream inputStream, Consumer<Line> consumer)
            throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String delimiter = ";";
            boolean titleSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!titleSkipped) {
                    //skip title
                    titleSkipped = true;

                    String[] commaSplit = line.split(",");
                    String[] semicolonSplit = line.split(";");

                    if (commaSplit.length > semicolonSplit.length) {
                        delimiter = ",";
                    }
                    continue;
                }

                consumer.accept(new Line(line.split(delimiter)));
            }
        }
    }
}
