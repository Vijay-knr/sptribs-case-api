package uk.gov.hmcts.sptribs.caseworker.service.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.OrganisationPolicy;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.Solicitor;
import uk.gov.hmcts.sptribs.ciccase.model.UserRole;
import uk.gov.hmcts.sptribs.ciccase.util.AccessCodeGenerator;
import uk.gov.hmcts.sptribs.document.CaseDataDocumentService;
import uk.gov.hmcts.sptribs.document.content.CoversheetApplicant2TemplateContent;
import uk.gov.hmcts.sptribs.document.content.CoversheetSolicitorTemplateContent;
import uk.gov.hmcts.sptribs.document.content.NoticeOfProceedingContent;
import uk.gov.hmcts.sptribs.document.content.NoticeOfProceedingJointContent;
import uk.gov.hmcts.sptribs.document.content.NoticeOfProceedingSolicitorContent;
import uk.gov.hmcts.sptribs.document.content.NoticeOfProceedingsWithAddressContent;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.NO;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.YES;
import static uk.gov.hmcts.sptribs.caseworker.service.task.GenerateApplicant1NoticeOfProceedingTest.caseData;
import static uk.gov.hmcts.sptribs.caseworker.service.task.GenerateApplicant1NoticeOfProceedingTest.caseDetails;
import static uk.gov.hmcts.sptribs.caseworker.service.task.util.FileNameUtil.formatDocumentName;
import static uk.gov.hmcts.sptribs.ciccase.model.ApplicationType.JOINT_APPLICATION;
import static uk.gov.hmcts.sptribs.ciccase.model.ApplicationType.SOLE_APPLICATION;
import static uk.gov.hmcts.sptribs.ciccase.model.ServiceMethod.COURT_SERVICE;
import static uk.gov.hmcts.sptribs.ciccase.model.ServiceMethod.SOLICITOR_SERVICE;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.COVERSHEET_APPLICANT;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.COVERSHEET_APPLICANT2_SOLICITOR;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.COVERSHEET_DOCUMENT_NAME;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.NFD_NOP_AS1_SOLEJOINT_APP1APP2_SOL_CS;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.NFD_NOP_JA1_JOINT_APP1APP2_CIT;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.NFD_NOP_R1_SOLE_APP2_CIT_ONLINE;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.NFD_NOP_R2_SOLE_APP2_CIT_OFFLINE;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.NFD_NOP_RS1_SOLE_APP2_SOL_ONLINE;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.NFD_NOP_RS2_SOLE_APP2_SOL_OFFLINE;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.NOTICE_OF_PROCEEDINGS_APP_2_DOCUMENT_NAME;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.DISPLAY_HEADER_ADDRESS;
import static uk.gov.hmcts.sptribs.document.model.DocumentType.COVERSHEET;
import static uk.gov.hmcts.sptribs.document.model.DocumentType.NOTICE_OF_PROCEEDINGS_APP_2;
import static uk.gov.hmcts.sptribs.testutil.ClockTestUtil.setMockClock;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.ACCESS_CODE;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_CASE_ID;

@ExtendWith(MockitoExtension.class)
public class GenerateApplicant2NoticeOfProceedingsTest {

    @Mock
    private CaseDataDocumentService caseDataDocumentService;

    @Mock
    private CoversheetApplicant2TemplateContent coversheetApplicant2TemplateContent;

    @Mock
    private NoticeOfProceedingContent noticeOfProceedingContent;

    @Mock
    private NoticeOfProceedingsWithAddressContent noticeOfProceedingsWithAddressContent;

    @Mock
    private NoticeOfProceedingJointContent noticeOfProceedingJointContent;

    @Mock
    private NoticeOfProceedingSolicitorContent noticeOfProceedingSolicitorContent;

    @Mock
    private CoversheetSolicitorTemplateContent coversheetSolicitorTemplateContent;

    @Mock
    private Clock clock;

    @InjectMocks
    private GenerateApplicant2NoticeOfProceedings generateApplicant2NoticeOfProceedings;

    @Test
    void shouldGenerateRS2AndCoversheetWhenSoleWithAppRepresentedAndOffline() {
        setMockClock(clock);
        MockedStatic<AccessCodeGenerator> classMock = mockStatic(AccessCodeGenerator.class);
        classMock.when(AccessCodeGenerator::generateAccessCode).thenReturn(ACCESS_CODE);

        final CaseData caseData = caseData(SOLE_APPLICATION, NO, YES);
        caseData.getApplication().setServiceMethod(COURT_SERVICE);

        final Map<String, Object> templateContent = new HashMap<>();

        when(noticeOfProceedingContent.apply(caseData, TEST_CASE_ID, caseData.getApplicant1())).thenReturn(templateContent);
        when(coversheetSolicitorTemplateContent.apply(caseData, TEST_CASE_ID)).thenReturn(templateContent);

        final var result = generateApplicant2NoticeOfProceedings.apply(caseDetails(caseData));

        verifyInteractions(caseData, templateContent, NFD_NOP_RS2_SOLE_APP2_SOL_OFFLINE);
        verify(caseDataDocumentService)
            .renderDocumentAndUpdateCaseData(
                caseData,
                COVERSHEET,
                templateContent,
                TEST_CASE_ID,
                COVERSHEET_APPLICANT2_SOLICITOR,
                caseData.getApplicant2().getLanguagePreference(),
                formatDocumentName(TEST_CASE_ID, COVERSHEET_DOCUMENT_NAME, now(clock)));

        assertThat(result.getData()).isEqualTo(caseData);
        classMock.close();
    }

    @Test
    void shouldGenerateRS1WhenSoleWithAppRepresentedAndOnline() {
        setMockClock(clock);
        MockedStatic<AccessCodeGenerator> classMock = mockStatic(AccessCodeGenerator.class);
        classMock.when(AccessCodeGenerator::generateAccessCode).thenReturn(ACCESS_CODE);

        final CaseData caseData = caseData(SOLE_APPLICATION, NO, YES);
        caseData.getApplication().setServiceMethod(COURT_SERVICE);
        caseData.getApplicant2().setSolicitor(
            Solicitor.builder()
                .organisationPolicy(OrganisationPolicy.<UserRole>builder().build())
                .build()
        );

        final Map<String, Object> templateContent = new HashMap<>();

        when(noticeOfProceedingsWithAddressContent.apply(caseData, TEST_CASE_ID, caseData.getApplicant1())).thenReturn(templateContent);

        final var result = generateApplicant2NoticeOfProceedings.apply(caseDetails(caseData));

        verifyInteractions(caseData, templateContent, NFD_NOP_RS1_SOLE_APP2_SOL_ONLINE);

        assertThat(result.getData()).isEqualTo(caseData);
        classMock.close();
    }

    @Test
    void shouldGenerateCoverletterWhenSoleWithAppRepresentedAndOnlineAndSolicitorService() {
        setMockClock(clock);
        MockedStatic<AccessCodeGenerator> classMock = mockStatic(AccessCodeGenerator.class);
        classMock.when(AccessCodeGenerator::generateAccessCode).thenReturn(ACCESS_CODE);

        final CaseData caseData = caseData(SOLE_APPLICATION, NO, YES);
        caseData.getApplication().setServiceMethod(SOLICITOR_SERVICE);
        caseData.getApplicant2().setSolicitor(
            Solicitor.builder()
                .organisationPolicy(OrganisationPolicy.<UserRole>builder().build())
                .build()
        );

        final Map<String, Object> templateContent = new HashMap<>();
        final Map<String, Object> expectedTemplateContent = new HashMap<>();
        expectedTemplateContent.put(DISPLAY_HEADER_ADDRESS, false);

        when(noticeOfProceedingContent.apply(caseData, TEST_CASE_ID, caseData.getApplicant1())).thenReturn(templateContent);

        final var result = generateApplicant2NoticeOfProceedings.apply(caseDetails(caseData));

        verifyInteractions(caseData, templateContent, NFD_NOP_RS1_SOLE_APP2_SOL_ONLINE);
        verify(caseDataDocumentService)
            .renderDocumentAndUpdateCaseData(
                caseData,
                COVERSHEET,
                templateContent,
                TEST_CASE_ID,
                COVERSHEET_APPLICANT2_SOLICITOR,
                caseData.getApplicant2().getLanguagePreference(),
                formatDocumentName(TEST_CASE_ID, COVERSHEET_DOCUMENT_NAME, now(clock)));
        assertThat(result.getData()).isEqualTo(caseData);
        classMock.close();
    }

    @Test
    void shouldGenerateR2WhenSoleWithAppNotRepresentedAndOffline() {
        setMockClock(clock);
        MockedStatic<AccessCodeGenerator> classMock = mockStatic(AccessCodeGenerator.class);
        classMock.when(AccessCodeGenerator::generateAccessCode).thenReturn(ACCESS_CODE);

        final CaseData caseData = caseData(SOLE_APPLICATION, NO, NO);
        caseData.getApplication().setServiceMethod(COURT_SERVICE);
        caseData.getApplicant2().setEmail(null);

        final Map<String, Object> templateContent = new HashMap<>();

        when(noticeOfProceedingContent.apply(caseData, TEST_CASE_ID, caseData.getApplicant1())).thenReturn(templateContent);

        final var result = generateApplicant2NoticeOfProceedings.apply(caseDetails(caseData));

        verifyInteractions(caseData, templateContent, NFD_NOP_R2_SOLE_APP2_CIT_OFFLINE);
        verify(caseDataDocumentService, times(1))
            .renderDocumentAndUpdateCaseData(
                caseData,
                COVERSHEET,
                templateContent,
                TEST_CASE_ID,
                COVERSHEET_APPLICANT,
                caseData.getApplicant2().getLanguagePreference(),
                formatDocumentName(TEST_CASE_ID, COVERSHEET_DOCUMENT_NAME, now(clock))
            );
        assertThat(result.getData()).isEqualTo(caseData);
        classMock.close();
    }

    @Test
    void shouldGenerateR1WhenSoleWithAppNotRepresentedAndOnline() {
        setMockClock(clock);
        MockedStatic<AccessCodeGenerator> classMock = mockStatic(AccessCodeGenerator.class);
        classMock.when(AccessCodeGenerator::generateAccessCode).thenReturn(ACCESS_CODE);

        final CaseData caseData = caseData(SOLE_APPLICATION, NO, NO);
        caseData.getApplication().setServiceMethod(COURT_SERVICE);
        caseData.getApplicant2().setEmail("notnull@something.com");

        final Map<String, Object> templateContent = new HashMap<>();

        when(noticeOfProceedingContent.apply(caseData, TEST_CASE_ID, caseData.getApplicant1())).thenReturn(templateContent);

        final var result = generateApplicant2NoticeOfProceedings.apply(caseDetails(caseData));

        verifyInteractions(caseData, templateContent, NFD_NOP_R1_SOLE_APP2_CIT_ONLINE);

        assertThat(result.getData()).isEqualTo(caseData);
        classMock.close();
    }

    @Test
    void shouldGenerateAS1WhenJointWithAppRepresented() {
        setMockClock(clock);
        MockedStatic<AccessCodeGenerator> classMock = mockStatic(AccessCodeGenerator.class);
        classMock.when(AccessCodeGenerator::generateAccessCode).thenReturn(ACCESS_CODE);

        final CaseData caseData = caseData(JOINT_APPLICATION, NO, YES);
        caseData.getApplication().setServiceMethod(COURT_SERVICE);
        caseData.getApplicant2().setEmail("notnull@something.com");

        final Map<String, Object> templateContent = new HashMap<>();

        when(noticeOfProceedingSolicitorContent.apply(caseData, TEST_CASE_ID, false)).thenReturn(templateContent);

        final var result = generateApplicant2NoticeOfProceedings.apply(caseDetails(caseData));

        verifyInteractions(caseData, templateContent, NFD_NOP_AS1_SOLEJOINT_APP1APP2_SOL_CS);

        assertThat(result.getData()).isEqualTo(caseData);
        classMock.close();
    }

    @Test
    void shouldGenerateJA1WhenJointWithAppNotRepresented() {
        setMockClock(clock);
        MockedStatic<AccessCodeGenerator> classMock = mockStatic(AccessCodeGenerator.class);
        classMock.when(AccessCodeGenerator::generateAccessCode).thenReturn(ACCESS_CODE);

        final CaseData caseData = caseData(JOINT_APPLICATION, NO, NO);
        caseData.getApplication().setServiceMethod(COURT_SERVICE);
        caseData.getApplicant2().setEmail("notnull@something.com");

        final Map<String, Object> templateContent = new HashMap<>();

        when(noticeOfProceedingJointContent.apply(caseData, TEST_CASE_ID, caseData.getApplicant2(), caseData.getApplicant1()))
            .thenReturn(templateContent);

        final var result = generateApplicant2NoticeOfProceedings.apply(caseDetails(caseData));

        verifyInteractions(caseData, templateContent, NFD_NOP_JA1_JOINT_APP1APP2_CIT);

        assertThat(result.getData()).isEqualTo(caseData);
        classMock.close();
    }

    private void verifyInteractions(CaseData caseData, Map<String, Object> templateContent,
                                    String templateId) {
        verify(caseDataDocumentService, times(1))
            .renderDocumentAndUpdateCaseData(
                caseData,
                NOTICE_OF_PROCEEDINGS_APP_2,
                templateContent,
                TEST_CASE_ID,
                templateId,
                caseData.getApplicant2().getLanguagePreference(),
                formatDocumentName(TEST_CASE_ID, NOTICE_OF_PROCEEDINGS_APP_2_DOCUMENT_NAME, now(clock))
            );
    }
}
