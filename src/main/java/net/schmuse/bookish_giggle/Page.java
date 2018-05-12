package net.schmuse.bookish_giggle;

import java.util.List;

public class Page {

    private Long id;
    private String title;
    private List<String> tags;
    private String doc;

    Page(String title, List<String> tags, String doc) {
        this.id = null;
        this.title = title;
        this.tags = tags;
        this.doc = doc;
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    List<String> getTags() {
        return tags;
    }

    void setTags(List<String> tags) {
        this.tags = tags;
    }

    String getDoc() {
        return doc;
    }

    void setDoc(String doc) {
        this.doc = doc;
    }
}
