package edu.usf.cutr.transitfeedqualitycalculator;

import java.io.*;
import java.util.*;

import edu.usf.cutr.transitfeedqualitycalculator.model.Agency;
import edu.usf.cutr.transitfeedqualitycalculator.model.Feed;
import edu.usf.cutr.transitfeedqualitycalculator.model.OutputData;
import edu.usf.cutr.transitfeedqualitycalculator.util.ErrorDescription;
import edu.usf.cutr.transitfeedqualitycalculator.util.WarningDescription;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TransitFeedResultsExporter {

    OutputData outputCollection;
    Workbook workbook;
    Sheet dataSheet, graphSheet, countSheet, histogramSheet;
    Integer dataSheetRowIndex = 0, graphSheetRowIndex = 0, countSheetRowIndex = 0, histogramSheetRowIndex = 0;
    Integer totalFeeds = 0, feedsWithErrors = 0, feedsWithWarnings = 0;
    CellStyle cellStyle;

    TransitFeedResultsExporter(OutputData outputCollection) throws IOException {
        this.outputCollection = outputCollection;
        workbook = new XSSFWorkbook(new FileInputStream("template.xlsx") );
        dataSheet = workbook.getSheet("1-Data");
        graphSheet = workbook.getSheet("1-Error Frequency");
        countSheet = workbook.getSheet("1-Error Count");
        histogramSheet = workbook.getSheet("1-Histogram");
        cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    public void createOutputExcel() throws java.io.FileNotFoundException, java.io.IOException, NoSuchFieldException, IllegalAccessException {
        fillDataSheet();
        fillGraphSheet();
        fillCountSheet();
        fillHistogramSheet();
        flushOutput();
    }

    private void fillHistogramSheet() {
        int[] numberOfErrors = {0, 0, 0, 0, 0, 0, 0, 0};
        int[] numberOfWarnings = {0, 0, 0, 0, 0, 0, 0, 0};
        Row row;
        Cell cell;
        row = histogramSheet.createRow(histogramSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Number of Errors");
        cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Frequency");

        Iterator it = outputCollection.getAgencies().iterator();
        while (it.hasNext()) {
            Iterator i = ((Agency)it.next()).getFeedList().iterator();
            while (i.hasNext()) {
                Feed f = (Feed) i.next();
                if (f.getErrorList().size()>0 && f.getErrorList().size()<8) {
                    numberOfErrors[f.getErrorList().size()]++;
                }
                if (f.getWarningList().size()>0 && f.getWarningList().size()<8) {
                    numberOfWarnings[f.getWarningList().size()]++;
                }
            }
        }
       for (int i = 0; i < numberOfErrors.length; i++) {
            row = histogramSheet.createRow(histogramSheetRowIndex++);
            cell = row.createCell(0);
            cell.setCellValue(i+1);
            cell = row.createCell(1);
            cell.setCellValue(numberOfErrors[i]);
        }
        row = histogramSheet.createRow(histogramSheetRowIndex++);
        row = histogramSheet.createRow(histogramSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellValue("Total");
        cell = row.createCell(1);
        int sum = 0;
        for (int i : numberOfErrors)
            sum += i;
        cell.setCellValue(sum);
        row = histogramSheet.createRow(histogramSheetRowIndex++);

        row = histogramSheet.createRow(histogramSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Number of Warnings");
        cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Frequency");
        for (int i = 0; i < numberOfWarnings.length; i++) {
            row = histogramSheet.createRow(histogramSheetRowIndex++);
            cell = row.createCell(0);
            cell.setCellValue(i+1);
            cell = row.createCell(1);
            cell.setCellValue(numberOfWarnings[i]);
        }
        row = histogramSheet.createRow(histogramSheetRowIndex++);
        row = histogramSheet.createRow(histogramSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellValue("Total");
        cell = row.createCell(1);
        sum = 0;
        for (int i : numberOfWarnings)
            sum += i;
        cell.setCellValue(sum);
        autosizeHistogramSheet();
    }

    private void fillCountSheet() {
        Row row;
        Cell cell;
        Map<String, Integer> countMap;
        for (int i = 0; i < totalFeeds; i++) {
            row = countSheet.createRow(countSheetRowIndex++);
        }
        countSheetRowIndex = 0;
        row = countSheet.createRow(countSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Error Count in Feeds");
        cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Count");

        Iterator it = outputCollection.getAgencies().iterator();
        countMap = new HashMap<>();
        while (it.hasNext()) {
            Iterator i = ((Agency)it.next()).getFeedList().iterator();
            while (i.hasNext()) {
               Feed f = (Feed) i.next();
               if (f.getErrorList().size()>0) {
                   countMap.put(f.getName(), f.getErrorList().size());
               }
            }
        }
        countMap = sortMap(countMap);
        createCells(countMap, countSheetRowIndex, countSheet, 0);

        countSheetRowIndex = 0;
        row = countSheet.getRow(countSheetRowIndex++);
        cell = row.createCell(3);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Warning Count in Feeds");
        cell = row.createCell(4);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Count");

        it = outputCollection.getAgencies().iterator();
        countMap = new HashMap<>();
        while (it.hasNext()) {
            Iterator i = ((Agency)it.next()).getFeedList().iterator();
            while (i.hasNext()) {
                Feed f = (Feed) i.next();
                if (f.getWarningList().size()>0) {
                    countMap.put(f.getName(), f.getWarningList().size());
                }
            }
        }
        countMap = sortMap(countMap);
        it = countMap.entrySet().iterator();
        while (it.hasNext()) {
            int cellIndex = 3;
            Map.Entry pair = (Map.Entry) it.next();
            row = countSheet.getRow(countSheetRowIndex++);
            cell = row.createCell(cellIndex++);
            cell.setCellValue(pair.getKey().toString());
            cell = row.createCell(cellIndex++);
            cell.setCellValue(Integer.parseInt(pair.getValue().toString()));
        }
        autosizeColumnSheet();
    }

    private void fillGraphSheet() throws java.io.IOException, NoSuchFieldException, IllegalAccessException {
        Row row;
        Cell cell;
        Map<String, Integer> countMap;

        row = graphSheet.createRow(graphSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Most Frequent");
        cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Count");
        Iterator it = outputCollection.getErrorMap().entrySet().iterator();
        countMap = new HashMap<>();
        int count = 0;
        while (it.hasNext() && count < 7) {
            Map.Entry pair = (Map.Entry) it.next();
            countMap.put(pair.getKey().toString() + " - " + ErrorDescription.class.getField(pair.getKey().toString()).get(new ErrorDescription()) , ((List) pair.getValue()).size());
            count++;
        }
        countMap = sortMap(countMap);
        it = countMap.entrySet().iterator();
        while (it.hasNext()) {
            int cellIndex = 0;
            Map.Entry pair = (Map.Entry) it.next();
            row = graphSheet.createRow(graphSheetRowIndex++);
            cell = row.createCell(cellIndex++);
            cell.setCellValue(pair.getKey().toString());
            cell = row.createCell(cellIndex++);
            cell.setCellValue(Integer.parseInt(pair.getValue().toString()));
        }

        it = outputCollection.getWarningMap().entrySet().iterator();
        countMap = new HashMap<>();
        count = 0;
        while (it.hasNext() && count < 7) {
            Map.Entry pair = (Map.Entry) it.next();
            countMap.put(pair.getKey().toString() + " - " + WarningDescription.class.getField(pair.getKey().toString()).get(new WarningDescription()), ((List) pair.getValue()).size());
            count++;
        }
        countMap = sortMap(countMap);
        //createCells(countMap, graphSheetRowIndex, graphSheet, 0);
        it = countMap.entrySet().iterator();
        while (it.hasNext()) {
            int cellIndex = 0;
            Map.Entry pair = (Map.Entry) it.next();
            row = graphSheet.createRow(graphSheetRowIndex++);
            cell = row.createCell(cellIndex++);
            cell.setCellValue(pair.getKey().toString());
            cell = row.createCell(cellIndex++);
            cell.setCellValue(Integer.parseInt(pair.getValue().toString()));
        }
        autosizeGraphSheet();
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

    private void createCells(Map<String, Integer> countMap, Integer rowIndex, Sheet sheet, Integer colStart) {
        Row row;
        Cell cell;
        Iterator it = countMap.entrySet().iterator();
        while (it.hasNext()) {
            int cellIndex = colStart;
            Map.Entry pair = (Map.Entry) it.next();
            row = sheet.createRow(rowIndex++);
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
                if (!"".equals(feed.getErrors().trim().replaceAll(" ", ", "))) {
                    feedsWithErrors++;
                }
                cell = row.createCell(cellIndex++);
                cell.setCellValue(feed.getWarnings().trim().replaceAll(" ", ", "));
                if (!"".equals(feed.getWarnings().trim().replaceAll(" ", ", "))) {
                    feedsWithWarnings++;
                }
                totalFeeds++;
            }
        }
        fillChartData();
        autosizeDataSheet();
    }

    private void fillChartData() {
        Row row = dataSheet.createRow(132);
        Cell cell = row.createCell(0);
        cell.setCellValue("Feeds with errors");
        cell = row.createCell(1);
        cell.setCellValue("Feeds without errors");

        row = dataSheet.createRow(133);
        cell = row.createCell(0);
        cell.setCellValue(feedsWithErrors);
        cell = row.createCell(1);
        cell.setCellValue(totalFeeds - feedsWithErrors);

        row = dataSheet.createRow(135);
        cell = row.createCell(0);
        cell.setCellValue("Feeds with warnings");
        cell = row.createCell(1);
        cell.setCellValue("Feeds without warnings");

        row = dataSheet.createRow(136);
        cell = row.createCell(0);
        cell.setCellValue(feedsWithWarnings);
        cell = row.createCell(1);
        cell.setCellValue(totalFeeds - feedsWithWarnings);

        row = dataSheet.createRow(143);
        cell = row.createCell(0);
        cell.setCellValue("Total feeds processed");
        cell = row.createCell(1);
        cell.setCellValue(totalFeeds);

        row = dataSheet.createRow(144);
        cell = row.createCell(0);
        cell.setCellValue("Feeds with errors");
        cell = row.createCell(1);
        cell.setCellValue(feedsWithErrors);

        row = dataSheet.createRow(145);
        cell = row.createCell(0);
        cell.setCellValue("Feeds with warnings");
        cell = row.createCell(1);
        cell.setCellValue(feedsWithWarnings);
    }

    private void initalizeDataSheet() throws java.io.IOException {
        int cellIndex = 0;
        Row row = dataSheet.createRow(dataSheetRowIndex++);
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

    }

    private void flushOutput() throws java.io.IOException {
        FileOutputStream fileOut = new FileOutputStream("graphs.xlsx");
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

    private void autosizeColumnSheet() {
        countSheet.autoSizeColumn(0);
        countSheet.autoSizeColumn(1);
        countSheet.autoSizeColumn(3);
        countSheet.autoSizeColumn(4);
    }

    private void autosizeHistogramSheet() {
        histogramSheet.autoSizeColumn(0);
        histogramSheet.autoSizeColumn(1);
    }
}
