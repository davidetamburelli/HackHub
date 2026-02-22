package handlers;

import model.dto.requestdto.HackathonSearchCriteria;
import model.dto.responsedto.HackathonFullDetailsDTO;
import model.dto.responsedto.HackathonSummaryDTO;
import model.dto.responsedto.PrizePayoutResponseDTO;
import model.dto.responsedto.PublicHackathonViewDTO;
import model.mappers.HackathonDTOMapper;
import model.valueobjs.PrizePayout;
import utils.builders.HackathonBuilder;
import utils.builders.IHackathonBuilder;
import model.Hackathon;
import model.ParticipatingTeam;
import model.RankingCandidate;
import model.StaffProfile;
import model.Submission;
import model.dto.requestdto.CreateHackathonDTO;
import model.dto.requestdto.PaymentResult;
import model.enums.HackathonStatus;
import model.enums.PrizeStatus;
import model.enums.RankingPolicy;
import model.valueobjs.PayoutAccountRef;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.StaffProfileRepository;
import repository.SubmissionRepository;
import utils.DomainException;
import utils.IPaymentService;
import utils.WinnerService;
import utils.decorator.*;
import utils.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HackathonHandler {

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final SubmissionRepository submissionRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final WinnerService winnerService;
    private final IPaymentService paymentService;

    public HackathonHandler(
            StaffProfileRepository staffProfileRepository,
            HackathonRepository hackathonRepository,
            SubmissionRepository submissionRepository,
            ParticipatingTeamRepository participatingTeamRepository,
            WinnerService winnerService,
            IPaymentService paymentService) {
        this.staffProfileRepository = staffProfileRepository;
        this.hackathonRepository = hackathonRepository;
        this.submissionRepository = submissionRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.winnerService = winnerService;
        this.paymentService = paymentService;
    }

    @Transactional
    public void createHackathon(Long staffProfileId, CreateHackathonDTO createHackathonDTO) {
        StaffProfile organizer = staffProfileRepository.getById(staffProfileId);

        boolean existsByName = hackathonRepository.existsByName(createHackathonDTO.getName());
        if (existsByName) {
            throw new DomainException("Esiste già un hackathon con questo nome");
        }

        StaffProfile judge = staffProfileRepository.findByEmail(createHackathonDTO.getJudgeEmail());
        if (judge == null) {
            throw new DomainException("Nessun profilo staff trovato per l'email del giudice");
        }

        List<Long> mentorsId = new ArrayList<>();
        if (createHackathonDTO.getMentorEmails() != null) {
            for (String mentorEmail : createHackathonDTO.getMentorEmails()) {
                StaffProfile mentor = staffProfileRepository.findByEmail(mentorEmail);
                if (mentor == null) {
                    throw new DomainException("Nessun profilo staff trovato per l'email del mentore: " + mentorEmail);
                }
                mentorsId.add(mentor.getId());
            }
        }

        IHackathonBuilder builder = new HackathonBuilder();
        Hackathon createdHackathon = builder
                .buildOrganizer(organizer.getId())
                .buildName(createHackathonDTO.getName())
                .buildType(createHackathonDTO.getType())
                .buildRegulation(createHackathonDTO.getRegulation())
                .buildLocation(createHackathonDTO.getLocation())
                .buildPrize(createHackathonDTO.getPrize())
                .buildMaxTeamSize(createHackathonDTO.getMaxTeamSize())
                .buildSubscriptionDates(createHackathonDTO.getSubscriptionDates())
                .buildDates(createHackathonDTO.getDates())
                .buildDelivery(createHackathonDTO.getDelivery())
                .buildJudge(judge.getId())
                .buildMentors(mentorsId)
                .buildRankingPolicy(createHackathonDTO.getRankingPolicy())
                .build();

        createdHackathon.setStatus(HackathonStatus.IN_REGISTRATION);

        hackathonRepository.save(createdHackathon);
    }

    @Transactional
    public HackathonFullDetailsDTO getHackathonDetails(Long staffProfileId, Long hackathonId) {
        boolean isStaff = hackathonRepository.existsStaff(hackathonId, staffProfileId);
        if (!isStaff) {
            throw new DomainException("Operazione non consentita: non fai parte dello staff di questo hackathon");
        }
        Hackathon hackathon = hackathonRepository.getById(hackathonId);

        StaffProfile organizer = staffProfileRepository.getById(hackathon.getOrganizer());
        StaffProfile judge = staffProfileRepository.getById(hackathon.getJudge());
        List<Long> mentorIds = hackathon.getMentors(); // List<Long>
        List<StaffProfile> mentors = (mentorIds == null || mentorIds.isEmpty())
                ? List.of()
                : staffProfileRepository.findAllById(mentorIds); // usa il findAllById che hai aggiunto

        return HackathonDTOMapper.toFullDetails(hackathon, organizer, judge, mentors);
    }

    @Transactional
    public PublicHackathonViewDTO getHackathonPublicInfos(Long hackathonId) {
        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        StaffProfile organizer = staffProfileRepository.getById(hackathon.getOrganizer());
        PublicHackathonView view = composeView(hackathon, organizer);
        return view.toDto();
    }

    @Transactional
    public void confirmEvaluations(Long judgeId, Long hackathonId) {
        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) {
            throw new DomainException("Hackathon non trovato.");
        }

        if (!hackathon.getJudge().equals(judgeId)) {
            throw new DomainException("Solo il giudice assegnato può confermare le valutazioni.");
        }

        List<RankingCandidate> candidates = submissionRepository.getRankingCandidates(hackathonId);
        Long winnerParticipatingTeamId = winnerService.selectWinner(hackathon.getRankingPolicy(), candidates);

        if (winnerParticipatingTeamId != null) {
            hackathon.declareWinner(winnerParticipatingTeamId);
            hackathon.close();

            if (hackathon.getPrize() > 0) {
                ParticipatingTeam winnerTeam = participatingTeamRepository.getById(winnerParticipatingTeamId);
                PayoutAccountRef accountRef = winnerTeam.getPaymentAccountRef();
                PaymentResult result = paymentService.transfer(hackathon.getPrize(), accountRef);

                if (result.isSuccess()) {
                    hackathon.confirmPrizePaid(result.getTransactionId(), LocalDateTime.now());
                } else {
                    hackathon.markPrizeFailed(result.getErrorMessage(), LocalDateTime.now());
                }
            } else {
                hackathon.confirmPrizePaid("NO_PRIZE", LocalDateTime.now());
            }
        } else {
            throw new DomainException("Impossibile determinare un vincitore (nessun candidato valido).");
        }

        hackathonRepository.save(hackathon);
    }

    @Transactional
    public PrizePayoutResponseDTO sendPrizeToWinner(Long staffProfileId, Long hackathonId) {
        boolean isOrganizer = hackathonRepository.existsOrganizer(hackathonId, staffProfileId);
        if (!isOrganizer) {
            throw new DomainException("Operazione non consentita: non sei l'organizzatore di questo hackathon");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.CLOSED) {
            throw new DomainException("L'hackathon non è ancora chiuso");
        }

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) {
            throw new DomainException("Hackathon non trovato");
        }

        Long participatingTeamId = hackathon.getWinnerParticipatingTeamId();
        if (participatingTeamId == null) {
            throw new DomainException("Nessun team vincitore assegnato a questo hackathon");
        }

        ParticipatingTeam participatingTeam = participatingTeamRepository.getByIdAndHackathonId(participatingTeamId, hackathonId);
        double prize = hackathon.getPrize();
        PrizeStatus prizeStatus = hackathon.getPrizeStatus();

        if (prizeStatus == PrizeStatus.PAID) {
            throw new DomainException("Il premio è già stato erogato");
        }

        PayoutAccountRef destination = participatingTeam.getPaymentAccountRef();
        PaymentResult result = paymentService.transfer(prize, destination);

        if (result.isSuccess()) {
            hackathon.confirmPrizePaid(result.getTransactionId(), LocalDateTime.now());
        } else {
            hackathon.markPrizeFailed(result.getErrorMessage(), LocalDateTime.now());
        }

        hackathonRepository.save(hackathon);
        // costruisci DTO di risposta dallo stato aggiornato dell'hackathon
        PrizePayout updated = hackathon.getPrizePayout();

        return new PrizePayoutResponseDTO(
                hackathon.getId(),
                updated.getStatus(),
                updated.getPaidAt(),
                updated.getProviderRef(),
                updated.getFailureReason()
        );
    }

    @Transactional
    public void declareWinner(Long staffProfileId, Long hackathonId) {
        boolean isOrganizer = hackathonRepository.existsOrganizer(hackathonId, staffProfileId);
        if (!isOrganizer) {
            throw new DomainException("Operazione non autorizzata");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.CLOSED) {
            throw new DomainException("L'hackathon deve essere chiuso per proclamare il vincitore");
        }

        List<ParticipatingTeam> eligibleParticipatingTeams = participatingTeamRepository.findEligibleForRanking(hackathonId);
        List<RankingCandidate> candidates = new ArrayList<>();

        for (ParticipatingTeam pt : eligibleParticipatingTeams) {
            Submission s = submissionRepository.findByHackathonIdAndParticipatingTeamId(hackathonId, pt.getId());
            if (s != null && s.hasEvaluation()) {
                int baseScore = s.getScore();
                int penaltyPoints = pt.getTotalPenaltyPoints();
                int finalScore = baseScore - penaltyPoints;
                LocalDateTime submissionUpdatedAt = s.getUpdatedAt();
                LocalDateTime teamRegisteredAt = pt.getRegisteredAt();
                int teamSize = pt.getTeamSize();

                candidates.add(new RankingCandidate(
                        pt.getId(),
                        finalScore,
                        submissionUpdatedAt,
                        teamRegisteredAt,
                        teamSize
                ));
            }
        }

        if (candidates.isEmpty()) {
            throw new DomainException("Nessun candidato valido per la classifica");
        }

        Hackathon h = hackathonRepository.getById(hackathonId);
        RankingPolicy rankingPolicy = h.getRankingPolicy();

        Long winnerParticipatingTeamId = winnerService.selectWinner(rankingPolicy, candidates);

        h.declareWinner(winnerParticipatingTeamId);
        hackathonRepository.save(h);
    }

    public List<HackathonSummaryDTO> searchHackathon(HackathonSearchCriteria hackathonSearchCriteria) {
        List<Hackathon> hackathons;

        if (hackathonSearchCriteria != null) {
            hackathons = hackathonRepository.search(hackathonSearchCriteria);
        } else {
            hackathons = hackathonRepository.findAll();
        }

        return hackathons.stream()
                .map(HackathonDTOMapper::toSummary)
                .toList();
    }

    private PublicHackathonView composeView(Hackathon h, StaffProfile organizer) {
        PublicHackathonView view = new BasePublicHackathonView(h, organizer);

        // IN_REGISTRATION: base + maxTeamSize, regulation, rankingPolicy
        // READY_TO_START: uguale a in_registration
        // RUNNING: uguale a in_registration
        // IN_EVALUATION: in_registration + delivery
        // CLOSED (+ winner != null): in_evaluation + winner
        switch (h.getStatus()) {
            case IN_REGISTRATION, READY_TO_START, RUNNING -> {
                view = new RegistrationInfoDecorator(view, h);
            }
            case IN_EVALUATION -> {
                view = new RegistrationInfoDecorator(view, h);
                view = new EvaluationInfoDecorator(view, h);
            }
            case CLOSED -> {
                view = new RegistrationInfoDecorator(view, h);
                view = new EvaluationInfoDecorator(view, h);
                if (h.getWinnerParticipatingTeamId() != null) {
                    view = new WinnerInfoDecorator(view, h);
                }
            }
            default -> {
            }
        }

        return view;
    }
}