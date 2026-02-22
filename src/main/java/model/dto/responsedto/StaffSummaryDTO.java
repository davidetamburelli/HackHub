package model.dto.responsedto;

import model.valueobjs.Email;

public record StaffSummaryDTO(
        Long id,
        Email email,
        String name,
        String surname
) {
}
