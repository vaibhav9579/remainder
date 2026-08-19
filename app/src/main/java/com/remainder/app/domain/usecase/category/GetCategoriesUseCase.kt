package com.remainder.app.domain.usecase.category

import com.remainder.app.domain.model.Category
import com.remainder.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(): Flow<List<Category>> = categoryRepository.getAllCategories()
}
