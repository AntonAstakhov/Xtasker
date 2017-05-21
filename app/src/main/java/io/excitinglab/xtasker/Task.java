package io.excitinglab.xtasker;

public class Task {

//    Todo: ADD MORE CONSTRUCTORS !!!

    int id;
    long create;
    String name;
    int status;
    long deadline;
    long reminder;
    String note;
    int p_id;

    public Task() {

    }

    public Task(String name) {
        this.name = name;
    }

    public Task(String name, int status, long deadline, long reminder, String note) {
        this.name = name;
        this.status = status;
        this.deadline = deadline;
        this.reminder = reminder;
        this.note = note;
    }

//    public Task(String name, int status) {
//        this.id = id;
//        this.name = name;
//        this.status = status;
//    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public void setReminder(long reminder) {
        this.reminder = reminder;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public long getId() {
        return this.id;
    }

    public long getCreate() {
        return this.create;
    }

    public String getName() {
        return this.name;
    }

    public int getStatus() {
        return this.status;
    }

    public long getDeadline() {
        return this.deadline;
    }

    public long getReminder() {
        return this.reminder;
    }

    public String getNote() {
        return this.note;
    }

    public int getP_id() {
        return this.p_id;
    }
}
