import './App.css';
import MvcDemo from './components/MvcDemo';
import WebfluxDemo from './components/WebfluxDemo';
import LoomDemo from './components/LoomDemo';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>Spring Boot 3 & Virtual Threads Demo</h1>
      </header>
      <div className="DemoComponentsWrapper">
        <div className="DemoComponent">
          <MvcDemo />
        </div>
        <div className="DemoComponent">
          <WebfluxDemo />
        </div>
        <div className="DemoComponent">
          <LoomDemo />
        </div>
      </div>
    </div>
  );
}

export default App;
