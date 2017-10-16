package edu.usf.cutr.transitfeedqualitycalculator.model;

import java.util.List;
import java.util.Map;

public class AnalysisOutput {

    List<Agency> agencies;
    Map<String, List<Feed>> errorMap;
    Map<String, List<Feed>> warningMap;

    public List<Agency> getAgencies() {
        return agencies;
    }

    public Map<String, List<Feed>> getErrorMap() {
        return errorMap;
    }

    public void setErrorMap(Map<String, List<Feed>> errorMap) {
        this.errorMap = errorMap;
    }

    public Map<String, List<Feed>> getWarningMap() {
        return warningMap;
    }

    public void setWarningMap(Map<String, List<Feed>> warningMap) {
        this.warningMap = warningMap;
    }

    public void setAgencies(List<Agency> agencies) {
        this.agencies = agencies;
    }

}
