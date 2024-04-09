package ch.sbb.rssched.client.dto.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author munterfi
 */
class TripMatrixTest {
    @Test
    void testBuildWithValidInput() {
        // Create a DeadHeadTripMatrixBuilder
        TripMatrix.Builder builder = new TripMatrix.Builder();

        // Add some relations to the builder
        builder.addRelation("loc1", "loc2", 600, 1000).addRelation("loc2", "loc1", 6000, 10000)
                .addRelation("loc1", "loc3", 300, 500).addRelation("loc3", "loc1", 3000, 5000)
                .addRelation("loc2", "loc3", 400, 700).addRelation("loc3", "loc2", 4000, 7000);

        // Build the DeadHeadTripMatrix
        TripMatrix deadHeadTrip = builder.build();

        // Verify the matrix dimensions
        Assertions.assertEquals(3, deadHeadTrip.indices().size(), "Matrix size should be 3.");
        Assertions.assertEquals(3, deadHeadTrip.distances().size(), "Matrix size should be 3.");
        Assertions.assertEquals(3, deadHeadTrip.durations().size(), "Matrix size should be 3.");
        Assertions.assertEquals(3, deadHeadTrip.distances().get(0).size(), "Matrix size should be 3.");
        Assertions.assertEquals(3, deadHeadTrip.durations().get(0).size(), "Matrix size should be 3.");
    }

    @Test
    void testBuildWithLessThanTwoLocations() {
        // Create a DeadHeadTripMatrixBuilder
        TripMatrix.Builder builder = new TripMatrix.Builder();

        // Add only one location
        builder.addRelation("loc1", "loc2", 600, 1000);

        // Building the DeadHeadTripMatrix should throw an exception
        Assertions.assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void testBuildWithInconsistentLocations() {
        // Create a DeadHeadTripMatrixBuilder
        TripMatrix.Builder builder = new TripMatrix.Builder();

        // Add inconsistent relations to the builder (missing relation "loc2" - "loc1")
        builder.addRelation("loc1", "loc3", 600, 1000).addRelation("loc3", "loc1", 6000, 10000)
                .addRelation("loc1", "loc2", 6000, 10000);

        // Building the DeadHeadTripMatrix should throw an exception
        Assertions.assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void testAddRelationWithNullLocationId() {
        // Create a DeadHeadTripMatrixBuilder
        TripMatrix.Builder builder = new TripMatrix.Builder();

        // Add a relation with null location ID should throw an exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.addRelation(null, "loc2", 600, 1000));
    }

    @Test
    void testAddRelationWithEmptyLocationId() {
        // Create a DeadHeadTripMatrixBuilder
        TripMatrix.Builder builder = new TripMatrix.Builder();

        // Add a relation with empty location ID should throw an exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.addRelation("loc1", "", 600, 1000));
    }
}