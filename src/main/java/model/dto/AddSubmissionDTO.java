package model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddSubmissionDTO {

    private Long userId;
    private Long hackathonId;
    private String response;
    private String responseURL;

}