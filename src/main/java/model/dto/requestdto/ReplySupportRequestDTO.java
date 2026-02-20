package model.dto.requestdto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplySupportRequestDTO {

    private String message;

    public ReplySupportRequestDTO(String message) {
        this.message = message;
    }
}