package utils;

import bo.StopIdPair;
import bo.Tap;
import bo.TapType;
import bo.Trip;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TripCalculatorUtilTest{

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    @Test
    public void TestCalculateIncompleteTripCosts_NullInput_ShouldReturnEmpty() {
        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<String, BigDecimal> result = tripCalculatorUtil.calculateIncompleteTripCosts(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void TestCalculateIncompleteTripCosts_EmptyMapInput_ShouldReturnEmpty() {
        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> emptyMap = new HashMap<>();
        Map<String, BigDecimal> result = tripCalculatorUtil.calculateIncompleteTripCosts(emptyMap);
        assertTrue(result.isEmpty());
    }

    @Test
    public void TestCalculateIncompleteTripCosts_ValidMapInput_ShouldPass() {
        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> validMap = new HashMap<>();
        String stop1 = "1";
        String stop2 = "2";
        BigDecimal chargeAmount = new BigDecimal(10);
        validMap.put(new StopIdPair(stop1, stop2), chargeAmount);
        Map<String, BigDecimal> result = tripCalculatorUtil.calculateIncompleteTripCosts(validMap);
        assertEquals(chargeAmount, result.get(stop1));
        assertEquals(chargeAmount, result.get(stop2));
    }

    @Test
    public void TestCalculateIncompleteTripCosts_ValidMapMultipleValuesInput_ShouldPass() {
        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> validMap = new HashMap<>();
        String stop1 = "1";
        String stop2 = "2";
        String stop3 = "3";
        BigDecimal chargeAmount10 = new BigDecimal(10);
        BigDecimal chargeAmount20 = new BigDecimal(20);
        BigDecimal chargeAmount30 = new BigDecimal(30);

        validMap.put(new StopIdPair(stop1, stop2), chargeAmount10);
        validMap.put(new StopIdPair(stop1, stop3), chargeAmount20);
        validMap.put(new StopIdPair(stop3, stop2), chargeAmount30);

        Map<String, BigDecimal> result = tripCalculatorUtil.calculateIncompleteTripCosts(validMap);
        assertEquals(chargeAmount20, result.get(stop1));
        assertEquals(chargeAmount30, result.get(stop2));
        assertEquals(chargeAmount30, result.get(stop3));
    }

    @Test
    public void TestCalculateIncompleteTripCosts_IncompleteMap_ShouldPass() {
        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> validMap = new HashMap<>();
        String stop1 = "1";
        String stop2 = "2";
        String stop3 = "3";
        BigDecimal chargeAmount10 = new BigDecimal(10);
        BigDecimal chargeAmount20 = new BigDecimal(20);

        validMap.put(new StopIdPair(stop1, stop2), chargeAmount10);
        validMap.put(new StopIdPair(stop1, stop3), chargeAmount20);

        Map<String, BigDecimal> result = tripCalculatorUtil.calculateIncompleteTripCosts(validMap);
        assertEquals(chargeAmount20, result.get(stop1));
        assertEquals(chargeAmount10, result.get(stop2));
        assertEquals(chargeAmount20, result.get(stop3));
    }

    @Test
    public void TestCalculateTripsFromTaps_SingleTap_ShouldPass() {
        BigDecimal chargeAmount = new BigDecimal(20);
        String stop1 = "Stop1";
        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> completedTripStopIdsToChargeMap = new HashMap<>();
        completedTripStopIdsToChargeMap.put(new StopIdPair(stop1, "2"), new BigDecimal(10));
        Map<String, BigDecimal> incompleteTripStopIdToChargeMap = new HashMap<>();
        incompleteTripStopIdToChargeMap.put(stop1, chargeAmount);
        Tap tap = new Tap(1, LocalDateTime.parse("22-01-2018 13:00:00", formatter), TapType.ON,
                stop1, "Company1", "Bus43", "4462030000000000");
        List<Tap> taps = Arrays.asList(tap);

        List<Trip> trips = tripCalculatorUtil.calculateTripsFromTaps(taps, completedTripStopIdsToChargeMap, incompleteTripStopIdToChargeMap);
        assertEquals(1, trips.size());
        assertEquals(chargeAmount, trips.get(0).getChargeAmount() );
    }

    @Test
    public void TestCalculateTripsFromTaps_CompleteTrip_ShouldPass() {
        BigDecimal completeTripChargeAmount = new BigDecimal(10);
        BigDecimal incompleteTripChargeAmount = new BigDecimal(20);
        String stop1 = "Stop1";
        String stop2 = "Stop2";

        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> completedTripStopIdsToChargeMap = new HashMap<>();
        completedTripStopIdsToChargeMap.put(new StopIdPair(stop1, stop2), completeTripChargeAmount);
        Map<String, BigDecimal> incompleteTripStopIdToChargeMap = new HashMap<>();
        incompleteTripStopIdToChargeMap.put(stop1, incompleteTripChargeAmount);
        Tap tapOn = new Tap(1, LocalDateTime.parse("22-01-2018 13:00:00", formatter), TapType.ON,
                stop1, "Company1", "Bus43", "4462030000000000");
        Tap tapOff = new Tap(2, LocalDateTime.parse("22-01-2018 13:00:01", formatter), TapType.OFF,
                stop2, "Company1", "Bus43", "4462030000000000");
        List<Tap> taps = Arrays.asList(tapOn, tapOff);
        List<Trip> trips = tripCalculatorUtil.calculateTripsFromTaps(taps, completedTripStopIdsToChargeMap, incompleteTripStopIdToChargeMap);
        assertEquals(1, trips.size());
        assertEquals(completeTripChargeAmount, trips.get(0).getChargeAmount());
    }

    @Test
    public void TestCalculateTripsFromTaps_CancelledTrip_ShouldPass() {
        BigDecimal completeTripChargeAmount = new BigDecimal(10);
        BigDecimal incompleteTripChargeAmount = new BigDecimal(20);
        BigDecimal cancelledTripChargeAmount = new BigDecimal(0);
        String stop1 = "Stop1";
        String stop2 = "Stop2";

        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> completedTripStopIdsToChargeMap = new HashMap<>();
        completedTripStopIdsToChargeMap.put(new StopIdPair(stop1, stop2), completeTripChargeAmount);
        Map<String, BigDecimal> incompleteTripStopIdToChargeMap = new HashMap<>();
        incompleteTripStopIdToChargeMap.put(stop1, incompleteTripChargeAmount);
        Tap tapOn = new Tap(1, LocalDateTime.parse("22-01-2018 13:00:00", formatter), TapType.ON,
                stop1, "Company1", "Bus43", "4462030000000000");
        Tap tapOff = new Tap(2, LocalDateTime.parse("22-01-2018 13:00:01", formatter), TapType.OFF,
                stop1, "Company1", "Bus43", "4462030000000000");
        List<Tap> taps = Arrays.asList(tapOn, tapOff);

        List<Trip> trips = tripCalculatorUtil.calculateTripsFromTaps(taps, completedTripStopIdsToChargeMap, incompleteTripStopIdToChargeMap);
        assertEquals(1, trips.size());
        assertEquals(cancelledTripChargeAmount, trips.get(0).getChargeAmount());
        System.out.println(trips.get(0).getFromStopId() + " " + trips.get(0).getToStopId());
    }

    @Test
    public void TestCalculateTripsFromTaps_IncompleteTrip_ShouldPass() {
        BigDecimal completeTripChargeAmount = new BigDecimal(10);
        BigDecimal incompleteTripChargeAmount = new BigDecimal(20);
        BigDecimal cancelledTripChargeAmount = new BigDecimal(0);
        String stop1 = "Stop1";
        String stop2 = "Stop2";

        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> completedTripStopIdsToChargeMap = new HashMap<>();
        completedTripStopIdsToChargeMap.put(new StopIdPair(stop1, stop2), completeTripChargeAmount);
        Map<String, BigDecimal> incompleteTripStopIdToChargeMap = new HashMap<>();
        incompleteTripStopIdToChargeMap.put(stop1, incompleteTripChargeAmount);
        Tap tapOn = new Tap(1, LocalDateTime.parse("22-01-2018 13:00:00", formatter), TapType.ON,
                stop1, "Company1", "Bus43", "4462030000000000");
        Tap tapOff = new Tap(2, LocalDateTime.parse("22-01-2018 13:00:01", formatter), TapType.ON,
                stop2, "Company1", "Bus53", "4462030000000000");
        List<Tap> taps = Arrays.asList(tapOn, tapOff);

        List<Trip> trips = tripCalculatorUtil.calculateTripsFromTaps(taps, completedTripStopIdsToChargeMap, incompleteTripStopIdToChargeMap);
        assertEquals(1, trips.size());
        assertEquals(incompleteTripChargeAmount, trips.get(0).getChargeAmount());
        System.out.println(trips.get(0).getFromStopId() + " " + trips.get(0).getToStopId());
    }

    @Test
    public void TestCalculateTripsFromTaps_MultiplePansTrip_ShouldPass() {
        BigDecimal completeTripChargeAmount = new BigDecimal(20);
        BigDecimal incompleteTripChargeAmount = new BigDecimal(10);
        String stop1 = "Stop1";
        String stop2 = "Stop2";

        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();
        Map<StopIdPair, BigDecimal> completedTripStopIdsToChargeMap = new HashMap<>();
        completedTripStopIdsToChargeMap.put(new StopIdPair(stop1, stop2), completeTripChargeAmount);
        Map<String, BigDecimal> incompleteTripStopIdToChargeMap = new HashMap<>();
        incompleteTripStopIdToChargeMap.put(stop1, incompleteTripChargeAmount);
        Tap tapOn1 = new Tap(1, LocalDateTime.parse("22-01-2018 13:00:00", formatter), TapType.ON,
                stop1, "Company1", "Bus43", "4462030000000000");
        Tap tapOff1 = new Tap(2, LocalDateTime.parse("22-01-2018 13:00:01", formatter), TapType.OFF,
                stop2, "Company1", "Bus43", "4462030000000000");
        Tap tapOn2 = new Tap(1, LocalDateTime.parse("22-01-2018 13:00:00", formatter), TapType.ON,
                stop1, "Company1", "Bus43", "4911830000000");
        Tap tapOff2 = new Tap(2, LocalDateTime.parse("22-01-2018 13:00:01", formatter), TapType.OFF,
                stop2, "Company1", "Bus43", "4911830000000");
        List<Tap> taps = Arrays.asList(tapOn1, tapOn2, tapOff1, tapOff2);
        List<Trip> trips = tripCalculatorUtil.calculateTripsFromTaps(taps, completedTripStopIdsToChargeMap, incompleteTripStopIdToChargeMap);
        assertEquals(2, trips.size());
        assertEquals(completeTripChargeAmount, trips.get(0).getChargeAmount());
    }
}
