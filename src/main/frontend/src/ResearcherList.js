import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';

class ResearcherList extends Component {

    constructor(props) {
        super(props);
        this.state = {researchers: []};
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        fetch('/api/researcher')
            .then(response => response.json())
            .then(data => this.setState({researchers: data}));
    }
}
export default ResearcherList;