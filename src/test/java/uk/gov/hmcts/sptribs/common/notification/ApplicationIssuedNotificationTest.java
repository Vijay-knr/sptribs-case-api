package uk.gov.hmcts.sptribs.common.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.AddressGlobalUK;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.sptribs.ciccase.model.Applicant;
import uk.gov.hmcts.sptribs.ciccase.model.Application;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.Solicitor;
import uk.gov.hmcts.sptribs.common.config.EmailTemplatesConfig;
import uk.gov.hmcts.sptribs.common.service.HoldingPeriodService;
import uk.gov.hmcts.sptribs.notification.CommonContent;
import uk.gov.hmcts.sptribs.notification.NotificationService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.join;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static uk.gov.hmcts.sptribs.ciccase.model.ApplicationType.JOINT_APPLICATION;
import static uk.gov.hmcts.sptribs.ciccase.model.ApplicationType.SOLE_APPLICATION;
import static uk.gov.hmcts.sptribs.ciccase.model.DivorceOrDissolution.DISSOLUTION;
import static uk.gov.hmcts.sptribs.ciccase.model.DivorceOrDissolution.DIVORCE;
import static uk.gov.hmcts.sptribs.ciccase.model.Gender.FEMALE;
import static uk.gov.hmcts.sptribs.ciccase.model.Gender.MALE;
import static uk.gov.hmcts.sptribs.ciccase.model.LanguagePreference.ENGLISH;
import static uk.gov.hmcts.sptribs.ciccase.model.LanguagePreference.WELSH;
import static uk.gov.hmcts.sptribs.ciccase.model.ServiceMethod.COURT_SERVICE;
import static uk.gov.hmcts.sptribs.ciccase.model.ServiceMethod.PERSONAL_SERVICE;
import static uk.gov.hmcts.sptribs.ciccase.model.ServiceMethod.SOLICITOR_SERVICE;
import static uk.gov.hmcts.sptribs.ciccase.search.CaseFieldsConstants.DUE_DATE;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.ISSUE_DATE;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.NOT_PROVIDED;
import static uk.gov.hmcts.sptribs.notification.CommonContent.APPLICANT_NAME;
import static uk.gov.hmcts.sptribs.notification.CommonContent.APPLICATION_REFERENCE;
import static uk.gov.hmcts.sptribs.notification.CommonContent.IS_DISSOLUTION;
import static uk.gov.hmcts.sptribs.notification.CommonContent.IS_DIVORCE;
import static uk.gov.hmcts.sptribs.notification.CommonContent.NO;
import static uk.gov.hmcts.sptribs.notification.CommonContent.PARTNER;
import static uk.gov.hmcts.sptribs.notification.CommonContent.RESPONDENT_NAME;
import static uk.gov.hmcts.sptribs.notification.CommonContent.SIGN_IN_URL;
import static uk.gov.hmcts.sptribs.notification.CommonContent.SOLICITOR_NAME;
import static uk.gov.hmcts.sptribs.notification.CommonContent.SOLICITOR_REFERENCE;
import static uk.gov.hmcts.sptribs.notification.CommonContent.SUBMISSION_RESPONSE_DATE;
import static uk.gov.hmcts.sptribs.notification.CommonContent.YES;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.APPLICANT_SOLICITOR_SERVICE;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.JOINT_APPLICATION_ACCEPTED;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.JOINT_SOLICITOR_NOTICE_OF_PROCEEDINGS;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.RESPONDENT_SOLICITOR_NOTICE_OF_PROCEEDINGS;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.SOLE_APPLICANT_APPLICATION_ACCEPTED;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.SOLE_APPLICANT_SOLICITOR_NOTICE_OF_PROCEEDINGS;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.SOLE_APPLICANT_SOLICITOR_NOTICE_OF_PROCEEDINGS_REISSUE;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.SOLE_RESPONDENT_APPLICATION_ACCEPTED;
import static uk.gov.hmcts.sptribs.notification.FormatUtil.DATE_TIME_FORMATTER;
import static uk.gov.hmcts.sptribs.notification.FormatUtil.formatId;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.APPLICANT_2_FIRST_NAME;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_APPLICANT_2_USER_EMAIL;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_CASE_ID;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_FIRST_NAME;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_LAST_NAME;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_SOLICITOR_EMAIL;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_SOLICITOR_NAME;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_USER_EMAIL;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.LOCAL_DATE;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.applicantRepresentedBySolicitor;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.getApplicant;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.getApplicant2;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.getConfigTemplateVars;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.getMainTemplateVars;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.respondent;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.respondentWithDigitalSolicitor;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.validCaseDataForIssueApplication;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.validJointApplicant1CaseData;

@ExtendWith(MockitoExtension.class)
public class ApplicationIssuedNotificationTest {

    private static final String CASE_ID = "case id";

    @Mock
    private NotificationService notificationService;

    @Mock
    private CommonContent commonContent;

    @Mock
    private EmailTemplatesConfig emailTemplatesConfig;

    @Mock
    private HoldingPeriodService holdingPeriodService;

    @InjectMocks
    private ApplicationIssuedNotification notification;

    @Test
    void shouldSendEmailToSoleApplicant1WithDivorceContent() {
        //Given
        CaseData data = validCaseDataForIssueApplication();
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());

        Map<String, String> divorceTemplateVars = new HashMap<>();
        divorceTemplateVars.putAll(getMainTemplateVars());
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2()))
            .thenReturn(divorceTemplateVars);
        when(holdingPeriodService.getRespondByDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(16));
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant1(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_USER_EMAIL),
            eq(SOLE_APPLICANT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(SUBMISSION_RESPONSE_DATE, data.getApplication().getIssueDate().plusDays(141).format(DATE_TIME_FORMATTER)),
                hasEntry(IS_DIVORCE, YES),
                hasEntry(IS_DISSOLUTION, NO)
            )),
            eq(ENGLISH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2());
    }

    @Test
    void shouldSendEmailToSoleApplicant1WithDissolutionContent() {
        //Given
        CaseData data = validCaseDataForIssueApplication();
        data.setDivorceOrDissolution(DISSOLUTION);
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        Map<String, String> dissolutionTemplateVars = new HashMap<>();
        dissolutionTemplateVars.putAll(getMainTemplateVars());
        dissolutionTemplateVars.putAll(Map.of(IS_DIVORCE, NO, IS_DISSOLUTION, YES));
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2()))
            .thenReturn(dissolutionTemplateVars);
        when(holdingPeriodService.getRespondByDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(16));
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant1(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_USER_EMAIL),
            eq(SOLE_APPLICANT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(SUBMISSION_RESPONSE_DATE, data.getApplication().getIssueDate().plusDays(141).format(DATE_TIME_FORMATTER)),
                hasEntry(IS_DIVORCE, NO),
                hasEntry(IS_DISSOLUTION, YES)
            )),
            eq(ENGLISH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2());
    }

    @Test
    void shouldNotSendEmailToSoleApplicant1IfPersonalService() {
        //Given
        CaseData data = validCaseDataForIssueApplication();
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        data.getApplication().setServiceMethod(PERSONAL_SERVICE);

        Map<String, String> divorceTemplateVars = new HashMap<>();
        divorceTemplateVars.putAll(getMainTemplateVars());

        //When
        notification.sendToApplicant1(data, 1234567890123456L);

        //Then
        verifyNoInteractions(notificationService, commonContent);
    }

    @Test
    void shouldSendEmailToSoleRespondentWithDivorceContent() {
        //Given
        CaseData data = validCaseDataForIssueApplication();
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        data.getApplicant2().setEmail(null);

        Map<String, String> divorceTemplateVars = new HashMap<>();
        divorceTemplateVars.putAll(getMainTemplateVars());
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1()))
            .thenReturn(divorceTemplateVars);
        when(emailTemplatesConfig.getTemplateVars()).thenReturn(getConfigTemplateVars());
        when(holdingPeriodService.getRespondByDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(16));
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant2(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_APPLICANT_2_USER_EMAIL),
            eq(SOLE_RESPONDENT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(IS_DIVORCE, YES),
                hasEntry(IS_DISSOLUTION, NO)
            )),
            eq(ENGLISH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1());
    }

    @Test
    void shouldSendEmailToSoleRespondentWithDissolutionContent() {
        //Given
        CaseData data = validCaseDataForIssueApplication();
        data.setDivorceOrDissolution(DISSOLUTION);
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        data.getApplicant2().setEmail(null);

        Map<String, String> dissolutionTemplateVars = new HashMap<>();
        dissolutionTemplateVars.putAll(getMainTemplateVars());
        dissolutionTemplateVars.putAll(Map.of(IS_DIVORCE, NO, IS_DISSOLUTION, YES));
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1()))
            .thenReturn(dissolutionTemplateVars);

        when(emailTemplatesConfig.getTemplateVars()).thenReturn(getConfigTemplateVars());
        when(holdingPeriodService.getRespondByDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(16));
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant2(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_APPLICANT_2_USER_EMAIL),
            eq(SOLE_RESPONDENT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(IS_DIVORCE, NO),
                hasEntry(IS_DISSOLUTION, YES)
            )),
            eq(ENGLISH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1());
    }

    @Test
    void shouldSendEmailToJointApplicant1WithDivorceContent() {
        //Given
        CaseData data = validJointApplicant1CaseData();
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        Map<String, String> divorceTemplateVars = new HashMap<>();
        divorceTemplateVars.putAll(getMainTemplateVars());
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2()))
            .thenReturn(divorceTemplateVars);
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant1(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_USER_EMAIL),
            eq(JOINT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(SUBMISSION_RESPONSE_DATE, data.getApplication().getIssueDate().plusDays(141).format(DATE_TIME_FORMATTER)),
                hasEntry(IS_DIVORCE, YES),
                hasEntry(IS_DISSOLUTION, NO)
            )),
            eq(ENGLISH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2());
    }

    @Test
    void shouldSendEmailToJointApplicant1WithDissolutionContent() {
        //Given
        CaseData data = validJointApplicant1CaseData();
        data.setDivorceOrDissolution(DISSOLUTION);
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        Map<String, String> dissolutionTemplateVars = new HashMap<>();
        dissolutionTemplateVars.putAll(getMainTemplateVars());
        dissolutionTemplateVars.putAll(Map.of(IS_DIVORCE, NO, IS_DISSOLUTION, YES));
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2()))
            .thenReturn(dissolutionTemplateVars);
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant1(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_USER_EMAIL),
            eq(JOINT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(SUBMISSION_RESPONSE_DATE, data.getApplication().getIssueDate().plusDays(141).format(DATE_TIME_FORMATTER)),
                hasEntry(IS_DIVORCE, NO),
                hasEntry(IS_DISSOLUTION, YES)
            )),
            eq(ENGLISH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2());
    }

    @Test
    void shouldSendEmailToApplicant1WithWelshPartnerContent() {
        //Given
        CaseData data = validCaseDataForIssueApplication();
        data.setDivorceOrDissolution(DIVORCE);
        data.getApplicant1().setLanguagePreferenceWelsh(YesOrNo.YES);
        data.getApplication().setIssueDate(LocalDate.now());

        Map<String, String> divorceTemplateVars = new HashMap<>();
        divorceTemplateVars.put(PARTNER, "gŵr");
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2()))
            .thenReturn(divorceTemplateVars);

        when(holdingPeriodService.getRespondByDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(16));
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant1(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_USER_EMAIL),
            eq(SOLE_APPLICANT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(PARTNER, "gŵr")
            )),
            eq(WELSH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant1(), data.getApplicant2());
    }

    @Test
    void shouldSendEmailToApplicant2WithDivorceContent() {
        //Given
        CaseData data = validJointApplicant1CaseData();
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        Map<String, String> divorceTemplateVars = new HashMap<>();
        divorceTemplateVars.putAll(getMainTemplateVars());
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1()))
            .thenReturn(divorceTemplateVars);
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant2(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_APPLICANT_2_USER_EMAIL),
            eq(JOINT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(SUBMISSION_RESPONSE_DATE, data.getApplication().getIssueDate().plusDays(141).format(DATE_TIME_FORMATTER)),
                hasEntry(IS_DIVORCE, YES),
                hasEntry(IS_DISSOLUTION, NO)
            )),
            eq(ENGLISH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1());
    }

    @Test
    void shouldSendEmailToApplicant2WithDissolutionContent() {
        //Given
        CaseData data = validJointApplicant1CaseData();
        data.setDivorceOrDissolution(DISSOLUTION);
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        Map<String, String> dissolutionTemplateVars = new HashMap<>();
        dissolutionTemplateVars.putAll(getMainTemplateVars());
        dissolutionTemplateVars.putAll(Map.of(IS_DIVORCE, NO, IS_DISSOLUTION, YES));
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1()))
            .thenReturn(dissolutionTemplateVars);
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant2(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_APPLICANT_2_USER_EMAIL),
            eq(JOINT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(SUBMISSION_RESPONSE_DATE, data.getApplication().getIssueDate().plusDays(141).format(DATE_TIME_FORMATTER)),
                hasEntry(IS_DIVORCE, NO),
                hasEntry(IS_DISSOLUTION, YES)
            )),
            eq(ENGLISH)
        );
        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1());
    }

    @Test
    void shouldSendNotificationToApplicantSolicitor() {
        //Given
        final CaseData caseData = CaseData.builder()
            .divorceOrDissolution(DIVORCE)
            .applicant1(applicantRepresentedBySolicitor())
            .applicant2(respondent())
            .applicationType(SOLE_APPLICATION)
            .divorceOrDissolution(DIVORCE)
            .dueDate(LOCAL_DATE.plusDays(7))
            .application(Application.builder().issueDate(LOCAL_DATE).build())
            .build();

        when(holdingPeriodService.getDueDateFor(LOCAL_DATE)).thenReturn(caseData.getApplication().getIssueDate().plusDays(141));

        when(commonContent.basicTemplateVars(caseData, TEST_CASE_ID)).thenReturn(commonTemplateVars());

        //When
        notification.sendToApplicant1Solicitor(caseData, TEST_CASE_ID);

        //Then
        verify(notificationService).sendEmail(
            TEST_SOLICITOR_EMAIL,
            SOLE_APPLICANT_SOLICITOR_NOTICE_OF_PROCEEDINGS,
            nopSolicitorTemplateVars(),
            ENGLISH
        );

        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void shouldSendNotificationToApplicantSolicitorOnReissue() {
        //Given
        final CaseData caseData = CaseData.builder()
            .divorceOrDissolution(DIVORCE)
            .applicant1(applicantRepresentedBySolicitor())
            .applicant2(respondent())
            .applicationType(SOLE_APPLICATION)
            .divorceOrDissolution(DIVORCE)
            .dueDate(LOCAL_DATE.plusDays(7))
            .application(Application.builder().issueDate(LOCAL_DATE).reissueDate(LOCAL_DATE.plusDays(2)).build())
            .build();

        when(holdingPeriodService.getDueDateFor(LOCAL_DATE)).thenReturn(caseData.getApplication().getIssueDate().plusDays(141));

        when(commonContent.basicTemplateVars(caseData, TEST_CASE_ID)).thenReturn(commonTemplateVars());

        //When
        notification.sendToApplicant1Solicitor(caseData, TEST_CASE_ID);

        //Then
        verify(notificationService).sendEmail(
            TEST_SOLICITOR_EMAIL,
            SOLE_APPLICANT_SOLICITOR_NOTICE_OF_PROCEEDINGS_REISSUE,
            nopSolicitorTemplateVars(),
            ENGLISH
        );

        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void shouldSendNotificationToRespondentSolicitorIfSoleApplicationAndNotSolicitorServiceWithoutSolicitorReference() {
        //Given
        final CaseData caseData = CaseData.builder()
            .applicationType(SOLE_APPLICATION)
            .applicant1(getApplicant())
            .applicant2(respondentWithDigitalSolicitor())
            .divorceOrDissolution(DIVORCE)
            .dueDate(LOCAL_DATE.plusDays(7))
            .application(Application.builder().serviceMethod(COURT_SERVICE).issueDate(LOCAL_DATE).build())
            .build();

        when(commonContent.basicTemplateVars(caseData, TEST_CASE_ID)).thenReturn(commonTemplateVars());

        //When
        notification.sendToApplicant2Solicitor(caseData, TEST_CASE_ID);

        //Then
        verify(notificationService).sendEmail(
            TEST_SOLICITOR_EMAIL,
            RESPONDENT_SOLICITOR_NOTICE_OF_PROCEEDINGS,
            respondentSolicitorTemplateVars(),
            ENGLISH
        );

        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void shouldSendNotificationToRespondentSolicitorIfSoleApplicationAndNotSolicitorServiceWithSolicitorReference() {
        //Given
        final CaseData caseData = CaseData.builder()
            .applicationType(SOLE_APPLICATION)
            .applicant1(getApplicant())
            .applicant2(respondentWithDigitalSolicitor())
            .divorceOrDissolution(DIVORCE)
            .dueDate(LOCAL_DATE.plusDays(7))
            .application(Application.builder().serviceMethod(COURT_SERVICE).issueDate(LOCAL_DATE).build())
            .build();

        caseData.getApplicant2().getSolicitor().setReference("TEST");

        when(commonContent.basicTemplateVars(caseData, TEST_CASE_ID)).thenReturn(commonTemplateVars());

        //When
        notification.sendToApplicant2Solicitor(caseData, TEST_CASE_ID);

        //Then
        verify(notificationService).sendEmail(
            TEST_SOLICITOR_EMAIL,
            RESPONDENT_SOLICITOR_NOTICE_OF_PROCEEDINGS,
            respondentSolicitorTemplateVarsWithSolicitorReference(),
            ENGLISH
        );

        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void shouldSendNotificationToApplicant2SolicitorIfJointApplicationAndNotSolicitorService() {
        //Given
        final CaseData caseData = CaseData.builder()
            .divorceOrDissolution(DIVORCE)
            .applicant1(applicantRepresentedBySolicitor())
            .applicant2(applicantRepresentedBySolicitor())
            .applicationType(JOINT_APPLICATION)
            .divorceOrDissolution(DIVORCE)
            .dueDate(LOCAL_DATE.plusDays(7))
            .application(Application.builder().serviceMethod(COURT_SERVICE).issueDate(LOCAL_DATE).build())
            .build();
        caseData.getApplicant1().setGender(MALE);

        when(holdingPeriodService.getDueDateFor(LOCAL_DATE)).thenReturn(caseData.getApplication().getIssueDate().plusDays(141));

        when(commonContent.basicTemplateVars(caseData, TEST_CASE_ID)).thenReturn(commonTemplateVars());

        //When
        notification.sendToApplicant2Solicitor(caseData, TEST_CASE_ID);

        //Then
        verify(notificationService).sendEmail(
            TEST_SOLICITOR_EMAIL,
            JOINT_SOLICITOR_NOTICE_OF_PROCEEDINGS,
            nopSolicitorTemplateVars(),
            ENGLISH
        );

        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void shouldNotSendNotificationToRespondentSolicitorIfSolicitorEmailIsNotSet() {
        //Given
        final Applicant applicant2 = getApplicant2(FEMALE);
        applicant2.setSolicitor(Solicitor.builder().build());
        final CaseData caseData = CaseData.builder()
            .applicationType(SOLE_APPLICATION)
            .applicant1(getApplicant())
            .applicant2(applicant2)
            .application(Application.builder()
                .serviceMethod(SOLICITOR_SERVICE)
                .build())
            .build();

        //When
        notification.sendToApplicant2Solicitor(caseData, TEST_CASE_ID);

        //Then
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotSendNotificationToRespondentSolicitorIfSolicitorService() {
        //Given
        final Applicant applicant2 = getApplicant2(FEMALE);
        applicant2.setSolicitor(Solicitor.builder().build());
        final CaseData caseData = CaseData.builder()
            .applicationType(SOLE_APPLICATION)
            .applicant1(getApplicant())
            .applicant2(respondentWithDigitalSolicitor())
            .application(Application.builder()
                .serviceMethod(SOLICITOR_SERVICE)
                .build())
            .build();

        //When
        notification.sendToApplicant2Solicitor(caseData, TEST_CASE_ID);

        //Then
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldSendPersonalServiceNotificationToApplicantSolicitorForDivorceApplication() {
        //Given
        final CaseData caseData = CaseData.builder()
            .divorceOrDissolution(DIVORCE)
            .applicant1(applicantRepresentedBySolicitor())
            .application(Application.builder()
                .serviceMethod(SOLICITOR_SERVICE)
                .build())
            .build();

        when(commonContent.basicTemplateVars(caseData, TEST_CASE_ID)).thenReturn(commonTemplateVars());
        when(commonContent.getUnionType(caseData, ENGLISH)).thenReturn("divorce");
        when(commonContent.getProfessionalUsersSignInUrl(TEST_CASE_ID))
            .thenReturn("https://manage-case.aat.platform.hmcts.net/cases/case-details/" + TEST_CASE_ID);

        //When
        notification.sendToApplicant1Solicitor(caseData, TEST_CASE_ID);

        //Then
        Map<String, String> personalServiceTemplateVars = personalServiceTemplateVars();
        personalServiceTemplateVars.put(SIGN_IN_URL, commonContent.getProfessionalUsersSignInUrl(TEST_CASE_ID));
        personalServiceTemplateVars.put(APPLICATION_REFERENCE, TEST_CASE_ID.toString());
        personalServiceTemplateVars.put("union type", "divorce");
        personalServiceTemplateVars.put("solicitor reference", "not provided");

        verify(notificationService).sendEmail(
            TEST_SOLICITOR_EMAIL,
            APPLICANT_SOLICITOR_SERVICE,
            personalServiceTemplateVars,
            ENGLISH
        );

        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void shouldSendPersonalServiceNotificationToApplicantSolicitorForDissolutionApplication() {
        //Given
        Applicant applicant1 = applicantRepresentedBySolicitor();
        applicant1.getSolicitor().setReference("someRef");

        final CaseData caseData = CaseData.builder()
            .divorceOrDissolution(DISSOLUTION)
            .applicant1(applicant1)
            .application(Application.builder()
                .serviceMethod(SOLICITOR_SERVICE)
                .build())
            .build();

        when(commonContent.basicTemplateVars(caseData, TEST_CASE_ID)).thenReturn(commonTemplateVars());
        when(commonContent.getUnionType(caseData, ENGLISH)).thenReturn("dissolution");
        when(commonContent.getProfessionalUsersSignInUrl(TEST_CASE_ID))
            .thenReturn("https://manage-case.aat.platform.hmcts.net/cases/case-details/" + TEST_CASE_ID);

        //When
        notification.sendToApplicant1Solicitor(caseData, TEST_CASE_ID);

        //Then
        Map<String, String> personalServiceTemplateVars = personalServiceTemplateVars();
        personalServiceTemplateVars.put(SIGN_IN_URL, commonContent.getProfessionalUsersSignInUrl(TEST_CASE_ID));
        personalServiceTemplateVars.put(APPLICATION_REFERENCE, TEST_CASE_ID.toString());
        personalServiceTemplateVars.put("union type", "dissolution");
        personalServiceTemplateVars.put("solicitor reference", "someRef");

        verify(notificationService).sendEmail(
            TEST_SOLICITOR_EMAIL,
            APPLICANT_SOLICITOR_SERVICE,
            personalServiceTemplateVars,
            ENGLISH
        );

        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void shouldNotSendEmailToSoleRespondentWhenBasedOverseas() {
        //Given
        CaseData data = validCaseDataForIssueApplication();
        data.setDivorceOrDissolution(DISSOLUTION);
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        data.getApplication().setIssueDate(LocalDate.now());
        data.getApplicant2().setEmail(TEST_USER_EMAIL);
        data.getApplicant2().setAddress(AddressGlobalUK.builder()
            .addressLine1("223b")
            .addressLine2("Baker Street")
            .postTown("Tampa")
            .county("Florida")
            .country("United States")
            .build());

        //When
        notification.sendToApplicant2(data, 1234567890123456L);

        //Then
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldSendEmailInWelshToSoleRespondentWithDivorceContent() {
        //Given
        CaseData data = validCaseDataForIssueApplication();
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        data.getApplicant2().setEmail(null);
        data.getApplicant2().setLanguagePreferenceWelsh(YesOrNo.YES);

        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1()))
            .thenReturn(getMainTemplateVars());
        when(emailTemplatesConfig.getTemplateVars()).thenReturn(getConfigTemplateVars());
        when(holdingPeriodService.getRespondByDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(16));
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant2(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_APPLICANT_2_USER_EMAIL),
            eq(SOLE_RESPONDENT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(IS_DIVORCE, YES),
                hasEntry(IS_DISSOLUTION, NO)
            )),
            eq(WELSH)
        );

        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1());
    }

    @Test
    void shouldSendEmailInWelshToApplicant2() {
        //Given
        CaseData data = validJointApplicant1CaseData();
        data.setDivorceOrDissolution(DISSOLUTION);
        data.setDueDate(LocalDate.now().plusDays(141));
        data.getApplication().setIssueDate(LocalDate.now());
        data.getApplicant2().setLanguagePreferenceWelsh(YesOrNo.YES);
        Map<String, String> dissolutionTemplateVars = new HashMap<>();
        dissolutionTemplateVars.putAll(getMainTemplateVars());
        dissolutionTemplateVars.putAll(Map.of(IS_DIVORCE, NO, IS_DISSOLUTION, YES));
        when(commonContent.mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1()))
            .thenReturn(dissolutionTemplateVars);
        when(holdingPeriodService.getDueDateFor(data.getApplication().getIssueDate()))
            .thenReturn(data.getApplication().getIssueDate().plusDays(141));

        //When
        notification.sendToApplicant2(data, 1234567890123456L);

        //Then
        verify(notificationService).sendEmail(
            eq(TEST_APPLICANT_2_USER_EMAIL),
            eq(JOINT_APPLICATION_ACCEPTED),
            argThat(allOf(
                hasEntry(APPLICATION_REFERENCE, formatId(1234567890123456L)),
                hasEntry(SUBMISSION_RESPONSE_DATE, data.getApplication().getIssueDate().plusDays(141).format(DATE_TIME_FORMATTER)),
                hasEntry(IS_DIVORCE, NO),
                hasEntry(IS_DISSOLUTION, YES)
            )),
            eq(WELSH)
        );

        verify(commonContent).mainTemplateVars(data, 1234567890123456L, data.getApplicant2(), data.getApplicant1());
    }

    private Map<String, String> respondentSolicitorTemplateVars() {
        final Map<String, String> templateVars = solicitorTemplateVars();

        templateVars.put(SOLICITOR_REFERENCE, NOT_PROVIDED);
        templateVars.put(DUE_DATE, LOCAL_DATE.plusDays(7).format(DATE_TIME_FORMATTER));
        templateVars.put(ISSUE_DATE, LOCAL_DATE.format(DATE_TIME_FORMATTER));
        templateVars.put(SIGN_IN_URL, null);
        templateVars.put(IS_DISSOLUTION, NO);
        templateVars.put(IS_DIVORCE, YES);
        templateVars.put(SUBMISSION_RESPONSE_DATE, LOCAL_DATE.plusDays(16).format(DATE_TIME_FORMATTER));
        return templateVars;
    }

    private Map<String, String> respondentSolicitorTemplateVarsWithSolicitorReference() {
        final Map<String, String> templateVars = solicitorTemplateVars();

        templateVars.put(SOLICITOR_REFERENCE, "TEST");
        templateVars.put(DUE_DATE, LOCAL_DATE.plusDays(7).format(DATE_TIME_FORMATTER));
        templateVars.put(ISSUE_DATE, LOCAL_DATE.format(DATE_TIME_FORMATTER));
        templateVars.put(SIGN_IN_URL, null);
        templateVars.put(IS_DISSOLUTION, NO);
        templateVars.put(IS_DIVORCE, YES);
        templateVars.put(SUBMISSION_RESPONSE_DATE, LOCAL_DATE.plusDays(16).format(DATE_TIME_FORMATTER));
        return templateVars;
    }

    private Map<String, String> nopSolicitorTemplateVars() {
        final Map<String, String> templateVars = solicitorTemplateVars();

        templateVars.put(SOLICITOR_REFERENCE, NOT_PROVIDED);
        templateVars.put(DUE_DATE, LOCAL_DATE.plusDays(7).format(DATE_TIME_FORMATTER));
        templateVars.put(ISSUE_DATE, LOCAL_DATE.format(DATE_TIME_FORMATTER));
        templateVars.put(SIGN_IN_URL, null);
        templateVars.put(IS_DISSOLUTION, NO);
        templateVars.put(IS_DIVORCE, YES);
        templateVars.put(SUBMISSION_RESPONSE_DATE, LOCAL_DATE.plusDays(141).format(DATE_TIME_FORMATTER));
        return templateVars;
    }

    private Map<String, String> solicitorTemplateVars() {

        final Map<String, String> templateVars = commonTemplateVars();

        templateVars.put(SOLICITOR_NAME, TEST_SOLICITOR_NAME);
        templateVars.put(CASE_ID, TEST_CASE_ID.toString());

        return templateVars;
    }

    private Map<String, String> personalServiceTemplateVars() {
        final Map<String, String> templateVars = commonTemplateVars();
        templateVars.put(SOLICITOR_NAME, TEST_SOLICITOR_NAME);
        return templateVars;
    }

    private Map<String, String> commonTemplateVars() {
        final Map<String, String> templateVars = new HashMap<>();
        templateVars.put(APPLICANT_NAME, join(" ", TEST_FIRST_NAME, TEST_LAST_NAME));
        templateVars.put(RESPONDENT_NAME, join(" ", APPLICANT_2_FIRST_NAME, TEST_LAST_NAME));

        return templateVars;
    }
}
