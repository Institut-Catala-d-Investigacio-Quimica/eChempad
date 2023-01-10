import 'bootstrap/dist/css/bootstrap.min.css';
import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import ResearcherList from "./ResearcherList";
import ResearcherEdit from "./ResearcherEdit";

function getAuthorizationHeaderContent(user, password)
{
    const token = user + ":" + password;

    // Should I be encoding this value????? does it matter???
    // Base64 Encoding -> btoa
    const hash = btoa(token);

    return "Basic " + hash;
}
class App extends Component {

    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} component={Home}/>
                    <Route path='/api/researcher' exact={true} component={ResearcherList}/>
                    <Route path='/api/researcher/:id' component={ResearcherEdit}/>
                </Switch>
            </Router>
        )
    }
}

export default App;
