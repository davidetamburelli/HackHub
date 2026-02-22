package it.hackhub;

import handlers.*;
import model.dto.requestdto.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.DomainException;

@SpringBootApplication
@ComponentScan(basePackages = {"handlers", "config", "utils", "repository", "it.hackhub"})
@EntityScan(basePackages = {"model"})
@RestController
public class HackHubApplication {

    private final HackathonHandler hackathonHandler;
    private final SubmissionHandler submissionHandler;
    private final ReportHandler reportHandler;
    private final SupportRequestHandler supportRequestHandler;
    private final EvaluationHandler evaluationHandler;
    private final InvitationHandler invitationHandler;
    private final TeamHandler teamHandler;
    private final ParticipatingTeamHandler participatingTeamHandler;

    public HackHubApplication(
            HackathonHandler hackathonHandler,
            SubmissionHandler submissionHandler,
            ReportHandler reportHandler,
            SupportRequestHandler supportRequestHandler,
            EvaluationHandler evaluationHandler,
            InvitationHandler invitationHandler,
            TeamHandler teamHandler,
            ParticipatingTeamHandler participatingTeamHandler) {
        this.hackathonHandler = hackathonHandler;
        this.submissionHandler = submissionHandler;
        this.reportHandler = reportHandler;
        this.supportRequestHandler = supportRequestHandler;
        this.evaluationHandler = evaluationHandler;
        this.invitationHandler = invitationHandler;
        this.teamHandler = teamHandler;
        this.participatingTeamHandler = participatingTeamHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(HackHubApplication.class, args);
    }

    @PostMapping(value="/searchHackathon")
    public ResponseEntity<Object> searchHackathon (@RequestBody(required=false) HackathonSearchCriteria criteria) {
        try {
            return new ResponseEntity<>(hackathonHandler.searchHackathon(criteria), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/hackathon/{hackathonId}")
    public ResponseEntity<Object> viewHackathon (@PathVariable long hackathonId,
                                                 @RequestHeader(value="X-Actor-Id", required=false) Long id,
                                                 @RequestHeader(value="X-Actor-Type", required=false) String actorType) {
        if(actorType != null && actorType.equals("STAFF") || id != null) {
            return new ResponseEntity<>(hackathonHandler.getHackathonDetails(id, hackathonId), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(hackathonHandler.getHackathonPublicInfos(hackathonId), HttpStatus.OK);
        }
    }

    @GetMapping(value="/hackathon/{hackathonId}/submissions")
    public ResponseEntity<Object> viewSubmissions (@PathVariable long hackathonId,
                                                   @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(submissionHandler.getSubmissionsList(id, hackathonId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/hackathon/{hackathonId}/submission/{submissionId}")
    public ResponseEntity<Object> viewSubmissionDetails (@PathVariable long hackathonId,
                                                         @PathVariable long submissionId,
                                                         @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(submissionHandler.getSubmissionDetails(id, hackathonId, submissionId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/createHackathon")
    public ResponseEntity<Object> createHackathon (@RequestHeader(value="X-Actor-Id", required=true) Long id,
                                                   @RequestBody CreateHackathonDTO createHackathonDTO) {
        try {
            hackathonHandler.createHackathon(id, createHackathonDTO);
            return new ResponseEntity<>("Hackathon creato con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/hackathon/{hackathonId}/reports")
    public ResponseEntity<Object> viewReports (@PathVariable long hackathonId,
                                               @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(reportHandler.getReports(id, hackathonId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/hackathon/{hackathonId}/report/{reportId}")
    public ResponseEntity<Object> viewReportDetails (@PathVariable long hackathonId,
                                                     @PathVariable long reportId,
                                                     @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(reportHandler.getReportDetails(id, hackathonId, reportId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/report/{reportId}/archive")
    public ResponseEntity<Object> archiveReport (@PathVariable long hackathonId,
                                                 @PathVariable long reportId,
                                                 @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            reportHandler.archiveReport(id, hackathonId, reportId);
            return new ResponseEntity<>("Segnalazione archiviata con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/report/{reportId}/applySanction")
    public ResponseEntity<Object> applySanction (@PathVariable long hackathonId,
                                                 @PathVariable long reportId,
                                                 @RequestHeader(value="X-Actor-Id", required=true) Long id,
                                                 @RequestBody ApplySanctionDTO applySanctionDTO) {
        try {
            reportHandler.applySanction(id, hackathonId, reportId, applySanctionDTO);
            return new ResponseEntity<>("Sanzione applicata con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/declareWinner")
    public ResponseEntity<Object> declareWinner (@PathVariable long hackathonId,
                                                 @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {

            return new ResponseEntity<>("Vincitore proclamato con successo\n " +  hackathonHandler.declareWinner(id, hackathonId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/payout")
    public ResponseEntity<Object> sendPrizeToWinner (@PathVariable long hackathonId,
                                                     @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(hackathonHandler.sendPrizeToWinner(id, hackathonId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/hackathon/{hackathonId}/supportRequests")
    public ResponseEntity<Object> viewSupportRequests (@PathVariable long hackathonId,
                                                       @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(supportRequestHandler.getSupportRequests(id, hackathonId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/hackathon/{hackathonId}/supportRequest/{supportRequestId}")
    public ResponseEntity<Object> viewSupportRequestDetail (@PathVariable long hackathonId,
                                                            @PathVariable long supportRequestId,
                                                            @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(supportRequestHandler.getSupportRequestDetails(id, hackathonId, supportRequestId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/supportRequest/{supportRequestId}/reply")
    public ResponseEntity<Object> replyToSupportRequest (@PathVariable long hackathonId,
                                                         @PathVariable long supportRequestId,
                                                         @RequestHeader(value="X-Actor-Id", required=true) Long id,
                                                         @RequestBody ReplySupportRequestDTO reply) {
        try {
            supportRequestHandler.replyToSupportRequest(id, hackathonId, supportRequestId, reply);
            return new ResponseEntity<>("Risposta inviata con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/supportRequest/{supportRequestId}/bookCall")
    public ResponseEntity<Object> bookCall (@PathVariable long hackathonId,
                                            @PathVariable long supportRequestId,
                                            @RequestHeader(value="X-Actor-Id", required=true) Long id,
                                            @RequestBody BookSupportCallDTO supportCall) {
        try {
            supportRequestHandler.bookSupportCall(id, hackathonId, supportRequestId, supportCall);
            return new ResponseEntity<>("Call pianificata con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/partecipatingTeam/{partecipatingTeamId}/sendReport")
    public ResponseEntity<Object> sendReport (@PathVariable long hackathonId,
                                              @PathVariable long partecipatingTeamId,
                                              @RequestHeader(value="X-Actor-Id", required=true) Long id,
                                              @RequestBody CreateReportDTO report) {
        try {
            reportHandler.createReport(id, hackathonId, partecipatingTeamId, report);
            return new ResponseEntity<>("Segnalazione creata con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/submission/{submissionId}/addEvaluation")
    public ResponseEntity<Object> addEvaluation (@PathVariable long hackathonId,
                                                 @PathVariable long submissionId,
                                                 @RequestHeader(value="X-Actor-Id", required=true) Long id,
                                                 @RequestBody AddEvaluationDTO evaluation) {
        try {
            evaluationHandler.addEvaluation(id, hackathonId, submissionId, evaluation);
            return new ResponseEntity<>("Valutazione aggiunta con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/confirmEvaluations")
    public ResponseEntity<Object> confirmEvaluations (@PathVariable long hackathonId,
                                                      @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            hackathonHandler.confirmEvaluations(id, hackathonId);
            return new ResponseEntity<>("Valutazioni confermate con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/invitations")
    public ResponseEntity<Object> viewInvitations (@RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(invitationHandler.getInvitationsList(id), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/invitation/{invitationId}")
    public ResponseEntity<Object> viewInvitationDetail (@PathVariable long invitationId,
                                                        @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            return new ResponseEntity<>(invitationHandler.getInvitationDetails(id, invitationId), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/invitation/{invitationId}/accept")
    public ResponseEntity<Object> acceptInvitation (@PathVariable long invitationId,
                                                    @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            invitationHandler.acceptInvitation(id, invitationId);
            return new ResponseEntity<>("Invito accettato", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/invitation/{invitationId}/reject")
    public ResponseEntity<Object> rejectInvitation (@PathVariable long invitationId,
                                                    @RequestHeader(value="X-Actor-Id", required=true) Long id) {
        try {
            invitationHandler.rejectInvitation(id, invitationId);
            return new ResponseEntity<>("Invito rifiutato", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/createTeam")
    public ResponseEntity<Object> createTeam (@RequestHeader(value="X-Actor-Id", required=true) Long id,
                                              @RequestBody CreateTeamDTO createTeam) {
        try {
            teamHandler.createTeam(id, createTeam);
            return new ResponseEntity<>("Team creato con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/sendSupportRequest")
    public ResponseEntity<Object> sendSupportRequest (@PathVariable long hackathonId,
                                                      @RequestHeader(value="X-Actor-Id", required=true) Long id,
                                                      @RequestBody CreateSupportRequestDTO createSupportRequest) {
        try {
            supportRequestHandler.createSupportRequest(id, hackathonId, createSupportRequest);
            return new ResponseEntity<>("Richiesta di supporto inviata con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/addSubmission")
    public ResponseEntity<Object> addSubmission (@PathVariable long hackathonId,
                                                 @RequestHeader(value="X-Actor-Id", required=true) Long id,
                                                 @RequestBody AddSubmissionDTO addSubmission) {
        try {
            submissionHandler.createSubmission(id, hackathonId, addSubmission);
            return new ResponseEntity<>("Sottomissione completata con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/users")
    public ResponseEntity<Object> searchUser (@RequestParam String username) {
        try {
            return new ResponseEntity<>(invitationHandler.searchUser(username), HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/user/{userId}/inviteToTeam")
    public ResponseEntity<Object> inviteUser (@RequestHeader(value="X-Actor-Id", required=true) Long id,
                                              @PathVariable long userId) {
        try {
            invitationHandler.inviteUser(id, userId);
            return new ResponseEntity<>("Utente invitato con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value="/hackathon/{hackathonId}/registerTeam")
    public ResponseEntity<Object> registerToHackathon (@PathVariable long hackathonId,
                                                       @RequestHeader(value="X-Actor-Id", required=true) Long id,
                                                       @RequestBody RegisterTeamDTO registerTeamDTO) {
        try {
            participatingTeamHandler.registerTeamToHackathon(id, hackathonId, registerTeamDTO);
            return new ResponseEntity<>("Team registrato all'hackathon con successo", HttpStatus.OK);
        } catch (DomainException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}