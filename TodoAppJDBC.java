import java.sql.*;
import java.util.Scanner;

public class TodoAppJDBC {

    static Scanner sc = new Scanner(System.in);
    static Connection conn;

    public static void main(String[] args) {

        try {
            // 1. Connect to MySQL
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb", "root", "Vamsi444#");
            System.out.println("Connected to MySQL successfully!");

            int choice;
            do {
                System.out.println("\n====== TO-DO LIST MENU ======");
                System.out.println("1. Add Task");
                System.out.println("2. View Tasks");
                System.out.println("3. Mark Task as Completed");
                System.out.println("4. Delete Task");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                while (!sc.hasNextInt()) {
                    System.out.println("Please enter a valid number!");
                    sc.next();
                }

                choice = sc.nextInt();
                sc.nextLine(); // clear buffer

                switch (choice) {
                    case 1 -> addTask();
                    case 2 -> viewTasks();
                    case 3 -> markTaskCompleted();
                    case 4 -> deleteTask();
                    case 5 -> System.out.println("Exiting... Thank You!");
                    default -> System.out.println("Invalid choice!");
                }

            } while (choice != 5);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Add Task
    public static void addTask() throws SQLException {
        System.out.print("Enter Task Title: ");
        String title = sc.nextLine();

        if (title.trim().isEmpty()) {
            System.out.println("Task title cannot be empty!");
            return;
        }

        String sql = "INSERT INTO tasks (title) VALUES (?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, title);
        pst.executeUpdate();
        System.out.println("Task Added Successfully!");
    }

    // View Tasks
    public static void viewTasks() throws SQLException {
        String sql = "SELECT * FROM tasks";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.println("\nYour Tasks:");
        boolean hasTasks = false;
        while (rs.next()) {
            hasTasks = true;
            int id = rs.getInt("id");
            String title = rs.getString("title");
            boolean completed = rs.getBoolean("is_completed");
            System.out.println(id + ". " + title + (completed ? " [Completed]" : " [Pending]"));
        }
        if (!hasTasks) System.out.println("No tasks available.");
    }

    // Mark Task Completed
    public static void markTaskCompleted() throws SQLException {
        viewTasks();
        System.out.print("Enter task id to mark completed: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "UPDATE tasks SET is_completed = TRUE WHERE id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, id);
        int rows = pst.executeUpdate();
        if (rows > 0) System.out.println("Task Marked as Completed!");
        else System.out.println("Invalid Task ID!");
    }

    // Delete Task
    public static void deleteTask() throws SQLException {
        viewTasks();
        System.out.print("Enter task id to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM tasks WHERE id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, id);
        int rows = pst.executeUpdate();
        if (rows > 0) System.out.println("Task Deleted Successfully!");
        else System.out.println("Invalid Task ID!");
    }
}
