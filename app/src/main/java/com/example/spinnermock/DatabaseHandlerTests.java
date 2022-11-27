package com.example.spinnermock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseHandlerTests {
    public static void run() {
        createSampleCourses();
        createSampleUsers();
    }
    // Test
    private static void createSampleCourses() {
        // public Course(String code, String name, Map<String, boolean> sessions, List<String> prerequisites) {
        // Larger size requires Map.ofEntries( entry(k,v), ... )
        ArrayList<Course> courses = new ArrayList<>();

        courses.add(new Course(
                "CSCC24",
                "Principles of Programming Languages",
                Map.of(0, true, 1, true, 2, false),
                List.of("CSCB07", "CSCB09"))
        );

        courses.add(new Course(
                "CSCB07",
                "Software Design",
                Map.of(0, false, 1, true, 2, true),
                List.of("CSCA48"))
        );

        courses.add(new Course(
                "CSCB09",
                "Software Tools and Systems Programming",
                Map.of(0, true, 1, true, 2, false),
                List.of("CSCA48"))
        );

        courses.add(new Course(
                "CSCA48",
                "Introduction to Compute Science II",
                Map.of(0, true,1, true, 2, false),
                List.of("CSCA08"))
        );

        courses.add(new Course(
                "CSCA08",
                "Introduction to Compute Science I",
                Map.of(0, true, 1, false, 2, true),
                List.of())
        );

        courses.add(new Course(
                "CSCC63",
                "Computability and Computational Complexity",
                Map.of(0, true, 1, false, 2, true),
                List.of("CSCB63", "CSCB36"))
        );

        courses.add(new Course(
                "CSCB63",
                "Design and Analysis of Data Structures",
                Map.of(0, true, 1, true, 2, false),
                List.of("CSCB36"))
        );

        courses.add(new Course(
                "CSCB36",
                "Introduction to the Theory of Computation",
                Map.of(0, false, 1, true, 2, true),
                List.of("CSCA48", "CSCA67"))
        );

        courses.add(new Course( // Problem course as there exists MATA67
                "CSCA67",
                "Discrete Mathematics",
                Map.of(0, false, 1, true, 2, true),
                List.of())
        );

        for (Course course : courses) DatabaseHandler.addCourse(course);

    }

    // Test
    private static void createSampleUsers() {
        ArrayList<User> users = new ArrayList<>();

        Student s = new Student(
                "nobeans@mail.utoronto.ca",
                "0beans"
        );

        s.addCourse(List.of("MATA41",  "CSCB36",  "CSCA08"));
        users.add(s);

        s = new Student(
                "lilwayne@mail.utoronto.ca",
                "wane"
        );

        s.addCourse(List.of());
        users.add(s);

        users.add(new User(
                "admin@mail.utoronto.ca",
                "imAProfessor",
                true
        ));

        for (User user : users) DatabaseHandler.addUser(user);
    }
}
