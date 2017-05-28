package io.excitinglab.lifemanager;

public class Subtask {
    int id;
    String name;
    int status;
    int p_id;

    public Subtask() {

    }

    public Subtask(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getStatus() {
        return this.status;
    }

    public int getP_id() {
        return this.p_id;
    }
}
