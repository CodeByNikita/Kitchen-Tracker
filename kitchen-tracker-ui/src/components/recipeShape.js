import PropTypes from "prop-types";

export const recipeShape = {
  id: PropTypes.number,
  title: PropTypes.string.isRequired,
  uses: PropTypes.arrayOf(PropTypes.string).isRequired,
  extraIngredients: PropTypes.arrayOf(PropTypes.string).isRequired,
  steps: PropTypes.arrayOf(PropTypes.string).isRequired,
  timeMinutes: PropTypes.number.isRequired,
  difficulty: PropTypes.string.isRequired,
};
