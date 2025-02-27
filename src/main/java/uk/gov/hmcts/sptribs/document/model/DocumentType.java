package uk.gov.hmcts.sptribs.document.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum DocumentType implements HasLabel {

    @JsonProperty("aosOverdueCoverLetter")
    AOS_OVERDUE_COVER_LETTER("AOS overdue cover letter"),

    @JsonProperty("acknowledgementOfService")
    @JsonAlias({"acknowledgeOfService", "aos"})
    ACKNOWLEDGEMENT_OF_SERVICE("Acknowledgement of service"),

    @JsonProperty("amendedApplication")
    AMENDED_APPLICATION("Amended Application"),

    @JsonProperty("annexA")
    ANNEX_A("Annex A"),

    @JsonProperty("application")
    @JsonAlias("divorceApplication")
    APPLICATION("Application"),

    @JsonProperty("bailiffCertificateOfService")
    BAILIFF_CERTIFICATE_OF_SERVICE("Bailiff certificate of service"),

    @JsonProperty("bailiffService")
    @JsonAlias({"serviceBaliff"})
    BAILIFF_SERVICE("Bailiff Service"),

    @JsonProperty("bailiffServiceRefused")
    BAILIFF_SERVICE_REFUSED("Bailiff Service Refused"),

    @JsonProperty("certificateOfEntitlement")
    CERTIFICATE_OF_ENTITLEMENT("Certificate of entitlement"),

    @JsonProperty("certificateOfService")
    CERTIFICATE_OF_SERVICE("Certificate of service"),

    @JsonProperty("conditionalOrderAnswers")
    CONDITIONAL_ORDER_ANSWERS("Conditional order answers"),

    @JsonProperty("conditionalOrderApplication")
    CONDITIONAL_ORDER_APPLICATION("Conditional order application (D84)"),

    @JsonProperty("conditionalOrderGranted")
    CONDITIONAL_ORDER_GRANTED("Conditional Order Granted"),

    @JsonProperty("conditionalOrderRefusal")
    CONDITIONAL_ORDER_REFUSAL("Conditional order refusal - clarification response"),

    @JsonProperty("correspondence")
    CORRESPONDENCE("Correspondence"),

    @JsonProperty("costs")
    COSTS("Costs"),

    @JsonProperty("costsOrder")
    COSTS_ORDER("Costs order"),

    @JsonProperty("coversheet")
    COVERSHEET("Coversheet"),

    @JsonProperty("d84")
    D84("D84"),

    @JsonProperty("d9D")
    D9D("D9D"),

    @JsonProperty("d9H")
    D9H("D9H"),

    @JsonProperty("d10")
    D10("D10"),

    @JsonProperty("d11")
    D11("D11"),

    @JsonProperty("deemedService")
    @JsonAlias({"serviceDeemed"})
    DEEMED_SERVICE("Deemed service"),

    @JsonProperty("deemedAsServiceGranted")
    DEEMED_AS_SERVICE_GRANTED("Deemed as service granted"),

    @JsonProperty("deemedServiceRefused")
    DEEMED_SERVICE_REFUSED("Deemed service refused"),

    @JsonProperty("dispenseWithService")
    @JsonAlias({"serviceDispensedWith"})
    DISPENSE_WITH_SERVICE("Dispense with service"),

    @JsonProperty("dispenseWithServiceGranted")
    @JsonAlias({"serviceDispensedWithGranted"})
    DISPENSE_WITH_SERVICE_GRANTED("Dispense with service granted"),

    @JsonProperty("dispenseWithServiceRefused")
    DISPENSE_WITH_SERVICE_REFUSED("Dispense with service refused"),

    @JsonProperty("email")
    EMAIL("Email"),

    @JsonProperty("finalOrderApplication")
    FINAL_ORDER_APPLICATION("Final Order application"),

    @JsonProperty("finalOrderGranted")
    FINAL_ORDER_GRANTED("Final order granted"),

    @JsonProperty("generalOrder")
    GENERAL_ORDER("General order"),

    @JsonProperty("marriageCertificate")
    MARRIAGE_CERTIFICATE("Marriage/Civil Partnership Certificate"),

    @JsonProperty("marriageCertificateTranslation")
    MARRIAGE_CERTIFICATE_TRANSLATION("Marriage/Civil Partnership Certificate translation"),

    @JsonProperty("nameChangeEvidence")
    NAME_CHANGE_EVIDENCE("Name change evidence"),

    @JsonProperty("noticeOfProceedings")
    NOTICE_OF_PROCEEDINGS_APP_1("Notice of proceedings for applicant/applicant 1"),

    @JsonProperty("noticeOfProceedingsApp2")
    NOTICE_OF_PROCEEDINGS_APP_2("Notice of proceedings for respondent/applicant 2"),

    @JsonProperty("noticeOfRefusalOfEntitlement")
    NOTICE_OF_REFUSAL_OF_ENTITLEMENT("Notice of refusal of entitlement to a CO"),

    @JsonProperty("objectionToCosts")
    OBJECTION_TO_COSTS("Objection to costs"),

    @JsonProperty("other")
    @JsonAlias({"aosOfflineInvitationLetterToApplicant2"})
    OTHER("Other"),

    @JsonProperty("pronouncementList")
    PRONOUNCEMENT_LIST("Pronouncement List"),

    @JsonProperty("respondentAnswers")
    @JsonAlias("applicant2Answers")
    RESPONDENT_ANSWERS("Respondent answers"),

    @JsonProperty("aos")
    @Deprecated
    RESPONDENT_INVITATION("Respondent Invitation"),

    @JsonProperty("solicitorService")
    @JsonAlias({"serviceSolicitor"})
    SOLICITOR_SERVICE("Solicitor Service"),

    @JsonProperty("welshTranslation")
    WELSH_TRANSLATION("Welsh Translation"),

    @JsonProperty("aosResponseLetter")
    AOS_RESPONSE_LETTER("Aos response letter"),

    @JsonProperty("aosOverdueLetter")
    AOS_OVERDUE_LETTER("Aos overdue letter"),

    @JsonProperty("generalLetter")
    GENERAL_LETTER("General letter");

    private final String label;
}
