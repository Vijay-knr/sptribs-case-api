package uk.gov.hmcts.sptribs.ciccase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum RepresentativeCIC implements HasLabel {
    @JsonProperty("RepresentativeCIC")
    REPRESENTATIVE("Representative");
    private final String label;
    public boolean isRepresentativeCIC() {
        return REPRESENTATIVE.name().equalsIgnoreCase(this.name());
    }
}
