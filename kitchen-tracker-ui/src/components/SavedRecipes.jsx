import PropTypes from "prop-types";
import RecipeCard from "./RecipeCard";
import { recipeShape } from "./recipeShape";

function SavedRecipes({ recipes, loading, onDelete }) {
  if (loading) return <p className="empty">Loading saved recipes...</p>;
  if (recipes.length === 0) {
    return <p className="empty">No saved recipes yet.</p>;
  }

  return (
    <div className="saved-recipes">
      {recipes.map((recipe) => (
        <RecipeCard key={recipe.id} recipe={recipe} onDelete={onDelete} />
      ))}
    </div>
  );
}

SavedRecipes.propTypes = {
  recipes: PropTypes.arrayOf(PropTypes.shape(recipeShape)).isRequired,
  loading: PropTypes.bool.isRequired,
  onDelete: PropTypes.func.isRequired,
};

export default SavedRecipes;
