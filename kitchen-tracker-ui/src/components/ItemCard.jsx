import { useState } from "react";
import PropTypes from "prop-types";

const UNIT_LABELS = {
  GRAMS:       { one: "g",       many: "g" },
  KILOGRAMS:   { one: "kg",      many: "kg" },
  MILLILITRES: { one: "ml",      many: "ml" },
  LITRES:      { one: "L",       many: "L" },
  ITEMS:       { one: "item",    many: "items" },
  CANS:        { one: "can",     many: "cans" },
  CARTONS:     { one: "carton",  many: "cartons" },
  PACKETS:     { one: "packet",  many: "packets" },
  BOTTLES:     { one: "bottle",  many: "bottles" },
  BAGS:        { one: "bag",     many: "bags" },
  JARS:        { one: "jar",     many: "jars" },
  BOXES:       { one: "box",     many: "boxes" },
  LOAVES:      { one: "loaf",    many: "loaves" },
  TUBS:        { one: "tub",     many: "tubs" },
};

function unitLabel(quantity, unit) {
  const labels = UNIT_LABELS[unit];
  if (!labels) return unit?.toLowerCase() ?? "";
  return quantity === 1 ? labels.one : labels.many;
}

function expiryStatus(expiryDate) {
  if (!expiryDate) return "ok";
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const cutoff = new Date(today);
  cutoff.setDate(today.getDate() + 7);
  const expiry = new Date(expiryDate + "T00:00:00");
  if (expiry < today) return "expired";
  if (expiry <= cutoff) return "expiring";
  return "ok";
}

function priorityLabel(expiryDate, quantity) {
  if (quantity === 0) return { text: "Used up", className: "priority-empty" };
  if (!expiryDate) return { text: "Fine", className: "priority-fine" };
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const expiry = new Date(expiryDate + "T00:00:00");
  const days = Math.ceil((expiry - today) / 86_400_000);
  if (days < 0) return { text: "Use now", className: "priority-now" };
  if (days === 0) return { text: "Use today", className: "priority-today" };
  if (days <= 3) return { text: "Use soon", className: "priority-soon" };
  return { text: "Fine", className: "priority-fine" };
}

function formatDate(dateStr) {
  if (!dateStr) return null;
  return new Date(dateStr + "T00:00:00").toLocaleDateString(undefined, {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

function ItemCard({ item, onEdit, onDelete, onMarkOpened, onUseOne, onAddToShopping }) {
  const [confirmingDelete, setConfirmingDelete] = useState(false);
  const status = expiryStatus(item.expiryDate);
  const priority = priorityLabel(item.expiryDate, item.quantity);
  const categoryClass = `badge cat-${item.category?.toLowerCase()}`;

  const openedUseBy =
    item.dateOpened && item.onceOpenedDays
      ? new Date(
          new Date(item.dateOpened + "T00:00:00").getTime() +
            item.onceOpenedDays * 86_400_000
        ).toLocaleDateString(undefined, { day: "numeric", month: "short", year: "numeric" })
      : null;

  return (
    <div className={`item-card ${status}`}>
      <div className="card-top">
        <span className="item-name">{item.name}</span>
        <span className="badge location-badge">
          {item.location?.charAt(0) + item.location?.slice(1).toLowerCase()}
        </span>
      </div>

      <div className="card-meta">
        <span className={`priority-badge ${priority.className}`}>{priority.text}</span>
        <span className="qty">
          {item.quantity} {unitLabel(item.quantity, item.unit)}
        </span>
        <span className={categoryClass}>
          {item.category?.charAt(0) + item.category?.slice(1).toLowerCase()}
        </span>
      </div>

      {item.expiryDate && (
        <p className={`expiry expiry-${status}`}>
          {status === "expired" ? "Expired" : "Expires"} {formatDate(item.expiryDate)}
        </p>
      )}

      {item.dateOpened && (
        <p className="opened-info">
          Opened {formatDate(item.dateOpened)}
          {openedUseBy && ` · use by ${openedUseBy}`}
        </p>
      )}

      <div className="card-actions">
        {!item.dateOpened && (
          <button className="btn btn-sm btn-ghost" onClick={onMarkOpened}>
            Mark opened
          </button>
        )}
        <button className="btn btn-sm btn-ghost" onClick={onEdit}>
          Edit
        </button>
        <button className="btn btn-sm btn-ghost" disabled={item.quantity === 0} onClick={onUseOne}>
          Use one
        </button>
        <button className="btn btn-sm btn-ghost" onClick={onAddToShopping}>
          Add to list
        </button>
        {confirmingDelete ? (
          <>
            <button className="btn btn-sm btn-danger" onClick={onDelete}>
              Confirm
            </button>
            <button className="btn btn-sm btn-ghost" onClick={() => setConfirmingDelete(false)}>
              Cancel
            </button>
          </>
        ) : (
          <button className="btn btn-sm btn-danger" onClick={() => setConfirmingDelete(true)}>
            Delete
          </button>
        )}
      </div>
    </div>
  );
}

ItemCard.propTypes = {
  item: PropTypes.shape({
    name: PropTypes.string,
    quantity: PropTypes.number,
    unit: PropTypes.string,
    category: PropTypes.string,
    location: PropTypes.string,
    expiryDate: PropTypes.string,
    dateOpened: PropTypes.string,
    onceOpenedDays: PropTypes.number,
  }).isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onMarkOpened: PropTypes.func.isRequired,
  onUseOne: PropTypes.func.isRequired,
  onAddToShopping: PropTypes.func.isRequired,
};

export default ItemCard;
