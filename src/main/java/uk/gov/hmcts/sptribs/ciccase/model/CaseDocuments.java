package uk.gov.hmcts.sptribs.ciccase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.DynamicList;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.ccd.sdk.type.ScannedDocument;
import uk.gov.hmcts.sptribs.ciccase.model.access.Applicant2Access;
import uk.gov.hmcts.sptribs.ciccase.model.access.CaseworkerAccessOnlyAccess;
import uk.gov.hmcts.sptribs.ciccase.model.access.CaseworkerCourtAdminWithSolicitorAccess;
import uk.gov.hmcts.sptribs.ciccase.model.access.DefaultAccess;
import uk.gov.hmcts.sptribs.document.model.ConfidentialDivorceDocument;
import uk.gov.hmcts.sptribs.document.model.DivorceDocument;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;
import static uk.gov.hmcts.ccd.sdk.type.FieldType.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseDocuments {

    @CCD(
        label = "Applicant 1 uploaded documents",
        typeOverride = Collection,
        typeParameterOverride = "DivorceDocument",
        access = {DefaultAccess.class}
    )
    private List<ListValue<DivorceDocument>> applicant1DocumentsUploaded;

    @CCD(
        label = "Applicant 2 Documents uploaded",
        typeOverride = Collection,
        typeParameterOverride = "DivorceDocument",
        access = {Applicant2Access.class}
    )
    private List<ListValue<DivorceDocument>> applicant2DocumentsUploaded;

    @CCD(
        label = "Documents uploaded",
        typeOverride = Collection,
        typeParameterOverride = "DivorceDocument",
        access = {DefaultAccess.class}
    )
    private List<ListValue<DivorceDocument>> documentsUploaded;

    @CCD(
        label = "Confidential documents uploaded",
        typeOverride = Collection,
        typeParameterOverride = "ConfidentialDivorceDocument",
        access = {CaseworkerCourtAdminWithSolicitorAccess.class}
    )
    private List<ListValue<ConfidentialDivorceDocument>> confidentialDocumentsUploaded;

    @CCD(
        label = "Confidential documents generated",
        typeOverride = Collection,
        typeParameterOverride = "ConfidentialDivorceDocument",
        access = {CaseworkerCourtAdminWithSolicitorAccess.class}
    )
    private List<ListValue<ConfidentialDivorceDocument>> confidentialDocumentsGenerated;

    @CCD(
        label = "Documents generated",
        typeOverride = Collection,
        typeParameterOverride = "DivorceDocument",
        access = {DefaultAccess.class}
    )
    private List<ListValue<DivorceDocument>> documentsGenerated;

    @CCD(
        label = "Scanned documents",
        typeOverride = Collection,
        typeParameterOverride = "ScannedDocument"
    )
    private List<ListValue<ScannedDocument>> scannedDocuments;

    @CCD(
        label = "Upload Answer Received supporting documents",
        typeOverride = Collection,
        typeParameterOverride = "DivorceDocument",
        access = {CaseworkerAccessOnlyAccess.class}
    )
    private List<ListValue<DivorceDocument>> answerReceivedSupportingDocuments;

    @CCD(
        label = "Select respondent answers document",
        access = {CaseworkerAccessOnlyAccess.class}
    )
    private DynamicList scannedDocumentNames;

    @CCD(
        label = "Amended applications",
        typeOverride = Collection,
        typeParameterOverride = "DivorceDocument",
        access = {DefaultAccess.class}
    )
    private List<ListValue<DivorceDocument>> amendedApplications;


    public static <T> List<ListValue<T>> addDocumentToTop(final List<ListValue<T>> documents, final T value) {
        return addDocumentToTop(documents, value, null);
    }

    public static <T> List<ListValue<T>> addDocumentToTop(final List<ListValue<T>> documents, final T value, final String id) {
        final var listItemId = isBlank(id) ? String.valueOf(randomUUID()) : id;
        final var listValue = new ListValue<T>(listItemId, value);
        final List<ListValue<T>> list = isEmpty(documents) ? new ArrayList<>() : new ArrayList<>(documents);

        list.add(0, listValue);

        return list;
    }

    public static <T> List<ListValue<T>> sortByNewest(final List<ListValue<T>> previous, final List<ListValue<T>> updated) {
        if (isEmpty(previous)) {
            return updated;
        }

        final var previousListValueIds = previous
            .stream()
            .map(ListValue::getId)
            .collect(toCollection(HashSet::new));

        //Split the collection into two lists one without id's(newly added documents) and other with id's(existing documents)
        final var documentsWithoutIds =
            updated
                .stream()
                .collect(groupingBy(listValue -> !previousListValueIds.contains(listValue.getId())));

        return sortDocuments(documentsWithoutIds);
    }

    private static <T> List<ListValue<T>> sortDocuments(final Map<Boolean, List<ListValue<T>>> documentsWithoutIds) {
        final List<ListValue<T>> sortedDocuments = new ArrayList<>();
        final var newDocuments = documentsWithoutIds.get(true);
        final var previousDocuments = documentsWithoutIds.getOrDefault(false, new ArrayList<>());

        if (null != newDocuments) {
            sortedDocuments.addAll(0, newDocuments); // add new documents to start of the list
            sortedDocuments.addAll(1, previousDocuments);
            sortedDocuments.forEach(
                uploadedDocumentListValue -> uploadedDocumentListValue.setId(String.valueOf(randomUUID()))
            );
            return sortedDocuments;
        }

        return previousDocuments;
    }

    public static <T> boolean hasAddedDocuments(final List<ListValue<T>> after,
                                                final List<ListValue<T>> before) {

        if (isNull(before) && !after.isEmpty()) {
            return true;
        } else if (isNull(before) || isNull(after)) {
            return false;
        }

        return !after.stream()
            .allMatch(afterValue -> before.stream()
                .anyMatch(beforeValue -> Objects.equals(beforeValue.getId(), afterValue.getId())));
    }
}
