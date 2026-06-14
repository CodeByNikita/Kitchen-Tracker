import PropTypes from "prop-types";

function normalizeTime(value) {
  const trimmed = value.trim();
  const compactMatch = /^([01]?\d|2[0-3])([0-5]\d)$/.exec(trimmed);
  if (compactMatch) {
    return `${compactMatch[1].padStart(2, "0")}:${compactMatch[2]}`;
  }

  const colonMatch = /^([01]?\d|2[0-3]):([0-5]\d)$/.exec(trimmed);
  if (colonMatch) {
    return `${colonMatch[1].padStart(2, "0")}:${colonMatch[2]}`;
  }

  return null;
}

function NotificationSettings({
  profileName,
  profileSaving,
  settings,
  saving,
  onAddTime,
  onChangeTime,
  onRemoveTime,
  onSaveProfile,
}) {
  const times = settings?.notificationTimes?.map((time) => time.slice(0, 5)) ?? ["09:00"];

  const submitProfile = (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    onSaveProfile(String(formData.get("displayName") ?? ""));
  };

  const commitTime = (event, index, currentTime) => {
    const nextTime = normalizeTime(event.currentTarget.value);
    if (!nextTime) {
      event.currentTarget.value = currentTime;
      return;
    }
    event.currentTarget.value = nextTime;
    if (nextTime !== currentTime) {
      onChangeTime(index, nextTime);
    }
  };

  return (
    <div className="settings-stack">
      <section className="settings-panel">
        <div>
          <h2>Profile</h2>
          <p>Choose the name shown in the app header.</p>
        </div>

        <form className="profile-form" onSubmit={submitProfile}>
          <label className="field">
            <span>Name</span>
            <input
              defaultValue={profileName ?? ""}
              maxLength="60"
              name="displayName"
              required
              type="text"
            />
          </label>
          <button className="btn btn-primary" disabled={profileSaving} type="submit">
            {profileSaving ? "Saving..." : "Save profile"}
          </button>
        </form>
      </section>

      <section className="settings-panel">
        <div>
          <h2>Notifications</h2>
          <p>Choose when expiry reminders should be sent each day. Add more times if useful.</p>
        </div>

        <div className="settings-times">
          {times.map((time, index) => (
            <div className="settings-time-row" key={`${time}-${index}`}>
              <label className="field settings-time-field">
                <span>Reminder time {index + 1}</span>
                <input
                  aria-label={`Reminder time ${index + 1}`}
                  defaultValue={time}
                  disabled={saving}
                  inputMode="numeric"
                  maxLength="5"
                  onBlur={(event) => commitTime(event, index, time)}
                  onKeyDown={(event) => {
                    if (event.key === "Enter") {
                      event.preventDefault();
                      event.currentTarget.blur();
                    }
                  }}
                  pattern="([01]?[0-9]|2[0-3]):[0-5][0-9]"
                  placeholder="09:00"
                  type="text"
                />
              </label>
              <button
                className="btn btn-sm btn-danger"
                disabled={saving || times.length === 1}
                onClick={() => onRemoveTime(index)}
                type="button"
              >
                Remove
              </button>
            </div>
          ))}
        </div>

        <button className="btn btn-ghost add-time-btn" disabled={saving} onClick={onAddTime} type="button">
          + Add reminder
        </button>
      </section>
    </div>
  );
}

NotificationSettings.propTypes = {
  profileName: PropTypes.string,
  profileSaving: PropTypes.bool.isRequired,
  settings: PropTypes.shape({
    notificationTimes: PropTypes.arrayOf(PropTypes.string),
    lastNotificationDate: PropTypes.string,
  }),
  saving: PropTypes.bool.isRequired,
  onAddTime: PropTypes.func.isRequired,
  onChangeTime: PropTypes.func.isRequired,
  onRemoveTime: PropTypes.func.isRequired,
  onSaveProfile: PropTypes.func.isRequired,
};

export default NotificationSettings;
