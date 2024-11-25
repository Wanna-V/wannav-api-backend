package com.ssg.wannavapibackend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OCRResponseDTO {

    @JsonProperty("images")
    List<Image> images;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    private static class Image {

        @JsonProperty("inferResult")
        private String inferResult;

        @JsonProperty("message")
        private String message;

        @JsonProperty("matchedTemplate")
        private MatchedTemplate matchedTemplate;

        @JsonProperty("fields")
        private List<Field> fields;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MatchedTemplate {

        @JsonProperty("name")
        private String usedTemplate;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Field {

        @JsonProperty("name")
        String field;

        @JsonProperty("inferText")
        String inferText;
    }

    public String getInferResult() {
        return images.get(0).inferResult;
    }

    public String Message() {
        return images.get(0).getMessage();
    }

    public String getUsedTemplate() {
        return images.get(0).getMatchedTemplate().usedTemplate;
    }

    public String getRestaurantName() {
        return images.get(0).getFields().get(0).inferText;
    }

    public String getAddress() {
        return images.get(0).getFields().get(1).inferText;
    }

    public String getVisitedDate() {
        return images.get(0).getFields().get(2).inferText;
    }
}
