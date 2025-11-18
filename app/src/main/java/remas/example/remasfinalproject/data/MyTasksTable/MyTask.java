package remas.example.remasfinalproject.data.MyTasksTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "myTasks")
    public class MyTask
{
    @PrimaryKey(autoGenerate = true)
    public long keyId;
    public int importance;
    public String shortTitle;
    public String text;
    public long time;
    public boolean isCompleted;
    public long subjId;
    public long userId;

}

