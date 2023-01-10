import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import { getAuthorizationHeaderContent } from './getAuthorizationHeaderContent'


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

    async remove(id) {
        await fetch(`/api/researcher/${id}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': getAuthorizationHeaderContent("eChempad@iciq.es", "chemistry")
            }
        }).then(() => {
            let updatedResearchers = [...this.state.researchers].filter(i => i.id !== id);
            this.setState({researchers: updatedResearchers});
        });
    }

    render() {
        const {researchers, isLoading} = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const researcherList = researchers.map(researcher => {
            return <tr key={researcher.id}>
                <td style={{whiteSpace: 'nowrap'}}>{researcher.name}</td>
                <td>{researcher.email}</td>
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="primary" tag={Link} to={"/api/researchers/" + researcher.id}>Edit</Button>
                        <Button size="sm" color="danger" onClick={() => this.remove(researcher.id)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <div className="float-right">
                        <Button color="success" tag={Link} to="/api/researcher">Add Researcher</Button>
                    </div>
                    <h3>Researchers</h3>
                    <Table className="mt-4">
                        <thead>
                        <tr>
                            <th width="30%">Name</th>
                            <th width="30%">Email</th>
                            <th width="40%">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {researcherList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}
export default ResearcherList;