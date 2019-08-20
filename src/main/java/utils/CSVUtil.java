package utils;

import bo.StopIdPair;
import bo.Tap;
import bo.TapType;
import bo.Trip;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CSVUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(CSVUtil.class);
    private static final String[] TRIPHEADERS = {"Started", "Finished", "DurationSecs", "FromStopId", "ToStopId", "ChargeAmount",
            "CompanyId", "BusID", "PAN", "Status"};
    private static final String TRIPCSVFILE = "trips.csv";

   public List<Tap> processTapsCSV(String pathToCSV, Set<String> stopIdSet) {
        List<Tap> taps = new ArrayList<>();
        CreditCardValidator creditCardValidator = new CreditCardValidator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        try (Reader bufferedReader = Files.newBufferedReader(Paths.get(pathToCSV));
             CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines()
                     .withIgnoreSurroundingSpaces());
        ) {
            for (CSVRecord record : csvParser) {
                String iDString = record.get("ID");
                String dateTimeUTCString = record.get("DateTimeUTC");
                String tapTypeString = record.get("TapType");
                String stopID = record.get("StopId");
                String companyId = record.get("CompanyId");
                String busId = record.get("BusID");
                String pan = record.get("PAN");

                if (iDString.isEmpty()) {
                    String message = "Missing ID in record at line" + record.getRecordNumber();
                    throw new Exception(message);
                }
                int iD = Integer.parseInt(iDString);

                if (stopID.isEmpty()) {
                    String message = "Missing StopID in record with ID: " + iD;
                    throw new Exception(message);
                }
                if (!stopIdSet.contains(stopID)) {
                    String message = "Invalid StopID: " + stopID + " in record with ID: " + iD;
                    throw new Exception(message);
                }
                TapType tapType = TapType.fromValue(tapTypeString);
                if (tapType == null) {
                    String message = "Invalid TapType: " + tapTypeString + " in record with ID: " + iD;
                    throw new Exception(message);
                }
                if (!creditCardValidator.isValid(pan)) {
                    String message = "Invalid PAN: " + pan + " in record with ID: " + iD;
                    throw new Exception(message);
                }
                if (companyId.isEmpty()) {
                    String message = "Missing CompanyId in record with ID: " + iD;
                    throw new Exception(message);
                }

                LocalDateTime dateTimeUTC = LocalDateTime.parse(dateTimeUTCString, formatter);

                Tap tap = new Tap(iD, dateTimeUTC, tapType, stopID, companyId, busId, pan);
                taps.add(tap);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return taps;
    }

    public void loadPricingCSV(String pathToCSV, Set<String> stopIdSet, Map<StopIdPair, BigDecimal> stopIdPairChargeAmountMap) {
        try (Reader bufferedReader = Files.newBufferedReader(Paths.get(pathToCSV));
             CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines()
                     .withIgnoreSurroundingSpaces());
        ) {
            for (CSVRecord record : csvParser) {
                String stopID1 = record.get("StopID1");
                String stopID2 = record.get("StopID2");
                String chargeAmountString = record.get("ChargeAmount");

                if (stopID1.isEmpty() || stopID2.isEmpty()) {
                    String message = "Missing StopID in record at line" + record.getRecordNumber();
                    throw new Exception(message);
                }
                if (chargeAmountString.isEmpty()) {
                    String message = "Missing StopID in record at line" + record.getRecordNumber();
                    throw new Exception(message);
                }
                BigDecimal chargeAmount = new BigDecimal(chargeAmountString);
                StopIdPair stopIdPair = new StopIdPair(stopID1, stopID2);
                stopIdPairChargeAmountMap.put(stopIdPair, chargeAmount);
                stopIdSet.add(stopID1);
                stopIdSet.add(stopID2);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void writeTripsToCSV(List<Trip> trips) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(TRIPCSVFILE));
             CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT.withHeader(TRIPHEADERS));
             )
        {
            for (Trip trip : trips) {
                csvPrinter.printRecord(trip.getStarted(), trip.getFinished(), trip.getDurationSecs(), trip.getFromStopId(),
                        trip.getChargeAmount(), trip.getComapanyId(), trip.getBusId(), trip.getPan());
            }
            csvPrinter.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
