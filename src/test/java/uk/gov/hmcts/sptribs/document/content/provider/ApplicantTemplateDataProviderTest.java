package uk.gov.hmcts.sptribs.document.content.provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.sptribs.ciccase.model.Applicant;
import uk.gov.hmcts.sptribs.ciccase.model.ContactDetailsType;
import uk.gov.hmcts.sptribs.ciccase.model.Solicitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.NO;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.YES;
import static uk.gov.hmcts.sptribs.ciccase.model.FinancialOrderFor.APPLICANT;
import static uk.gov.hmcts.sptribs.ciccase.model.FinancialOrderFor.CHILDREN;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_1_EMAIL;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_1_POSTAL_ADDRESS;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_2_EMAIL;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.APPLICANT_2_POSTAL_ADDRESS;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.IS_APP1_CONTACT_PRIVATE;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.IS_APP1_REPRESENTED;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.IS_APP2_CONTACT_PRIVATE;
import static uk.gov.hmcts.sptribs.document.content.DocmosisTemplateConstants.IS_APP2_REPRESENTED;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.APPLICANT_ADDRESS;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.LINE_1_LINE_2_CITY_POSTCODE;

@ExtendWith(MockitoExtension.class)
class ApplicantTemplateDataProviderTest {

    @InjectMocks
    private ApplicantTemplateDataProvider applicantTemplateDataProvider;

    @Test
    void shouldReturnNullForJointIfNoFinancialOrder() {
        //When
        final Applicant applicant = Applicant.builder().build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(applicant)).isNull();
    }

    @Test
    void shouldReturnNullForJointIfEmptyFinancialOrder() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(YES)
            .financialOrdersFor(emptySet())
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(applicant)).isNull();
    }

    @Test
    void shouldReturnNullForJointIfFinancialOrderIsNo() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(NO)
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(applicant)).isNull();
    }

    @Test
    void shouldReturnCorrectStringForJointFinancialOrderForApplicantAndChildrenForJoint() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT, CHILDREN))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(applicant))
            .isEqualTo("applicants, and for the children of both the applicants.");
    }

    @Test
    void shouldReturnCorrectStringForJointFinancialOrderForApplicant() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(applicant))
            .isEqualTo("applicants.");
    }

    @Test
    void shouldReturnCorrectStringForJointFinancialOrderForChildren() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(CHILDREN))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(applicant))
            .isEqualTo("children of both the applicants.");
    }

    @Test
    void shouldReturnNullForJointIfEmptyFinancialOrderFor() {
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(emptySet())).isNull();
    }

    @Test
    void shouldReturnCorrectStringIfFinancialOrderForContainsApplicantAndChildrenForJoint() {
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(Set.of(APPLICANT, CHILDREN)))
            .isEqualTo("applicants, and for the children of both the applicants.");
    }

    @Test
    void shouldReturnCorrectStringIfFinancialOrderForContainsApplicant() {
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(Set.of(APPLICANT)))
            .isEqualTo("applicants.");
    }

    @Test
    void shouldReturnCorrectStringIfFinancialOrderForContainsChildren() {
        assertThat(applicantTemplateDataProvider.deriveJointFinancialOrder(Set.of(CHILDREN)))
            .isEqualTo("children of both the applicants.");
    }

    @Test
    void shouldReturnNullForSoleIfNoFinancialOrder() {
        //When
        final Applicant applicant = Applicant.builder().build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant)).isNull();
    }

    @Test
    void shouldReturnNullForSoleIfEmptyFinancialOrder() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(YES)
            .financialOrdersFor(emptySet())
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant)).isNull();
    }

    @Test
    void shouldReturnNullForSoleIfFinancialOrderIsNo() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(NO)
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant)).isNull();
    }

    @Test
    void shouldReturnCorrectStringForSoleFinancialOrderForApplicantAndChildrenForJoint() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT, CHILDREN))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant))
            .isEqualTo("applicant, and for the children of the applicant and the respondent.");
    }

    @Test
    void shouldReturnCorrectStringForSoleFinancialOrderForApplicant() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant))
            .isEqualTo("applicant.");
    }

    @Test
    void shouldReturnCorrectStringForSoleFinancialOrderForChildren() {
        //When
        final Applicant applicant = Applicant.builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(CHILDREN))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant))
            .isEqualTo("children of the applicant and the respondent.");
    }

    @Test
    void shouldReturnWelshContentForSoleFinancialOrderForApplicantAndChildrenForJoint() {
        //When
        final Applicant applicant = Applicant.builder()
            .languagePreferenceWelsh(YES)
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT, CHILDREN))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant))
            .isEqualTo("y ceisydd a phlant y ceisydd a'r atebydd.");
    }

    @Test
    void shouldReturnWelshContentForSoleFinancialOrderForApplicant() {
        //When
        final Applicant applicant = Applicant.builder()
            .languagePreferenceWelsh(YES)
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant))
            .isEqualTo("y ceisydd.");
    }

    @Test
    void shouldReturnWelshContentForSoleFinancialOrderForChildren() {
        //When
        final Applicant applicant = Applicant.builder()
            .languagePreferenceWelsh(YES)
            .financialOrder(YES)
            .financialOrdersFor(Set.of(CHILDREN))
            .build();

        //Then
        assertThat(applicantTemplateDataProvider.deriveSoleFinancialOrder(applicant))
            .isEqualTo("plant y ceisydd a'r atebydd.");
    }

    @Test
    public void shouldMapApplicantContactDetailsWhenApplicantContactIsNotPrivateAndIsRepresented() {
        //Given
        Applicant applicant1 = buildApplicant(YES, ContactDetailsType.PUBLIC);
        Applicant applicant2 = buildApplicant(YES, ContactDetailsType.PUBLIC);

        Map<String, Object> templateContent = new HashMap<>();

        //When
        applicantTemplateDataProvider.mapContactDetails(applicant1, applicant2, templateContent);

        //Then
        assertThat(templateContent).contains(
            entry(APPLICANT_1_EMAIL, "sol@gm.com"),
            entry(APPLICANT_1_POSTAL_ADDRESS, "sol address"),
            entry(APPLICANT_2_EMAIL, "sol@gm.com"),
            entry(APPLICANT_2_POSTAL_ADDRESS, "sol address"),
            entry(IS_APP1_CONTACT_PRIVATE, false),
            entry(IS_APP1_REPRESENTED, true),
            entry(IS_APP2_CONTACT_PRIVATE, false),
            entry(IS_APP2_REPRESENTED, true)
        );
    }

    @Test
    public void shouldMapSolicitorContactDetailsWhenApplicantContactIsPrivateAndIsRepresented() {
        //Given
        Applicant applicant1 = buildApplicant(YES, ContactDetailsType.PRIVATE);
        Applicant applicant2 = buildApplicant(YES, ContactDetailsType.PRIVATE);

        Map<String, Object> templateContent = new HashMap<>();

        //When
        applicantTemplateDataProvider.mapContactDetails(applicant1, applicant2, templateContent);

        //Then
        assertThat(templateContent).contains(
            entry(APPLICANT_1_EMAIL, "sol@gm.com"),
            entry(APPLICANT_1_POSTAL_ADDRESS, "sol address"),
            entry(APPLICANT_2_EMAIL, "sol@gm.com"),
            entry(APPLICANT_2_POSTAL_ADDRESS, "sol address"),
            entry(IS_APP1_CONTACT_PRIVATE, true),
            entry(IS_APP1_REPRESENTED, true),
            entry(IS_APP2_CONTACT_PRIVATE, true),
            entry(IS_APP2_REPRESENTED, true)
        );
    }

    @Test
    public void shouldMapSolicitorContactDetailsWhenApplicantContactIsPrivateAndIsNotRepresented() {
        //Given
        Applicant applicant1 = buildApplicant(NO, ContactDetailsType.PRIVATE);
        Applicant applicant2 = buildApplicant(YES, ContactDetailsType.PRIVATE);

        Map<String, Object> templateContent = new HashMap<>();

        //When
        applicantTemplateDataProvider.mapContactDetails(applicant1, applicant2, templateContent);

        //Then
        assertThat(templateContent).contains(
            entry(APPLICANT_1_EMAIL, null),
            entry(APPLICANT_1_POSTAL_ADDRESS, null),
            entry(APPLICANT_2_EMAIL, "sol@gm.com"),
            entry(APPLICANT_2_POSTAL_ADDRESS, "sol address"),
            entry(IS_APP1_CONTACT_PRIVATE, true),
            entry(IS_APP1_REPRESENTED, false),
            entry(IS_APP2_CONTACT_PRIVATE, true),
            entry(IS_APP2_REPRESENTED, true)
        );
    }

    @Test
    public void shouldMapApplicantContactDetailsWhenApplicantContactIsNotPrivateAndIsNotRepresented() {
        //Given
        Applicant applicant1 = buildApplicant(NO, ContactDetailsType.PUBLIC);
        Applicant applicant2 = buildApplicant(NO, ContactDetailsType.PUBLIC);

        Map<String, Object> templateContent = new HashMap<>();

        //When
        applicantTemplateDataProvider.mapContactDetails(applicant1, applicant2, templateContent);

        //Then
        assertThat(templateContent).contains(
            entry(APPLICANT_1_EMAIL, "app@gm.com"),
            entry(APPLICANT_1_POSTAL_ADDRESS, LINE_1_LINE_2_CITY_POSTCODE),
            entry(APPLICANT_2_EMAIL, "app@gm.com"),
            entry(APPLICANT_2_POSTAL_ADDRESS, LINE_1_LINE_2_CITY_POSTCODE),
            entry(IS_APP1_CONTACT_PRIVATE, false),
            entry(IS_APP1_REPRESENTED, false),
            entry(IS_APP2_CONTACT_PRIVATE, false),
            entry(IS_APP2_REPRESENTED, false)
        );
    }

    private Applicant buildApplicant(YesOrNo isRepresented, ContactDetailsType contactDetailsType) {
        return Applicant.builder()
            .contactDetailsType(contactDetailsType)
            .email("app@gm.com")
            .address(APPLICANT_ADDRESS)
            .solicitorRepresented(isRepresented)
            .solicitor(Solicitor.builder().email("sol@gm.com").address("sol address").build())
            .build();
    }
}
