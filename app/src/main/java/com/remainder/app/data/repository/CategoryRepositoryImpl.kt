package com.remainder.app.data.repository

import com.remainder.app.data.local.dao.CategoryDao
import com.remainder.app.data.local.entity.toDomain
import com.remainder.app.data.local.entity.toEntity
import com.remainder.app.domain.model.Category
import com.remainder.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { entities -> entities.map { it.toDomain() } }

    override fun getCategoryById(id: Long): Flow<Category?> =
        categoryDao.getCategoryById(id).map { it?.toDomain() }

    override suspend fun addCategory(category: Category): Long = categoryDao.insert(category.toEntity())

    override suspend fun updateCategory(category: Category) = categoryDao.update(category.toEntity())

    override suspend fun deleteCategory(category: Category) = categoryDao.delete(category.toEntity())
}
