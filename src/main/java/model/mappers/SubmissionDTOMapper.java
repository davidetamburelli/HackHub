package model.mappers;

import model.Evaluation;
import model.Submission;
import model.dto.responsedto.EvaluationDTO;
import model.dto.responsedto.SubmissionDetailsDTO;
import model.dto.responsedto.SubmissionSummaryDTO;

public final class SubmissionDTOMapper {

    private SubmissionDTOMapper() {}

    public static SubmissionSummaryDTO toSummary(Submission s) {

        boolean evaluated = s.getEvaluation() != null;

        Integer score = evaluated
                ? s.getEvaluation().getScore()
                : null;

        return new SubmissionSummaryDTO(
                s.getId(),
                s.getHackathon(),
                s.getParticipatingTeam(),
                s.getUpdatedAt(),
                evaluated,
                score
        );
    }

    public static SubmissionDetailsDTO toDetails(Submission s) {
        return new SubmissionDetailsDTO(
                s.getId(),
                s.getHackathon(),
                s.getParticipatingTeam(),
                s.getResponse(),
                s.getResponseURL(),
                s.getUpdatedAt(),
                toEvaluationDTO(s.getEvaluation())
        );
    }


    private static EvaluationDTO toEvaluationDTO(Evaluation e) {
        if (e == null) return null;

        return new EvaluationDTO(
                e.getScore(),
                e.getComment()
        );
    }
}