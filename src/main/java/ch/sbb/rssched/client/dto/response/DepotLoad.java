package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DepotLoad {
    private String depot;
    private List<Load> load;
}
