package uk.gov.hmcts.sptribs.ciccase.validation;

import uk.gov.hmcts.sptribs.ciccase.model.Application;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.MarriageDetails;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.time.temporal.ChronoUnit.YEARS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

public final class ValidationUtil {

    public static final String LESS_THAN_ONE_YEAR_AGO = " can not be less than one year ago.";
    public static final String MORE_THAN_ONE_HUNDRED_YEARS_AGO = " can not be more than 100 years ago.";
    public static final String IN_THE_FUTURE = " can not be in the future.";
    public static final String EMPTY = " cannot be empty or null";
    public static final String CONNECTION = "Connection ";
    public static final String CANNOT_EXIST = " cannot exist";
    public static final String SOT_REQUIRED = "Statement of truth must be accepted by the person making the application";

    private ValidationUtil() {
    }

    public static List<String> validateBasicCase(CaseData caseData) {
        return flattenLists(
            notNull(caseData.getApplicationType(), "ApplicationType"),
            notNull(caseData.getApplicant1().getFirstName(), "Applicant1FirstName"),
            notNull(caseData.getApplicant1().getLastName(), "Applicant1LastName"),
            notNull(caseData.getApplicant2().getFirstName(), "Applicant2FirstName"),
            notNull(caseData.getApplicant2().getLastName(), "Applicant2LastName"),
            notNull(caseData.getApplicant1().getFinancialOrder(), "Applicant1FinancialOrder"),
            !caseData.getApplicant1().isOffline() ? notNull(caseData.getApplicant1().getGender(), "Applicant1Gender") : emptyList(),
            !isBlank(caseData.getApplicant2().getEmail()) && !caseData.getApplication().isPaperCase()
                ? notNull(caseData.getApplicant2().getGender(), "Applicant2Gender")
                : emptyList(),
            notNull(caseData.getApplication().getMarriageDetails().getApplicant1Name(), "MarriageApplicant1Name"),
            notNull(caseData.getApplicant1().getContactDetailsType(), "Applicant1ContactDetailsType"),
            hasStatementOfTruth(caseData.getApplication()),
            !caseData.getApplicant1().isOffline()
                ? caseData.getApplicant1().getApplicantPrayer().validatePrayerApplicant1(caseData)
                : emptyList(),
            validateMarriageDate(caseData.getApplication().getMarriageDetails().getDate(), "MarriageDate"),
            validateJurisdictionConnections(caseData)
        );
    }

    private static List<String> hasStatementOfTruth(Application application) {
        return application.hasStatementOfTruth() ? emptyList() : List.of(SOT_REQUIRED);
    }

    public static List<String> validateApplicant1BasicCase(CaseData caseData) {
        return flattenLists(
            notNull(caseData.getApplicant1().getFirstName(), "Applicant1FirstName"),
            notNull(caseData.getApplicant1().getLastName(), "Applicant1LastName"),
            notNull(caseData.getApplicant1().getFinancialOrder(), "Applicant1FinancialOrder"),
            !caseData.getApplicant1().isOffline() ? notNull(caseData.getApplicant1().getGender(), "Applicant1Gender") : emptyList(),
            !isBlank(caseData.getApplicant2().getEmail()) ? notNull(caseData.getApplicant2().getGender(), "Applicant2Gender") : emptyList(),
            notNull(caseData.getApplication().getMarriageDetails().getApplicant1Name(), "MarriageApplicant1Name"),
            validateMarriageDate(caseData.getApplication().getMarriageDetails().getDate(), "MarriageDate"),
            validateJurisdictionConnections(caseData)
        );
    }

    public static List<String> validateApplicant2BasicCase(CaseData caseData) {
        return flattenLists(
            notNull(caseData.getApplicant2().getFirstName(), "Applicant2FirstName"),
            notNull(caseData.getApplicant2().getLastName(), "Applicant2LastName"),
            notNull(caseData.getApplication().getApplicant2StatementOfTruth(), "Applicant2StatementOfTruth"),
            !isBlank(caseData.getApplicant2().getEmail())
                ? caseData.getApplicant2().getApplicantPrayer().validatePrayerApplicant2(caseData)
                : emptyList(),
            notNull(caseData.getApplication().getMarriageDetails().getApplicant2Name(), "MarriageApplicant2Name")
        );
    }

    public static List<String> validateApplicant2RequestChanges(Application application) {
        return flattenLists(
            notNull(application.getApplicant2ConfirmApplicant1Information(), "Applicant2ConfirmApplicant1Information"),
            notNull(application.getApplicant2ExplainsApplicant1IncorrectInformation(), "Applicant2ExplainsApplicant1IncorrectInformation")
        );
    }

    public static List<String> notNull(Object value, String field) {
        return value == null ? List.of(field + EMPTY) : emptyList();
    }

    public static List<String> validateMarriageDate(LocalDate localDate, String field) {
        if (localDate == null) {
            return List.of(field + EMPTY);
        } else if (isLessThanOneYearAgo(localDate)) {
            return List.of(field + LESS_THAN_ONE_YEAR_AGO);
        } else if (isOverOneHundredYearsAgo(localDate)) {
            return List.of(field + MORE_THAN_ONE_HUNDRED_YEARS_AGO);
        } else if (isInTheFuture(localDate)) {
            return List.of(field + IN_THE_FUTURE);
        }
        return emptyList();
    }

    public static List<String> validateJurisdictionConnections(CaseData caseData) {
        if (caseData.getApplication().isPaperCase() || caseData.getApplicant1().isRepresented()) {
            if (isEmpty(caseData.getApplication().getJurisdiction().getConnections())) {
                return List.of("JurisdictionConnections" + EMPTY);
            }
            return emptyList();
        }

        return caseData.getApplication().getJurisdiction().validateJurisdiction(caseData);
    }

    private static boolean isLessThanOneYearAgo(LocalDate date) {
        return !date.isAfter(LocalDate.now())
            && date.isAfter(LocalDate.now().minus(1, YEARS));
    }

    private static boolean isOverOneHundredYearsAgo(LocalDate date) {
        return date.isBefore(LocalDate.now().minus(100, YEARS));
    }

    private static boolean isInTheFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    public static List<String> validateCaseFieldsForIssueApplication(MarriageDetails marriageDetails) {
        //MarriageApplicant1Name and MarriageDate are validated in validateBasicCase
        return flattenLists(
            notNull(marriageDetails.getApplicant2Name(), "MarriageApplicant2Name"),
            notNull(marriageDetails.getPlaceOfMarriage(), "PlaceOfMarriage")
        );
    }

    @SafeVarargs
    public static <E> List<E> flattenLists(List<E>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(toList());
    }
}
