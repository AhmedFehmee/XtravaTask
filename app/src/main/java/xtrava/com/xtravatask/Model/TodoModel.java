package xtrava.com.xtravatask.Model;

/**
 * Created by Fehoo on 3/2/2018.
 */

public class TodoModel {
    private String completed;
    private String title;
    private String url;
    private String id ;

    public TodoModel(){}
    public TodoModel(String title, String completed, String id, String url) {
        this.setTitle(title);
        this.setCompleted(completed);
        this.setId(id);
        this.setUrl(url);
    }


    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
