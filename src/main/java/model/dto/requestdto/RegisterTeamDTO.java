    package model.dto.requestdto;

    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import model.enums.PayoutMethod;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class RegisterTeamDTO {

        @NotBlank(message = "L'email di contatto è obbligatoria")
        @Email(message = "L'email fornita non è valida")
        private String contactEmail;

        @NotNull(message = "Il metodo di pagamento è obbligatorio")
        private PayoutMethod payoutMethod;

        @NotBlank(message = "Il riferimento per il pagamento (es. IBAN o email PayPal) è obbligatorio")
        private String payoutRef;

    }