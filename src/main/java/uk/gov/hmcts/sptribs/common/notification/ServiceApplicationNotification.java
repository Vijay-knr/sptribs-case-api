package uk.gov.hmcts.sptribs.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.sptribs.ciccase.model.AlternativeService;
import uk.gov.hmcts.sptribs.ciccase.model.AlternativeServiceType;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.notification.ApplicantNotification;
import uk.gov.hmcts.sptribs.notification.CommonContent;
import uk.gov.hmcts.sptribs.notification.EmailTemplateName;
import uk.gov.hmcts.sptribs.notification.NotificationService;
import uk.gov.hmcts.sptribs.notification.exception.NotificationTemplateException;

import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static uk.gov.hmcts.sptribs.ciccase.model.AlternativeServiceType.BAILIFF;
import static uk.gov.hmcts.sptribs.ciccase.model.AlternativeServiceType.DEEMED;
import static uk.gov.hmcts.sptribs.ciccase.model.AlternativeServiceType.DISPENSED;
import static uk.gov.hmcts.sptribs.notification.CommonContent.NO;
import static uk.gov.hmcts.sptribs.notification.CommonContent.YES;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.SERVICE_APPLICATION_GRANTED;
import static uk.gov.hmcts.sptribs.notification.EmailTemplateName.SERVICE_APPLICATION_REJECTED;

@Component
@Slf4j
public class ServiceApplicationNotification implements ApplicantNotification {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommonContent commonContent;

    private static final String LOGGER_MESSAGE = "Notifying applicant that service application for {} was {}";

    @Override
    public void sendToApplicant1(final CaseData caseData, final Long id) {
        notificationService.sendEmail(
            caseData.getApplicant1().getEmail(),
            getEmailTemplate(caseData.getAlternativeService(), id),
            getServiceApplicationVars(caseData, id),
            caseData.getApplicant1().getLanguagePreference());
    }

    private Map<String, String> getServiceApplicationVars(final CaseData caseData, final Long id) {

        Map<String, String> templateVars =
            commonContent.mainTemplateVars(caseData, id, caseData.getApplicant1(), caseData.getApplicant2());

        AlternativeServiceType alternativeServiceType = caseData.getAlternativeService().getAlternativeServiceType();

        templateVars.put("IS_DEEMED_SERVICE", DEEMED.equals(alternativeServiceType) ? YES : NO);
        templateVars.put("IS_DISPENSE_SERVICE", DISPENSED.equals(alternativeServiceType) ? YES : NO);
        templateVars.put("IS_BAILIFF_SERVICE", BAILIFF.equals(alternativeServiceType) ? YES : NO);

        return templateVars;
    }

    private EmailTemplateName getEmailTemplate(final AlternativeService alternativeService, final Long caseId) {
        if (isNull(alternativeService.getServiceApplicationGranted())) {
            throw new NotificationTemplateException(format("MISSING_FIELD_MESSAGE", "serviceApplicationGranted", caseId));
        }

        if (alternativeService.isApplicationGranted()) {
            log.info(LOGGER_MESSAGE, alternativeService.getAlternativeServiceType().getLabel(), "granted");
            return SERVICE_APPLICATION_GRANTED;
        } else {
            log.info(LOGGER_MESSAGE, alternativeService.getAlternativeServiceType().getLabel(), "rejected");
            return SERVICE_APPLICATION_REJECTED;
        }
    }
}
