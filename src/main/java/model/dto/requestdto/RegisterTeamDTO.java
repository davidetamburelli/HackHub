package model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.PayoutMethod;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTeamDTO {

    private String contactEmail;
    private PayoutMethod payoutMethod;
    private String payoutRef;

}
