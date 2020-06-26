import java.util.Date;

public class Task {
    private String name;
    private boolean complete;
    private Date dateCreated;
    private Date dateOfTask;
    private String note;
    public Task(String inName, Date inDateOfTask)
    {
        name = inName;
        complete = false;
        dateOfTask = inDateOfTask;
        dateCreated = new Date();
        note = "";
    }
    public Task(Task task)
    {
        name = task.getName();
        complete = task.isComplete();
        dateOfTask = task.getDateOfTask();
        dateCreated = task.getDateCreated();
        note = task.getNote();
    }
    public String getName()
    {
        return name;
    }
    public boolean isComplete()
    {
        return complete;
    }
    public Date getDateCreated()
    {
        return dateCreated;
    }
    public Date getDateOfTask()
    {
        return dateOfTask;
    }
    public String getNote()
    {
        return note;
    }
    public void setName(String inName)
    {
        name = inName;
    }
    public void setComplete(boolean completionStatus)
    {
        complete = completionStatus;
    }
    public void setDateOfTask(Date inDate)
    {
        dateOfTask = inDate;
    }
    public void setNote(String inNote)
    {
        note = inNote;
    }
    public int compareByName(Task task)
    {
        return name.toLowerCase().compareTo(task.getName().toLowerCase());
    }
    public boolean after(Task task)
    {
        return dateOfTask.after(task.getDateOfTask());
    }
    public boolean createdAfter(Task task)
    {
        return dateCreated.after(task.getDateCreated());
    }
    public String toString()
    {
        return name;
    }
}
