package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Info {
    private String runningTime;
    private int numberOfThreads;
    private LocalDateTime timestampUTC;
    private String hostname;
}

