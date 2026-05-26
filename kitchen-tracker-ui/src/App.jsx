import { useState, useEffect, useCallback } from "react";
import axios from "axios";
import ItemCard from "./components/ItemCard";
import ItemForm from "./components/ItemForm";
import RecipeModal from "./components/RecipeModal";
import SavedRecipes from "./components/SavedRecipes";
import NotificationSettings from "./components/NotificationSettings";
import ShoppingList from "./components/ShoppingList";
import { useExpiryNotifications } from "./hooks/useExpiryNotifications";
import { usePushSubscription } from "./hooks/usePushSubscription";
import { apiUrl } from "./config";
import "./App.css";

const notifSupported = "Notification" in globalThis;

const ITEM_API = apiUrl("/api/items");
const RECIPE_API = apiUrl("/api/recipes/suggest");
const SAVED_RECIPE_API = apiUrl("/api/saved-recipes");
const SETTINGS_API = apiUrl("/api/settings");
const SHOPPING_API = apiUrl("/api/shopping-list");

const CATEGORIES = ["ALL", "DAIRY", "MEAT", "FISH", "VEG", "FRUIT", "PANTRY", "FROZEN", "OTHER"];
const LOCATIONS = ["ALL", "FRIDGE", "FREEZER", "CUPBOARD"];
const SORT_OPTIONS = [
  { value: "expiry", label: "Expiry" },
  { value: "name",   label: "Name" },
  { value: "added",  label: "Added" },
];
const QUICK_PRESETS = [
  { name: "Milk", category: "DAIRY", unit: "CARTONS", location: "FRIDGE" },
  { name: "Eggs", category: "DAIRY", unit: "ITEMS", location: "FRIDGE" },
  { name: "Bread", category: "PANTRY", unit: "LOAVES", location: "CUPBOARD" },
  { name: "Chicken", category: "MEAT", unit: "PACKETS", location: "FRIDGE" },
  { name: "Spinach", category: "VEG", unit: "BAGS", location: "FRIDGE" },
];

function renderItems(
  loading,
  filtered,
  setModalItem,
  handleDelete,
  handleMarkOpened,
  handleUseOne,
  handleAddToShopping
) {
  if (loading) return <p className="empty">Loading…</p>;
  if (filtered.length === 0) return <p className="empty">No items found.</p>;
  return (
    <div className="items-grid">
      {filtered.map((item) => (
        <ItemCard
          key={item.id}
          item={item}
          onEdit={() => setModalItem(item)}
          onDelete={() => handleDelete(item.id)}
          onMarkOpened={() => handleMarkOpened(item.id)}
          onUseOne={() => handleUseOne(item)}
          onAddToShopping={() => handleAddToShopping(item.name)}
        />
      ))}
    </div>
  );
}

function App() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [filterCategory, setFilterCategory] = useState("ALL");
  const [filterLocation, setFilterLocation] = useState("ALL");
  const [expiringOnly, setExpiringOnly] = useState(false);
  const [view, setView] = useState("inventory");
  const [modalItem, setModalItem] = useState(null);
  const [recipeModalOpen, setRecipeModalOpen] = useState(false);
  const [recipeIdeas, setRecipeIdeas] = useState([]);
  const [recipeLoading, setRecipeLoading] = useState(false);
  const [recipeError, setRecipeError] = useState(null);
  const [savedRecipes, setSavedRecipes] = useState([]);
  const [savedRecipesLoading, setSavedRecipesLoading] = useState(true);
  const [shoppingItems, setShoppingItems] = useState([]);
  const [shoppingLoading, setShoppingLoading] = useState(true);
  const [settings, setSettings] = useState(null);
  const [settingsSaving, setSettingsSaving] = useState(false);
  const [sortBy, setSortBy] = useState("expiry");
  const [notifPermission, setNotifPermission] = useState(
    notifSupported ? Notification.permission : "denied"
  );

  const requestNotifPermission = async () => {
    const result = await Notification.requestPermission();
    setNotifPermission(result);
  };

  const pushSubscribed = usePushSubscription(notifPermission);
  useExpiryNotifications(items, pushSubscribed);

  const fetchItems = useCallback(() => {
    axios
      .get(ITEM_API)
      .then((res) => setItems(res.data))
      .catch(() => setError("Could not load items — is the backend running?"))
      .finally(() => setLoading(false));
  }, []);

  const fetchSavedRecipes = useCallback(() => {
    axios
      .get(SAVED_RECIPE_API)
      .then((res) => setSavedRecipes(res.data))
      .catch(() => setError("Could not load saved recipes."))
      .finally(() => setSavedRecipesLoading(false));
  }, []);

  const fetchSettings = useCallback(() => {
    axios
      .get(SETTINGS_API)
      .then((res) => setSettings(res.data))
      .catch(() => setError("Could not load notification settings."));
  }, []);

  const fetchShoppingItems = useCallback(() => {
    axios
      .get(SHOPPING_API)
      .then((res) => setShoppingItems(res.data))
      .catch(() => setError("Could not load shopping list."))
      .finally(() => setShoppingLoading(false));
  }, []);

  useEffect(() => { fetchItems(); }, [fetchItems]);
  useEffect(() => { fetchSavedRecipes(); }, [fetchSavedRecipes]);
  useEffect(() => { fetchSettings(); }, [fetchSettings]);
  useEffect(() => { fetchShoppingItems(); }, [fetchShoppingItems]);

  const handleSave = async (payload, id) => {
    setSaving(true);
    setError(null);
    try {
      if (id) {
        await axios.put(`${ITEM_API}/${id}`, payload);
      } else {
        await axios.post(ITEM_API, payload);
      }
      setModalItem(null);
      fetchItems();
    } catch {
      setError("Failed to save — please try again.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    setError(null);
    try {
      await axios.delete(`${ITEM_API}/${id}`);
      fetchItems();
    } catch {
      setError("Failed to delete — please try again.");
    }
  };

  const handleMarkOpened = async (id) => {
    setError(null);
    try {
      await axios.patch(`${ITEM_API}/${id}/open`);
      fetchItems();
    } catch {
      setError("Failed to update item — please try again.");
    }
  };

  const handleUseOne = async (item) => {
    setError(null);
    try {
      await axios.patch(`${ITEM_API}/${item.id}/use-one`);
      if (item.quantity === 1) {
        await handleAddToShopping(item.name);
      }
      fetchItems();
    } catch {
      setError("Failed to update quantity — please try again.");
    }
  };

  const handleAddToShopping = async (name) => {
    setError(null);
    try {
      const res = await axios.post(SHOPPING_API, { name });
      setShoppingItems((current) => [res.data, ...current]);
    } catch {
      setError("Failed to add to shopping list — please try again.");
    }
  };

  const handleToggleShoppingItem = async (id) => {
    setError(null);
    try {
      const res = await axios.patch(`${SHOPPING_API}/${id}/toggle`);
      setShoppingItems((current) =>
        current.map((item) => (item.id === id ? res.data : item))
      );
    } catch {
      setError("Failed to update shopping list — please try again.");
    }
  };

  const handleDeleteShoppingItem = async (id) => {
    setError(null);
    try {
      await axios.delete(`${SHOPPING_API}/${id}`);
      setShoppingItems((current) => current.filter((item) => item.id !== id));
    } catch {
      setError("Failed to remove shopping item — please try again.");
    }
  };

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const cutoff = new Date(today);
  cutoff.setDate(today.getDate() + 7);

  const expiringItems = items.filter((item) => {
    if (!item.expiryDate) return false;
    const expiry = new Date(item.expiryDate + "T00:00:00");
    return expiry <= cutoff;
  });

  const todayItems = items.filter((item) => {
    if (item.quantity === 0) return true;
    if (item.dateOpened) return true;
    if (!item.expiryDate) return false;
    const expiry = new Date(item.expiryDate + "T00:00:00");
    return expiry <= today;
  });

  const filtered = items
    .filter((item) => filterCategory === "ALL" || item.category === filterCategory)
    .filter((item) => filterLocation === "ALL" || item.location === filterLocation)
    .filter((item) => {
      if (!expiringOnly) return true;
      if (!item.expiryDate) return false;
      const expiry = new Date(item.expiryDate + "T00:00:00");
      return expiry <= cutoff;
    })
    .sort((a, b) => {
      if (sortBy === "name") return (a.name ?? "").localeCompare(b.name ?? "");
      if (sortBy === "expiry") {
        if (!a.expiryDate && !b.expiryDate) return 0;
        if (!a.expiryDate) return 1;
        if (!b.expiryDate) return -1;
        return a.expiryDate.localeCompare(b.expiryDate);
      }
      return 0;
    });

  const recipeIngredients = () =>
    expiringItems.map((item) => ({
      name: item.name,
      quantity: item.quantity,
      unit: item.unit,
      category: item.category,
      expiryDate: item.expiryDate,
    }));

  const fetchRecipeIdeas = async ({ append = false } = {}) => {
    setRecipeModalOpen(true);
    setRecipeLoading(true);
    setRecipeError(null);
    if (!append) setRecipeIdeas([]);

    try {
      const res = await axios.post(RECIPE_API, {
        ingredients: recipeIngredients(),
        excludeTitles: append ? recipeIdeas.map((recipe) => recipe.title) : [],
      });
      const nextRecipes = res.data.recipes ?? [];
      setRecipeIdeas((current) => (append ? [...current, ...nextRecipes] : nextRecipes));
    } catch (err) {
      const message = err.response?.data?.message ?? "Could not generate recipe ideas right now.";
      setRecipeError(message);
    } finally {
      setRecipeLoading(false);
    }
  };

  const handleSuggestRecipes = () => fetchRecipeIdeas();
  const handleMoreRecipes = () => fetchRecipeIdeas({ append: true });

  const handleQuickAdd = (preset) => {
    setModalItem({
      ...preset,
      quantity: 1,
      expiryDate: "",
      onceOpenedDays: "",
    });
  };

  const handleSaveRecipe = async (recipe, alreadySaved = false) => {
    setError(null);
    try {
      if (alreadySaved) {
        const saved = savedRecipes.find(
          (current) => current.title.toLowerCase() === recipe.title.toLowerCase()
        );
        setSavedRecipes((current) =>
          current.filter((savedRecipe) => savedRecipe.title.toLowerCase() !== recipe.title.toLowerCase())
        );
        if (saved?.id) {
          axios.delete(`${SAVED_RECIPE_API}/${saved.id}`).catch(() => {});
        }
        return;
      }

      const res = await axios.post(SAVED_RECIPE_API, recipe);
      setSavedRecipes((current) => {
        const withoutDuplicate = current.filter(
          (saved) => saved.title.toLowerCase() !== res.data.title.toLowerCase()
        );
        return [res.data, ...withoutDuplicate];
      });
    } catch {
      setError("Failed to save recipe — please try again.");
    }
  };

  const handleDeleteSavedRecipe = async (id) => {
    setError(null);
    setSavedRecipes((current) => current.filter((recipe) => recipe.id !== id));
    axios.delete(`${SAVED_RECIPE_API}/${id}`).catch(() => {});
  };

  const saveNotificationTimes = async (notificationTimes) => {
    setSettingsSaving(true);
    setError(null);
    try {
      const uniqueTimes = [...new Set(notificationTimes)].sort();
      const res = await axios.put(SETTINGS_API, { notificationTimes: uniqueTimes });
      setSettings(res.data);
    } catch {
      setError("Failed to save notification times — please try again.");
    } finally {
      setSettingsSaving(false);
    }
  };

  const currentNotificationTimes = () =>
    settings?.notificationTimes?.map((time) => time.slice(0, 5)) ?? ["09:00"];

  const handleAddNotificationTime = () => {
    const times = currentNotificationTimes();
    saveNotificationTimes([...times, "18:00"]);
  };

  const handleChangeNotificationTime = (index, notificationTime) => {
    const times = currentNotificationTimes();
    const next = times.map((time, i) => (i === index ? notificationTime : time));
    saveNotificationTimes(next);
  };

  const handleRemoveNotificationTime = (index) => {
    const times = currentNotificationTimes().filter((_, i) => i !== index);
    saveNotificationTimes(times.length ? times : ["09:00"]);
  };

  const selectCategory = (cat) => {
    setFilterCategory(cat);
    setExpiringOnly(false);
  };

  const selectLocation = (loc) => {
    setFilterLocation(loc);
    setExpiringOnly(false);
  };

  const savedTitles = savedRecipes.map((recipe) => recipe.title.toLowerCase());

  return (
    <div className="app">
      <header className="header">
        <div>
          <h1>Kitchen Tracker</h1>
          <p className="subtitle">
            {loading ? "Loading…" : `${filtered.length} of ${items.length} items`}
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => setModalItem({})}>
          + Add Item
        </button>
      </header>

      <nav className="view-tabs" aria-label="Main sections">
        <button
          className={`tab ${view === "inventory" ? "active" : ""}`}
          onClick={() => setView("inventory")}
        >
          Inventory
        </button>
        <button
          className={`tab ${view === "today" ? "active" : ""}`}
          onClick={() => setView("today")}
        >
          Today ({todayItems.length})
        </button>
        <button
          className={`tab ${view === "saved" ? "active" : ""}`}
          onClick={() => setView("saved")}
        >
          Saved Recipes ({savedRecipes.length})
        </button>
        <button
          className={`tab ${view === "shopping" ? "active" : ""}`}
          onClick={() => setView("shopping")}
        >
          Shopping ({shoppingItems.filter((item) => !item.checked).length})
        </button>
        <button
          className={`tab ${view === "settings" ? "active" : ""}`}
          onClick={() => setView("settings")}
        >
          Settings
        </button>
      </nav>

      {view === "inventory" && (
        <section className="quick-add-panel">
          <span>Quick add</span>
          <div className="quick-add-buttons">
            {QUICK_PRESETS.map((preset) => (
              <button
                className="btn btn-sm btn-ghost"
                key={preset.name}
                onClick={() => handleQuickAdd(preset)}
              >
                + {preset.name}
              </button>
            ))}
          </div>
        </section>
      )}

      {view === "inventory" && (
        <section className="recipe-panel">
          <div>
            <strong>{expiringItems.length} expiring soon</strong>
            <p>Get recipe ideas that use the food closest to expiry.</p>
          </div>
          <button
            className="btn btn-primary"
            disabled={expiringItems.length === 0 || recipeLoading}
            onClick={handleSuggestRecipes}
          >
            {recipeLoading ? "Thinking..." : "Recipe ideas"}
          </button>
        </section>
      )}

      {notifSupported && notifPermission === "default" && (
        <div className="notif-prompt">
          <span>Enable notifications to get daily expiry alerts</span>
          <div className="notif-prompt-actions">
            <button className="btn btn-sm btn-primary" onClick={requestNotifPermission}>
              Enable
            </button>
            <button className="btn btn-sm btn-ghost" onClick={() => setNotifPermission("denied")}>
              Not now
            </button>
          </div>
        </div>
      )}

      {error && (
        <div className="error-banner">
          {error}
          <button className="error-dismiss" onClick={() => setError(null)}>✕</button>
        </div>
      )}

      {view === "inventory" && <div className="filter-bar">
        <div className="filter-row">
          <span className="filter-label">Location</span>
          {LOCATIONS.map((loc) => (
            <button
              key={loc}
              className={`tab ${filterLocation === loc ? "active" : ""}`}
              onClick={() => selectLocation(loc)}
            >
              {loc === "ALL" ? "All" : loc.charAt(0) + loc.slice(1).toLowerCase()}
            </button>
          ))}
        </div>

        <div className="filter-row">
          <span className="filter-label">Category</span>
          {CATEGORIES.map((cat) => (
            <button
              key={cat}
              className={`pill ${filterCategory === cat ? "active" : ""}`}
              onClick={() => selectCategory(cat)}
            >
              {cat === "ALL" ? "All" : cat.charAt(0) + cat.slice(1).toLowerCase()}
            </button>
          ))}
        </div>

        <div className="filter-row">
          <button
            className={`expiring-toggle ${expiringOnly ? "active" : ""}`}
            onClick={() => setExpiringOnly((v) => !v)}
          >
            Expiring within 7 days
          </button>
        </div>

        <div className="filter-row">
          <span className="filter-label">Sort</span>
          {SORT_OPTIONS.map((opt) => (
            <button
              key={opt.value}
              className={`tab ${sortBy === opt.value ? "active" : ""}`}
              onClick={() => setSortBy(opt.value)}
            >
              {opt.label}
            </button>
          ))}
        </div>
      </div>}

      {view === "inventory" &&
        renderItems(
          loading,
          filtered,
          setModalItem,
          handleDelete,
          handleMarkOpened,
          handleUseOne,
          handleAddToShopping
        )}

      {view === "today" &&
        renderItems(
          loading,
          todayItems,
          setModalItem,
          handleDelete,
          handleMarkOpened,
          handleUseOne,
          handleAddToShopping
        )}

      {view === "saved" && (
        <SavedRecipes
          recipes={savedRecipes}
          loading={savedRecipesLoading}
          onDelete={handleDeleteSavedRecipe}
        />
      )}

      {view === "shopping" && (
        <ShoppingList
          items={shoppingItems}
          loading={shoppingLoading}
          onAdd={handleAddToShopping}
          onToggle={handleToggleShoppingItem}
          onDelete={handleDeleteShoppingItem}
        />
      )}

      {view === "settings" && (
        <NotificationSettings
          settings={settings}
          saving={settingsSaving}
          onAddTime={handleAddNotificationTime}
          onChangeTime={handleChangeNotificationTime}
          onRemoveTime={handleRemoveNotificationTime}
        />
      )}

      {modalItem !== null && (
        <ItemForm
          item={modalItem}
          saving={saving}
          onSave={handleSave}
          onClose={() => setModalItem(null)}
        />
      )}

      {recipeModalOpen && (
        <RecipeModal
          recipes={recipeIdeas}
          savedTitles={savedTitles}
          loading={recipeLoading}
          error={recipeError}
          onSaveRecipe={handleSaveRecipe}
          onMore={handleMoreRecipes}
          onClose={() => setRecipeModalOpen(false)}
        />
      )}
    </div>
  );
}

export default App;
