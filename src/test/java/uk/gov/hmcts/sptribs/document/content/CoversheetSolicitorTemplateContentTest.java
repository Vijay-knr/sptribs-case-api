package uk.gov.hmcts.sptribs.document.content;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.Solicitor;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_2_SOLICITOR_ADDRESS;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_2_SOLICITOR_NAME;
import static uk.gov.hmcts.sptribs.notification.FormatUtil.formatId;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_CASE_ID;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.caseData;

@ExtendWith(MockitoExtension.class)
class CoversheetSolicitorTemplateContentTest {

    @InjectMocks
    private CoversheetSolicitorTemplateContent coversheetSolicitorTemplateContent;

    @Test
    void shouldReturnCoversheetTemplateContent() {
        //Given
        final CaseData caseData = caseData();
        caseData.getApplicant2().setSolicitor(Solicitor.builder()
            .name(APPLICANT_2_SOLICITOR_NAME)
            .address(APPLICANT_2_SOLICITOR_ADDRESS)
            .build());

        //When
        final Map<String, Object> result = coversheetSolicitorTemplateContent.apply(caseData, TEST_CASE_ID);

        //Then
        Map<String, Object> expectedEntries = new LinkedHashMap<>();
        expectedEntries.put("caseReference", formatId(1616591401473378L));
        expectedEntries.put("solicitorName", APPLICANT_2_SOLICITOR_NAME);
        expectedEntries.put("solicitorAddress", APPLICANT_2_SOLICITOR_ADDRESS);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(expectedEntries);
    }
}
