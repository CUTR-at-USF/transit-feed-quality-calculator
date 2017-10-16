package edu.usf.cutr.transitfeedqualitycalculator.util;

public class ErrorDescription {
    public static final String E001 = "Not in POSIX time";
    public static final String E002 = "Unsorted stop_sequence";
    public static final String E003 = "GTFS-rt trip_id does not exist in GTFS data";
    public static final String E004 = "GTFS-rt route_id does not exist in GTFS data";
    public static final String E006 = "Missing required trip field for frequency-based exact_times = 0";
    public static final String E009 = "GTFS-rt stop_sequence isn't provided for trip that visits same stop_id more than once";
    public static final String E010 = "location_type not 0 in stops.txt (Note that this is implemented but not executed because it's specific to GTFS - see issue #126)";
    public static final String E011 = "GTFS-rt stop_id does not exist in GTFS data";
    public static final String E012 = "Header timestamp should be greater than or equal to all other timestamps";
    public static final String E013 = "Frequency type 0 trip schedule_relationship should be UNSCHEDULED or empty";
    public static final String E015 = "All stop_ids referenced in GTFS-rt feeds must have the location_type = 0";
    public static final String E016 = "trip_ids with schedule_relationship ADDED must not be in GTFS data";
    public static final String E017 = "GTFS-rt content changed but has the same header timestamp";
    public static final String E018 = "GTFS-rt header timestamp decreased between two sequential iterations";
    public static final String E019 = "GTFS-rt frequency type 1 trip start_time must be a multiple of GTFS headway_secs later than GTFS start_time";
    public static final String E020 = "Invalid start_time format";
    public static final String E021 = "Invalid start_date format";
    public static final String E022 = "Sequential stop_time_update times are not increasing";
    public static final String E023 = "trip start_time does not match first GTFS arrival_time";
    public static final String E024 = "trip direction_id does not match GTFS data";
    public static final String E025 = "stop_time_update departure time is before arrival time";
    public static final String E026 = "Invalid vehicle position";
    public static final String E027 = "Invalid vehicle bearing";
    public static final String E028 = "Vehicle position outside agency coverage area";
    public static final String E029 = "Vehicle position far from trip shape";
    public static final String E030 = "GTFS-rt alert trip_id does not belong to GTFS-rt alert route_id in GTFS trips.txt";
    public static final String E031 = "Alert informed_entity.route_id does not match informed_entity.trip.route_id";
    public static final String E032 = "Alert does not have an informed_entity";
    public static final String E033 = "Alert informed_entity does not have any specifiers";
    public static final String E034 = "GTFS-rt agency_id does not exist in GTFS data";
    public static final String E035 = "GTFS-rt trip.trip_id does not belong to GTFS-rt trip.route_id in GTFS trips.txt";
    public static final String E036 = "Sequential stop_time_updates have the same stop_sequence";
    public static final String E037 = "Sequential stop_time_updates have the same stop_id";
    public static final String E038 = "Invalid header.gtfs_realtime_version";
    public static final String E039 = "FULL_DATASET feeds should not include entity.is_deleted";
    public static final String E040 = "stop_time_update doesn't contain stop_id or stop_sequence";
    public static final String E041 = "trip doesn't have any stop_time_updates";
    public static final String E042 = "arrival or departure provided for NO_DATA stop_time_update";
    public static final String E043 = "stop_time_update doesn't have arrival or departure";
    public static final String E044 = "stop_time_update arrival/departure doesn't have delay or time";
    public static final String E045 = "GTFS-rt stop_time_update stop_sequence and stop_id do not match GTFS";
    public static final String E046 = "GTFS-rt stop_time_update without time doesn't have arrival/departure time in GTFS";
    public static final String E047 = "VehiclePosition and TripUpdate ID pairing mismatch";


}
