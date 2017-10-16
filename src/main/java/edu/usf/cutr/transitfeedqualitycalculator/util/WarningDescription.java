package edu.usf.cutr.transitfeedqualitycalculator.util;

public class WarningDescription {
    public static final String W001 = "timestamps not populated";
    public static final String W002 = "vehicle_id not populated";
    public static final String W003 = "ID in one feed missing from the other";
    public static final String W004 = "vehicle speed is unrealistic";
    public static final String W005 = "Missing vehicle_id in trip_update for frequency-based exact_times = 0";
    public static final String W006 = "trip_update missing trip_id";
    public static final String W007 = "Refresh interval is more than 35 seconds";
    public static final String W008 = "Header timestamp is older than 65 seconds";
    public static final String W009 = "schedule_relationship not populated";

}
