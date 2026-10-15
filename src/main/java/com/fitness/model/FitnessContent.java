package com.fitness.model;

/**
 * Domain model representing a fitness educational article, guide, or tip.
 */
public class FitnessContent extends BaseEntity {

    private String title;
    private String content;
    private ContentCategory category = ContentCategory.GENERAL;
    private Integer authorId;
    private String authorName;
    private ContentStatus status = ContentStatus.PENDING;
    private String rejectionReason;

    public FitnessContent() {
    }

    public FitnessContent(Integer id, String title, String content, ContentCategory category,
                          Integer authorId, ContentStatus status) {
        super(id);
        this.title = title;
        this.content = content;
        this.category = category != null ? category : ContentCategory.GENERAL;
        this.authorId = authorId;
        this.status = status != null ? status : ContentStatus.PENDING;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentCategory getCategory() {
        return category;
    }

    public void setCategory(ContentCategory category) {
        this.category = category;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public boolean isApproved() {
        return this.status == ContentStatus.APPROVED;
    }
}
