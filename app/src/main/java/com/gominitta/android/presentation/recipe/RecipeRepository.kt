package com.gominitta.android.presentation.recipe

/**
 * 마음 레시피 데이터 소스를 추상화하는 Repository.
 *
 * 지금은 더미데이터를 반환하는 구현체(DummyRecipeRepository)만 존재한다.
 * 나중에 실제 API 연동 시 이 interface를 구현하는 RemoteRecipeRepository 등으로 교체하면,
 * ViewModel/UI 코드는 손대지 않아도 된다.
 */
interface RecipeRepository {
    fun getRecipes(): List<RecipeItem>
    fun getRecommendedRecipes(): List<RecommendedRecipe>
    fun addRecipe(recipe: RecipeItem)
    fun updateRecipe(recipe: RecipeItem)
    fun deleteRecipe(recipeId: Long)
}

/**
 * MVP 단계용 더미 Repository.
 *
 * 기존에 정의되어 있던 sampleRecipes / sampleRecommendedRecipes를 그대로 재사용한다.
 * (화면 default param에서 쓰는 값과 동일한 소스를 공유 — UI 쪽 코드 변경 없음)
 */
class DummyRecipeRepository : RecipeRepository {

    private val recipes = sampleRecipes.toMutableList()

    override fun getRecipes(): List<RecipeItem> = recipes.toList()

    override fun getRecommendedRecipes(): List<RecommendedRecipe> = sampleRecommendedRecipes

    override fun addRecipe(recipe: RecipeItem) {
        recipes.add(recipe)
    }

    override fun updateRecipe(recipe: RecipeItem) {
        val index = recipes.indexOfFirst { it.id == recipe.id }
        if (index != -1) {
            recipes[index] = recipe
        }
    }

    override fun deleteRecipe(recipeId: Long) {
        recipes.removeAll { it.id == recipeId }
    }
}