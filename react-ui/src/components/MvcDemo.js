import React, { useState, useEffect, useCallback } from 'react';

function MvcDemo() {
  const [auditCount, setAuditCount] = useState(0);
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const fetchAuditCount = useCallback(async () => {
    setIsLoading(true);
    setMessage('Fetching audit count...');
    try {
      const response = await fetch('/api/demo/mvc');
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setAuditCount(data); // Backend returns a number directly
      setMessage('Audit count fetched successfully.');
    } catch (error) {
      console.error("Failed to fetch audit count:", error);
      setAuditCount(0); // Reset or handle as appropriate
      setMessage(`Failed to fetch audit count: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  }, []); // Empty dependency array means this function is created once

  useEffect(() => {
    fetchAuditCount();
  }, [fetchAuditCount]); // fetchAuditCount is now a dependency

  const handleProcessRequest = async () => {
    setIsLoading(true);
    setMessage('Processing MVC request...');
    try {
      const response = await fetch('/api/demo/mvc', { method: 'POST' });
      if (!response.ok) {
        const errorData = await response.text(); // Or response.json() if backend sends structured error
        throw new Error(`HTTP error! status: ${response.status}, details: ${errorData}`);
      }
      const data = await response.json();
      setMessage(`MVC request processed successfully! Response: ${data.message}`);
      fetchAuditCount(); // Refresh count after successful POST
    } catch (error) {
      console.error("Failed to process MVC request:", error);
      setMessage(`Failed to process MVC request: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <h2>MVC Demo</h2>
      <p className="count-display">Current Audit Entry Count: {auditCount}</p>
      <button onClick={handleProcessRequest} disabled={isLoading}>
        {isLoading ? 'Processing...' : 'Process Request (MVC)'}
      </button>
      {message && <p className="message-area">{message}</p>}
    </>
  );
}

export default MvcDemo;
