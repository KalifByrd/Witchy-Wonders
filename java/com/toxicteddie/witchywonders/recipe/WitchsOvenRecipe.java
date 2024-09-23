package com.toxicteddie.witchywonders.recipe;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.toxicteddie.witchywonders.WitchyWonders;

public class WitchsOvenRecipe implements Recipe<SimpleContainer> {
    private static final Logger LOGGER = WitchyWonders.LOGGER;
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;

    public WitchsOvenRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.output = output;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide()) {
            LOGGER.debug("Recipe matching on client side, returning false.");
            return false;
        }

        List<Ingredient> remainingIngredients = new ArrayList<>(ingredients);
        LOGGER.debug("Matching recipe with id {}. Ingredients: {}", id, ingredients);

        for (int i = 0; i < 3; i++) {
            ItemStack stackInSlot = container.getItem(i);
            LOGGER.debug("Checking slot {}: {}", i, stackInSlot);

            if (!stackInSlot.isEmpty()) {
                boolean matched = false;
                Iterator<Ingredient> iterator = remainingIngredients.iterator();

                while (iterator.hasNext()) {
                    Ingredient ingredient = iterator.next();
                    LOGGER.debug("Testing ingredient: {}", ingredient);
                    if (ingredient.test(stackInSlot)) {
                        matched = true;
                        iterator.remove();
                        LOGGER.debug("Ingredient matched: {}", stackInSlot);
                        break;
                    }
                }

                if (!matched) {
                    LOGGER.debug("No matching ingredient found for slot {}, item: {}", i, stackInSlot);
                    return false;
                }
            }
        }

        boolean isMatch = remainingIngredients.isEmpty();
        LOGGER.debug("Remaining ingredients after matching: {}", remainingIngredients);
        LOGGER.debug("Recipe match result: {}", isMatch);
        return isMatch;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // return width * height >= ingredients.size();
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
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<WitchsOvenRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "oil_infusing";
    }

    public static class Serializer implements RecipeSerializer<WitchsOvenRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(WitchyWonders.MODID, "oil_infusing");

        @Override
        public WitchsOvenRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonObject resultObject = GsonHelper.getAsJsonObject(pSerializedRecipe, "result");
            ItemStack output = ShapedRecipe.itemStackFromJson(resultObject);

            if (resultObject.has("nbt")) {
                try {
                    CompoundTag nbt = TagParser.parseTag(GsonHelper.getAsString(resultObject, "nbt"));
                    output.setTag(nbt);
                } catch (Exception e) {
                    WitchyWonders.LOGGER.error("Failed to parse NBT for result in recipe {}", pRecipeId, e);
                }
            }

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            LOGGER.debug("Deserialized recipe {}: output={}, ingredients={}", pRecipeId, output, inputs);
            return new WitchsOvenRecipe(pRecipeId, output, inputs);
        }

        @Override
        public @Nullable WitchsOvenRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack output = pBuffer.readItem();
            if (pBuffer.readBoolean()) {
                output.setTag(pBuffer.readNbt());
            }

            LOGGER.debug("Deserialized recipe from network {}: output={}, ingredients={}", pRecipeId, output, inputs);
            return new WitchsOvenRecipe(pRecipeId, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, WitchsOvenRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.ingredients.size());

            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
            pBuffer.writeBoolean(pRecipe.getResultItem(null).hasTag());
            if (pRecipe.getResultItem(null).hasTag()) {
                pBuffer.writeNbt(pRecipe.getResultItem(null).getTag());
            }

            LOGGER.debug("Serialized recipe to network {}: output={}, ingredients={}", pRecipe.getId(), pRecipe.output, pRecipe.ingredients);
        }
    }
}
