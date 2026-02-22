package model.dto.responsedto;

import model.enums.HackathonStatus;

import java.time.LocalDate;

public record HackathonSummaryDTO(
        Long id,
        String name,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        HackathonStatus status
) {}