/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 Carter I. Walker
 */

package ucf.assignments;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class GUIController
{

    @FXML
    private Button addButton;

    @FXML
    private Button clearButton;

    @FXML
    private MenuItem closeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button doneButton;

    @FXML
    private Button editButton;

    @FXML
    private MenuItem saveButton;

    @FXML
    private MenuItem loadButton;

    @FXML
    private TextField taskDescription;

    @FXML
    private DatePicker taskDueDate;

    @FXML
    private TextField taskName;

    @FXML
    private ListView<Task> tasksList;

    @FXML
    private MenuItem viewAllTasksButton;

    @FXML
    private MenuItem viewCompleteButton;

    @FXML
    private MenuItem viewOnlyIncompleteButton;

    @FXML
    ObservableList<Task> masterList = FXCollections.observableArrayList();

    @FXML
    FilteredList<Task> filteredList = new FilteredList<>(masterList, x -> true);

    @FXML
    void addTask(MouseEvent event)
    {
        //Necessary Variables
        String newTitle, newDesc, newDate;
        Boolean done = false;

        //Creating New Task from Field Boxes
        newTitle = taskName.getText();
        newDesc = taskDescription.getText();
        newDate = taskDueDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Creating and adding
        Task newTask = new Task(newTitle, newDesc, newDate, done);
        masterList.add(newTask);
    }

    @FXML
    void clearAllTasks(MouseEvent event)
    {
        int tasks = masterList.size();
        for (int i = 0; i < tasks; i++)
        {
            masterList.remove(0);
        }
    }

    @FXML
    void deleteTask(MouseEvent event)
    {
        int tasks = masterList.size();
        for (int i = 0; i < tasks; i++)
        {
            if (tasksList.getSelectionModel().getSelectedItem().title.equals(masterList.get(i).title))
            {
                masterList.remove(i);
                break;
            }
        }
    }

    @FXML
    void markAsDone(MouseEvent event)
    {
        //Necessary Variables
        String newTaskName, newTaskDesc, newTaskDate;
        Boolean done;

        //Checking if task is complete or not
        if (tasksList.getSelectionModel().getSelectedItem().complete)
        {
            newTaskName = tasksList.getSelectionModel().getSelectedItem().title;
            newTaskName = newTaskName.substring(0, newTaskName.length() - 8);
            done = false;
        }
        else
        {
            newTaskName = tasksList.getSelectionModel().getSelectedItem().title + " (Done) ";
            done = true;
        }

        //Creating new task
        newTaskDesc = tasksList.getSelectionModel().getSelectedItem().description;
        newTaskDate = taskDueDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Task newTask = new Task(newTaskName, newTaskDesc, newTaskDate, done);

        //Searching for task to overwrite
        int tasks = masterList.size();
        for (int i = 0; i < tasks; i++)
        {
            if (tasksList.getSelectionModel().getSelectedItem().title.equals(masterList.get(i).title))
            {
                masterList.set(i, newTask);
            }
        }
    }

    @FXML
    void saveToCSV(ActionEvent event) throws FileNotFoundException
    {
        //Creating file choosing window
        FileChooser fileWindow = new FileChooser();
        fileWindow.setTitle("Save Export");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)",  "*.csv");
        fileWindow.getExtensionFilters().add(extFilter);

        //Save file created
        File output = fileWindow.showSaveDialog(addButton.getScene().getWindow());

        //Necessary Variables
        PrintWriter fileStream = new PrintWriter(output);
        StringBuilder writer = new StringBuilder();

        //Unnecessary column identification
        writer.append("Title, Description, Due Date, Completed\n");

        //Loop to create values for every task
        for (int i = 0; i < masterList.size(); i++)
        {
            writer.append(masterList.get(i).title + ",");
            writer.append(masterList.get(i).description + ",");
            writer.append(masterList.get(i).dueDate + ",");
            writer.append(masterList.get(i).complete + "\n");
        }

        //Cleanup!
        fileStream.write(writer.toString());
        fileStream.close();


    }

    @FXML
    void loadFile(ActionEvent event) throws IOException
    {
        //Creating file choose window
        FileChooser fileWindow = new FileChooser();
        fileWindow.setTitle("Select CSV File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileWindow.getExtensionFilters().add(extFilter);

        //Creating file path for loading
        File input = fileWindow.showOpenDialog(addButton.getScene().getWindow());

        //Necessary Variables
        BufferedReader instream = Files.newBufferedReader(input.toPath());
        String line = instream.readLine();

        //Skip Column Identification
        line = instream.readLine();

        //While file is read, read in values and add to list.
        while (line != null)
        {
            String[] currentLine = line.split(",");
            Task newTask = new Task(currentLine[0], currentLine[1], currentLine[2], Boolean.valueOf(currentLine[3]));
            masterList.add(newTask);
            line = instream.readLine();
        }

    }

    @FXML
    void shutdown(ActionEvent event)
    {
        System.exit(0);
    }

    @FXML
    void updateTask(MouseEvent event)
    {
        ////Necessary Variables
        String newTaskName, newTaskDesc, newTaskDate;

        //Read in updated task information
        newTaskName = taskName.getText();
        newTaskDesc = taskDescription.getText();
        newTaskDate = taskDueDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Create new task to replace
        Task newTask = new Task(newTaskName, newTaskDesc, newTaskDate, tasksList.getSelectionModel().getSelectedItem().complete);

        //Search and add updated version
        int tasks = masterList.size();
        for (int i = 0; i < tasks; i++)
        {
            if (tasksList.getSelectionModel().getSelectedItem().title.equals(masterList.get(i).title))
            {
                masterList.set(i, newTask);
            }
        }
    }

    @FXML
    void viewAll(ActionEvent event)
    {
        filteredList.setPredicate(null);
    }

    @FXML
    void viewComplete(ActionEvent event)
    {
        Predicate<Task> complete = i -> i.complete;
        filteredList.setPredicate(complete);
    }

    @FXML
    void viewIncomplete(ActionEvent event)
    {
        Predicate<Task> notComplete = i -> !i.complete;
        filteredList.setPredicate(notComplete);
    }

    @FXML
    void initialize()
    {
        //Model to track changes and refresh task list
        tasksList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>()
        {
            @Override
            public void changed(ObservableValue<? extends Task> observable, Task oldValue, Task newValue)
            {
                String currentName,currentDesc,currentDate;
                currentName = tasksList.getSelectionModel().getSelectedItem().toString();
                currentDesc = tasksList.getSelectionModel().getSelectedItem().descToString();
                currentDate = tasksList.getSelectionModel().getSelectedItem().dateToString();

                taskName.setText(currentName);
                taskDescription.setText(currentDesc);
                taskDueDate.setValue(LocalDate.parse(currentDate));

                //Code to change button based on completion
                if (tasksList.getSelectionModel().getSelectedItem().complete == true)
                {
                    doneButton.setText("Unmark as Done");
                }
                else if (tasksList.getSelectionModel().getSelectedItem().complete == false)
                {
                    doneButton.setText("Mark as Done");
                }
            }
        });

        //Refresh changes with filter
        masterList.addListener(new ListChangeListener<Task>()
        {
            @Override
            public void onChanged(Change<? extends Task> c) {
                tasksList.setItems(filteredList);

            }

        });
    }
}
