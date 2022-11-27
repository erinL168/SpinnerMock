package com.example.spinnermock;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.hash.Hashing;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Objects;

import kotlin.NotImplementedError;

/** This class connects directly to the firebase database
 *
 */
public abstract class DatabaseHandler {

    // Logcat Tag Name
    private static final String TAG = "DATABASE HANDLER";

    // Connect to the firebase emulator
    private static final boolean USE_EMULATOR = false;

    // The root of the database JSON
    private static DatabaseReference dbRootRef;
    private static DatabaseReference dbCoursesRef;
    private static DatabaseReference dbUsersRef;
    private static DatabaseReference dbStudentsRef;

    private static ValueEventListener listener;
    private static ChildEventListener onCourseAdded;

    private DatabaseHandler() {initialise();}

//    public DatabaseReference getCoursesWithFieldEqualTo(String fieldName, String value) {
//        return dbRootRef.child("course").orderByChild(fieldName).equalTo(value).getRef();
//    }

//    /**
//     * Returns the DatabaseReference pointing to the entire database
//     * @return DatabaseReference
//     */
//    public DatabaseReference getReference() {
//        return dbRootRef;
//    }

    /**
     * Converts plaintext string into hashed string
     * @param plain the input string
     * @return sha256 hashed input string
     */
    public static String hashString(String plain) {
        return Hashing.sha256().hashBytes(plain.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public static void getStudentData(String userId) {
        dbStudentsRef.equalTo(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Log.d("FUCK", "YEAH" + dataSnapshot.getValue());
                    }
                });
    }

    /**
     * Queries the database for a user whos email and password match the given input
     * @param email The email provided by the user
     * @param rawPassword The password provided by the user
     * @param callback A callback interface to call after events are triggered
     */
    public static void getUser(String email, String rawPassword, Listener callback) {
        String password = hashString(rawPassword);
        dbUsersRef.orderByChild("password").equalTo(password).getRef()
                .orderByChild("email").equalTo(email).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        // Check length
                        int i = (int) dataSnapshot.getChildrenCount();

                        //for (DataSnapshot d : dataSnapshot.getChildren()) i++;
                        if (i > 1 || i == 0) {callback.onFailure(null);}
                        else {
                            User instance;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                instance = child.getValue(User.class);
                                assert instance != null;
                                instance.setId(dataSnapshot.getKey());
                                callback.onSuccess(dataSnapshot.toString(),
                                        List.of(instance));
                                return;
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.toString());
                    }
                });
    }

    /**
     * Inserts into database.courses a new course.
     * @param course The object model of the course
     */
    public static void addCourse(Course course) {
        DatabaseReference id = dbCoursesRef.push();
        id.setValue(course)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {}
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Failure: " + e.toString());
                }
            });
    }

    public static void removeCourse(Course course) {
        throw new NotImplementedError();
    }

    /**
     * Inserts into database.students a new student. Called by addUser()
     * @param userId A foreign key to the corresponding database.users entry
     * @param student The object model of the student
     */
    private static void addStudent(String userId, Student student) {
       dbStudentsRef.child(userId).setValue(student._getCoursesTaken());
    }

    /**
     * Inserts into database.users a new user.
     * If the User was upcasted from Student, calls addStudent()
     * @param user The object model of the user
     */
    public static void addUser(User user) {
        DatabaseReference newUser = dbUsersRef.push();
        newUser.setValue(user);
        // Generate entry in student table
        if (user instanceof Student) {
            addStudent(newUser.getKey(), (Student) user);
        }
    }

    /**
     * Called on startup.
     * @param coursesRef
     */
    private static void attachCourseListener(DatabaseReference coursesRef) {
        if (onCourseAdded == null) onCourseAdded = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                Log.d(TAG, "Added Course: " + snapshot.toString());

                // TODO: Safety Checks
                Course result = snapshot.getValue(Course.class);
                assert result != null;
                result.setKey(snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {
                Log.d(TAG, "Child Changed: " + snapshot.toString() + "\n" + previousChildName);

                Course photo = snapshot.getValue(ExcludableCourse.class);
                Course.updateCourse(Objects.requireNonNull(photo), snapshot.getKey());//previousChildName);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Removed Course: " + snapshot.toString());
                // TODO: Safety Checks
                Course photo = snapshot.getValue(ExcludableCourse.class);
                Course.removeCourse(Objects.requireNonNull(photo));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                //Log.d(TAG, "Child Moved: " + snapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Cancelled? " + error.toString());
            }
        };
        coursesRef.addChildEventListener(onCourseAdded);
    }

    public static void initialise() {
        FirebaseDatabase database;
        if (USE_EMULATOR) { // Connect to the local database
            database = FirebaseDatabase.getInstance("https://utsc-b07-projcourses.firebaseio.com");
            database.useEmulator("10.0.2.2", 9000);
            Log.d(TAG, "Emulator Connected");
        } else { // Connect to the online database
            database = FirebaseDatabase.getInstance("https://utsc-b07-projcourses-default-rtdb.firebaseio.com");
        }
        // Assign references to the top-level tables
        dbRootRef = database.getReference();
        dbCoursesRef = dbRootRef.child("courses");
        dbUsersRef = dbRootRef.child("users");
        dbStudentsRef = dbRootRef.child("students");

        // Begin listening to new data provided
        attachCourseListener(dbCoursesRef.getRef());
        runTests();
    }

    public static void runTests() {
        Log.d("TEST", "Running Tests");
        dbRootRef.setValue(null);
        DatabaseHandlerTests.run();
    }
}