package uk.gov.hmcts.sptribs.ciccase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum ContactPreferencePartiesCIC implements HasLabel {

    @JsonProperty("SubjectCIC")
    SUBJECT("Subject"),

    @JsonProperty("RepresentativeCIC")
    REPRESENTATIVE("Representative"),

    @JsonProperty("ApplicantCIC")
    APPLICANT("Applicant (if different from subject).");

    private final String label;

    public boolean isSubject() {
        return SUBJECT.name().equalsIgnoreCase(this.name());
    }

    public boolean isRepresentativeCIC() {
        return REPRESENTATIVE.name().equalsIgnoreCase(this.name());
    }

    public boolean isApplicantCIC() {
        return APPLICANT.name().equalsIgnoreCase(this.name());
    }
}
