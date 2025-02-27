package uk.gov.hmcts.sptribs.document.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DocumentInfo {

    private final String url;

    private final String filename;

    private final String binaryUrl;
}
