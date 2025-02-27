package uk.gov.hmcts.sptribs.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.notification.ApplicantNotification;
import uk.gov.hmcts.sptribs.notification.CommonContent;
import uk.gov.hmcts.sptribs.notification.NotificationService;

import java.util.Map;

import static uk.gov.hmcts.sptribs.notification.CommonContent.REVIEW_DEADLINE_DATE;
import static uk.gov.hmcts.sptribs.notification.CommonContent.SUBMISSION_RESPONSE_DATE;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.OVERSEAS_RESPONDENT_HAS_EMAIL_APPLICATION_ISSUED;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.OVERSEAS_RESPONDENT_NO_EMAIL_APPLICATION_ISSUED;
import static uk.gov.hmcts.sptribs.notification.FormatUtil.DATE_TIME_FORMATTER;

@Component
@Slf4j
public class ApplicationIssuedOverseasNotification implements ApplicantNotification {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommonContent commonContent;

    @Override
    public void sendToApplicant1(final CaseData caseData, final Long id) {

        log.info("Notifying sole applicant of application issue (case {}) to overseas respondent", id);

        final boolean hasEmail = caseData.getApplicant2EmailAddress() != null
            && !caseData.getApplicant2EmailAddress().isEmpty();
        notificationService.sendEmail(
            caseData.getApplicant1().getEmail(),
            hasEmail ? OVERSEAS_RESPONDENT_HAS_EMAIL_APPLICATION_ISSUED : OVERSEAS_RESPONDENT_NO_EMAIL_APPLICATION_ISSUED,
            overseasRespondentTemplateVars(caseData, id),
            caseData.getApplicant1().getLanguagePreference()
        );
    }

    private Map<String, String> overseasRespondentTemplateVars(final CaseData caseData, Long id) {
        final Map<String, String> templateVars = commonContent.mainTemplateVars(
            caseData,
            id,
            caseData.getApplicant1(),
            caseData.getApplicant2());
        templateVars.put(SUBMISSION_RESPONSE_DATE, caseData.getDueDate().format(DATE_TIME_FORMATTER));
        templateVars.put(REVIEW_DEADLINE_DATE, caseData.getApplication().getIssueDate().plusDays(28).format(DATE_TIME_FORMATTER));
        return templateVars;
    }
}
