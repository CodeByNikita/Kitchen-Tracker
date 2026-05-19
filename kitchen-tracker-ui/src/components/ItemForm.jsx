import { useState, useRef, useEffect } from "react";
import PropTypes from "prop-types";

const BLANK = {
  name: "",
  quantity: 1,
  unit: "ITEMS",
  category: "DAIRY",
  location: "FRIDGE",
  expiryDate: "",
  onceOpenedDays: "",
};

const TODAY = new Date().toISOString().split("T")[0];

function ItemForm({ item, saving, onSave, onClose }) {
  const isNew = !item?.id;
  const dialogRef = useRef(null);

  const [form, setForm] = useState(
    isNew
      ? { ...BLANK, ...item }
      : {
          name: item.name ?? "",
          quantity: item.quantity ?? 1,
          unit: item.unit ?? "ITEMS",
          category: item.category ?? "DAIRY",
          location: item.location ?? "FRIDGE",
          expiryDate: item.expiryDate ?? "",
          onceOpenedDays: item.onceOpenedDays ?? "",
        }
  );

  const [trackOpened, setTrackOpened] = useState(
    !isNew && item.onceOpenedDays != null
  );
  const [nameError, setNameError] = useState("");

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  const set = (key, val) => setForm((f) => ({ ...f, [key]: val }));

  const handleToggleOpened = (checked) => {
    setTrackOpened(checked);
    if (!checked) set("onceOpenedDays", "");
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.name.trim()) {
      setNameError("Name cannot be empty.");
      return;
    }
    setNameError("");
    const payload = {
      name: form.name.trim(),
      quantity: Number(form.quantity),
      unit: form.unit,
      category: form.category,
      location: form.location,
      expiryDate: form.expiryDate || null,
      onceOpenedDays: trackOpened && form.onceOpenedDays !== "" ? Number(form.onceOpenedDays) : null,
      dateOpened: item?.dateOpened ?? null,
    };
    onSave(payload, item?.id);
  };

  const expiryMin = isNew ? TODAY : undefined;

  return (
    <dialog ref={dialogRef} className="modal" onClose={onClose}>
      <h2>{isNew ? "Add Item" : "Edit Item"}</h2>
      <form onSubmit={handleSubmit} noValidate>
        <div className="form-fields">

          <label className="field">
            <span>Name</span>
            <input
              value={form.name}
              onChange={(e) => { set("name", e.target.value); setNameError(""); }}
              placeholder="e.g. Whole milk"
            />
            {nameError && <span className="field-error">{nameError}</span>}
          </label>

          <div className="field-row">
            <label className="field">
              <span>Quantity</span>
              <input
                type="number"
                min="0"
                required
                value={form.quantity}
                onChange={(e) => set("quantity", e.target.value)}
              />
            </label>
            <label className="field">
              <span>Unit</span>
              <select value={form.unit} onChange={(e) => set("unit", e.target.value)}>
                <optgroup label="Count">
                  <option value="ITEMS">Items</option>
                  <option value="CANS">Cans</option>
                  <option value="CARTONS">Cartons</option>
                  <option value="PACKETS">Packets</option>
                  <option value="BOTTLES">Bottles</option>
                  <option value="BAGS">Bags</option>
                  <option value="JARS">Jars</option>
                  <option value="BOXES">Boxes</option>
                  <option value="LOAVES">Loaves</option>
                  <option value="TUBS">Tubs</option>
                </optgroup>
                <optgroup label="Weight">
                  <option value="GRAMS">Grams</option>
                  <option value="KILOGRAMS">Kilograms</option>
                </optgroup>
                <optgroup label="Volume">
                  <option value="MILLILITRES">Millilitres</option>
                  <option value="LITRES">Litres</option>
                </optgroup>
              </select>
            </label>
          </div>

          <div className="field-row">
            <label className="field">
              <span>Category</span>
              <select value={form.category} onChange={(e) => set("category", e.target.value)}>
                {["DAIRY", "MEAT", "FISH", "VEG", "FRUIT", "PANTRY", "FROZEN", "OTHER"].map(
                  (c) => (
                    <option key={c} value={c}>
                      {c.charAt(0) + c.slice(1).toLowerCase()}
                    </option>
                  )
                )}
              </select>
            </label>
            <label className="field">
              <span>Location</span>
              <select value={form.location} onChange={(e) => set("location", e.target.value)}>
                {["FRIDGE", "FREEZER", "CUPBOARD"].map((l) => (
                  <option key={l} value={l}>
                    {l.charAt(0) + l.slice(1).toLowerCase()}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <label className="field">
            <span>Expiry date</span>
            <input
              type="date"
              value={form.expiryDate}
              min={expiryMin}
              onChange={(e) => set("expiryDate", e.target.value)}
            />
          </label>

          <div className="field">
            <label className="toggle-row">
              <span>Track use-by after opening</span>
              <input
                type="checkbox"
                className="toggle-switch"
                checked={trackOpened}
                onChange={(e) => handleToggleOpened(e.target.checked)}
              />
            </label>
            {trackOpened && (
              <label className="field toggle-field-input">
                <span>Days usable once opened</span>
                <input
                  type="number"
                  min="1"
                  required
                  value={form.onceOpenedDays}
                  onChange={(e) => set("onceOpenedDays", e.target.value)}
                  placeholder="e.g. 3"
                />
              </label>
            )}
          </div>

        </div>

        <div className="form-actions">
          <button type="button" className="btn btn-ghost" onClick={onClose} disabled={saving}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? "Saving…" : "Save"}
          </button>
        </div>
      </form>
    </dialog>
  );
}

ItemForm.propTypes = {
  item: PropTypes.shape({
    id: PropTypes.number,
    name: PropTypes.string,
    quantity: PropTypes.number,
    unit: PropTypes.string,
    category: PropTypes.string,
    location: PropTypes.string,
    expiryDate: PropTypes.string,
    onceOpenedDays: PropTypes.number,
    dateOpened: PropTypes.string,
  }).isRequired,
  saving: PropTypes.bool.isRequired,
  onSave: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ItemForm;
