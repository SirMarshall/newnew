/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 Carter I. Walker
 */

package ucf.assignments;
public class Task {
    String title;
    String description;
    String dueDate;
    boolean complete;

    public Task(String newTitle, String desc, String date, Boolean done)
    {
        title = newTitle;
        description = desc;
        dueDate = date;
        complete = done;
    }

    //String override for proper listView
    public String toString()
    {
        return title;
    }

    public String descToString()
    {
        return description;
    }
    public String dateToString()
    {
        return dueDate;
    }
}
