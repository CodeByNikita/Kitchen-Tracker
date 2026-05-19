import { useEffect, useRef } from "react";
import PropTypes from "prop-types";
import RecipeCard from "./RecipeCard";
import { recipeShape } from "./recipeShape";

function RecipeModal({ recipes, savedTitles, loading, error, onSaveRecipe, onMore, onClose }) {
  const dialogRef = useRef(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  return (
    <dialog ref={dialogRef} className="modal recipe-modal" onClose={onClose}>
      <div className="recipe-modal-header">
        <div>
          <h2>Recipe ideas</h2>
          <p>Built around the items closest to expiry.</p>
        </div>
      </div>

      {loading && <p className="empty recipe-empty">Thinking up recipes...</p>}
      {error && <p className="recipe-error">{error}</p>}

      {!loading && !error && recipes.length === 0 && (
        <p className="empty recipe-empty">No recipe ideas yet.</p>
      )}

      {!loading && !error && recipes.length > 0 && (
        <div className="recipe-list">
          {recipes.map((recipe) => (
            <RecipeCard
              key={recipe.title}
              recipe={recipe}
              saved={savedTitles.includes(recipe.title.toLowerCase())}
              onSave={onSaveRecipe}
            />
          ))}
        </div>
      )}

      <div className="form-actions">
        {recipes.length > 0 && (
          <button className="btn btn-ghost" disabled={loading} onClick={onMore}>
            {loading ? "Thinking..." : "More ideas"}
          </button>
        )}
        <button className="btn btn-primary" onClick={onClose}>
          Done
        </button>
      </div>
    </dialog>
  );
}

RecipeModal.propTypes = {
  recipes: PropTypes.arrayOf(PropTypes.shape(recipeShape)).isRequired,
  savedTitles: PropTypes.arrayOf(PropTypes.string).isRequired,
  loading: PropTypes.bool.isRequired,
  error: PropTypes.string,
  onSaveRecipe: PropTypes.func.isRequired,
  onMore: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default RecipeModal;
