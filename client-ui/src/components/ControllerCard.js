import React from 'react';

const ControllerCard = ({ title, count, onExecute, isLoading, statusMessage }) => {
  let statusClassName = 'status-message';
  if (statusMessage) {
    if (statusMessage.toLowerCase().includes('error') || statusMessage.toLowerCase().includes('failed')) {
      statusClassName += ' error';
    } else if (statusMessage.toLowerCase().includes('success') || statusMessage.toLowerCase().includes('successful')) {
      statusClassName += ' success';
    }
  }

  return (
    <div className="controller-card">
      <h2>{title}</h2>
      <p>Audit Entry Count: {count}</p>
      <button onClick={onExecute} disabled={isLoading}>
        {isLoading ? (
          <>
            Loading...
            <span></span>
          </>
        ) : (
          'Execute'
        )}
      </button>
      {statusMessage && <p className={statusClassName}>{statusMessage}</p>}
    </div>
  );
};

export default ControllerCard;
