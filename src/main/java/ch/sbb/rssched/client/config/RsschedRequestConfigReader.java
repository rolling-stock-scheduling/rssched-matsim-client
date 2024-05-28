package ch.sbb.rssched.client.config;

import ch.sbb.rssched.client.config.selection.VehicleTypeFilterStrategy;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Reader for XLSX configuration
 *
 * @author munterfi
 */
@NoArgsConstructor
@Log4j2
public class RsschedRequestConfigReader {

    public static final String DEPOT_UNDEFINED_VEHICLE_TYPE = "TOTAL";
    private static final RsschedRequestConfig.Builder builder = RsschedRequestConfig.builder();
    private final Set<String> allVehicleTypes = new HashSet<>();

    public RsschedRequestConfig readExcelFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        parseScenarioInfoSheet(workbook.getSheet("scenario_info"));
        parseVehicleTypesSheet(workbook.getSheet("vehicle_types"));
        parseShuntingLocationsOnRouteSheet(workbook.getSheet("shunting_locations_on_route"));
        parseDepotLocationsSheet(workbook.getSheet("depot_locations"));
        parseMaintenanceSlotsSheet(workbook.getSheet("maintenance_slots"));
        return builder.buildWithDefaults();
    }

    private void parseScenarioInfoSheet(Sheet sheet) {
        if (sheet == null) return;
        for (Row row : sheet) {
            Cell groupCell = row.getCell(0);
            Cell parameterCell = row.getCell(1);
            Cell valueCell = row.getCell(2);

            if (groupCell != null && parameterCell != null && valueCell != null) {
                String group = groupCell.getStringCellValue();
                String parameter = parameterCell.getStringCellValue();

                switch (group) {
                    case "global":
                        switch (parameter) {
                            case "runId":
                                builder.setRunId(valueCell.getStringCellValue());
                                break;
                            case "inputDirectory":
                                builder.setInputDirectory(valueCell.getStringCellValue());
                                break;
                            case "outputDirectory":
                                builder.setOutputDirectory(valueCell.getStringCellValue());
                                break;
                            case "sampleSize":
                                builder.config.getGlobal().setSampleSize(valueCell.getNumericCellValue());
                                break;
                            case "deadHeadTripSpeedLimit":
                                builder.config.getGlobal().setDeadHeadTripSpeedLimit(valueCell.getNumericCellValue());
                                break;
                            case "forbidDeadHeadTrips":
                                builder.config.getGlobal().setForbidDeadHeadTrips(valueCell.getBooleanCellValue());
                                break;
                            case "dayLimitThreshold":
                                builder.config.getGlobal().setDayLimitThreshold((int) valueCell.getNumericCellValue());
                                break;
                            case "seatDurationThreshold":
                                builder.config.getGlobal()
                                        .setSeatDurationThreshold((int) valueCell.getNumericCellValue());
                                break;
                        }
                        break;
                    case "depot":
                        switch (parameter) {
                            case "defaultCapacity":
                                builder.config.getDepot().setDefaultCapacity((int) valueCell.getNumericCellValue());
                                break;
                            case "defaultIdPrefix":
                                builder.config.getDepot().setDefaultIdPrefix(valueCell.getStringCellValue());
                                break;
                            case "createAtTerminalLocations":
                                builder.config.getDepot().setCreateAtTerminalLocations(valueCell.getBooleanCellValue());
                                break;
                        }
                        break;
                    case "shunting":
                        int value = (int) valueCell.getNumericCellValue();
                        switch (parameter) {
                            case "defaultMaximalFormationCount":
                                builder.config.getShunting().setDefaultMaximalFormationCount(value);
                                break;
                            case "minimalDuration":
                                builder.config.getShunting().setMinimalDuration(value);
                                break;
                            case "deadHeadTripDuration":
                                builder.config.getShunting().setDeadHeadTripDuration(value);
                                break;
                            case "couplingDuration":
                                builder.config.getShunting().setCouplingDuration(value);
                                break;
                        }
                        break;
                    case "maintenance":
                        if (parameter.equals("maximalDistance")) {
                            builder.config.getMaintenance().setMaximalDistance((int) valueCell.getNumericCellValue());
                        }
                        break;
                    case "costs":
                        int cost = (int) valueCell.getNumericCellValue();
                        switch (parameter) {
                            case "staff":
                                builder.config.getCosts().setStaff(cost);
                                break;
                            case "idle":
                                builder.config.getCosts().setIdle(cost);
                                break;
                            case "serviceTrip":
                                builder.config.getCosts().setServiceTrip(cost);
                                break;
                            case "deadHeadTrip":
                                builder.config.getCosts().setDeadHeadTrip(cost);
                                break;
                            case "maintenance":
                                builder.config.getCosts().setMaintenance(cost);
                                break;
                        }
                        break;
                }
            }
        }
    }

    private void parseVehicleTypesSheet(Sheet sheet) {
        Map<String, Set<String>> vehicleTypesPerGroup = new HashMap<>();

        if (sheet == null) return;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // skip header row
            Cell groupIdCell = row.getCell(0);
            Cell vehicleTypeIdCell = row.getCell(1);
            Cell lengthCell = row.getCell(2);
            Cell standingRoomCell = row.getCell(3);
            Cell seatsCell = row.getCell(4);

            if (groupIdCell != null && vehicleTypeIdCell != null && lengthCell != null && standingRoomCell != null && seatsCell != null) {
                String grouId = groupIdCell.getStringCellValue();
                String vehicleTypeId = vehicleTypeIdCell.getStringCellValue();
                double length = lengthCell.getNumericCellValue();
                double standingRoom = standingRoomCell.getNumericCellValue();
                double seats = seatsCell.getNumericCellValue();

                Set<String> group = vehicleTypesPerGroup.computeIfAbsent(grouId, ignored -> new HashSet<>());
                group.add(vehicleTypeId);
                allVehicleTypes.add(vehicleTypeId);

                // handle vehicle types as needed
                System.out.printf("Vehicle Type: %s, Length: %.2f, Standing Room: %.2f, Seats: %.2f%n", vehicleTypeId,
                        length, standingRoom, seats);
            }
        }

        // add filter strategy for vehicle types
        Set<VehicleTypeFilterStrategy.VehicleCategory> vehicleCategories = new HashSet<>();
        for (var group : vehicleTypesPerGroup.entrySet()) {
            vehicleCategories.add(new VehicleTypeFilterStrategy.VehicleCategory(group.getKey(), group.getValue()));
        }
        builder.setFilterStrategy(new VehicleTypeFilterStrategy(vehicleCategories));
    }

    private void parseShuntingLocationsOnRouteSheet(Sheet sheet) {
        if (sheet == null) return;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // skip header row
            Cell locationIdCell = row.getCell(0);
            if (locationIdCell != null) {
                String locationId = locationIdCell.getStringCellValue();
                builder.addShuntingLocation(locationId);
            }
        }
    }

    private void parseDepotLocationsSheet(Sheet sheet) {
        Map<String, Map<String, Integer>> depotCapacities = new HashMap<>();

        if (sheet == null) return;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // skip header row
            Cell locationIdCell = row.getCell(0);
            Cell vehicleTypeIdCell = row.getCell(1);
            Cell capacityCell = row.getCell(2);

            if (locationIdCell != null && vehicleTypeIdCell != null && capacityCell != null) {
                String locationId = locationIdCell.getStringCellValue();
                String vehicleTypeId = vehicleTypeIdCell.getStringCellValue();
                int capacity = (int) capacityCell.getNumericCellValue();

                Map<String, Integer> allowedType = depotCapacities.computeIfAbsent(locationId,
                        ignored -> new HashMap<>());
                allowedType.put(vehicleTypeId, capacity);
            }
        }

        for (Map.Entry<String, Map<String, Integer>> entry : depotCapacities.entrySet()) {
            Map<String, Integer> allowedTypes = entry.getValue();
            String locationId = entry.getKey();
            String depotId = builder.config.getDepot().getDefaultIdPrefix() + locationId;

            if (allowedTypes.containsKey(DEPOT_UNDEFINED_VEHICLE_TYPE)) {
                if (allowedTypes.size() > 1) {
                    throw new IllegalArgumentException(
                            "Specific vehicle types in cannot be specified for generic depot: " + depotId);
                }

                // add generic depot with total capacity
                log.debug("Depot {} at location {} is generic ({})", depotId, locationId, DEPOT_UNDEFINED_VEHICLE_TYPE);
                int totalCapacity = allowedTypes.get(DEPOT_UNDEFINED_VEHICLE_TYPE);
                builder.addDepot(depotId, locationId, totalCapacity);

                // add allowed types for all vehicle types to depot
                for (String vehicleTypeId : allVehicleTypes) {
                    log.debug("Add vehicle type {} with total capacity {} to depot {}", vehicleTypeId, totalCapacity,
                            depotId);
                    builder.addAllowedTypeToDepot(depotId, vehicleTypeId, totalCapacity);
                }

            } else {
                // add depot to location
                int totalCapacity = allowedTypes.values().stream().mapToInt(Integer::intValue).sum();
                builder.addDepot(depotId, locationId, totalCapacity);

                // add allowed types to depot
                for (Map.Entry<String, Integer> allowedType : allowedTypes.entrySet()) {
                    String vehicleTypeId = allowedType.getKey();
                    int capacity = allowedType.getValue();
                    log.debug("Add vehicle type {} with capacity {} to depot {}", vehicleTypeId, capacity, depotId);
                    builder.addAllowedTypeToDepot(depotId, vehicleTypeId, capacity);
                }
            }
        }
    }

    private void parseMaintenanceSlotsSheet(Sheet sheet) {
        if (sheet == null) return;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // skip header row
            Cell locationIdCell = row.getCell(0);
            Cell tracksCell = row.getCell(1);
            Cell fromCell = row.getCell(2);
            Cell toCell = row.getCell(3);

            if (locationIdCell != null && tracksCell != null && fromCell != null && toCell != null) {
                String locationId = locationIdCell.getStringCellValue();
                int tracks = (int) tracksCell.getNumericCellValue();
                LocalTime fromTime = LocalTime.ofSecondOfDay((long) (fromCell.getNumericCellValue() * 86400));
                LocalTime toTime = LocalTime.ofSecondOfDay((long) (toCell.getNumericCellValue() * 86400));
                LocalDateTime start = LocalDateTime.of(LocalDateTime.now().toLocalDate(), fromTime);
                LocalDateTime end = LocalDateTime.of(LocalDateTime.now().toLocalDate(), toTime);

                // check if the end time is before the start time, indicating an overnight slot
                if (toTime.isBefore(fromTime)) {
                    end = end.plusDays(1);
                }

                log.debug("Add maintenance slot {}, {}, {}, {}", locationId, tracks, start, end);
                builder.addMaintenanceSlot(locationId, locationId, start, end, tracks);
            }
        }
    }
}
