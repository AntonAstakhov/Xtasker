package io.excitinglab.xtasker;

public class Lists {
    int id;
    String name;
    int sort;

//    0 - alphabetically
//    1 - alphabetically reverse
//    2 - date due
//    3 - date due reverse
//    4 - creation date
//    5 - creation date reverse
//    6 - completed
//    7 - completed reverse


    public Lists() {

    }

    public Lists(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getSort() {
        return this.sort;
    }
}
