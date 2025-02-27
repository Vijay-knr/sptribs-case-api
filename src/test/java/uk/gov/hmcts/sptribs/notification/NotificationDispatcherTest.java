package uk.gov.hmcts.sptribs.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.sptribs.ciccase.model.Applicant;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.CaseInvite;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.NO;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.YES;

@ExtendWith(MockitoExtension.class)
class NotificationDispatcherTest {

    @Spy
    private ApplicantNotification applicantNotification = new TestNotification();

    @InjectMocks
    private NotificationDispatcher notificationDispatcher;

    @Test
    void shouldNotifyApplicant1SolicitorIfRepresented() {
        //Given
        final long caseId = 1L;
        final CaseData caseData = CaseData.builder()
            .applicant1(Applicant.builder()
                .solicitorRepresented(YES)
                .build())
            .build();

        //When
        notificationDispatcher.send(applicantNotification, caseData, caseId);

        //Then
        verify(applicantNotification).sendToApplicant1Solicitor(caseData, caseId);
    }

    @Test
    void shouldNotifyApplicant1OfflineIfIsApplicant1OffLineTrue() {
        //Given
        final long caseId = 1L;
        final CaseData caseData = mock(CaseData.class);
        final Applicant applicant1 = mock(Applicant.class);
        final Applicant applicant2 = mock(Applicant.class);

        when(caseData.getApplicant1()).thenReturn(applicant1);
        when(applicant1.isRepresented()).thenReturn(false);
        when(caseData.getApplicant2()).thenReturn(applicant2);
        when(applicant2.isRepresented()).thenReturn(true);
        when(applicant1.isOffline()).thenReturn(true);

        //When
        notificationDispatcher.send(applicantNotification, caseData, caseId);

        //Then
        verify(applicantNotification).sendToApplicant1Offline(caseData, caseId);
    }

    @Test
    void shouldNotifyApplicant1IfNotRepresented() {
        //Given
        final long caseId = 1L;
        final CaseData caseData = CaseData.builder()
            .applicant1(Applicant.builder()
                .solicitorRepresented(NO)
                .build())
            .build();

        //When
        notificationDispatcher.send(applicantNotification, caseData, caseId);

        //Then
        verify(applicantNotification).sendToApplicant1(caseData, caseId);
    }

    @Test
    void shouldNotifyApplicant2SolicitorIfRepresented() {
        //Given
        final long caseId = 1L;
        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder()
                .solicitorRepresented(YES)
                .build())
            .build();

        //When
        notificationDispatcher.send(applicantNotification, caseData, caseId);

        //Then
        verify(applicantNotification).sendToApplicant2Solicitor(caseData, caseId);
    }

    @Test
    void shouldNotifyApplicant2OfflineIsApplicant1OffLineTrue() {
        //Given
        final long caseId = 1L;
        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder()
                .offline(YES)
                .build())
            .build();

        //When
        notificationDispatcher.send(applicantNotification, caseData, caseId);

        //Then
        verify(applicantNotification).sendToApplicant2Offline(caseData, caseId);
    }

    @Test
    void shouldNotifyApplicant2IfNotRepresented() {
        //Given
        final long caseId = 1L;
        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder()
                .email("app2@email.com")
                .solicitorRepresented(NO)
                .build())
            .build();

        //When
        notificationDispatcher.send(applicantNotification, caseData, caseId);

        //Then
        verify(applicantNotification).sendToApplicant2(caseData, caseId);
    }

    @Test
    void shouldNotifyApplicant2IfNotRepresentedWithCaseInvite() {
        //Given
        final long caseId = 1L;
        final CaseData caseData = CaseData.builder()
            .applicant2(Applicant.builder()
                .solicitorRepresented(NO)
                .build())
            .caseInvite(CaseInvite.builder()
                .applicant2InviteEmailAddress("app2@email.com")
                .build())
            .build();

        //When
        notificationDispatcher.send(applicantNotification, caseData, caseId);

        //Then
        verify(applicantNotification).sendToApplicant2(caseData, caseId);
    }

    public static class TestNotification implements ApplicantNotification {
    }
}
