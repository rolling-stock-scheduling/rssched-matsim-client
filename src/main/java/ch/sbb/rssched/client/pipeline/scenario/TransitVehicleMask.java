package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.pipeline.core.Filter;
import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.Vehicles;

import java.util.List;
import java.util.Set;

/**
 * Masks the transit vehicles based on transit line IDs of interest.
 *
 * @author munterfi
 */
@Log4j2
class TransitVehicleMask implements Filter<ScenarioPipe> {

    private static List<Vehicle> maskVehicles(Set<Id<TransitLine>> transitLineIds, TransitSchedule transitSchedule, Vehicles transitVehicles) {
        var transitVehiclesToKeep = transitLineIds.stream().flatMap(
                        transitLineId -> transitSchedule.getTransitLines().get(transitLineId).getRoutes().values().stream()
                                .flatMap(transitRoute -> transitRoute.getDepartures().values().stream()
                                        .map(departure -> transitVehicles.getVehicles().get(departure.getVehicleId()))))
                .distinct().toList();
        var transitVehiclesToRemove = transitVehicles.getVehicles().values().stream()
                .filter(vehicle -> !transitVehiclesToKeep.contains(vehicle)).toList();
        transitVehiclesToRemove.forEach(vehicle -> transitVehicles.removeVehicle(vehicle.getId()));
        return transitVehiclesToKeep;
    }

    private static void maskVehicleTypes(Vehicles transitVehicles, List<Vehicle> transitVehiclesToKeep) {
        var transitVehicleTypesToKeep = transitVehiclesToKeep.stream().map(Vehicle::getType).distinct().toList();
        var transitVehicleTypesToRemove = transitVehicles.getVehicleTypes().values().stream()
                .filter(vehicleType -> !transitVehicleTypesToKeep.contains(vehicleType)).toList();
        transitVehicleTypesToRemove.forEach(vehicleType -> transitVehicles.removeVehicleType(vehicleType.getId()));
    }

    @Override
    public void apply(ScenarioPipe pipe) {
        maskTransitVehicles(pipe.scenario, pipe.selection.getLineIds());
    }

    private void maskTransitVehicles(Scenario scenario, Set<Id<TransitLine>> transitLineIds) {
        var transitSchedule = scenario.getTransitSchedule();
        var transitVehicles = scenario.getTransitVehicles();
        log.info("Masking transit vehicles (vehicle: {}, types: {})", transitVehicles.getVehicles().size(),
                transitVehicles.getVehicleTypes().size());
        maskVehicleTypes(transitVehicles, maskVehicles(transitLineIds, transitSchedule, transitVehicles));
        log.info("Done (remaining vehicle: {}, types: {})", transitVehicles.getVehicles().size(),
                transitVehicles.getVehicleTypes().size());
    }
}
