import PropTypes from "prop-types";
import { recipeShape } from "./recipeShape";

function RecipeCard({ recipe, saved, onSave, onDelete }) {
  return (
    <article className="recipe-card">
      <div className="recipe-card-top">
        <div>
          <h3>{recipe.title}</h3>
          <p className="recipe-difficulty">{recipe.difficulty}</p>
        </div>
        <div className="recipe-card-tools">
          <span>{recipe.timeMinutes} min</span>
          {onSave && (
            <button
              className={`heart-btn ${saved ? "saved" : ""}`}
              aria-label={saved ? "Recipe saved" : "Save recipe"}
              disabled={saved}
              onClick={() => onSave(recipe)}
            >
              {saved ? "♥" : "♡"}
            </button>
          )}
          {onDelete && (
            <button className="btn btn-sm btn-danger" onClick={() => onDelete(recipe.id)}>
              Remove
            </button>
          )}
        </div>
      </div>

      <div className="recipe-section">
        <strong>Uses</strong>
        <p>{recipe.uses.join(", ")}</p>
      </div>

      {recipe.extraIngredients.length > 0 && (
        <div className="recipe-section">
          <strong>Also needed</strong>
          <p>{recipe.extraIngredients.join(", ")}</p>
        </div>
      )}

      <ol className="recipe-steps">
        {recipe.steps.map((step) => (
          <li key={step}>{step}</li>
        ))}
      </ol>
    </article>
  );
}

RecipeCard.propTypes = {
  recipe: PropTypes.shape(recipeShape).isRequired,
  saved: PropTypes.bool,
  onSave: PropTypes.func,
  onDelete: PropTypes.func,
};

export default RecipeCard;
