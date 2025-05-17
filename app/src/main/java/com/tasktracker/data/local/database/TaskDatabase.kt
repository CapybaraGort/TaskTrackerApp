package com.tasktracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tasktracker.R
import com.tasktracker.data.converter.DateConverter
import com.tasktracker.data.local.dao.CategoryDao
import com.tasktracker.data.local.dao.TaskDao
import com.tasktracker.data.local.entity.CategoryEntity
import com.tasktracker.data.local.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(version = 6, entities = [TaskEntity::class, CategoryEntity::class])
@TypeConverters(DateConverter::class)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
    abstract fun getCategoryDao(): CategoryDao

}

class DatabaseCallback @Inject constructor(
    private val provider: Provider<TaskDatabase>,
    private val applicationScope: CoroutineScope,
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        applicationScope.launch {
            val db = provider.get()
            val categoryDao = db.getCategoryDao()

            if (categoryDao.getAllCategories().first().isEmpty()) {
                categoryDao.insertCategory(CategoryEntity(name = context.getString(R.string.home)))
                categoryDao.insertCategory(CategoryEntity(name = context.getString(R.string.work)))
            }
        }
    }
}