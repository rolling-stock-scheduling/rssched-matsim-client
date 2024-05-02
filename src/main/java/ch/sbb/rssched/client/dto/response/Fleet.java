package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class Fleet {
    private String vehicleType;
    private List<Vehicle> vehicles;
    private List<List<String>> vehicleCycles;
}
