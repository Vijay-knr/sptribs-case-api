package uk.gov.hmcts.sptribs.hearingvenue.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HearingVenue {
    @JsonProperty("code")
    private String feeCode;

    @JsonProperty("fee_amount")
    private Double amount;

    private Integer version;

    private String description;
}
