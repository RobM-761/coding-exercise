package bo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trip {

    LocalDateTime started;
    LocalDateTime finished;
    long durationSecs;
    String fromStopId;
    String toStopId;
    BigDecimal chargeAmount;
    String comapanyId;
    String busId;
    String pan;
    Status status;

    public Trip(LocalDateTime started, LocalDateTime finished, long durationSecs, String fromStopId, String toStopId,
                BigDecimal chargeAmount, String comapanyId, String busId, String pan, Status status) {
        this.started = started;
        this.finished = finished;
        this.durationSecs = durationSecs;
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.chargeAmount = chargeAmount;
        this.comapanyId = comapanyId;
        this.busId = busId;
        this.pan = pan;
        this.status = status;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public LocalDateTime getFinished() {
        return finished;
    }

    public long getDurationSecs() {
        return durationSecs;
    }

    public String getFromStopId() {
        return fromStopId;
    }

    public String getToStopId() {
        return toStopId;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public String getComapanyId() {
        return comapanyId;
    }

    public String getBusId() {
        return busId;
    }

    public String getPan() {
        return pan;
    }

    public Status getStatus() {
        return status;
    }
}
