package edu.usf.cutr.transitfeedqualitycalculator;

import edu.usf.cutr.gtfsrtvalidator.lib.model.ValidationRule;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.ValidationRules;
import edu.usf.cutr.transitfeedqualitycalculator.model.Agency;
import edu.usf.cutr.transitfeedqualitycalculator.model.AnalysisOutput;
import edu.usf.cutr.transitfeedqualitycalculator.model.Feed;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Exports a summary of the analysis to an Excel spreadsheet, using the provided tamplate.xlsx for graph formats
 */
public class ExcelExporter {

    private AnalysisOutput mAnalysisOutput;
    private final static String TEMPLATE_FILE_NAME = "template.xlsx";
    private String mOutputFileName = "analysis-graphs.xlsx";
    private Workbook mWorkbook;
    private Sheet mDataSheet, mSummarySheet, mFrequencySheet, mCountSheet, mHistogramSheet, mRulesSheet;
    private Integer mDataSheetRowIndex = 0, mGraphSheetRowIndex = 0, mCountSheetRowIndex = 0, mHistogramSheetRowIndex = 0, mRulesSheetRowIndex = 0;
    private Integer mTotalFeeds = 0, mFeedsWithErrors = 0, mFeedsWithWarnings = 0;
    private CellStyle mCellStyle;

    public ExcelExporter(AnalysisOutput analysisOutput) throws IOException {
        mAnalysisOutput = analysisOutput;
        mWorkbook = new XSSFWorkbook(new FileInputStream(TEMPLATE_FILE_NAME) );
        mDataSheet = mWorkbook.getSheet("Data");
        mSummarySheet = mWorkbook.getSheet("Summary");
        mFrequencySheet = mWorkbook.getSheet("Error Frequency");
        mCountSheet = mWorkbook.getSheet("Error Count");
        mHistogramSheet = mWorkbook.getSheet("Histogram");
        mRulesSheet = mWorkbook.getSheet("Rules");
        mCellStyle = mWorkbook.createCellStyle();
        mCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        mCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    public void export() throws java.io.IOException, NoSuchFieldException, IllegalAccessException {
        fillDataSheet();
        fillSummarySheet();
        fillGraphSheet();
        fillCountSheet();
        fillHistogramSheet();
        fillRulesSheet();
        flushOutput();
    }

    /**
     * Gets the Excel output file name
     * @return the Excel output file name
     */
    public String getOutputFileName() {
        return mOutputFileName;
    }

    /**
     * Sets the Excel output file name
     * @param outputFileName the Excel output file name
     */
    public void setOutputFileName(String outputFileName) {
        mOutputFileName = outputFileName;
    }

    private void fillRulesSheet() {
        Row row;
        Cell cell;
        mRulesSheetRowIndex = 0;
        row = mRulesSheet.createRow(mRulesSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Error Id");
        cell = row.createCell(1);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Error Description");

        for (ValidationRule rule : ValidationRules.getRules()) {
            if ("ERROR".equals(rule.getSeverity())) {
                row = mRulesSheet.createRow(mRulesSheetRowIndex++);
                cell = row.createCell(0);
                cell.setCellValue(rule.getErrorId());
                cell = row.createCell(1);
                cell.setCellValue(rule.getTitle());
            }
        }
        row = mRulesSheet.createRow(mRulesSheetRowIndex++);
        row = mRulesSheet.createRow(mRulesSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Warning Id");
        cell = row.createCell(1);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Warning Description");
        for (ValidationRule rule : ValidationRules.getRules()) {
            if ("WARNING".equals(rule.getSeverity())) {
                row = mRulesSheet.createRow(mRulesSheetRowIndex++);
                cell = row.createCell(0);
                cell.setCellValue(rule.getErrorId());
                cell = row.createCell(1);
                cell.setCellValue(rule.getTitle());
            }
        }
        autosizeRulesSheet();
    }

    private void fillHistogramSheet() {
        final int HISTOGRAM_WIDTH = 8;
        int[] numberOfErrors = {0, 0, 0, 0, 0, 0, 0, 0};
        int[] numberOfWarnings = {0, 0, 0, 0, 0, 0, 0, 0};
        Row row;
        Cell cell;
        row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Number of Errors");
        cell = row.createCell(1);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Frequency");

        Iterator it = mAnalysisOutput.getAgencies().iterator();
        while (it.hasNext()) {
            Iterator i = ((Agency)it.next()).getFeedList().iterator();
            while (i.hasNext()) {
                Feed f = (Feed) i.next();
                if (f.getErrorList().size() > 0 && f.getErrorList().size() < HISTOGRAM_WIDTH) {
                    numberOfErrors[f.getErrorList().size()-1]++;
                }
                if (f.getWarningList().size() > 0 && f.getWarningList().size() < HISTOGRAM_WIDTH) {
                    numberOfWarnings[f.getWarningList().size()-1]++;
                }
            }
        }
       for (int i = 0; i < numberOfErrors.length; i++) {
            row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);
            cell = row.createCell(0);
            cell.setCellValue(i+1);
            cell = row.createCell(1);
            cell.setCellValue(numberOfErrors[i]);
        }
        row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);
        row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellValue("Total");
        cell = row.createCell(1);
        int sum = 0;
        for (int i : numberOfErrors)
            sum += i;
        cell.setCellValue(sum);
        row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);

        row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Number of Warnings");
        cell = row.createCell(1);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Frequency");
        for (int i = 0; i < numberOfWarnings.length; i++) {
            row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);
            cell = row.createCell(0);
            cell.setCellValue(i+1);
            cell = row.createCell(1);
            cell.setCellValue(numberOfWarnings[i]);
        }
        row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);
        row = mHistogramSheet.createRow(mHistogramSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellValue("Total");
        cell = row.createCell(1);
        sum = 0;
        for (int i : numberOfWarnings) {
            sum += i;
        }
        cell.setCellValue(sum);
        autosizeHistogramSheet();
    }

    private void fillCountSheet() {
        Row row;
        Cell cell;
        Map<String, Integer> countMap;
        for (int i = 0; i < mTotalFeeds; i++) {
            row = mCountSheet.createRow(mCountSheetRowIndex++);
        }
        mCountSheetRowIndex = 0;
        row = mCountSheet.createRow(mCountSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Error Count in Feeds");
        cell = row.createCell(1);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Count");

        Iterator it = mAnalysisOutput.getAgencies().iterator();
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
        createCells(countMap, mCountSheetRowIndex, mCountSheet, 0);

        mCountSheetRowIndex = 0;
        row = mCountSheet.getRow(mCountSheetRowIndex++);
        cell = row.createCell(3);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Warning Count in Feeds");
        cell = row.createCell(4);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Count");

        it = mAnalysisOutput.getAgencies().iterator();
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
            row = mCountSheet.getRow(mCountSheetRowIndex++);
            cell = row.createCell(cellIndex++);
            cell.setCellValue(pair.getKey().toString());
            cell = row.createCell(cellIndex++);
            cell.setCellValue(Integer.parseInt(pair.getValue().toString()));
        }
        autosizeColumnSheet();
    }

    private void fillGraphSheet() throws NoSuchFieldException, IllegalAccessException {
        Row row;
        Cell cell;
        Map<String, Integer> countMap;

        row = mFrequencySheet.createRow(mGraphSheetRowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Most Frequent");
        cell = row.createCell(1);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("Count");
        Iterator it = mAnalysisOutput.getErrorMap().entrySet().iterator();
        countMap = new HashMap<>();
        int count = 0;
        while (it.hasNext() && count < 7) {
            Map.Entry pair = (Map.Entry) it.next();
            countMap.put(pair.getKey().toString() + " - " + ((ValidationRule)(ValidationRules.class
                    .getDeclaredField(pair.getKey().toString()).get(new ValidationRules()))).getTitle(), ((List) pair.getValue()).size());
            count++;
        }
        countMap = sortMap(countMap);
        it = countMap.entrySet().iterator();
        while (it.hasNext()) {
            int cellIndex = 0;
            Map.Entry pair = (Map.Entry) it.next();
            row = mFrequencySheet.createRow(mGraphSheetRowIndex++);
            cell = row.createCell(cellIndex++);
            cell.setCellValue(pair.getKey().toString());
            cell = row.createCell(cellIndex++);
            cell.setCellValue(Integer.parseInt(pair.getValue().toString()));
        }

        it = mAnalysisOutput.getWarningMap().entrySet().iterator();
        countMap = new HashMap<>();
        count = 0;
        while (it.hasNext() && count < 7) {
            Map.Entry pair = (Map.Entry) it.next();
            countMap.put(pair.getKey().toString() + " - " + ((ValidationRule)(ValidationRules.class
                    .getDeclaredField(pair.getKey().toString()).get(new ValidationRules()))).getTitle(), ((List) pair.getValue()).size());
            count++;
        }
        countMap = sortMap(countMap);
        it = countMap.entrySet().iterator();
        while (it.hasNext()) {
            int cellIndex = 0;
            Map.Entry pair = (Map.Entry) it.next();
            row = mFrequencySheet.createRow(mGraphSheetRowIndex++);
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
        listOfEntries.sort(comparator);
        LinkedHashMap<String, Integer> sortedByValue = new LinkedHashMap<>(listOfEntries.size());
        for (Map.Entry<String, Integer> entry : listOfEntries) {
            sortedByValue.put(entry.getKey(), entry.getValue());
        }
        return sortedByValue;
    }

    private void createCells(Map<String, Integer> countMap, Integer rowIndex, Sheet sheet, Integer colStart) {
        Row row;
        Cell cell;
        for (Object o : countMap.entrySet()) {
            int cellIndex = colStart;
            Map.Entry pair = (Map.Entry) o;
            row = sheet.createRow(rowIndex++);
            cell = row.createCell(cellIndex++);
            cell.setCellValue(pair.getKey().toString());
            cell = row.createCell(cellIndex++);
            cell.setCellValue(Integer.parseInt(pair.getValue().toString()));
        }
    }

    private void fillDataSheet() throws java.io.IOException {
        initalizeDataSheet();
        for (Agency agency : mAnalysisOutput.getAgencies()) {
            for (Feed feed : agency.getFeedList()) {
                int cellIndex = 0;
                Row row = mDataSheet.createRow(mDataSheetRowIndex++);
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(feed.getName());
                cell = row.createCell(cellIndex++);
                cell.setCellValue(agency.getLocation());
                cell = row.createCell(cellIndex++);
                cell.setCellValue(feed.getErrors().trim().replaceAll(" ", ", "));
                if (!"".equals(feed.getErrors().trim().replaceAll(" ", ", "))) {
                    mFeedsWithErrors++;
                }
                cell = row.createCell(cellIndex++);
                cell.setCellValue(feed.getWarnings().trim().replaceAll(" ", ", "));
                if (!"".equals(feed.getWarnings().trim().replaceAll(" ", ", "))) {
                    mFeedsWithWarnings++;
                }
                mTotalFeeds++;
            }
        }
        autosizeDataSheet();
    }

    private void fillSummarySheet() {
        Row row = mSummarySheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Feeds with errors");
        cell = row.createCell(1);
        cell.setCellValue("Feeds without errors");

        row = mSummarySheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(mFeedsWithErrors);
        cell = row.createCell(1);
        cell.setCellValue(mTotalFeeds - mFeedsWithErrors);

        row = mSummarySheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Feeds with warnings");
        cell = row.createCell(1);
        cell.setCellValue("Feeds without warnings");

        row = mSummarySheet.createRow(4);
        cell = row.createCell(0);
        cell.setCellValue(mFeedsWithWarnings);
        cell = row.createCell(1);
        cell.setCellValue(mTotalFeeds - mFeedsWithWarnings);

        row = mSummarySheet.createRow(12);
        cell = row.createCell(0);
        cell.setCellValue("Total feeds processed");
        cell = row.createCell(1);
        cell.setCellValue(mTotalFeeds);

        row = mSummarySheet.createRow(13);
        cell = row.createCell(0);
        cell.setCellValue("Feeds with errors");
        cell = row.createCell(1);
        cell.setCellValue(mFeedsWithErrors);

        row = mSummarySheet.createRow(14);
        cell = row.createCell(0);
        cell.setCellValue("Feeds with warnings");
        cell = row.createCell(1);
        cell.setCellValue(mFeedsWithWarnings);
    }

    private void initalizeDataSheet() throws java.io.IOException {
        int cellIndex = 0;
        Row row = mDataSheet.createRow(mDataSheetRowIndex++);
        Cell cell = row.createCell(cellIndex++);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("FEED");
        cell = row.createCell(cellIndex++);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("LOCATION");
        cell = row.createCell(cellIndex++);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("ERRORS");
        cell = row.createCell(cellIndex++);
        cell.setCellStyle(mCellStyle);
        cell.setCellValue("WARNINGS");
        mDataSheet.autoSizeColumn(0);

    }

    private void flushOutput() throws java.io.IOException {
        FileOutputStream fileOut = new FileOutputStream(mOutputFileName);
        mWorkbook.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }

    private void autosizeDataSheet() {
        mDataSheet.autoSizeColumn(0);
        mDataSheet.autoSizeColumn(1);
        mDataSheet.autoSizeColumn(2);
        mDataSheet.autoSizeColumn(3);
    }

    private void autosizeGraphSheet() {
        mFrequencySheet.autoSizeColumn(0);
        mFrequencySheet.autoSizeColumn(1);
    }

    private void autosizeColumnSheet() {
        mCountSheet.autoSizeColumn(0);
        mCountSheet.autoSizeColumn(1);
        mCountSheet.autoSizeColumn(3);
        mCountSheet.autoSizeColumn(4);
    }

    private void autosizeHistogramSheet() {
        mHistogramSheet.autoSizeColumn(0);
        mHistogramSheet.autoSizeColumn(1);
    }

    private void autosizeRulesSheet() {
        mRulesSheet.autoSizeColumn(0);
        mRulesSheet.autoSizeColumn(1);
        mRulesSheet.autoSizeColumn(2);
        mRulesSheet.autoSizeColumn(3);
    }
}
