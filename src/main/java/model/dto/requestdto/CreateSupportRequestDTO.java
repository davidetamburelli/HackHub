package model.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.Urgency;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSupportRequestDTO {

    private String title;
    private String description;
    private Urgency urgency;

}
