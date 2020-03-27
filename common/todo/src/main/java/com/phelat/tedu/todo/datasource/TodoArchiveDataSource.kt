package com.phelat.tedu.todo.datasource

import com.phelat.tedu.datasource.Deletable
import com.phelat.tedu.datasource.Readable
import com.phelat.tedu.dependencyinjection.scope.CommonScope
import com.phelat.tedu.mapper.Mapper
import com.phelat.tedu.todo.database.dao.TodoEntityDao
import com.phelat.tedu.todo.database.entity.TodoDatabaseEntity
import com.phelat.tedu.todo.entity.TodoEntity
import com.phelat.tedu.todo.type.ArchivableTodos
import java.util.Date
import javax.inject.Inject

@CommonScope
internal class TodoArchiveDataSource @Inject constructor(
    private val todoEntityDao: TodoEntityDao,
    private val mapper: Mapper<TodoDatabaseEntity, TodoEntity>
) : Readable.Suspendable.IO<Date, @JvmSuppressWildcards ArchivableTodos>,
    Deletable.Suspendable.IO<@JvmSuppressWildcards ArchivableTodos, Boolean> {

    override suspend fun read(input: Date): ArchivableTodos {
        return todoEntityDao.selectAllDoneTodosBefore(input)
            .map(mapper::mapFirstToSecond)
    }

    override suspend fun delete(input: ArchivableTodos): Boolean {
        val todoDatabaseEntities = input.map(mapper::mapSecondToFirst)
        val deletionResult = todoEntityDao.deleteTodos(todoDatabaseEntities)
        return deletionResult == 1
    }
}