package model.dto.responsedto;

import model.enums.HackathonStatus;

import java.time.LocalDateTime;

public record HackathonSummaryDTO(
        Long id,
        String name,
        String type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        HackathonStatus status
) {}