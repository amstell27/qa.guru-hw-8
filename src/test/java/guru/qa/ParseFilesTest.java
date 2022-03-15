package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import guru.qa.domain.Pupil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParseFilesTest {

    String pdfFile = "report306.pdf";
    String csvFile = "SampleCSVFile_11kb.csv";
    String xlsxFile = "report_1.xlsx";

    @Test
    void parseZipFile() throws Exception {

        ZipFile zipFile = new ZipFile("src/test/resources/zip_sample_test.zip");
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().contains("pdf")) {
                assertThat(entry.getName()).isEqualTo(pdfFile);
                parsePdfFile(zipFile.getInputStream(entry));
            } else if (entry.getName().contains("xlsx")) {
                assertThat(entry.getName()).isEqualTo(xlsxFile);
                parseXlsFile(zipFile.getInputStream(entry));
            } else if (entry.getName().contains("csv")) {
                assertThat(entry.getName()).isEqualTo(csvFile);
                parseCsvFile(zipFile.getInputStream(entry));
            }
        }

    }

    void parsePdfFile(InputStream file) throws Exception {
        PDF pdf = new PDF(file);
        assertThat(pdf.title).contains("Счет-фактура (ОМС-2)");
    }

    void parseXlsFile(InputStream file) throws Exception {
        XLS xls = new XLS(file);
        assertThat(xls.excel
                .getSheetAt(0)
                .getRow(5)
                .getCell(1)
                .getStringCellValue()).contains("Прием врача-терапевта лечебно-диагностический, первичный, амб.");
    }

    void parseCsvFile(InputStream file) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file))) {
            List<String[]> content = reader.readAll();
            assertThat(content.get(0)).contains("Eldon Base for stackable storage shelf, platinum");
        }
    }

    @Test
    void parseJsonWithJackson() {

        String pathFileJson = "src/test/resources/simple.json";
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(pathFileJson);
            assertTrue(file.exists());
            Pupil pupil = mapper.readValue(file, Pupil.class);
            assertThat(pupil.name).isEqualTo("Sergey");
            assertThat(pupil.surname).isEqualTo("Ilin");
            assertThat(pupil.address.street).isEqualTo("Kirova");
            assertThat(pupil.address.house).isEqualTo(22);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
