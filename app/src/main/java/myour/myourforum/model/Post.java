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
    private boolean hasImage;
    //None database. to get Data. NOT SET DATA.
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

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public Post(int id, String title, String content, String createTime, String updateTime, int userId, int viewCount, int categoryId, boolean hasImage) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.userId = userId;
        this.viewCount = viewCount;
        this.categoryId = categoryId;
        this.hasImage = hasImage;
    }

    public Post() {
    }

    public void setPostUpdate(Post postUpdate) {
        this.id = postUpdate.getId();
        this.title = postUpdate.getTitle();
        this.content = postUpdate.getContent();
        this.createTime = postUpdate.getCreateTime();
        this.updateTime = postUpdate.getUpdateTime();
        this.userId = postUpdate.getUserId();
        this.categoryId = postUpdate.getCategoryId();
    }

    public void setPost(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createTime = post.getCreateTime();
        this.updateTime = post.getUpdateTime();
        this.userId = post.getUserId();
        this.viewCount = post.getViewCount();
        this.categoryId = post.getCategoryId();
        this.hasImage = post.isHasImage();
    }
}
