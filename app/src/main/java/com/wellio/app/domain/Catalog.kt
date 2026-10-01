package com.wellio.app.domain

object Catalog {
    private fun meal(name: String, minutes: Int, ingredients: String, steps: String, tags: String, cuisine: String = "Asian") =
        Meal(name, minutes, ingredients.split("|"), steps.split("|"), tags.split("|"), cuisine)
    val meals = listOf(
        meal("Banana & cinnamon oats", 10, "Oats|Banana|Water|Cinnamon", "Simmer oats in water until soft.|Slice banana over the oats and add cinnamon.", "Breakfast|Vegan", "Western"),
        meal("Apple overnight oats", 5, "Oats|Apple|Water|Chia seeds", "Mix oats, chia and water. Refrigerate overnight.|Top with chopped apple before serving.", "Breakfast|Vegan", "Western"),
        meal("Tofu morning scramble", 10, "Tofu|Spinach|Tomato|Olive oil", "Crumble tofu into a warm oiled pan.|Add vegetables and cook until tender.", "Breakfast|Vegan|Low carb|High protein|Vegetables"),
        meal("Egg & spinach plate", 10, "Eggs|Spinach|Tomato|Olive oil", "Cook eggs in a lightly oiled pan until fully set.|Wilt spinach and serve with sliced tomato.", "Breakfast|Vegetarian|Low carb|High protein|Vegetables", "Western"),
        meal("Fresh fruit bowl", 5, "Banana|Apple|Orange", "Wash and cut fruit.|Arrange in a bowl and enjoy.", "Snack|Vegan", "Anything"),
        meal("Cucumber & hummus", 5, "Cucumber|Chickpeas|Olive oil|Lemon", "Mash cooked chickpeas with olive oil and lemon.|Serve with cucumber sticks.", "Snack|Vegan|Low carb|Vegetables", "Western"),
        meal("Edamame cup", 8, "Edamame|Water|Lemon", "Boil edamame until tender and drain.|Finish with a squeeze of lemon.", "Snack|Vegan|Low carb|High protein", "Japanese"),
        meal("Yogurt & apple", 5, "Plain yogurt|Apple|Cinnamon", "Slice the apple.|Spoon yogurt into a bowl and add apple and cinnamon.", "Snack|Vegetarian|High protein", "Western"),
        meal("Tempeh rice bowl", 20, "Tempeh|Rice|Carrot|Green beans|Garlic|Olive oil", "Cook rice according to its package.|Pan-cook tempeh with garlic until golden.|Cook vegetables until tender and serve together.", "Main|Vegan|High protein|Vegetables", "Indonesian"),
        meal("Tahu & vegetable soup", 15, "Tofu|Carrot|Cabbage|Garlic|Water", "Simmer sliced vegetables and garlic in water.|Add tofu and simmer until everything is cooked through.", "Main|Vegan|Low carb|High protein|Vegetables", "Indonesian"),
        meal("Ginger tofu bowl", 15, "Tofu|Broccoli|Carrot|Ginger|Olive oil", "Cook tofu in a lightly oiled pan.|Add ginger and vegetables with a splash of water and cook until tender.", "Main|Vegan|Low carb|High protein|Vegetables", "Japanese"),
        meal("Chickpea garden salad", 10, "Cooked chickpeas|Lettuce|Cucumber|Tomato|Lemon|Olive oil", "Wash and chop the vegetables.|Toss with chickpeas, lemon and olive oil.", "Main|Vegan|Vegetables", "Western"),
        meal("Chicken vegetable bowl", 20, "Chicken|Rice|Broccoli|Carrot|Garlic|Olive oil", "Cook rice according to its package.|Cut chicken into small pieces and cook thoroughly.|Cook vegetables and combine in a bowl.", "Main|High protein|Vegetables", "Asian"),
        meal("Mild Korean tofu plate", 15, "Tofu|Cabbage|Carrot|Ginger|Sesame oil", "Pan-cook tofu until golden.|Sauté sliced vegetables with ginger and serve alongside tofu.", "Main|Vegan|Low carb|High protein|Vegetables", "Korean"),
        meal("Quick vegetable omelette", 10, "Eggs|Tomato|Spinach|Olive oil", "Whisk eggs and fold in chopped vegetables.|Cook in a lightly oiled pan until eggs are fully set.", "Main|Vegetarian|Low carb|High protein|Vegetables", "Western")
    )
}
