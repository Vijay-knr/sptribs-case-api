package uk.gov.hmcts.sptribs.common.service.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.sptribs.ciccase.model.Applicant;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.CaseInvite;
import uk.gov.hmcts.sptribs.ciccase.model.State;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_USER_EMAIL;

@ExtendWith(MockitoExtension.class)
class SetApplicant2EmailTest {

    @InjectMocks
    private SetApplicant2Email setApplicant2Email;

    @Test
    void shouldSetApplicant2EmailIfApplicant2InviteEmailAddressIsSetAndApplicant2EmailIsNotSet() {
        //Given
        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder().build())
            .caseInvite(CaseInvite.builder()
                .applicant2InviteEmailAddress(TEST_USER_EMAIL)
                .build())
            .build();

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setData(caseData);

        //When
        final CaseDetails<CaseData, State> result = setApplicant2Email.apply(caseDetails);

        //Then
        assertThat(result.getData().getApplicant2().getEmail()).isEqualTo(TEST_USER_EMAIL);
    }

    @Test
    void shouldSetApplicant2EmailIfApplicant2InviteEmailAddressIsSetAndApplicant2EmailIsBlank() {
        //Given
        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder()
                .email("")
                .build())
            .caseInvite(CaseInvite.builder()
                .applicant2InviteEmailAddress(TEST_USER_EMAIL)
                .build())
            .build();

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setData(caseData);

        //When
        final CaseDetails<CaseData, State> result = setApplicant2Email.apply(caseDetails);

        //Then
        assertThat(result.getData().getApplicant2().getEmail()).isEqualTo(TEST_USER_EMAIL);
    }

    @Test
    void shouldNotSetApplicant2EmailIfApplicant2InviteEmailAddressIsNotSet() {
        //Given
        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder().build())
            .caseInvite(CaseInvite.builder()
                .build())
            .build();

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setData(caseData);

        //When
        final CaseDetails<CaseData, State> result = setApplicant2Email.apply(caseDetails);

        //Then
        assertThat(result.getData().getApplicant2().getEmail()).isNull();
    }

    @Test
    void shouldNotSetApplicant2EmailIfApplicant2InviteEmailAddressIsBlank() {
        //Given
        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder()
                .build())
            .caseInvite(CaseInvite.builder()
                .applicant2InviteEmailAddress("")
                .build())
            .build();

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setData(caseData);

        //When
        final CaseDetails<CaseData, State> result = setApplicant2Email.apply(caseDetails);

        //Then
        assertThat(result.getData().getApplicant2().getEmail()).isNull();
    }

    @Test
    void shouldNotSetApplicant2EmailIfApplicant2EmailIsSet() {
        //Given
        final String email = "app2@email";

        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder()
                .email(email)
                .build())
            .caseInvite(CaseInvite.builder()
                .applicant2InviteEmailAddress(TEST_USER_EMAIL)
                .build())
            .build();

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setData(caseData);

        //When
        final CaseDetails<CaseData, State> result = setApplicant2Email.apply(caseDetails);

        //Then
        assertThat(result.getData().getApplicant2().getEmail()).isEqualTo(email);
    }

    @Test
    void shouldNotSetApplicant2EmailIfCaseInviteIsNull() {
        //Given
        final String email = "app2@email";

        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder()
                .email("")
                .build())
            .build();

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setData(caseData);

        //When
        final CaseDetails<CaseData, State> result = setApplicant2Email.apply(caseDetails);

        //Then
        assertThat(result.getData().getApplicant2().getEmail()).isEmpty();
    }
}
