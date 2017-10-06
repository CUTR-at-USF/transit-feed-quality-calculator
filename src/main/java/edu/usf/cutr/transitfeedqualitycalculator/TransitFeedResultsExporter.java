package edu.usf.cutr.transitfeedqualitycalculator;

import java.io.*;
import java.util.*;

import edu.usf.cutr.transitfeedqualitycalculator.model.Agency;
import edu.usf.cutr.transitfeedqualitycalculator.model.Feed;
import edu.usf.cutr.transitfeedqualitycalculator.model.OutputData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TransitFeedResultsExporter {

    OutputData outputCollection;
    File excelFile = new File("graphs.xlsx");
    Workbook workbook = new XSSFWorkbook();
    Sheet dataSheet = workbook.createSheet("Data");
    Integer dataSheetRowIndex = 0;
    Sheet graphSheet = workbook.createSheet("Graphs");
    Integer graphSheetRowIndex = 0;

    TransitFeedResultsExporter(OutputData outputCollection) {
        this.outputCollection = outputCollection;
    }

    public void createOutputExcel() throws java.io.FileNotFoundException, java.io.IOException {
        String sheetName = "Data";//name of sheet
        int cellIndex = 0;
        XSSFWorkbook workbook = new XSSFWorkbook();
        fillDataSheet();
        fillGraphSheet();
    }

    private void fillGraphSheet() throws java.io.IOException {
        Row row;
        Cell cell;
        CellStyle cellStyle;
        Map<String, Integer> countMap;

        createCustomGraphCells("Most Frequent Errors", "Count");
        Iterator it = outputCollection.getErrorMap().entrySet().iterator();
        countMap = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            countMap.put(pair.getKey().toString(), ((List) pair.getValue()).size());
        }
        countMap = sortMap(countMap);
        createGraphCells(countMap);
        graphSheet.createRow(graphSheetRowIndex++);

        createCustomGraphCells("Most Frequent Warnings", "Count");
        it = outputCollection.getWarningMap().entrySet().iterator();
        countMap = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            countMap.put(pair.getKey().toString(), ((List) pair.getValue()).size());
        }
        countMap = sortMap(countMap);
        createGraphCells(countMap);
        graphSheet.createRow(graphSheetRowIndex++);

        createCustomGraphCells("Error Count in Feeds", "Count");
        countMap = new HashMap<>();
        for (Agency agency : outputCollection.getAgencies()) {
            for (Feed feed : agency.getFeedList()) {
                if (feed.getErrorList().size() > 0) {
                    countMap.put(feed.getName(), feed.getErrorList().size());
                }
            }
        }
        countMap = sortMap(countMap);
        createGraphCells(countMap);
        graphSheet.createRow(graphSheetRowIndex++);

        createCustomGraphCells("Warning Count in Feeds", "Count");
        for (Agency agency : outputCollection.getAgencies()) {
            for (Feed feed : agency.getFeedList()) {
                if (feed.getWarningList().size() > 0) {
                    countMap.put(feed.getName(), feed.getWarningList().size());
                }
            }
        }
        countMap = sortMap(countMap);
        createGraphCells(countMap);
        autosizeGraphSheet();
        flushOutput();
    }

    private Map<String, Integer> sortMap(Map<String, Integer> map) {
        TreeMap<String, Integer> sorted = new TreeMap<>(map);
        Set<Map.Entry<String, Integer>> entries = sorted.entrySet();
        List<Map.Entry<String, Integer>> listOfEntries = new ArrayList<Map.Entry<String, Integer>>(entries);
        Comparator<Map.Entry<String, Integer>> comparator = (e1, e2) -> {
            Integer v1 = e1.getValue();
            Integer v2 = e2.getValue();
            return v2.compareTo(v1);
        };
        Collections.sort(listOfEntries, comparator);
        LinkedHashMap<String, Integer> sortedByValue = new LinkedHashMap<String, Integer>(listOfEntries.size());
        for (Map.Entry<String, Integer> entry : listOfEntries) {
            sortedByValue.put(entry.getKey(), entry.getValue());
        }
        return sortedByValue;
    }

    private void createCustomGraphCells(String col1, String col2) {
        int cellIndex = 0;
        Row row;
        Cell cell;
        CellStyle cellStyle;
        Map<String, Integer> countMap;
        row = graphSheet.createRow(graphSheetRowIndex++);
        cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(col1);
        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(col2);
    }

    private void createGraphCells(Map<String, Integer> countMap) {
        Row row;
        Cell cell;
        Iterator it = countMap.entrySet().iterator();
        while (it.hasNext()) {
            int cellIndex = 0;
            Map.Entry pair = (Map.Entry) it.next();
            row = graphSheet.createRow(graphSheetRowIndex++);
            cell = row.createCell(cellIndex++);
            cell.setCellValue(pair.getKey().toString());
            cell = row.createCell(cellIndex++);
            cell.setCellValue(Integer.parseInt(pair.getValue().toString()));
        }
    }

    private void fillDataSheet() throws java.io.IOException {
        initalizeDataSheet();
        for (Agency agency : outputCollection.getAgencies()) {
            for (Feed feed : agency.getFeedList()) {
                int cellIndex = 0;
                Row row = dataSheet.createRow(dataSheetRowIndex++);
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(feed.getName());
                cell = row.createCell(cellIndex++);
                cell.setCellValue(agency.getLocation());
                cell = row.createCell(cellIndex++);
                cell.setCellValue(feed.getErrors().trim().replaceAll(" ", ", "));
                cell = row.createCell(cellIndex++);
                cell.setCellValue(feed.getWarnings().trim().replaceAll(" ", ", "));
            }
        }
        autosizeDataSheet();
        flushOutput();
    }

    private void initalizeDataSheet() throws java.io.IOException {
        int cellIndex = 0;
        Row row = dataSheet.createRow(dataSheetRowIndex++);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Cell cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("FEED");
        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("LOCATION");
        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("ERRORS");
        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("WARNINGS");
        dataSheet.autoSizeColumn(0);
        flushOutput();

    }

    private void flushOutput() throws java.io.IOException {
        FileOutputStream fileOut = new FileOutputStream(excelFile);
        workbook.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }

    private void autosizeDataSheet() {
        dataSheet.autoSizeColumn(0);
        dataSheet.autoSizeColumn(1);
        dataSheet.autoSizeColumn(2);
        dataSheet.autoSizeColumn(3);
    }

    private void autosizeGraphSheet() {
        graphSheet.autoSizeColumn(0);
        graphSheet.autoSizeColumn(1);
    }
}
