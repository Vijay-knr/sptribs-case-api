package uk.gov.hmcts.sptribs.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.sptribs.ciccase.model.Applicant;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.notification.ApplicantNotification;
import uk.gov.hmcts.sptribs.notification.CommonContent;
import uk.gov.hmcts.sptribs.notification.NotificationService;

import java.util.Map;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.sptribs.notification.CommonContent.IS_REMINDER;
import static uk.gov.hmcts.sptribs.notification.CommonContent.SOLICITOR_NAME;
import static uk.gov.hmcts.sptribs.notification.CommonContent.YES;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.CITIZEN_APPLY_FOR_CONDITIONAL_ORDER;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.SOLICITOR_AWAITING_CONDITIONAL_ORDER;

@Component
@Slf4j
public class AwaitingConditionalOrderReminderNotification implements ApplicantNotification {

    @Autowired
    private CommonContent commonContent;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void sendToApplicant1(final CaseData caseData, final Long id) {
        log.info("Sending reminder to applicant 1 that they can apply for a conditional order: {}", id);

        final Applicant applicant1 = caseData.getApplicant1();

        final Map<String, String> templateVars = commonContent
            .conditionalOrderTemplateVars(caseData, id, applicant1, caseData.getApplicant2());
        templateVars.put(IS_REMINDER, YES);

        notificationService.sendEmail(
            applicant1.getEmail(),
            CITIZEN_APPLY_FOR_CONDITIONAL_ORDER,
            templateVars,
            applicant1.getLanguagePreference()
        );
    }

    @Override
    public void sendToApplicant1Solicitor(final CaseData caseData, final Long id) {
        log.info("Sending reminder to applicant 1 solicitor that they can apply for a conditional order: {}", id);
        final Applicant applicant1 = caseData.getApplicant1();

        final Map<String, String> templateVars = commonContent.basicTemplateVars(caseData, id);
        templateVars.put(SOLICITOR_NAME, applicant1.getSolicitor().getName());

        notificationService.sendEmail(
            applicant1.getSolicitor().getEmail(),
            SOLICITOR_AWAITING_CONDITIONAL_ORDER,
            templateVars,
            applicant1.getLanguagePreference()
        );
    }

    @Override
    public void sendToApplicant2(final CaseData caseData, final Long id) {
        if (!caseData.getApplicationType().isSole() && nonNull(caseData.getApplicant2().getEmail())) {
            log.info("Sending reminder applicant 2 that they can apply for a conditional order: {}", id);

            final Applicant applicant2 = caseData.getApplicant2();

            final Map<String, String> templateVars = commonContent
                .conditionalOrderTemplateVars(caseData, id, applicant2, caseData.getApplicant1());
            templateVars.put(IS_REMINDER, YES);

            notificationService.sendEmail(
                applicant2.getEmail(),
                CITIZEN_APPLY_FOR_CONDITIONAL_ORDER,
                templateVars,
                applicant2.getLanguagePreference()
            );
        }
    }

    @Override
    public void sendToApplicant2Solicitor(final CaseData caseData, final Long id) {
        if (!caseData.getApplicationType().isSole()) {
            log.info("Sending reminder applicant 2 solicitor that they can apply for a conditional order: {}", id);
            final Applicant applicant2 = caseData.getApplicant2();

            final Map<String, String> templateVars = commonContent.basicTemplateVars(caseData, id);
            templateVars.put(SOLICITOR_NAME, applicant2.getSolicitor().getName());

            notificationService.sendEmail(
                applicant2.getSolicitor().getEmail(),
                SOLICITOR_AWAITING_CONDITIONAL_ORDER,
                templateVars,
                applicant2.getLanguagePreference()
            );
        }
    }
}
