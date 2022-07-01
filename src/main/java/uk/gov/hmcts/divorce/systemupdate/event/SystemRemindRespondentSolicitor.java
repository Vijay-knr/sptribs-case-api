package uk.gov.hmcts.divorce.systemupdate.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.divorce.ciccase.model.CaseData;
import uk.gov.hmcts.divorce.ciccase.model.State;
import uk.gov.hmcts.divorce.ciccase.model.UserRole;

import static uk.gov.hmcts.divorce.ciccase.model.State.AwaitingAos;
import static uk.gov.hmcts.divorce.ciccase.model.UserRole.SYSTEMUPDATE;
import static uk.gov.hmcts.divorce.ciccase.model.access.Permissions.CREATE_READ_UPDATE;
import static uk.gov.hmcts.divorce.common.ccd.CcdPageConfiguration.NEVER_SHOW;

@Slf4j
@Component
public class SystemRemindRespondentSolicitor implements CCDConfig<CaseData, State, UserRole> {

    public static final String SYSTEM_REMIND_RESPONDENT_SOLICITOR_TO_RESPOND = "system-remind-respondent-solicitor-to-respond";

    @Override
    public void configure(final ConfigBuilder<CaseData, State, UserRole> configBuilder) {

        configBuilder
            .event(SYSTEM_REMIND_RESPONDENT_SOLICITOR_TO_RESPOND)
            .forState(AwaitingAos)
            .showCondition(NEVER_SHOW)
            .name("Remind Respondent Solicitor")
            .description("Remind Respondent Solicitor to respond to the application (Notice of Proceedings)")
            .grant(CREATE_READ_UPDATE, SYSTEMUPDATE)
            .retries(120, 120)
            .aboutToSubmitCallback(this::aboutToSubmit);
    }

    public AboutToStartOrSubmitResponse<CaseData, State> aboutToSubmit(CaseDetails<CaseData, State> details,
                                                                       CaseDetails<CaseData, State> beforeDetails) {

        CaseData data = details.getData();

        //notificationDispatcher.send(respondentSolicitorReminderNotification, data, details.getId());
        data.getApplication().setRespondentSolicitorReminderSent(YesOrNo.YES);

        return AboutToStartOrSubmitResponse.<CaseData, State>builder()
            .data(data)
            .build();
    }
}
