package uk.gov.hmcts.sptribs.common.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.State;
import uk.gov.hmcts.sptribs.ciccase.task.CaseTask;
import uk.gov.hmcts.sptribs.document.CaseDataDocumentService;
import uk.gov.hmcts.sptribs.document.content.RespondentAnswersTemplateContent;

import static uk.gov.hmcts.sptribs.document.DocumentConstants.RESPONDENT_ANSWERS_DOCUMENT_NAME;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.RESPONDENT_ANSWERS_TEMPLATE_ID;
import static uk.gov.hmcts.sptribs.document.model.DocumentType.RESPONDENT_ANSWERS;

@Component
@Slf4j
public class GenerateRespondentAnswersDoc implements CaseTask {

    @Autowired
    private CaseDataDocumentService caseDataDocumentService;

    @Autowired
    private RespondentAnswersTemplateContent templateContent;

    @Override
    public CaseDetails<CaseData, State> apply(CaseDetails<CaseData, State> caseDetails) {

        final Long caseId = caseDetails.getId();
        final CaseData caseData = caseDetails.getData();

        log.info("Generating respondent answers pdf for CaseID: {}", caseDetails.getId());

        caseDataDocumentService.renderDocumentAndUpdateCaseData(
            caseData,
            RESPONDENT_ANSWERS,
            templateContent.apply(caseData, caseId),
            caseId,
            RESPONDENT_ANSWERS_TEMPLATE_ID,
            caseData.getApplicant1().getLanguagePreference(),
            RESPONDENT_ANSWERS_DOCUMENT_NAME
        );

        return caseDetails;
    }
}
