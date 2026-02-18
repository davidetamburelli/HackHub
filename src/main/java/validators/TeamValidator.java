package validators;

import model.dto.CreateTeamDTO;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

public class TeamValidator {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamValidator(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public void validate(CreateTeamDTO dto, Long userId) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");
        if (userId == null) throw new IllegalArgumentException("L'ID utente è obbligatorio");

        if (dto.getName() == null || dto.getName().trim().isBlank()) {
            throw new IllegalArgumentException("Il nome del team è obbligatorio");
        }

        if (userRepository.getById(userId) == null) {
            throw new DomainException("Utente leader non trovato con ID: " + userId);
        }

        if (teamRepository.existsByName(dto.getName())) {
            throw new DomainException("Esiste già un team con il nome: " + dto.getName());
        }

        if (teamRepository.existsByMemberId(userId)) {
            throw new DomainException("L'utente è già membro di un team e non può crearne uno nuovo.");
        }
    }
}