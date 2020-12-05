package myour.myourforum.model;

import java.io.Serializable;

public class Post implements Serializable {
    private int id;
    private String title;
    private String content;
    private String createTime;
    private String updateTime;
    private int userId;
    private int viewCount;
    private int categoryId;
    private String source;
    private int likeCount;
    private boolean edited;
    private String authorUsername;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public Post(int id, String title, String content, String createTime, String updateTime, int userId, int viewCount, int categoryId, String source, int likeCount, boolean edited) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.userId = userId;
        this.viewCount = viewCount;
        this.categoryId = categoryId;
        this.source = source;
        this.likeCount = likeCount;
        this.edited = edited;
    }

    public Post() {
    }
}
