import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ParseFiles {
    ClassLoader classLoader = ParseFiles.class.getClassLoader();

    @Test
    @DisplayName("Checking csv parsing from a zip file")
    public void zipParseCSV() throws Exception {
        try (InputStream inputStream = classLoader.getResourceAsStream("Files.zip");
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().contains(".csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zipInputStream));
                    List<String[]> content = reader.readAll();
                    assertThat(content.get(0)[1]).contains("Second");
                }
            }
        }
    }

    @Test
    @DisplayName("Checking pdf parsing from a zip file")
    public void zipParsePDF() throws Exception{
        try(InputStream inputStream = classLoader.getResourceAsStream("Files.zip");
            ZipInputStream zipInputStream = new ZipInputStream(inputStream)){
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null){
                if (zipEntry.getName().contains(".pdf")){
                    PDF pdf = new PDF(zipInputStream);
                    assertThat(pdf.text).contains("First Second Third");
                }
            }
        }
    }

    @Test
    @DisplayName("Checking xlsx parsing from a zip file")
    public void zipParseXLSX() throws Exception{
        try(InputStream inputStream = classLoader.getResourceAsStream("Files.zip");
            ZipInputStream zipInputStream = new ZipInputStream(inputStream)){
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null){
                if (zipEntry.getName().contains(".xlsx")){
                    XLS xls = new XLS(zipInputStream);
                    assertThat(xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue()).contains("First");
                }
            }
        }
    }

    @DisplayName("Checking json parsing from a file")
    @Test
    public void jsonParse() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/example.json");
        ExampleJson exampleJson = objectMapper.readValue(file, ExampleJson.class);

        assertThat(exampleJson.title).contains("example glossary");
        assertThat(exampleJson.glossDiv.flag).isTrue();
        assertThat(exampleJson.glossDiv.array[1]).isEqualTo(2);
    }
}
