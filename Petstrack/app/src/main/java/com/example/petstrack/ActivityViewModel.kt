package com.example.petstrack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar

class ActivityViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val _activities = MutableLiveData<List<ActivityLog>>()
    val activities: LiveData<List<ActivityLog>> = _activities

    private val _weeklyProgress = MutableLiveData<Pair<Int, Int>>() // Pair(completed, total)
    val weeklyProgress: LiveData<Pair<Int, Int>> = _weeklyProgress

    fun fetchActivities() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("activities")
            .whereEqualTo("userId", userId)
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                
                val list = value?.map { doc ->
                    ActivityLog(
                        id = doc.id,
                        petId = doc.getString("petId") ?: "",
                        petName = doc.getString("petName") ?: "",
                        type = doc.getString("type") ?: "",
                        dateTime = doc.getLong("dateTime") ?: 0L,
                        note = doc.getString("note") ?: ""
                    )
                } ?: emptyList()
                _activities.value = list
            }
        
        calculateWeeklyChecklistProgress()
    }

    private fun calculateWeeklyChecklistProgress() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("checklists")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                
                var totalTasks = 0
                var completedTasks = 0
                
                value?.documents?.forEach { doc ->
                    val items = doc.get("items") as? List<Map<String, Any>>
                    items?.forEach { item ->
                        totalTasks++
                        if (item["isChecked"] == true) {
                            completedTasks++
                        }
                    }
                }
                
                _weeklyProgress.value = Pair(completedTasks, totalTasks)
            }
    }

    fun addActivity(log: ActivityLog) {
        val userId = auth.currentUser?.uid ?: return
        val data = hashMapOf(
            "petId" to log.petId,
            "petName" to log.petName,
            "type" to log.type,
            "dateTime" to log.dateTime,
            "note" to log.note,
            "userId" to userId
        )
        db.collection("activities").add(data)
    }

    fun deleteActivity(logId: String) {
        db.collection("activities").document(logId).delete()
    }
}