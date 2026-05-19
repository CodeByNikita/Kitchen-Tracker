import { useState } from "react";
import PropTypes from "prop-types";

function ShoppingList({ items, loading, onAdd, onToggle, onDelete }) {
  const [name, setName] = useState("");

  const submit = (e) => {
    e.preventDefault();
    if (!name.trim()) return;
    onAdd(name.trim());
    setName("");
  };

  return (
    <section className="shopping-list">
      <form className="shopping-add" onSubmit={submit}>
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Add shopping item"
        />
        <button className="btn btn-primary" type="submit">
          Add
        </button>
      </form>

      {loading && <p className="empty">Loading shopping list...</p>}
      {!loading && items.length === 0 && <p className="empty">Shopping list is empty.</p>}

      {!loading && items.length > 0 && (
        <div className="shopping-items">
          {items.map((item) => (
            <div className={`shopping-item ${item.checked ? "checked" : ""}`} key={item.id}>
              <button className="shopping-check" onClick={() => onToggle(item.id)}>
                {item.checked ? "✓" : ""}
              </button>
              <span>{item.name}</span>
              <button className="btn btn-sm btn-danger" onClick={() => onDelete(item.id)}>
                Remove
              </button>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}

ShoppingList.propTypes = {
  items: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      name: PropTypes.string.isRequired,
      checked: PropTypes.bool.isRequired,
    })
  ).isRequired,
  loading: PropTypes.bool.isRequired,
  onAdd: PropTypes.func.isRequired,
  onToggle: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
};

export default ShoppingList;
