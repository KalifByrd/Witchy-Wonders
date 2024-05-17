// package com.toxicteddie.witchywonders.recipe;

// import com.google.gson.JsonArray;
// import com.google.gson.JsonObject;
// import com.google.gson.JsonParseException;
// import net.minecraft.network.FriendlyByteBuf;
// import net.minecraft.resources.ResourceLocation;
// import net.minecraft.util.GsonHelper;
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.crafting.Ingredient;
// import net.minecraft.world.item.crafting.RecipeSerializer;
// import net.minecraft.world.item.crafting.ShapedRecipe;
// import net.minecraft.core.NonNullList;

// public class MortarAndPestleRecipeSerializer implements RecipeSerializer<MortarAndPestleRecipe> {

//     @Override
//     public MortarAndPestleRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
//         JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
//         NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientsJson.size(), Ingredient.EMPTY);

//         if (ingredientsJson.size() > 3) {
//             throw new JsonParseException("The number of ingredients must be 3 or fewer");
//         }

//         for (int i = 0; i < ingredientsJson.size(); i++) {
//             ingredients.set(i, Ingredient.fromJson(ingredientsJson.get(i)));
//         }

//         ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

//         return new MortarAndPestleRecipe(recipeId, result, ingredients);
//     }

//     @Override
//     public MortarAndPestleRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
//         int size = buffer.readVarInt();
//         NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);

//         for (int i = 0; i < size; i++) {
//             ingredients.set(i, Ingredient.fromNetwork(buffer));
//         }

//         ItemStack result = buffer.readItem();

//         return new MortarAndPestleRecipe(recipeId, result, ingredients);
//     }

//     @Override
//     public void toNetwork(FriendlyByteBuf buffer, MortarAndPestleRecipe recipe) {
//         buffer.writeVarInt(recipe.getIngredients().size());
//         for (Ingredient ingredient : recipe.getIngredients()) {
//             ingredient.toNetwork(buffer);
//         }
//         buffer.writeItem(recipe.getResultItem(null));
//     }
// }
