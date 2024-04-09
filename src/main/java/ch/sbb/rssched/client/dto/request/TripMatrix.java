package ch.sbb.rssched.client.dto.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author munterfi
 */
record TripMatrix(List<String> indices, List<List<Integer>> durations, List<List<Integer>> distances) {
    static class Builder {

        private final Set<String> locations = new HashSet<>();
        private final Map<String, Map<String, Relation>> relations = new HashMap<>();

        Builder() {
        }

        public Builder addRelation(String originLocationId, String destinationLocationId, int duration, int distance) {
            validateAndAddLocation(originLocationId);
            validateAndAddLocation(destinationLocationId);
            relations.computeIfAbsent(originLocationId, k -> new HashMap<>()).put(destinationLocationId,
                    new Relation(originLocationId, destinationLocationId, duration, distance));
            return this;
        }

        private void validateAndAddLocation(String locationId) {
            if (locationId == null || locationId.isEmpty()) {
                throw new IllegalArgumentException("Location ID cannot be null or empty.");
            }
            locations.add(locationId);
        }

        public boolean containsRelation(String originLocationId, String destinationLocationId) {
            Map<String, Relation> originRelations = relations.get(originLocationId);
            return originRelations != null && originRelations.containsKey(destinationLocationId);
        }

        public TripMatrix build() {
            validateLocationsCount();
            validateOriginDestinationConsistency();
            // setup
            List<String> indices = new ArrayList<>(locations);
            Collections.sort(indices);
            final int n = indices.size();
            List<List<Integer>> durations = new ArrayList<>(n);
            List<List<Integer>> distances = new ArrayList<>(n);
            // fill matrix
            for (String origIndex : indices) {
                List<Integer> durationRow = new ArrayList<>(n);
                List<Integer> distanceRow = new ArrayList<>(n);
                for (String destIndex : indices) {
                    if (origIndex.equals(destIndex)) {
                        durationRow.add(0);
                        distanceRow.add(0);
                    } else {
                        var relation = relations.get(origIndex).get(destIndex);
                        durationRow.add(relation.duration);
                        distanceRow.add(relation.distance);
                    }
                }
                durations.add(durationRow);
                distances.add(distanceRow);
            }
            return new TripMatrix(indices, durations, distances);
        }

        private void validateLocationsCount() {
            if (locations.size() < 2) {
                throw new IllegalArgumentException("At least two locations are required to build the matrix.");
            }
        }

        private void validateOriginDestinationConsistency() {
            Set<String> originIds = relations.keySet();
            Set<String> destinationsIds = new HashSet<>(relations.size());
            for (var destinationMap : relations.values()) {
                destinationsIds.addAll(destinationMap.keySet());
            }
            if (!originIds.equals(destinationsIds)) {
                throw new IllegalArgumentException("Origins and destinations sets must contain the same values.");
            }
        }

        record Relation(String originLocationId, String destinationLocationId, int duration, int distance) {
        }
    }
}
