package com.example.courseworkadmin.ui.helpers;

import android.util.Log;
import com.example.courseworkadmin.ui.models.ClassInstance;
import com.example.courseworkadmin.ui.models.YogaClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private static final String COLLECTION_YOGA_CLASSES = "yoga_classes";
    private static final String CLASS_INSTANCES_COLLECTION = "class_instances";
    private final FirebaseFirestore firestore;

    public FirebaseHelper() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Add a new YogaClass to Firestore
    public void addYogaClass(YogaClass yogaClass, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference newDocRef = firestore.collection(COLLECTION_YOGA_CLASSES).document(String.valueOf(yogaClass.getId()));
        Map<String, Object> yogaClassMap = toMap(yogaClass);
        newDocRef.set(yogaClassMap).addOnCompleteListener(onCompleteListener);
    }

    // Update an existing YogaClass in Firestore
    public void updateYogaClass(YogaClass yogaClass, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference docRef = firestore.collection(COLLECTION_YOGA_CLASSES).document(String.valueOf(yogaClass.getId()));
        Map<String, Object> yogaClassMap = toMap(yogaClass);
        docRef.set(yogaClassMap).addOnCompleteListener(onCompleteListener);
    }

    // Delete a YogaClass from Firestore
    public void deleteYogaClass(int id, OnCompleteListener<Void> onCompleteListener) {
        // First, find and delete associated class instances
        firestore.collection(CLASS_INSTANCES_COLLECTION)
                .whereEqualTo("course_id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = firestore.batch();
                        for (DocumentSnapshot document : task.getResult()) {
                            batch.delete(document.getReference()); // Delete each class instance
                        }
                        // Commit the batch for class instance deletion
                        batch.commit().addOnCompleteListener(instanceDeletionTask -> {
                            if (instanceDeletionTask.isSuccessful()) {
                                // After class instances are deleted, delete the yoga class
                                firestore.collection(COLLECTION_YOGA_CLASSES)
                                        .document(String.valueOf(id))
                                        .delete()
                                        .addOnCompleteListener(onCompleteListener);
                            } else {
                                Log.e("FirebaseHelper", "Failed to delete class instances", instanceDeletionTask.getException());
                            }
                        });
                    } else {
                        Log.e("FirebaseHelper", "Failed to find class instances for deletion", task.getException());
                    }
                });
    }

    // Get a list of all YogaClasses from Firestore
    public void getAllYogaClasses(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        CollectionReference collectionRef = firestore.collection(COLLECTION_YOGA_CLASSES);
        collectionRef.get().addOnCompleteListener(onCompleteListener);
    }

    // Clear all yoga classes and their associated class instances
    public void clearAllClasses(OnCompleteListener<Void> onCompleteListener) {
        // First, delete all class instances
        firestore.collection(CLASS_INSTANCES_COLLECTION)
                .get()
                .addOnCompleteListener(instanceTask -> {
                    if (instanceTask.isSuccessful()) {
                        WriteBatch batch = firestore.batch();
                        for (DocumentSnapshot document : instanceTask.getResult()) {
                            batch.delete(document.getReference()); // Delete each class instance
                        }
                        // Commit the batch for class instance deletion
                        batch.commit().addOnCompleteListener(instanceDeletionTask -> {
                            if (instanceDeletionTask.isSuccessful()) {
                                // After class instances are deleted, delete all yoga classes
                                firestore.collection(COLLECTION_YOGA_CLASSES)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                WriteBatch classBatch = firestore.batch();
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    classBatch.delete(document.getReference()); // Delete each yoga class
                                                }
                                                // Commit the batch for yoga class deletion
                                                classBatch.commit().addOnCompleteListener(onCompleteListener);
                                            } else {
                                                Log.e("FirebaseHelper", "Failed to get yoga classes for deletion", task.getException());
                                            }
                                        });
                            } else {
                                Log.e("FirebaseHelper", "Failed to delete class instances", instanceDeletionTask.getException());
                            }
                        });
                    } else {
                        Log.e("FirebaseHelper", "Failed to get class instances for deletion", instanceTask.getException());
                    }
                });
    }


    // Convert a YogaClass object to a Map for Firestore
    private Map<String, Object> toMap(YogaClass yogaClass) {
        Map<String, Object> yogaClassMap = new HashMap<>();
        yogaClassMap.put("id", yogaClass.getId());
        yogaClassMap.put("day_of_week", yogaClass.getDayOfWeek());
        yogaClassMap.put("time_of_course", yogaClass.getTimeOfCourse());
        yogaClassMap.put("capacity", yogaClass.getCapacity());
        yogaClassMap.put("duration", yogaClass.getDuration());
        yogaClassMap.put("price", yogaClass.getPrice());
        yogaClassMap.put("type_of_class", yogaClass.getTypeOfClass());
        yogaClassMap.put("description", yogaClass.getDescription());
        yogaClassMap.put("title", yogaClass.getTitle());
        return yogaClassMap;
    }

    public YogaClass documentToYogaClass(DocumentSnapshot document) {
        YogaClass yogaClass = new YogaClass();
        yogaClass.setId(document.getLong("id").intValue());
        yogaClass.setDayOfWeek(document.getString("day_of_week"));
        yogaClass.setTimeOfCourse(document.getString("time_of_course"));
        yogaClass.setCapacity(document.getLong("capacity").intValue());
        yogaClass.setDuration(document.getLong("duration").intValue());
        yogaClass.setPrice(document.getDouble("price"));
        yogaClass.setTypeOfClass(document.getString("type_of_class"));
        yogaClass.setDescription(document.getString("description"));
        yogaClass.setTitle(document.getString("title"));
        return yogaClass;
    }

    // Get a YogaClass by ID
    public void getYogaClassById(int id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        firestore.collection(COLLECTION_YOGA_CLASSES)
                .document(String.valueOf(id))
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    // Add a class instance to Firestore
    public void addClassInstance(ClassInstance classInstance, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference newDocRef = firestore.collection(CLASS_INSTANCES_COLLECTION).document(String.valueOf(classInstance.getInstanceId()));
        Map<String, Object> classInstanceMap = toMap(classInstance);
        newDocRef.set(classInstanceMap).addOnCompleteListener(onCompleteListener);
    }

    // Get all class instances from Firestore
    public void getAllClassInstances(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        firestore.collection(CLASS_INSTANCES_COLLECTION)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    // Update a class instance in Firestore
    public void updateClassInstance(ClassInstance classInstance, OnCompleteListener<Void> onCompleteListener) {

        DocumentReference docRef = firestore.collection(CLASS_INSTANCES_COLLECTION).document(String.valueOf(classInstance.getInstanceId()));
        Map<String, Object> classInstanceMap = toMap(classInstance);
        docRef.set(classInstanceMap).addOnCompleteListener(onCompleteListener);
    }

    // Delete a class instance by ID in Firestore
    public void deleteClassInstance(String instanceId, OnCompleteListener<Void> listener) {
        // Your Firestore delete logic here
        FirebaseFirestore.getInstance().collection("classInstances").document(instanceId)
                .delete()
                .addOnCompleteListener(listener); // Pass the listener directly
    }

    // Search class instances by teacher name
    public void searchClassInstancesByTeacher(String teacherName, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        firestore.collection(CLASS_INSTANCES_COLLECTION)
                .whereEqualTo("teacher", teacherName)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void deleteAllClassInstances(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        firestore.collection(CLASS_INSTANCES_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        onSuccessListener.onSuccess(null); // Notify success with null
                    } else {
                        onFailureListener.onFailure(task.getException()); // Notify failure
                    }
                });
    }


    private Map<String, Object> toMap(ClassInstance classInstance) {
        Map<String, Object> classInstanceMap = new HashMap<>();
        classInstanceMap.put("instance_id", classInstance.getInstanceId());
        classInstanceMap.put("course_id", classInstance.getCourseId());
        classInstanceMap.put("date", classInstance.getDate());
        classInstanceMap.put("teacher", classInstance.getTeacher());
        classInstanceMap.put("comments", classInstance.getComments());
        return classInstanceMap;
    }

    // Convert Firestore DocumentSnapshot to ClassInstance
    public ClassInstance documentToClassInstance(DocumentSnapshot document) {
        ClassInstance classInstance = new ClassInstance();
        classInstance.setInstanceId(document.getLong("instance_id").intValue());
        classInstance.setCourseId(document.getLong("course_id").intValue());
        classInstance.setDate(document.getString("date"));
        classInstance.setTeacher(document.getString("teacher"));
        classInstance.setComments(document.getString("comments"));
        return classInstance;
    }
}
