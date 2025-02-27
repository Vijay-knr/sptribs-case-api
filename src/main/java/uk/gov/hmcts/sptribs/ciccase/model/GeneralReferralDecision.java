package uk.gov.hmcts.sptribs.ciccase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum GeneralReferralDecision implements HasLabel {
    @JsonProperty("approve")
    APPROVE("Approve"),

    @JsonProperty("refuse")
    REFUSE("Refuse"),

    @JsonProperty("other")
    OTHER("Other response");

    private final String label;
}
