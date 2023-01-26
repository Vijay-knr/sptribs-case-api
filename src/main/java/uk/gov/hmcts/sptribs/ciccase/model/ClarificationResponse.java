package uk.gov.hmcts.sptribs.ciccase.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

import java.time.LocalDate;
import java.util.List;

import static uk.gov.hmcts.ccd.sdk.type.FieldType.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClarificationResponse {

    @CCD(
        label = "Date clarification submitted"
    )
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate clarificationDate;

    @CCD(
        label = "List of responses for Conditional Order clarification",
        typeOverride = Collection,
        typeParameterOverride = "TextArea"
    )
    private List<ListValue<String>> clarificationResponses;

    @CCD(
        label = "Applicant cannot upload all or some Conditional Order Clarification documents"
    )
    private YesOrNo cannotUploadClarificationDocuments;
}
