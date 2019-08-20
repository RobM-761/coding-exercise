package utils;

import bo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.groupingBy;

public class TripCalculatorUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(TripCalculatorUtil.class);

    public List<Trip> calculateTripsFromTaps(List<Tap> taps, Map<StopIdPair, BigDecimal> completedTripStopIdsToChargeMap,
                                             Map<String, BigDecimal> incompleteTripStopIdToChargeMap) {
        List<Trip> trips = new ArrayList<>();

        try {
            Map<String, List<Tap>> TapsToPanMap = taps.stream()
                    .sorted(Comparator.comparing(Tap::getDateTimeUTC))
                    .collect(groupingBy(Tap::getPan));

            for(Map.Entry<String, List<Tap>> entry : TapsToPanMap.entrySet()) {
                if (entry.getValue().size() == 1) {
                    Tap tap = entry.getValue().get(0);
                    if (tap.getTapType() == TapType.ON) {
                        Trip trip = new Trip(tap.getDateTimeUTC(), tap.getDateTimeUTC(), 0, tap.getStopId(),
                                tap.getStopId(), incompleteTripStopIdToChargeMap.get(tap.getStopId()), tap.getCompanyId(),
                                tap.getBusId(), tap.getPan(), Status.INCOMPLETE);
                        trips.add(trip);
                    } else {
                        String message = "Unable to match Tap off with ID: " + tap.getId() + " with a Tap on";
                        throw new Exception(message);
                    }
                }
                else {
                    ListIterator<Tap> listIterator = entry.getValue().listIterator();
                    while (listIterator.hasNext()) {
                        Tap firstTap = listIterator.next();

                        String busId = firstTap.getBusId();
                        String companyId = firstTap.getCompanyId();
                        String pan = firstTap.getPan();
                        LocalDateTime started = firstTap.getDateTimeUTC();
                        String fromStopId = firstTap.getStopId();
                        BigDecimal chargeAmount;
                        Status status;

                        if (listIterator.hasNext()) {
                            Tap secondTap = listIterator.next();

                            LocalDateTime finished = secondTap.getDateTimeUTC();
                            long durationSecs = SECONDS.between(started, finished);
                            String toStopId = secondTap.getStopId();

                            if (firstTap.getTapType() == TapType.ON) {
                                if (!secondTap.getPan().equals(firstTap.getPan())) {
                                    String message = "PAN mismatch for trip with ID's" + secondTap.getId() + " " + firstTap.getId();
                                    throw new Exception(message);
                                }
                                if (secondTap.getTapType() == TapType.OFF) {
                                    if (!secondTap.getBusId().equalsIgnoreCase(firstTap.getBusId())) {
                                        String message = "BusID mismatch for trip with ID's" + secondTap.getId() + " " + firstTap.getId();
                                        throw new Exception(message);
                                    } else if (!secondTap.getCompanyId().equalsIgnoreCase(firstTap.getCompanyId())) {
                                        String message = "CompanyID mismatch for trip with ID's" + secondTap.getId() + " " + firstTap.getId();
                                        throw new Exception(message);
                                    }
                                    StopIdPair stopIdPair = new StopIdPair(firstTap.getStopId(), secondTap.getStopId());
                                    if (secondTap.getStopId().equalsIgnoreCase(firstTap.getStopId())) {
                                        chargeAmount = new BigDecimal(0);
                                        status = Status.CANCELLED;
                                    } else {
                                        chargeAmount = completedTripStopIdsToChargeMap.get(stopIdPair);
                                        status = Status.COMPLETED;
                                    }
                                } else {
                                    chargeAmount = incompleteTripStopIdToChargeMap.get(firstTap.getStopId());
                                    status = Status.INCOMPLETE;
                                    listIterator.previous();
                                }
                                Trip trip = new Trip(started, finished, durationSecs, fromStopId, toStopId, chargeAmount, companyId, busId, pan, status);
                                trips.add(trip);
                            }
                        } else if (firstTap.getTapType() == TapType.OFF){
                                    String message = "Unable to match Tap off with ID: " + firstTap.getId() + " with a Tap on";
                                    throw new Exception(message);
                                }
                            }
                        }
                    }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return trips;
    }

    /**
     *
     * @param stopIdPairChargeAmountMap
     * @return Map of stopIds to highest possible ChargeAmount associated from that stopId,
     */
    public Map<String, BigDecimal> calculateIncompleteTripCosts(Map<StopIdPair, BigDecimal> stopIdPairChargeAmountMap) {
        Map<String, BigDecimal> incompleteTripCostMap = new HashMap<>();
        if (stopIdPairChargeAmountMap != null && !stopIdPairChargeAmountMap.isEmpty()) {
            for (Map.Entry<StopIdPair, BigDecimal> entry : stopIdPairChargeAmountMap.entrySet()) {
                if (!incompleteTripCostMap.containsKey(entry.getKey().getLeft())) {
                    incompleteTripCostMap.put(entry.getKey().getLeft(), entry.getValue());
                } else if (incompleteTripCostMap.get(entry.getKey().getLeft()).compareTo(entry.getValue()) == -1) {
                    incompleteTripCostMap.put(entry.getKey().getLeft(), entry.getValue());
                }

                if (!incompleteTripCostMap.containsKey(entry.getKey().getRight())) {
                    incompleteTripCostMap.put(entry.getKey().getRight(), entry.getValue());
                } else if (incompleteTripCostMap.get(entry.getKey().getRight()).compareTo(entry.getValue()) == -1) {
                    incompleteTripCostMap.put(entry.getKey().getRight(), entry.getValue());
                }
            }
        }
        return incompleteTripCostMap;
    }
}
