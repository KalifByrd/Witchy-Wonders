package com.toxicteddie.witchywonders.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class MortarAndPestleRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;

    public MortarAndPestleRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.output = output;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        // Create a list of remaining ingredients to match
        List<Ingredient> remainingIngredients = new ArrayList<>(ingredients);

        // Check each slot in the container
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stackInSlot = container.getItem(i);
            if (!stackInSlot.isEmpty()) {
                boolean matched = false;
                Iterator<Ingredient> iterator = remainingIngredients.iterator();

                // Try to match the stack in the slot with any remaining ingredient
                while (iterator.hasNext()) {
                    Ingredient ingredient = iterator.next();
                    if (ingredient.test(stackInSlot)) {
                        matched = true;
                        iterator.remove();
                        break;
                    }
                }

                // If no match is found, return false
                if (!matched) {
                    return false;
                }
            }
        }

        // All items must be matched
        return remainingIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        //return width * height >= ingredients.size();
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        //return ModRecipes.MORTAR_AND_PESTLE_SERIALIZER.get();
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
    public static class Type implements RecipeType<MortarAndPestleRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "item_grounding";
    }
    public static class Serializer implements RecipeSerializer<MortarAndPestleRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(WitchyWonders.MODID, "item_grounding");
        @Override
        public MortarAndPestleRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY); // problem 1 but what if 3 ingredients.

            for(int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new MortarAndPestleRecipe(pRecipeId, output, inputs);
    }
    // @Override
    // public NonNullList<Ingredient> getIngredients() {
    //     return ingredients;
    // }

    // @Override
    // public ItemStack getToastSymbol() {
    //     return new ItemStack(WitchyWonders.MORTAR_AND_PESTLE_ITEM.get());
    // }
        @Override
        public @Nullable MortarAndPestleRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);

            for(int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack output = pBuffer.readItem();
            return new MortarAndPestleRecipe(pRecipeId, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MortarAndPestleRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.ingredients.size());

            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }
}
