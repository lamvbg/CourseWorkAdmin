package com.example.courseworkadmin.ui.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;
import com.example.courseworkadmin.ui.models.ClassInstance;
import com.example.courseworkadmin.ui.models.YogaClass;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yoga_classes.db";
    private static final int DATABASE_VERSION = 4;

    // Yoga classes table
    private static final String TABLE_YOGA_CLASSES = "yoga_classes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    private static final String COLUMN_TIME_OF_COURSE = "time_of_course";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_TYPE_OF_CLASS = "type_of_class";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TITLE = "title";

    // Class instances table (specific class occurrences)
    private static final String TABLE_CLASS_INSTANCES = "class_instances";
    private static final String COLUMN_INSTANCE_ID = "instance_id";
    private static final String COLUMN_COURSE_ID = "course_id";  // Foreign key to yoga_classes
    private static final String COLUMN_DATE = "date";  // Specific date for the class
    private static final String COLUMN_TEACHER = "teacher";  // Teacher for the class
    private static final String COLUMN_COMMENTS = "comments";  // Optional comments

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create yoga_classes table
        String createYogaClassesTable = "CREATE TABLE " + TABLE_YOGA_CLASSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_OF_WEEK + " TEXT NOT NULL, " +
                COLUMN_TIME_OF_COURSE + " TEXT NOT NULL, " +
                COLUMN_CAPACITY + " INTEGER NOT NULL, " +
                COLUMN_DURATION + " INTEGER NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_TYPE_OF_CLASS + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_TITLE + " TEXT NOT NULL)";
        db.execSQL(createYogaClassesTable);

        // Create class_instances table
        String createClassInstancesTable = "CREATE TABLE " + TABLE_CLASS_INSTANCES + " (" +
                COLUMN_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COURSE_ID + " INTEGER NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TEACHER + " TEXT NOT NULL, " +
                COLUMN_COMMENTS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_COURSE_ID + ") REFERENCES " + TABLE_YOGA_CLASSES + "(" + COLUMN_ID + "))";
        db.execSQL(createClassInstancesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_YOGA_CLASSES);
        onCreate(db);
    }

    // Open the database
    public void openDatabase() {
        this.getWritableDatabase();
    }

    @SuppressLint("Range")
    public YogaClass getYogaClassById(int id) {
        YogaClass yogaClass = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_YOGA_CLASSES, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            yogaClass = new YogaClass(
                    cursor.getString(cursor.getColumnIndex(COLUMN_DAY_OF_WEEK)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TIME_OF_COURSE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TYPE_OF_CLASS)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            );
            yogaClass.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        }
        if (cursor != null) {
            cursor.close();
        }
        return yogaClass;
    }

    // Method to add a yoga course
    public long addYogaClass(YogaClass yogaClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, yogaClass.getDayOfWeek());
        values.put(COLUMN_TIME_OF_COURSE, yogaClass.getTimeOfCourse());
        values.put(COLUMN_CAPACITY, yogaClass.getCapacity());
        values.put(COLUMN_DURATION, yogaClass.getDuration());
        values.put(COLUMN_PRICE, yogaClass.getPrice());
        values.put(COLUMN_TYPE_OF_CLASS, yogaClass.getTypeOfClass());
        values.put(COLUMN_DESCRIPTION, yogaClass.getDescription());
        values.put(COLUMN_TITLE, yogaClass.getTitle());
        long id = db.insert(TABLE_YOGA_CLASSES, null, values);
        if (id != -1) {
            yogaClass.setId((int) id);
        }
        return id;
    }


    // Method to get all yoga classes
    @SuppressLint("Range")
    public List<YogaClass> getAllYogaClasses() {
        List<YogaClass> yogaClasses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_YOGA_CLASSES, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    YogaClass yogaClass = new YogaClass(
                            cursor.getString(cursor.getColumnIndex(COLUMN_DAY_OF_WEEK)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_TIME_OF_COURSE)),
                            cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)),
                            cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_TYPE_OF_CLASS)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    );
                    yogaClass.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    yogaClasses.add(yogaClass);
                } while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
        return yogaClasses;
    }

    @SuppressLint("Range")
    public List<ClassInstance> getAllClassInstances() {
        List<ClassInstance> classInstances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASS_INSTANCES, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ClassInstance classInstance = new ClassInstance(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_COMMENTS))
                );
                classInstance.setInstanceId(cursor.getInt(cursor.getColumnIndex(COLUMN_INSTANCE_ID))); // Set instance ID
                classInstances.add(classInstance);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return classInstances;
    }

    public int updateYogaClass(YogaClass yogaClass, int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Check for null values before putting them in ContentValues
        if (yogaClass.getDayOfWeek() != null) {
            values.put(COLUMN_DAY_OF_WEEK, yogaClass.getDayOfWeek());
        }
        if (yogaClass.getTimeOfCourse() != null) {
            values.put(COLUMN_TIME_OF_COURSE, yogaClass.getTimeOfCourse());
        }
        values.put(COLUMN_CAPACITY, yogaClass.getCapacity());
        values.put(COLUMN_DURATION, yogaClass.getDuration());
        values.put(COLUMN_PRICE, yogaClass.getPrice());

        if (yogaClass.getTypeOfClass() != null) {
            values.put(COLUMN_TYPE_OF_CLASS, yogaClass.getTypeOfClass());
        }
        if (yogaClass.getDescription() != null) {
            values.put(COLUMN_DESCRIPTION, yogaClass.getDescription());
        }
        if (yogaClass.getTitle() != null) {
            values.put(COLUMN_TITLE, yogaClass.getTitle());
        }

        // Logging for debugging
        Log.d("Database Update", "Updating YogaClass with ID: " + courseId + " with values: " + values.toString());

        return db.update(TABLE_YOGA_CLASSES, values, COLUMN_ID + "=?", new String[]{String.valueOf(courseId)});
    }



    // Method to add a class instance
    public long addClassInstance(ClassInstance classInstance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_ID, classInstance.getCourseId());
        values.put(COLUMN_DATE, classInstance.getDate());
        values.put(COLUMN_TEACHER, classInstance.getTeacher());
        values.put(COLUMN_COMMENTS, classInstance.getComments());
        return db.insert(TABLE_CLASS_INSTANCES, null, values);
    }

    // Method to update a class instance
    public int updateClassInstance(ClassInstance classInstance, int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_ID, classInstance.getCourseId());
        values.put(COLUMN_DATE, classInstance.getDate());
        values.put(COLUMN_TEACHER, classInstance.getTeacher());
        values.put(COLUMN_COMMENTS, classInstance.getComments());

        // Update the class instance with the specific instance ID
        return db.update(TABLE_CLASS_INSTANCES, values, COLUMN_INSTANCE_ID + "=?", new String[]{String.valueOf(instanceId)});
    }

    public int deleteYogaClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, delete all class instances associated with this yoga class
        db.delete(TABLE_CLASS_INSTANCES, COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(id)});

        // Then, delete the yoga class itself
        return db.delete(TABLE_YOGA_CLASSES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteClassInstance(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CLASS_INSTANCES, COLUMN_INSTANCE_ID + "=?", new String[]{String.valueOf(instanceId)});
    }

    // Method to clear all classes
    public void clearAllClasses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_INSTANCES, null, null);
        db.delete(TABLE_YOGA_CLASSES, null, null);
        db.close();
    }

    public void clearAllClassInstances() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_INSTANCES, null, null); // Delete all records from class_instances table
        db.close();
    }

    public List<ClassInstance> searchClassesByTeacher(String teacherName) {
        List<ClassInstance> classInstances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASS_INSTANCES + " WHERE " + COLUMN_TEACHER + " LIKE ?", new String[]{"%" + teacherName + "%"});
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") ClassInstance classInstance = new ClassInstance(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_COMMENTS))
                );
                classInstances.add(classInstance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classInstances;
    }
}