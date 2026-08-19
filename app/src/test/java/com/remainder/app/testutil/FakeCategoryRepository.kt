package com.remainder.app.testutil

import com.remainder.app.domain.model.Category
import com.remainder.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCategoryRepository(initial: List<Category> = emptyList()) : CategoryRepository {

    private val categoriesFlow = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

    override fun getAllCategories(): Flow<List<Category>> = categoriesFlow

    override fun getCategoryById(id: Long): Flow<Category?> =
        categoriesFlow.map { list -> list.find { it.id == id } }

    override suspend fun addCategory(category: Category): Long {
        val id = nextId++
        categoriesFlow.value = categoriesFlow.value + category.copy(id = id)
        return id
    }

    override suspend fun updateCategory(category: Category) {
        categoriesFlow.value = categoriesFlow.value.map { if (it.id == category.id) category else it }
    }

    override suspend fun deleteCategory(category: Category) {
        categoriesFlow.value = categoriesFlow.value.filterNot { it.id == category.id }
    }
}
