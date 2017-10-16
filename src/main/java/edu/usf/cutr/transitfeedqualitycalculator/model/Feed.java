package edu.usf.cutr.transitfeedqualitycalculator.model;

import edu.usf.cutr.gtfsrtvalidator.helper.ErrorListHelperModel;

import java.util.List;

public class Feed extends Agency {
    private List<ErrorListHelperModel> errorList;
    private List<ErrorListHelperModel> warningList;
    private String errors;
    private String warnings;

    public List<ErrorListHelperModel> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorListHelperModel> errorList) {
        this.errorList = errorList;
    }

    public List<ErrorListHelperModel> getWarningList() {
        return warningList;
    }

    public void setWarningList(List<ErrorListHelperModel> warningList) {
        this.warningList = warningList;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }
}
