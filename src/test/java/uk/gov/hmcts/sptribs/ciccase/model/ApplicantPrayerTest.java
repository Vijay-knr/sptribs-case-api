package uk.gov.hmcts.sptribs.ciccase.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.YES;
import static uk.gov.hmcts.sptribs.ciccase.model.DivorceOrDissolution.DISSOLUTION;
import static uk.gov.hmcts.sptribs.ciccase.model.DivorceOrDissolution.DIVORCE;
import static uk.gov.hmcts.sptribs.ciccase.model.FinancialOrderFor.APPLICANT;
import static uk.gov.hmcts.sptribs.ciccase.model.FinancialOrderFor.CHILDREN;

public class ApplicantPrayerTest {

    @Test
    void shouldReturnWarningsWhenDivorceApplicant1DetailsAreValidatedAndPrayerForChildrenAndApplicantAreNotConfirmed() {
        //When
        var applicant1 = Applicant
            .builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT, CHILDREN))
            .build();

        var caseData = CaseData
            .builder()
            .divorceOrDissolution(DIVORCE)
            .applicant1(applicant1)
            .build();

        //Then
        assertThat(applicant1.getApplicantPrayer().validatePrayerApplicant1(caseData))
            .containsExactlyInAnyOrder(
                "Applicant 1 must confirm prayer to dissolve their marriage (get a divorce)",
                "Applicant 1 must confirm prayer for financial orders for themselves",
                "Applicant 1 must confirm prayer for financial orders for the children"
            );
    }

    @Test
    void shouldReturnWarningsWhenDissolutionApplicant1DetailsAreValidatedAndPrayerForChildrenAndApplicantAreNotConfirmed() {
        //When
        var applicant1 = Applicant
            .builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT, CHILDREN))
            .build();

        var caseData = CaseData
            .builder()
            .divorceOrDissolution(DISSOLUTION)
            .applicant1(applicant1)
            .build();

        //Then
        assertThat(applicant1.getApplicantPrayer().validatePrayerApplicant1(caseData))
            .containsExactlyInAnyOrder(
                "Applicant 1 must confirm prayer to end their civil partnership",
                "Applicant 1 must confirm prayer for financial orders for themselves",
                "Applicant 1 must confirm prayer for financial orders for the children"
            );
    }

    @Test
    void shouldReturnWarningsWhenDivorceApplicant2DetailsAreValidatedAndPrayerForChildrenAndApplicantAreNotConfirmed() {
        //When
        var applicant2 = Applicant
            .builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT, CHILDREN))
            .build();

        var caseData = CaseData
            .builder()
            .divorceOrDissolution(DIVORCE)
            .applicant2(applicant2)
            .build();

        //Then
        assertThat(applicant2.getApplicantPrayer().validatePrayerApplicant2(caseData))
            .containsExactlyInAnyOrder(
                "Applicant 2 must confirm prayer to dissolve their marriage (get a divorce)",
                "Applicant 2 must confirm prayer for financial orders for themselves",
                "Applicant 2 must confirm prayer for financial orders for the children"
            );
    }

    @Test
    void shouldReturnWarningsWhenDissolutionApplicant2DetailsAreValidatedAndPrayerForChildrenAndApplicantAreNotConfirmed() {
        //When
        var applicant2 = Applicant
            .builder()
            .financialOrder(YES)
            .financialOrdersFor(Set.of(APPLICANT, CHILDREN))
            .build();

        var caseData = CaseData
            .builder()
            .divorceOrDissolution(DISSOLUTION)
            .applicant2(applicant2)
            .build();

        //Then
        assertThat(applicant2.getApplicantPrayer().validatePrayerApplicant2(caseData))
            .containsExactlyInAnyOrder(
                "Applicant 2 must confirm prayer to end their civil partnership",
                "Applicant 2 must confirm prayer for financial orders for themselves",
                "Applicant 2 must confirm prayer for financial orders for the children"
            );
    }
}
