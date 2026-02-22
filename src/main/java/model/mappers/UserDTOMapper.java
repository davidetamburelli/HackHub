package model.mappers;

import model.User;
import model.dto.responsedto.UserSummaryDTO;

public final class UserDTOMapper {

    private UserDTOMapper() {}

    public static UserSummaryDTO toSummary(User u) {

        Long teamId = u.getTeam();   // o u.getTeam() se è oggetto

        return new UserSummaryDTO(
                u.getId(),
                u.getUsername(),
                u.getName(),
                u.getSurname(),
                teamId,
                teamId != null
        );
    }
}