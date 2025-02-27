package uk.gov.hmcts.sptribs.common.event.page;

import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.CicCase;
import uk.gov.hmcts.sptribs.common.ccd.CcdPageConfiguration;
import uk.gov.hmcts.sptribs.common.ccd.PageBuilder;

public class SubjectDetails implements CcdPageConfiguration {

    @Override
    public void addTo(PageBuilder pageBuilder) {
        pageBuilder.page("subjectDetails")
            .pageLabel("Who is the subject of this case?")
            .label("subjectDetailsObject", "<h3>Who is the subject of this case?</h3>")
            .complex(CaseData::getCicCase)
            .mandatory(CicCase::getFullName)
            .optional(CicCase::getPhoneNumber)
            .mandatoryWithLabel(CicCase::getDateOfBirth, "")
            .mandatoryWithLabel(CicCase::getContactPreferenceType, "")
            .mandatory(CicCase::getAddress,"cicCaseContactPreferenceType = \"Post\"")
            .mandatory(CicCase::getEmail, "cicCaseContactPreferenceType = \"Email\"")
            .done();
    }
}
