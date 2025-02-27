package uk.gov.hmcts.sptribs.document.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.sptribs.ciccase.model.CaseData;
import uk.gov.hmcts.sptribs.ciccase.model.State;
import uk.gov.hmcts.sptribs.document.DraftApplicationRemovalService;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ccd.sdk.type.YesOrNo.YES;
import static uk.gov.hmcts.sptribs.document.model.DocumentType.APPLICATION;
import static uk.gov.hmcts.sptribs.testutil.TestConstants.TEST_CASE_ID;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.LOCAL_DATE_TIME;
import static uk.gov.hmcts.sptribs.testutil.TestDataHelper.documentWithType;

@ExtendWith(MockitoExtension.class)
class DivorceApplicationRemoverTest {

    @Mock
    private DraftApplicationRemovalService draftApplicationRemovalService;

    @InjectMocks
    private DivorceApplicationRemover divorceApplicationRemover;

    @Test
    void shouldRemoveDraftApplication() {
        //Given
        final var generatedDocuments = singletonList(documentWithType(APPLICATION));
        final var caseData = CaseData.builder().build();
        caseData.getApplication().setSolSignStatementOfTruth(YES);
        caseData.getDocuments().setDocumentsGenerated(generatedDocuments);

        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
        caseDetails.setData(caseData);
        caseDetails.setId(TEST_CASE_ID);
        caseDetails.setCreatedDate(LOCAL_DATE_TIME);

        when(draftApplicationRemovalService.removeDraftApplicationDocument(generatedDocuments, TEST_CASE_ID))
            .thenReturn(emptyList());

        //When
        final var result = divorceApplicationRemover.apply(caseDetails);

        //Then
        assertThat(result.getData().getDocuments().getDocumentsGenerated(), empty());
        verify(draftApplicationRemovalService)
            .removeDraftApplicationDocument(
                generatedDocuments,
                TEST_CASE_ID);

        verifyNoMoreInteractions(draftApplicationRemovalService);
    }
}
