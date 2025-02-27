package uk.gov.hmcts.sptribs.common.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.State;
import uk.gov.hmcts.sptribs.ciccase.task.CaseTask;
import uk.gov.hmcts.sptribs.document.CaseDataDocumentService;
import uk.gov.hmcts.sptribs.document.content.AosOverdueLetterTemplateContent;

import static uk.gov.hmcts.sptribs.document.DocumentConstants.AOS_OVERDUE_LETTER_DOCUMENT_NAME;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.AOS_OVERDUE_TEMPLATE_ID;
import static uk.gov.hmcts.sptribs.document.model.DocumentType.AOS_OVERDUE_LETTER;

@Component
@Slf4j
public class GenerateAosOverdueLetterDocument implements CaseTask {

    @Autowired
    private CaseDataDocumentService caseDataDocumentService;

    @Autowired
    private AosOverdueLetterTemplateContent templateContent;

    @Override
    public CaseDetails<CaseData, State> apply(CaseDetails<CaseData, State> caseDetails) {

        final Long caseId = caseDetails.getId();
        final CaseData caseData = caseDetails.getData();

        log.info("Generating aos overdue letter pdf for case id: {}", caseDetails.getId());
        caseDataDocumentService.renderDocumentAndUpdateCaseData(
            caseData,
            AOS_OVERDUE_LETTER,
            templateContent.apply(caseData, caseId),
            caseId,
            AOS_OVERDUE_TEMPLATE_ID,
            caseData.getApplicant1().getLanguagePreference(),
            AOS_OVERDUE_LETTER_DOCUMENT_NAME
        );

        return caseDetails;
    }
}
