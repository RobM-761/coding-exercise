package bo;

import java.time.LocalDateTime;

public class Tap {

    int id;
    LocalDateTime dateTimeUTC;
    TapType tapType;
    String stopId;
    String companyId;
    String busId;
    String pan;

    public Tap(int id, LocalDateTime dateTimeUTC, TapType tapType, String stopId, String companyId, String busId,
               String pan) {
        this.id = id;
        this.dateTimeUTC = dateTimeUTC;
        this.tapType = tapType;
        this.stopId = stopId;
        this.companyId = companyId;
        this.busId = busId;
        this.pan = pan;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTimeUTC() {
        return dateTimeUTC;
    }

    public TapType getTapType() {
        return tapType;
    }

    public String getStopId() {
        return stopId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getBusId() {
        return busId;
    }

    public String getPan() {
        return pan;
    }
}
