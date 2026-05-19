import PropTypes from "prop-types";

function NotificationSettings({ settings, saving, onAddTime, onChangeTime, onRemoveTime }) {
  const times = settings?.notificationTimes?.map((time) => time.slice(0, 5)) ?? ["09:00"];

  return (
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
                type="time"
                value={time}
                disabled={saving}
                onChange={(e) => onChangeTime(index, e.target.value)}
              />
            </label>
            <button
              className="btn btn-sm btn-danger"
              disabled={saving || times.length === 1}
              onClick={() => onRemoveTime(index)}
            >
              Remove
            </button>
          </div>
        ))}
      </div>

      <button className="btn btn-ghost add-time-btn" disabled={saving} onClick={onAddTime}>
        + Add reminder
      </button>

      <p className="settings-note">
        For deployment, set cron-job.org to call the backend every hour. The backend only sends
        once per reminder time per day.
      </p>
    </section>
  );
}

NotificationSettings.propTypes = {
  settings: PropTypes.shape({
    notificationTimes: PropTypes.arrayOf(PropTypes.string),
    lastNotificationDate: PropTypes.string,
  }),
  saving: PropTypes.bool.isRequired,
  onAddTime: PropTypes.func.isRequired,
  onChangeTime: PropTypes.func.isRequired,
  onRemoveTime: PropTypes.func.isRequired,
};

export default NotificationSettings;
