package uk.gov.hmcts.sptribs.common.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.State;
import uk.gov.hmcts.sptribs.ciccase.task.CaseTask;
import uk.gov.hmcts.sptribs.document.CaseDataDocumentService;
import uk.gov.hmcts.sptribs.document.content.ConditionalOrderAnswersTemplateContent;

import static uk.gov.hmcts.sptribs.document.DocumentConstants.CONDITIONAL_ORDER_ANSWERS_DOCUMENT_NAME;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.CONDITIONAL_ORDER_ANSWERS_TEMPLATE_ID;
import static uk.gov.hmcts.sptribs.document.model.DocumentType.CONDITIONAL_ORDER_ANSWERS;

@Component
@Slf4j
public class GenerateConditionalOrderAnswersDocument implements CaseTask {

    @Autowired
    private CaseDataDocumentService caseDataDocumentService;

    @Autowired
    private ConditionalOrderAnswersTemplateContent conditionalOrderAnswersTemplateContent;

    @Override
    public CaseDetails<CaseData, State> apply(CaseDetails<CaseData, State> caseDetails) {

        final Long caseId = caseDetails.getId();
        final CaseData caseData = caseDetails.getData();

        log.info("Generating Conditional Order answers pdf for CaseID: {}", caseDetails.getId());

        caseDataDocumentService.renderDocumentAndUpdateCaseData(
            caseData,
            CONDITIONAL_ORDER_ANSWERS,
            conditionalOrderAnswersTemplateContent.apply(caseData, caseId),
            caseId,
            CONDITIONAL_ORDER_ANSWERS_TEMPLATE_ID,
            caseData.getApplicant1().getLanguagePreference(),
            CONDITIONAL_ORDER_ANSWERS_DOCUMENT_NAME
        );

        return caseDetails;
    }
}
