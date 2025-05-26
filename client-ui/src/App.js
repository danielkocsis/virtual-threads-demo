import React, { useState, useEffect } from 'react';
import './App.css';
import ControllerCard from './components/ControllerCard';

function App() {
  const [mvcCount, setMvcCount] = useState(0);
  const [loomCount, setLoomCount] = useState(0);
  const [webfluxCount, setWebfluxCount] = useState(0);

  const [mvcLoading, setMvcLoading] = useState(false);
  const [loomLoading, setLoomLoading] = useState(false);
  const [webfluxLoading, setWebfluxLoading] = useState(false);

  const [mvcStatus, setMvcStatus] = useState('');
  const [loomStatus, setLoomStatus] = useState('');
  const [webfluxStatus, setWebfluxStatus] = useState('');

  const fetchCount = async (url, setCount, setStatus) => {
    try {
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setCount(data);
    } catch (error) {
      setStatus(`Error fetching count: ${error.message}`);
      console.error(`Error fetching count from ${url}:`, error);
    }
  };

  useEffect(() => {
    fetchCount('/api/demo/mvc', setMvcCount, setMvcStatus);
    fetchCount('/api/demo/loom', setLoomCount, setLoomStatus);
    fetchCount('/api/demo/webflux', setWebfluxCount, setWebfluxStatus);
  }, []);

  const handleExecute = async (url, setLoading, setStatus, fetchCountCallback) => {
    setLoading(true);
    setStatus('');
    try {
      const response = await fetch(url, { method: 'POST' });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      setStatus('Execution successful');
      await fetchCountCallback(); // Refresh count
    } catch (error) {
      setStatus(`Execution error: ${error.message}`);
      console.error(`Error executing ${url}:`, error);
    } finally {
      setLoading(false);
    }
  };

  const handleMvcExecute = () => {
    handleExecute('/api/demo/mvc', setMvcLoading, setMvcStatus, () => fetchCount('/api/demo/mvc', setMvcCount, setMvcStatus));
  };

  const handleLoomExecute = () => {
    handleExecute('/api/demo/loom', setLoomLoading, setLoomStatus, () => fetchCount('/api/demo/loom', setLoomCount, setLoomStatus));
  };

  const handleWebfluxExecute = () => {
    handleExecute('/api/demo/webflux', setWebfluxLoading, setWebfluxStatus, () => fetchCount('/api/demo/webflux', setWebfluxCount, setWebfluxStatus));
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Controller Performance Comparison</h1>
      </header>
      <div className="controller-container">
        <ControllerCard
          title="Spring MVC"
          count={mvcCount}
          onExecute={handleMvcExecute}
          isLoading={mvcLoading}
          statusMessage={mvcStatus}
        />
        <ControllerCard
          title="Spring Loom"
          count={loomCount}
          onExecute={handleLoomExecute}
          isLoading={loomLoading}
          statusMessage={loomStatus}
        />
        <ControllerCard
          title="Spring WebFlux"
          count={webfluxCount}
          onExecute={handleWebfluxExecute}
          isLoading={webfluxLoading}
          statusMessage={webfluxStatus}
        />
      </div>
    </div>
  );
}

export default App;
