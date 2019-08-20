import bo.StopIdPair;
import bo.Tap;
import bo.Trip;
import utils.CSVUtil;
import utils.TripCalculatorUtil;

import java.math.BigDecimal;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String pricingCSVpath = args[0];
        String tapsCSVpath = args[1];
        Map<StopIdPair, BigDecimal> stopIdPairToChargeAmountMap = new HashMap<>();
        Set stopIdSet = new HashSet();
        CSVUtil csvUtil = new CSVUtil();
        TripCalculatorUtil tripCalculatorUtil = new TripCalculatorUtil();

        csvUtil.loadPricingCSV(pricingCSVpath, stopIdSet, stopIdPairToChargeAmountMap);
        List<Tap> taps = csvUtil.processTapsCSV(tapsCSVpath, stopIdSet);
        Map<String, BigDecimal> incompleteTripToChargeAmountMap = tripCalculatorUtil.calculateIncompleteTripCosts(stopIdPairToChargeAmountMap);
        List<Trip> trips = tripCalculatorUtil.calculateTripsFromTaps(taps, stopIdPairToChargeAmountMap, incompleteTripToChargeAmountMap);
        csvUtil.writeTripsToCSV(trips);
    }
}
