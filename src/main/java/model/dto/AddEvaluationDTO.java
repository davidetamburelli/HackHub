package model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEvaluationDTO {

    private Long staffProfileId;
    private Long hackathonId;
    private Long submissionId;
    private int score;
    private String comment;

}