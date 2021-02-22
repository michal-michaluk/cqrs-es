package devices.configuration.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

import static org.assertj.core.api.Assertions.assertThat;

class CsvReaderTest {

    @Test
    void Should_read_comma_separated_lines() throws Exception {
        //given
        FileSystemResource inputFile = new FileSystemResource("./src/test/resources/comma-separated.csv");
        List<CsvReader.Line> lines = new ArrayList<>();

        //when
        CsvReader.readLines(inputFile.getInputStream(), addToListHandler(lines));

        //then
        Assertions.assertThat(lines).hasSize(2);
        assertThat(lines.get(0).getItem(0)).isEqualTo("value A");
        assertThat(lines.get(0).getItem(1)).isEqualTo("value B");
        assertThat(lines.get(1).getItem(0)).isEqualTo("another value A");
        assertThat(lines.get(1).getItem(1)).isEqualTo("another value B");
    }

    @Test
    void Should_read_semicolon_separated_lines() throws Exception {
        //given
        FileSystemResource inputFile = new FileSystemResource("./src/test/resources/semicolon-separated.csv");
        List<CsvReader.Line> lines = new ArrayList<>();

        //when
        CsvReader.readLines(inputFile.getInputStream(), addToListHandler(lines));

        //then
        Assertions.assertThat(lines).hasSize(2);
        assertThat(lines.get(0).getItem(0)).isEqualTo("value A");
        assertThat(lines.get(0).getItem(1)).isEqualTo("value B");
        assertThat(lines.get(1).getItem(0)).isEqualTo("another value A");
        assertThat(lines.get(1).getItem(1)).isEqualTo("another value B");
    }

    @NotNull
    private Consumer<CsvReader.Line> addToListHandler(List<CsvReader.Line> lines) {
        return lines::add;
    }
}
