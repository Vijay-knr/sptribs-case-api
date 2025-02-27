package uk.gov.hmcts.sptribs.systemupdate.service.task;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.sptribs.ciccase.model.Applicant;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.CaseDocuments;
import uk.gov.hmcts.sptribs.ciccase.model.State;
import uk.gov.hmcts.sptribs.document.CaseDataDocumentService;
import uk.gov.hmcts.sptribs.document.content.ConditionalOrderPronouncedTemplateContent;
import uk.gov.hmcts.sptribs.document.model.DivorceDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.NO;
import static uk.gov.hmcts.sptribs.ciccase.model.LanguagePreference.ENGLISH;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.CONDITIONAL_ORDER_PRONOUNCED_DOCUMENT_NAME;
import static uk.gov.hmcts.sptribs.document.DocumentConstants.CONDITIONAL_ORDER_PRONOUNCED_TEMPLATE_ID;
import static uk.gov.hmcts.sptribs.document.model.DocumentType.APPLICATION;
import static uk.gov.hmcts.sptribs.document.model.DocumentType.CONDITIONAL_ORDER_GRANTED;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_CASE_ID;

@ExtendWith(MockitoExtension.class)
class GenerateConditionalOrderPronouncedDocumentTest {

    @Mock
    private CaseDataDocumentService caseDataDocumentService;

    @Mock
    private ConditionalOrderPronouncedTemplateContent conditionalOrderPronouncedTemplateContent;

    @InjectMocks
    private GenerateConditionalOrderPronouncedDocument generateConditionalOrderPronouncedDocument;

    @Test
    void shouldGenerateConditionalOrderGrantedDocAndUpdateCaseData() {
        //Given
        final Map<String, Object> templateContent = new HashMap<>();
        final CaseData caseData = CaseData.builder()
            .applicant1(Applicant.builder()
                .languagePreferenceWelsh(NO)
                .build())
            .build();

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setId(TEST_CASE_ID);
        caseDetails.setData(caseData);

        when(conditionalOrderPronouncedTemplateContent.apply(caseData, TEST_CASE_ID))
            .thenReturn(templateContent);

        //When
        generateConditionalOrderPronouncedDocument.apply(caseDetails);

        //Then
        verify(caseDataDocumentService).renderDocumentAndUpdateCaseData(
            caseData,
            CONDITIONAL_ORDER_GRANTED,
            templateContent,
            TEST_CASE_ID,
            CONDITIONAL_ORDER_PRONOUNCED_TEMPLATE_ID,
            ENGLISH,
            CONDITIONAL_ORDER_PRONOUNCED_DOCUMENT_NAME);
    }

    @Test
    public void shouldReturnConditionalOrderDocWhenExists() {
        //Given
        ListValue<DivorceDocument> divorceDocumentListValue = ListValue
            .<DivorceDocument>builder()
            .id(APPLICATION.getLabel())
            .value(DivorceDocument.builder()
                .documentType(APPLICATION)
                .build())
            .build();

        ListValue<DivorceDocument> coDocumentListValue = ListValue
            .<DivorceDocument>builder()
            .id(CONDITIONAL_ORDER_GRANTED.getLabel())
            .value(DivorceDocument.builder()
                .documentType(CONDITIONAL_ORDER_GRANTED)
                .build())
            .build();

        CaseData caseData = CaseData.builder()
            .documents(CaseDocuments.builder()
                .documentsGenerated(List.of(divorceDocumentListValue, coDocumentListValue))
                .build())
            .build();

        //When
        Optional<ListValue<DivorceDocument>> conditionalOrderGrantedDoc =
            generateConditionalOrderPronouncedDocument.getConditionalOrderGrantedDoc(caseData);

        //Then
        assertTrue(conditionalOrderGrantedDoc.isPresent());
    }

    @Test
    public void shouldNotReturnConditionalOrderDocWhenDoesNotExists() {
        //Given
        ListValue<DivorceDocument> divorceDocumentListValue = ListValue
            .<DivorceDocument>builder()
            .id(APPLICATION.getLabel())
            .value(DivorceDocument.builder()
                .documentType(APPLICATION)
                .build())
            .build();

        CaseData caseData = CaseData.builder()
            .documents(CaseDocuments.builder()
                .documentsGenerated(singletonList(divorceDocumentListValue))
                .build())
            .build();

        //When
        Optional<ListValue<DivorceDocument>> conditionalOrderGrantedDoc =
            generateConditionalOrderPronouncedDocument.getConditionalOrderGrantedDoc(caseData);

        //Then
        assertTrue(conditionalOrderGrantedDoc.isEmpty());
    }

    @Test
    public void shouldRemoveConditionalOrderGrantedDoc() {
        //Given
        final Map<String, Object> templateContent = new HashMap<>();
        final CaseData caseData = CaseData.builder()
            .applicant1(Applicant.builder()
                .languagePreferenceWelsh(NO)
                .build())
            .documents(CaseDocuments.builder()
                .documentsGenerated(Lists.newArrayList(
                    ListValue.<DivorceDocument>builder()
                        .id("1")
                        .value(DivorceDocument.builder()
                            .documentType(CONDITIONAL_ORDER_GRANTED)
                            .build())
                        .build(),
                    ListValue.<DivorceDocument>builder()
                        .id("2")
                        .value(DivorceDocument.builder()
                            .documentType(APPLICATION)
                            .build()).build()
                ))
                .build())
            .build();

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setId(TEST_CASE_ID);
        caseDetails.setData(caseData);

        when(conditionalOrderPronouncedTemplateContent.apply(caseData, TEST_CASE_ID))
            .thenReturn(templateContent);

        //When
        generateConditionalOrderPronouncedDocument
            .removeExistingAndGenerateNewConditionalOrderGrantedDoc(caseDetails);

        //Then
        verify(caseDataDocumentService).renderDocumentAndUpdateCaseData(
            caseData,
            CONDITIONAL_ORDER_GRANTED,
            templateContent,
            TEST_CASE_ID,
            CONDITIONAL_ORDER_PRONOUNCED_TEMPLATE_ID,
            ENGLISH,
            CONDITIONAL_ORDER_PRONOUNCED_DOCUMENT_NAME);

        assertEquals(1, caseData.getDocuments().getDocumentsGenerated().size());
        assertEquals(APPLICATION, caseData.getDocuments().getDocumentsGenerated().get(0).getValue().getDocumentType());
    }

}
