package uk.gov.hmcts.sptribs.ciccase.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;
import uk.gov.hmcts.ccd.sdk.type.OrganisationPolicy;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.sptribs.ciccase.model.access.AosAccess;
import uk.gov.hmcts.sptribs.ciccase.model.access.CaseworkerAccessOnlyAccess;
import uk.gov.hmcts.sptribs.ciccase.model.access.CaseworkerAndSuperUserAccess;
import uk.gov.hmcts.sptribs.ciccase.model.access.DefaultAccess;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.ccd.sdk.type.FieldType.Email;
import static uk.gov.hmcts.ccd.sdk.type.FieldType.TextArea;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.YES;
import static uk.gov.hmcts.sptribs.ciccase.model.HowToRespondApplication.DISPUTE_DIVORCE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcknowledgementOfService {

    @CCD(
        label = "Has the respondent read the application ${labelContentDivorceOrEndCivilPartnership}?",
        access = AosAccess.class
    )
    private YesOrNo confirmReadPetition;

    @CCD(
        label = "Respondent confirms that they want to dispute the application",
        access = AosAccess.class
    )
    private YesOrNo confirmDisputeApplication;

    @CCD(
        label = "Notification sent to Applicant indicating that the dispute form is overdue",
        access = {DefaultAccess.class}
    )
    private YesOrNo applicantNotifiedDisputeFormOverdue;

    @CCD(
        label = "Respondent agreed to claimed jurisdiction?",
        access = AosAccess.class
    )
    private YesOrNo jurisdictionAgree;

    @CCD(
        label = "Date AoS submitted to HMCTS",
        access = {DefaultAccess.class}
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateAosSubmitted;

    @CCD(
        label = "Notice of Proceedings email address",
        typeOverride = Email,
        access = {CaseworkerAndSuperUserAccess.class}
    )
    private String noticeOfProceedingsEmail;

    @CCD(
        label = "Notice of Proceedings solicitor's firm"
    )
    private String noticeOfProceedingsSolicitorFirm;

    @CCD(
        label = "Reason respondent disagreed to claimed jurisdiction",
        typeOverride = TextArea,
        access = {AosAccess.class}
    )
    private String reasonCourtsOfEnglandAndWalesHaveNoJurisdiction;

    @CCD(
        label = "In which country is the respondents life mainly based?",
        access = {AosAccess.class}
    )
    private String inWhichCountryIsYourLifeMainlyBased;

    @CCD(
        label = "Respondent Statement of Truth",
        access = AosAccess.class
    )
    private YesOrNo statementOfTruth;

    @CCD(
        label = "The respondent has given their \"prayer\".",
        hint = "\"The prayer\" means they confirm they wish to dissolve the union, pay any fees (if applicable),"
            + " and have decided how money and property will be split (\"financial order\").",
        access = AosAccess.class
    )
    private YesOrNo prayerHasBeenGiven;

    @CCD(
        label = "How do you want to respond?",
        access = {AosAccess.class}
    )
    private HowToRespondApplication howToRespondApplication;

    @CCD(
        label = "Solicitor’s name"
    )
    private String solicitorName;

    @CCD(
        label = "Solicitor’s firm"
    )
    private String solicitorFirm;

    @CCD(
        label = "Additional Comments",
        hint = "For the attention of court staff. These comments will not form part of the AOS",
        typeOverride = TextArea,
        access = {AosAccess.class}
    )
    private String additionalComments;

    @CCD(
        label = "What type of document was attached?"
    )
    private OfflineDocumentReceived typeOfDocumentAttached;

    @Getter
    @AllArgsConstructor
    public enum OfflineDocumentReceived implements HasLabel {

        @JsonProperty("D10")
        AOS_D10("Acknowledgement of service (D10)"),

        @JsonProperty("Other")
        OTHER("Other");

        private final String label;
    }

    @JsonUnwrapped(prefix = "disputingFee")
    @Builder.Default
    @CCD(access = {CaseworkerAccessOnlyAccess.class})
    private FeeDetails disputingFee = new FeeDetails();

    @JsonIgnore
    public void setNoticeOfProceedings(final Applicant applicant) {
        noticeOfProceedingsEmail = applicant.getCorrespondenceEmail();

        if (applicant.isRepresented()) {
            final OrganisationPolicy<UserRole> organisationPolicy = applicant.getSolicitor().getOrganisationPolicy();

            if (nonNull(organisationPolicy)) {
                noticeOfProceedingsSolicitorFirm = organisationPolicy.getOrganisation().getOrganisationName();
            } else {
                noticeOfProceedingsSolicitorFirm = applicant.getSolicitor().getFirmName();
            }
        }
    }

    @JsonIgnore
    public boolean isDisputed() {
        return DISPUTE_DIVORCE.equals(howToRespondApplication);
    }

    @JsonIgnore
    public boolean hasApplicantBeenNotifiedDisputeFormOverdue() {
        return YES.equals(applicantNotifiedDisputeFormOverdue);
    }
}
