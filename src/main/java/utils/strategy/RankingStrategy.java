package utils.strategy;

import model.RankingCandidate;
import java.util.List;

public interface RankingStrategy {

    Long selectWinner(List<RankingCandidate> candidates);

}