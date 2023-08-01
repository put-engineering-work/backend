package work.tutor.dto.tutor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class RequestPersonalTutorDataDTO {

    @NotNull
    @NotEmpty(message = "ACTIVITY_TYPE_MUST_BE_NOT_EMPTY")
    @Schema(description = "Activity type of a tutor.", example = "Firma/Osoba Prywatna")
    private String activityType;

    @NotNull
    @NotEmpty(message = "NAME_OF_A_TUTOR_MUST_BE_NOT_EMPTY")
    @Schema(description = "Name of a tutor.", example = "Jacek")
    private String name;

    @NotNull
    @NotEmpty(message = "SURNAME_OF_A_TUTOR_MUST_BE_NOT_EMPTY")
    @Schema(description = "Surname of a tutor.", example = "Olejnik")
    private String surname;

    @NotNull
    @NotEmpty(message = "PHONE_NUMBER_OF_A_TUTOR_MUST_BE_NOT_EMPTY")
    @Schema(description = "Phone number of a tutor.", example = "+123456789")
    private String phoneNumber;

    @NotNull
    @NotEmpty(message = "NIP_OF_A_TUTOR_MUST_BE_NOT_EMPTY")
    @Schema(description = "NIP of a tutor.", example = "6932433516")
    private String nip;

    @NotNull
    @NotEmpty(message = "REGON_MUST_BE_NOT_EMPTY")
    @Schema(description = "Regon of a tutor.", example = "6920433516")
    private String regon;

    @NotNull
    @NotEmpty(message = "COMPANY_NAME_MUST_BE_NOT_EMPTY")
    @Schema(description = "Company name.", example = "6920433516")
    private String companyName;

    @NotNull
    @NotEmpty(message = "STREET_MUST_BE_NOT_EMPTY")
    @Schema(description = "Street.", example = "6920433516")
    private String street;

    @NotNull
    @NotEmpty(message = "POST_CODE_MUST_BE_NOT_EMPTY")
    @Schema(description = "Post code.", example = "00-000")
    private String postCode;

    @NotNull
    @NotEmpty(message = "PLACE_MUST_BE_NOT_EMPTY")
    @Schema(description = "Place.", example = "Gwillice")
    private String place;

    @NotNull
    @NotEmpty(message = "BANK_NAME_MUST_BE_NOT_EMPTY")
    @Schema(description = "Bank name.", example = "ING")
    private String bankName;

    @NotNull
    @NotEmpty(message = "BANK_ACCOUNT_NUMBER_MUST_BE_NOT_EMPTY")
    @Schema(description = "Bank account number.", example = "0123012301230123")
    private String bankAccountNumber;
}
