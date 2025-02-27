package uk.gov.hmcts.sptribs.document.content;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.sptribs.ciccase.model.Applicant;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;

import java.time.Clock;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static uk.gov.hmcts.sptribs.ciccase.model.ApplicationType.SOLE_APPLICATION;
import static uk.gov.hmcts.sptribs.ciccase.model.DivorceOrDissolution.DIVORCE;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_1_FIRST_NAME;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_1_FULL_NAME;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_1_LAST_NAME;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_2_FULL_NAME;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.CCD_CASE_REFERENCE;
import static uk.gov.hmcts.sptribs.testutil.ClockTestUtil.getTemplateFormatDate;
import static uk.gov.hmcts.sptribs.testutil.ClockTestUtil.setMockClock;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.APPLICANT_2_FIRST_NAME;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.APPLICANT_2_LAST_NAME;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.FORMATTED_TEST_CASE_ID;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_CASE_ID;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_FIRST_NAME;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_LAST_NAME;

@ExtendWith(MockitoExtension.class)
class ConditionalOrderAnswersTemplateContentTest {

    @Mock
    private Clock clock;

    @InjectMocks
    private ConditionalOrderAnswersTemplateContent conditionalOrderAnswersTemplateContent;

    @Test
    void shouldApplyContentFromCaseDataForConditionalOrderAnswers() {
        //Given
        setMockClock(clock);

        final Applicant applicant1 = Applicant.builder()
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .build();
        final Applicant applicant2 = Applicant.builder()
            .firstName(APPLICANT_2_FIRST_NAME)
            .lastName(APPLICANT_2_LAST_NAME)
            .build();

        final CaseData caseData = CaseData.builder()
            .applicationType(SOLE_APPLICATION)
            .divorceOrDissolution(DIVORCE)
            .applicant1(applicant1)
            .applicant2(applicant2)
            .build();

        //When
        final Map<String, Object> result = conditionalOrderAnswersTemplateContent.apply(caseData, TEST_CASE_ID);

        //Then
        assertThat(result).contains(
            entry("isSole", true),
            entry("isDivorce", true),
            entry(CCD_CASE_REFERENCE, FORMATTED_TEST_CASE_ID),
            entry("documentDate", getTemplateFormatDate()),
            entry(APPLICANT_1_FIRST_NAME, TEST_FIRST_NAME),
            entry(APPLICANT_1_LAST_NAME, TEST_LAST_NAME),
            entry(APPLICANT_1_FULL_NAME, TEST_FIRST_NAME + " " + TEST_LAST_NAME),
            entry(DocmosisTemplateConstants.APPLICANT_2_FIRST_NAME, APPLICANT_2_FIRST_NAME),
            entry(DocmosisTemplateConstants.APPLICANT_2_LAST_NAME, APPLICANT_2_LAST_NAME),
            entry(APPLICANT_2_FULL_NAME, APPLICANT_2_FIRST_NAME + " " + APPLICANT_2_LAST_NAME)
        );
    }
}
