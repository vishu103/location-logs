package in.edureal.locationlogs;

class ListItem {

    private int logId;
    private double logLatitude;
    private double logLongitude;
    private String logDate;
    private String logTime12;
    private String logTime24;
    private String logAddress;
    private String logReason;
    private String logMobile;
    private String logWifi;
    private int logBattery;
    private String logClimate;
    private double logTemperature;

    ListItem(int logId, double logLatitude, double logLongitude, String logDate, String logTime12, String logTime24, String logAddress, String logReason, String logMobile, String logWifi, int logBattery, String logClimate, double logTemperature) {
        this.logId = logId;
        this.logLatitude = logLatitude;
        this.logLongitude = logLongitude;
        this.logDate = logDate;
        this.logTime12 = logTime12;
        this.logTime24 = logTime24;
        this.logAddress = logAddress;
        this.logReason = logReason;
        this.logMobile = logMobile;
        this.logWifi = logWifi;
        this.logBattery = logBattery;
        this.logClimate = logClimate;
        this.logTemperature = logTemperature;
    }

    int getLogId() {
        return logId;
    }

    double getLogLatitude() {
        return logLatitude;
    }

    double getLogLongitude() {
        return logLongitude;
    }

    String getLogDate() {
        return logDate;
    }

    String getLogTime12() {
        return logTime12;
    }

    String getLogTime24() {
        return logTime24;
    }

    String getLogAddress() {
        return logAddress;
    }

    String getLogReason() {
        return logReason;
    }

    String getLogMobile() {
        return logMobile;
    }

    String getLogWifi() {
        return logWifi;
    }

    int getLogBattery() {
        return logBattery;
    }

    String getLogClimate() {
        return logClimate;
    }

    double getLogTemperature() {
        return logTemperature;
    }
}
