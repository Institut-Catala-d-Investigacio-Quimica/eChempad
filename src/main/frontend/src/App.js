import 'bootstrap/dist/css/bootstrap.min.css';
import logo from './logo.svg';
import './App.css';
import React, { Component } from 'react';


class App extends Component {
  state = {
    researchers: []
  };

  async componentDidMount() {
    const response = await fetch('http://localhost:8081/api/researcher')
        .then(response =>
          {
            if (!response.ok)
            {
              throw new Error(response.statusText)
            }
            return response.json()
          }
        ).catch(err =>
          {
            console.log(err)
          }
        );
    const body = await response.json();
    this.setState({researchers: body});
  }

  render() {
    const {researchers} = this.state;
    return (
        <div className="App">
          <header className="App-header">
            <img src={logo} className="App-logo" alt="logo" />
            <div className="App-intro">
              <h2>Researchers</h2>
              {researchers.map(researcher =>
                  <div key={researcher.id}>
                    {researcher.username} ({researcher.email})
                  </div>
              )}
            </div>
          </header>
        </div>
    );
  }
}

export default App;
