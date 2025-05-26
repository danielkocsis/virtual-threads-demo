import React, { useState, useEffect, useCallback } from 'react';

function WebfluxDemo() {
  const [auditCount, setAuditCount] = useState(0);
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const fetchAuditCount = useCallback(async () => {
    setIsLoading(true);
    setMessage('Fetching audit count...');
    try {
      const response = await fetch('/api/demo/webflux');
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setAuditCount(data); // Backend returns a number directly
      setMessage('Audit count fetched successfully.');
    } catch (error) {
      console.error("Failed to fetch audit count:", error);
      setAuditCount(0);
      setMessage(`Failed to fetch audit count: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAuditCount();
  }, [fetchAuditCount]);

  const handleProcessRequest = async () => {
    setIsLoading(true);
    setMessage('Processing Webflux request...');
    try {
      const response = await fetch('/api/demo/webflux', { method: 'POST' });
      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(`HTTP error! status: ${response.status}, details: ${errorData}`);
      }
      const data = await response.json();
      setMessage(`Webflux request processed successfully! Response: ${data.message}`);
      fetchAuditCount(); // Refresh count
    } catch (error) {
      console.error("Failed to process Webflux request:", error);
      setMessage(`Failed to process Webflux request: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <h2>Webflux Demo</h2>
      <p className="count-display">Current Audit Entry Count: {auditCount}</p>
      <button onClick={handleProcessRequest} disabled={isLoading}>
        {isLoading ? 'Processing...' : 'Process Request (Webflux)'}
      </button>
      {message && <p className="message-area">{message}</p>}
    </>
  );
}

export default WebfluxDemo;
