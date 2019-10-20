package edu.usf.cutr.transitfeedqualitycalculator.test;

import edu.usf.cutr.gtfsrtvalidator.lib.validation.ValidationRules;
import edu.usf.cutr.transitfeedqualitycalculator.ExcelExporter;
import edu.usf.cutr.transitfeedqualitycalculator.ResultsAnalyzer;
import edu.usf.cutr.transitfeedqualitycalculator.model.AnalysisOutput;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ResultAnalyzerTest {

    @org.junit.jupiter.api.Test
    public void resultAnalyzerTest() throws java.io.IOException, java.lang.NoSuchFieldException, java.lang.IllegalAccessException {
        String directoryName = "src/test/resources/feeds";

        // Analysis and export results to Excel
        ResultsAnalyzer analyzer = new ResultsAnalyzer(Paths.get(directoryName), "", "");
        AnalysisOutput output = analyzer.analyzeResults();
        ExcelExporter exporter = new ExcelExporter(output);
        exporter.export();

        // Test contents of Excel output graph file
        Workbook workbook = new XSSFWorkbook(new FileInputStream(exporter.getOutputFileName()));
        testDataSheet(workbook);
        testSummarySheet(workbook);
        testFrequencySheet(workbook);
        testCountSheet(workbook);
        testHistogramSheet(workbook);
    }

    private void testHistogramSheet(Workbook workbook) {
        Sheet summarySheet = workbook.getSheet("Histogram");
        Row row = summarySheet.getRow(1);
        Cell cell = row.getCell(0);
        assertEquals(1, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(2);
        cell = row.getCell(0);
        assertEquals(2, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(2, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(3);
        cell = row.getCell(0);
        assertEquals(3, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(4);
        cell = row.getCell(0);
        assertEquals(4, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(5);
        cell = row.getCell(0);
        assertEquals(5, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(6);
        cell = row.getCell(0);
        assertEquals(6, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(7);
        cell = row.getCell(0);
        assertEquals(7, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(8);
        cell = row.getCell(0);
        assertEquals(8, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(10);
        cell = row.getCell(0);
        assertEquals("Total", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(5, cell.getNumericCellValue(), 0);


        row = summarySheet.getRow(13);
        cell = row.getCell(0);
        assertEquals(1, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(2, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(14);
        cell = row.getCell(0);
        assertEquals(2, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(3, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(15);
        cell = row.getCell(0);
        assertEquals(3, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(16);
        cell = row.getCell(0);
        assertEquals(4, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(17);
        cell = row.getCell(0);
        assertEquals(5, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(18);
        cell = row.getCell(0);
        assertEquals(6, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(19);
        cell = row.getCell(0);
        assertEquals(7, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(20);
        cell = row.getCell(0);
        assertEquals(8, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(0, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(22);
        cell = row.getCell(0);
        assertEquals("Total", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(6, cell.getNumericCellValue(), 0);
    }

    private void testCountSheet(Workbook workbook) {
        Sheet summarySheet = workbook.getSheet("Error Count");
        Row row = summarySheet.getRow(1);
        Cell cell = row.getCell(0);
        assertEquals("MTA Maryland Trip Updates", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(6, cell.getNumericCellValue(), 0);
        cell = row.getCell(3);
        assertEquals("HART Vehicle Positions", cell.getStringCellValue());
        cell = row.getCell(4);
        assertEquals(3, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(2);
        cell = row.getCell(0);
        assertEquals("MTA Maryland Vehicle Locations", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(4, cell.getNumericCellValue(), 0);
        cell = row.getCell(3);
        assertEquals("HART Trip Updates", cell.getStringCellValue());
        cell = row.getCell(4);
        assertEquals(2, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(3);
        cell = row.getCell(0);
        assertEquals("MetroTransit Trip Updates", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(2, cell.getNumericCellValue(), 0);
        cell = row.getCell(3);
        assertEquals("MTA Maryland Trip Updates", cell.getStringCellValue());
        cell = row.getCell(4);
        assertEquals(2, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(4);
        cell = row.getCell(0);
        assertEquals("MetroTransit Vehicle Positions", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(2, cell.getNumericCellValue(), 0);
        cell = row.getCell(3);
        assertEquals("MetroTransit Trip Updates", cell.getStringCellValue());
        cell = row.getCell(4);
        assertEquals(2, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(5);
        cell = row.getCell(0);
        assertEquals("HART Trip Updates", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);
        cell = row.getCell(3);
        assertEquals("MTA Maryland Vehicle Locations", cell.getStringCellValue());
        cell = row.getCell(4);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(6);
        cell = row.getCell(3);
        assertEquals("MetroTransit Vehicle Positions", cell.getStringCellValue());
        cell = row.getCell(4);
        assertEquals(1, cell.getNumericCellValue(), 0);
    }

    private void testFrequencySheet(Workbook workbook) {
        Sheet summarySheet = workbook.getSheet("Error Frequency");
        Row row = summarySheet.getRow(1);
        Cell cell = row.getCell(0);
        assertEquals("E017 - " + ValidationRules.E017.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(4, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(2);
        cell = row.getCell(0);
        assertEquals("E003 - " + ValidationRules.E003.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(2, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(3);
        cell = row.getCell(0);
        assertEquals("E022 - " + ValidationRules.E022.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(2, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(4);
        cell = row.getCell(0);
        assertEquals("E011 - " + ValidationRules.E011.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(5);
        cell = row.getCell(0);
        assertEquals("E028 - " + ValidationRules.E028.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(6);
        cell = row.getCell(0);
        assertEquals("E029 - " + ValidationRules.E029.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(7);
        cell = row.getCell(0);
        assertEquals("E041 - " + ValidationRules.E041.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(8);
        cell = row.getCell(0);
        assertEquals("W009 - " + ValidationRules.W009.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(6, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(9);
        cell = row.getCell(0);
        assertEquals("W001 - " + ValidationRules.W001.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(4, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(10);
        cell = row.getCell(0);
        assertEquals("W004 - " + ValidationRules.W004.getTitle(), cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);
    }

    private void testSummarySheet(Workbook workbook) {
        Sheet summarySheet = workbook.getSheet("Summary");
        Row row = summarySheet.getRow(0);
        Cell cell = row.getCell(0);
        assertEquals("Feeds with errors", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals("Feeds without errors", cell.getStringCellValue());

        row = summarySheet.getRow(1);
        cell = row.getCell(0);
        assertEquals(5, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(2, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(3);
        cell = row.getCell(0);
        assertEquals("Feeds with warnings", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals("Feeds without warnings", cell.getStringCellValue());

        row = summarySheet.getRow(4);
        cell = row.getCell(0);
        assertEquals(6, cell.getNumericCellValue(), 0);
        cell = row.getCell(1);
        assertEquals(1, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(12);
        cell = row.getCell(0);
        assertEquals("Total feeds processed", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(7, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(13);
        cell = row.getCell(0);
        assertEquals("Feeds with errors", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(5, cell.getNumericCellValue(), 0);

        row = summarySheet.getRow(14);
        cell = row.getCell(0);
        assertEquals("Feeds with warnings", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals(6, cell.getNumericCellValue() , 0);
    }

    private void testDataSheet(Workbook workbook) {
        Sheet dataSheet = workbook.getSheet("Data");
        for (int rowIndex = 1; rowIndex < 8; rowIndex++) {
            Row row = dataSheet.getRow(rowIndex);
            Cell cell = row.getCell(0);
            // We can't assume hard-coded row entries because on Travis it outputs in different order than Windows
            switch (cell.getStringCellValue()) {
                case "MTA Maryland Trip Updates":
                    cell = row.getCell(1);
                    assertEquals("Maryland, USA", cell.getStringCellValue());
                    cell = row.getCell(2);
                    assertEquals("E022, E037, E041, E003, E004, E011", cell.getStringCellValue());
                    cell = row.getCell(3);
                    assertEquals("W001, W009", cell.getStringCellValue());
                    break;
                case "MTA Maryland Vehicle Locations":
                    cell = row.getCell(1);
                    assertEquals("Maryland, USA", cell.getStringCellValue());
                    cell = row.getCell(2);
                    assertEquals("E028, E017, E003, E004", cell.getStringCellValue());
                    cell = row.getCell(3);
                    assertEquals("W009", cell.getStringCellValue());
                    break;
                case "HART Trip Updates":
                    cell = row.getCell(1);
                    assertEquals("Tampa, FL, USA", cell.getStringCellValue());
                    cell = row.getCell(2);
                    assertEquals("E017", cell.getStringCellValue());
                    cell = row.getCell(3);
                    assertEquals("W001, W009", cell.getStringCellValue());
                    break;
                case "HART Vehicle Positions":
                    cell = row.getCell(1);
                    assertEquals("Tampa, FL, USA", cell.getStringCellValue());
                    cell = row.getCell(2);
                    assertEquals("", cell.getStringCellValue());
                    cell = row.getCell(3);
                    assertEquals("W004, W001, W009", cell.getStringCellValue());
                    break;
                case "MetroTransit Service Alerts":
                    cell = row.getCell(1);
                    assertEquals("Halifax, NS, Canada", cell.getStringCellValue());
                    cell = row.getCell(2);
                    assertEquals("", cell.getStringCellValue());
                    cell = row.getCell(3);
                    assertEquals("", cell.getStringCellValue());
                    break;
                case "MetroTransit Trip Updates":
                    cell = row.getCell(1);
                    assertEquals("Halifax, NS, Canada", cell.getStringCellValue());
                    cell = row.getCell(2);
                    assertEquals("E017, E022", cell.getStringCellValue());
                    cell = row.getCell(3);
                    assertEquals("W001, W009", cell.getStringCellValue());
                    break;
                case "MetroTransit Vehicle Positions":
                    cell = row.getCell(1);
                    assertEquals("Halifax, NS, Canada", cell.getStringCellValue());
                    cell = row.getCell(2);
                    assertEquals("E029, E017", cell.getStringCellValue());
                    cell = row.getCell(3);
                    assertEquals("W009", cell.getStringCellValue());
                    break;
            }
        }
    }
}
