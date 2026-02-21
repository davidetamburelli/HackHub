package utils;

import org.springframework.stereotype.Service;
import model.RankingCandidate;
import model.enums.RankingPolicy;
import utils.strategy.RankingStrategy;
import utils.strategy.ScoreDescThenEarliestSubmissionStrategy;
import utils.strategy.ScoreDescThenMinorTeamSizeThenEarliestSubmission;
import utils.strategy.ScoreDescThenTeamRegistrationOrderStrategy;
import java.util.List;

@Service
public class WinnerService {

    public Long selectWinner(RankingPolicy policy, List<RankingCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        RankingStrategy strategy;

        switch (policy) {
            case SCORE_DESC_THEN_EARLIEST_SUBMISSION:
                strategy = new ScoreDescThenEarliestSubmissionStrategy();
                break;
            case SCORE_DESC_THEN_TEAM_REGISTRATION_ORDER:
                strategy = new ScoreDescThenTeamRegistrationOrderStrategy();
                break;
            case SCORE_DESC_THEN_MINOR_TEAM_SIZE_THEN_EARLIEST_SUBMISSION:
                strategy = new ScoreDescThenMinorTeamSizeThenEarliestSubmission();
                break;
            default:
                throw new IllegalArgumentException("Ranking Policy non supportata: " + policy);
        }

        return strategy.selectWinner(candidates);
    }
}