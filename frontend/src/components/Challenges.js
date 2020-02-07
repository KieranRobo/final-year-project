import React from 'react';
import ChallengeTable from './challenges/ChallengeTable';
import axios from "../api.service";
import { Redirect } from "react-router-dom";
import Login from './Login';

class Challenges extends React.Component {

    state = {
        challenges: null,
        classes: null
    };

    constructor(props) {
        super(props);

        this.assignChallenge = this.assignChallenge.bind(this);
    }

    componentDidMount() {
        axios.Challenges.all()
        .then(response => {
            const newState = Object.assign({}, this.state, {
                challenges: response.data
        });
        
        this.setState(newState);
        });

        if (localStorage.getItem('lecturerId') != null) {
            axios.Lecturers.detailsById(localStorage.getItem('lecturerId'))
            .then(response => {
                const newState = Object.assign({}, this.state, {
                    classes: response.data.classes
            });
            
            this.setState(newState);
            });
        } else if (localStorage.getItem('studentId') != null) {
            axios.Students.detailsById(localStorage.getItem('student'))
            .then(response => {
                const newState = Object.assign({}, this.state, {
                    classes: response.data
            });
            
            this.setState(newState);
            });
        }
    }

    assignChallenge = (challengeId, classId) => {
        axios.Classes.assignChallenge(challengeId, classId)
    }



    render() {
        if (localStorage.getItem('lecturerId') == null) {
            return (<Redirect to='/login' />)
        }
        return (
            <div>
                <h2>View All Challenges</h2>
                <div>Select a challenge from the list below:</div>
                <ChallengeTable challenges={this.state.challenges} classes={this.state.classes} assignChallenge={this.assignChallenge}></ChallengeTable>
            </div>
            
        );
    }
}

export default Challenges;